package dev.turtywurty.industria.menu;

import com.mojang.datafixers.util.Pair;
import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.MenuInit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

// TODO: Figure out how to pass the fake player inventory to the menu
public class EntityInteractorMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;

    protected EntityInteractorMenu(int containerId, Inventory playerInv, Container slots, BlockPos pos, ContainerData data) {
        super(MenuInit.ENTITY_INTERACTOR.get(), containerId);
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;

        final int slotSizePlus2 = 18;

        // Fake Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(slots, col + row * 9 + 9, 8 + col * slotSizePlus2, 84 + row * slotSizePlus2));
            }
        }

        // Fake Player Hotbar
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(slots, row, 8 + row * slotSizePlus2, 142));
        }

        for(int index = 0; index < 4; ++index) {
            final EquipmentSlot equipmentslot = InventoryMenu.SLOT_IDS[index];
            this.addSlot(new Slot(playerInv, 39 - index, 8, 8 + index * 18) {
                public void set(ItemStack stack) {
                    ItemStack itemstack = this.getItem();
                    super.set(stack);
                    .onEquipItem(equipmentslot, itemstack, stack);
                }

                public int getMaxStackSize() {
                    return 1;
                }

                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentslot, owner);
                }

                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(player);
                }

                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()]);
                }
            });
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

    public static MenuConstructor getServerMenu(EntityInteractorBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> new EntityInteractorMenu(id, playerInv,
                blockEntity.getPlayer().getInventory(), pos, blockEntity.getContainerData());
    }

    public static EntityInteractorMenu getClientMenu(int id, Inventory playerInv) {
        return new EntityInteractorMenu(id, playerInv, new SimpleContainer(41), BlockPos.ZERO,
                new SimpleContainerData(2));
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
        return stillValid(this.access, pPlayer, BlockInit.ENTITY_INTERACTOR.get());
    }

    public int getEnergy() {
        return this.data.get(4);
    }

    public int getMaxEnergy() {
        return this.data.get(5);
    }
}
