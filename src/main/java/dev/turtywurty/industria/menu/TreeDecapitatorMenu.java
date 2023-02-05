package dev.turtywurty.industria.menu;

import dev.turtywurty.industria.blockentity.TreeDecapitatorBlockEntity;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.MenuInit;
import dev.turtywurty.industria.menu.slots.MachineUpgradeSlot;
import dev.turtywurty.industria.registry.MachineUpgrade;
import io.github.darealturtywurty.turtylib.common.container.slot.SlotNoPlace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.Supplier;

public class TreeDecapitatorMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;

    protected TreeDecapitatorMenu(int containerId, Inventory playerInv, IItemHandler slots, BlockPos pos, ContainerData data, List<Supplier<MachineUpgrade>> upgrades) {
        super(MenuInit.TREE_DECAPITATOR.get(), containerId);
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;

        final int slotSizePlus2 = 18;

        // Item Slots
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 4; column++) {
                this.addSlot(new SlotNoPlace(slots, column + row * 4, 54 + column * slotSizePlus2,
                        17 + row * slotSizePlus2));
            }
        }

        for (int row = 0; row < 4; row++) {
            this.addSlot(
                    new MachineUpgradeSlot(slots, 12 + row, 180, 10 + row * slotSizePlus2, upgrades, 12, 13, 14, 15));
        }

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

    public static MenuConstructor getServerMenu(TreeDecapitatorBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> new TreeDecapitatorMenu(id, playerInv,
                blockEntity.getInventory().getCapabilityInstance(), pos, blockEntity.getContainerData(),
                blockEntity.getValidUpgrades());
    }

    public static TreeDecapitatorMenu getClientMenu(int id, Inventory playerInv, List<Supplier<MachineUpgrade>> upgrades) {
        return new TreeDecapitatorMenu(id, playerInv, new ItemStackHandler(16), BlockPos.ZERO,
                new SimpleContainerData(2), upgrades);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();
            if (pIndex < 16) {
                if (!this.moveItemStackTo(current, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(current, 0, 16, false)) {
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
        return stillValid(this.access, pPlayer, BlockInit.TREE_DECAPITATOR.get());
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }
}
