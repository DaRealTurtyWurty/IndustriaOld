package dev.turtywurty.industria.menu;

import com.mojang.datafixers.util.Pair;
import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.MenuInit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EntityInteractorMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final BlockPos pos;

    protected EntityInteractorMenu(int containerId, Inventory playerInv, BlockPos pos, ContainerData data, @Nullable Player fakePlayer) {
        super(MenuInit.ENTITY_INTERACTOR.get(), containerId);
        this.access = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;
        this.pos = pos;

        final int slotSizePlus2 = 18;

        // Fake Player Armor
        for (int index = 0; index < 4; ++index) {
            final EquipmentSlot equipmentslot = InventoryMenu.SLOT_IDS[index];
            this.addSlot(new Slot(fakePlayer.getInventory(), 39 - index, 8, 8 + index * 18) {
                @Override
                public void set(ItemStack stack) {
                    ItemStack itemstack = this.getItem();
                    super.set(stack);

                    fakePlayer.onEquipItem(equipmentslot, itemstack, stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentslot, fakePlayer);
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS,
                            InventoryMenu.TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()]);
                }
            });
        }

        // Fake Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(fakePlayer.getInventory(), col + row * 9 + 9, 8 + col * slotSizePlus2,
                        84 + row * slotSizePlus2));
            }
        }

        // Fake Player Hotbar
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(fakePlayer.getInventory(), row, 8 + row * slotSizePlus2, 142));
        }

        // Fake Player Off-hand
        this.addSlot(new Slot(fakePlayer.getInventory(), 40, 77, 62) {
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(
                        new Slot(playerInv, col + row * 9 + 9, 8 + col * slotSizePlus2, 174 + row * slotSizePlus2));
            }
        }

        // Player Hotbar
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInv, row, 8 + row * slotSizePlus2, 232));
        }

        this.addDataSlots(data);
    }

    public static MenuConstructor getServerMenu(EntityInteractorBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> new EntityInteractorMenu(id, playerInv, pos, blockEntity.getContainerData(),
                blockEntity.getPlayer());
    }

    public static EntityInteractorMenu getClientMenu(int id, Inventory playerInv, Player player, BlockPos pos) {
        return new EntityInteractorMenu(id, playerInv, pos, new SimpleContainerData(3), player);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();
            if (pIndex < 41) {
                if (!this.moveItemStackTo(current, 41, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(current, 0, 41, false)) {
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
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getInteractRate() {
        return this.data.get(2);
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
