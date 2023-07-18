package dev.turtywurty.industria.entity;

import dev.turtywurty.industria.init.EntityInit;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class ChestWoodBoat extends WoodBoat implements HasCustomInventoryScreen, ContainerEntity {
    private static final int CONTAINER_SIZE = 27;

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this));

    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public ChestWoodBoat(EntityType<? extends ChestWoodBoat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ChestWoodBoat(Level pLevel, double pX, double pY, double pZ) {
        this(EntityInit.CHEST_BOAT.get(), pLevel);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    protected float getSinglePassengerXOffset() {
        return 0.15F;
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addChestVehicleSaveData(pCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readChestVehicleSaveData(pCompound);
    }

    @Override
    public void destroy(DamageSource pDamageSource) {
        super.destroy(pDamageSource);
        this.chestVehicleDestroyed(pDamageSource, this.level, this);
    }

    @Override
    public void remove(Entity.RemovalReason pReason) {
        if (!this.level.isClientSide && pReason.shouldDestroy()) {
            Containers.dropContents(this.level, this, this);
        }

        super.remove(pReason);
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (canAddPassenger(pPlayer) && !pPlayer.isSecondaryUseActive()) {
            return super.interact(pPlayer, pHand);
        } else {
            InteractionResult interactionresult = interactWithContainerVehicle(pPlayer);
            if (interactionresult.consumesAction()) {
                gameEvent(GameEvent.CONTAINER_OPEN, pPlayer);
                PiglinAi.angerNearbyPiglins(pPlayer, true);
            }

            return interactionresult;
        }
    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {
        pPlayer.openMenu(this);
        if (!pPlayer.level.isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, pPlayer);
            PiglinAi.angerNearbyPiglins(pPlayer, true);
        }
    }

    @Override
    public Item getDropItem() {
        return getBoatType().getWoodSet().chestBoatItem.get();
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getContainerSize() {
        return 27;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getItem(int pSlot) {
        return this.getChestVehicleItem(pSlot);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return this.removeChestVehicleItem(pSlot, pAmount);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return this.removeChestVehicleItemNoUpdate(pSlot);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.setChestVehicleItem(pSlot, pStack);
    }

    @Override
    public SlotAccess getSlot(int pSlot) {
        return this.getChestVehicleSlot(pSlot);
    }

    /**
     * For block entities, ensures the chunk containing the block entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void setChanged() {
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    @Override
    public boolean stillValid(Player pPlayer) {
        return this.isChestVehicleStillValid(pPlayer);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if (this.lootTable != null && pPlayer.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(pPlayerInventory.player);
            return ChestMenu.threeRows(pContainerId, pPlayerInventory, this);
        }
    }

    public void unpackLootTable(@Nullable Player pPlayer) {
        this.unpackChestVehicleLootTable(pPlayer);
    }

    @Nullable
    @Override
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceLocation pLootTable) {
        this.lootTable = pLootTable;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long pLootTableSeed) {
        this.lootTableSeed = pLootTableSeed;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER) return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    }
}