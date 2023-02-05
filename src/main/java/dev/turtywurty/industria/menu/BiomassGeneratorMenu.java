package dev.turtywurty.industria.menu;

import dev.turtywurty.industria.blockentity.BiomassGeneratorBlockEntity;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.MenuInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BiomassGeneratorMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;

    protected BiomassGeneratorMenu(int containerId, Inventory playerInv, IItemHandler slots, BlockPos pos, ContainerData data) {
        super(MenuInit.BIOMASS_GENERATOR.get(), containerId);
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;

        final int slotSizePlus2 = 18;

        // Input Slot
        this.addSlot(new SlotItemHandler(slots, 0, 81, 27));

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

    public static MenuConstructor getServerMenu(BiomassGeneratorBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> new BiomassGeneratorMenu(id, playerInv,
                blockEntity.getInventory().getNullableInventory(Direction.UP), pos, blockEntity.getContainerData());
    }

    public static BiomassGeneratorMenu getClientMenu(int id, Inventory playerInv) {
        return new BiomassGeneratorMenu(id, playerInv, new ItemStackHandler(1), BlockPos.ZERO,
                new SimpleContainerData(4));
    }

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
        return stillValid(this.access, pPlayer, BlockInit.BIOMASS_GENERATOR.get());
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getAmountGenerated() {
        return this.data.get(2);
    }

    public int getMaxEnergyGenerated() {
        return this.data.get(3);
    }
}
