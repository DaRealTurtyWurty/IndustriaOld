package dev.turtywurty.industria.menu;

import dev.turtywurty.industria.blockentity.CrusherBlockEntity;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.MenuInit;
import io.github.darealturtywurty.turtylib.common.container.slot.SlotNoPlace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CrusherMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;

    protected CrusherMenu(int containerId, Inventory playerInv, IItemHandler inputSlot, IItemHandler outputSlot, BlockPos pos, ContainerData data) {
        super(MenuInit.CRUSHER.get(), containerId);
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;

        final int slotSizePlus2 = 18;

        // Input Slot
        this.addSlot(new SlotItemHandler(inputSlot, 0, 80, 13));

        // Output Slot
        this.addSlot(new SlotNoPlace(outputSlot, 0, 80, 57));

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * slotSizePlus2, 84 + row * slotSizePlus2));
            }
        }

        // Player Hotbar
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInv, row, 8 + row * slotSizePlus2, 142));
        }

        this.addDataSlots(data);
    }

    public static MenuConstructor getServerMenu(CrusherBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> new CrusherMenu(id, playerInv,
                blockEntity.getInventory().getCapabilityInstance(Direction.UP),
                blockEntity.getInventory().getCapabilityInstance(Direction.DOWN), pos, blockEntity.getContainerData());
    }

    public static CrusherMenu getClientMenu(int id, Inventory playerInv) {
        return new CrusherMenu(id, playerInv, new ItemStackHandler(1), new ItemStackHandler(1), BlockPos.ZERO,
                new SimpleContainerData(6));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();
            if (pIndex < 5) {
                if (!this.moveItemStackTo(current, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(current, 0, 2, false)) {
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
        return stillValid(this.access, pPlayer, BlockInit.CRUSHER.get());
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getEnergyProgress() {
        return this.data.get(1);
    }

    public int getMaxProgress() {
        return this.data.get(2);
    }

    public int getEnergyCost() {
        return this.data.get(3);
    }

    public int getEnergy() {
        return this.data.get(4);
    }

    public int getMaxEnergy() {
        return this.data.get(5);
    }
}
