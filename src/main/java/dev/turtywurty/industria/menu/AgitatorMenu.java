package dev.turtywurty.industria.menu;

import dev.turtywurty.industria.blockentity.AgitatorBlockEntity;
import dev.turtywurty.industria.blockentity.util.directional.MultiDirectionalFluidTank;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.MenuInit;
import dev.turtywurty.industria.menu.slots.ToggleSlotItemHandler;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CAgitatorFluidUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class AgitatorMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final BlockPos pos;
    private final List<FluidStack> fluids = new ArrayList<>();
    @Nullable
    private AgitatorBlockEntity blockEntity;
    @Nullable
    private Player player;

    protected AgitatorMenu(int containerId, Inventory playerInv, IItemHandler slots, BlockPos pos, ContainerData data) {
        super(MenuInit.AGITATOR.get(), containerId);
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;
        this.pos = pos;

        final int slotSizePlus2 = 18;

        // Input Slot
        this.addSlot(new ToggleSlotItemHandler(slots, 0, 81, 27));
        this.addSlot(new ToggleSlotItemHandler(slots, 1, 81, 45));
        this.addSlot(new ToggleSlotItemHandler(slots, 2, 81, 63));
        this.addSlot(new ToggleSlotItemHandler(slots, 3, 81, 81));
        this.addSlot(new ToggleSlotItemHandler(slots, 4, 81, 99));
        this.addSlot(new ToggleSlotItemHandler(slots, 5, 81, 117));

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(
                        new Slot(playerInv, col + row * 9 + 9, 8 + col * slotSizePlus2, 145 + row * slotSizePlus2));
            }
        }

        // Player Hotbar
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInv, row, 8 + row * slotSizePlus2, 203));
        }

        this.addDataSlots(data);
    }

    public static MenuConstructor getServerMenu(AgitatorBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> {
            var menu = new AgitatorMenu(id, playerInv, blockEntity.getInventory().getCapabilityInstance(), pos,
                    blockEntity.getContainerData());

            blockEntity.getFluidInventory().getFluidHandler().getFluidTanks().stream().sorted(getTankComparator())
                    .map(MultiDirectionalFluidTank::getFluid).forEachOrdered(fluidStack -> {
                        System.out.println(
                                "Sending Fluid: " + fluidStack.getDisplayName().getString() + " " + fluidStack.getAmount());
                        menu.fluids.add(fluidStack);
                    });


            menu.blockEntity = blockEntity;
            menu.blockEntity.addMenu(menu);

            menu.player = player;
            return menu;
        };
    }

    public static AgitatorMenu getClientMenu(int id, Inventory playerInv, BlockPos pos) {
        return new AgitatorMenu(id, playerInv, new ItemStackHandler(6), pos, new SimpleContainerData(4));
    }

    // TODO: Make this work for the enable/disable-able slots
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();
            if (pIndex < 1) {
                if (!this.moveItemStackTo(current, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(current, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (current.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, BlockInit.AGITATOR.get());
    }

    public void broadcastFluidChanges() {
        if (this.blockEntity != null && this.player != null) {
            List<FluidStack> stacks = this.blockEntity.getFluidInventory().getFluidHandler().getFluidTanks().stream()
                    .sorted(getTankComparator()).map(MultiDirectionalFluidTank::getFluid).toList();

            boolean changed = false;
            for (int index = 0; index < stacks.size(); index++) {
                FluidStack stack = stacks.get(index);
                if (!stack.isFluidEqual(this.fluids.get(index)) || stack.getAmount() != this.fluids.get(index).getAmount()) {
                    changed = true;
                    break;
                }
            }

            if (changed) {
                for (int index = 0; index < this.fluids.size(); index++) {
                    this.fluids.set(index, stacks.get(index));
                }

                PacketManager.sendToClient(new CAgitatorFluidUpdatePacket(this.pos, this.fluids), this.player);
            }
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        if (this.blockEntity != null) {
            this.blockEntity.removeMenu(this);
        }
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public int getEnergy() {
        return this.data.get(2);
    }

    public int getMaxEnergy() {
        return this.data.get(3);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public List<FluidStack> getFluids() {
        return this.fluids;
    }

    public static Comparator<MultiDirectionalFluidTank> getTankComparator() {
        return (tank0, tank1) -> {
            EnumSet<Direction> tank0Directions = tank0.getDirections();
            EnumSet<Direction> tank1Directions = tank1.getDirections();

            int tank0Ordinals = tank0Directions.stream().mapToInt(Direction::ordinal).sum();
            int tank1Ordinals = tank1Directions.stream().mapToInt(Direction::ordinal).sum();

            return Integer.compare(tank0Ordinals, tank1Ordinals);
        };
    }
}
