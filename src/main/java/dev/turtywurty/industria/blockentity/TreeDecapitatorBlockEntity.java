package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.block.TreeDecapitatorBlock;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.MachineUpgradeInit;
import dev.turtywurty.industria.items.MachineUpgradeItem;
import dev.turtywurty.industria.registry.MachineUpgrade;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.EnergyModule;
import dev.turtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TreeDecapitatorBlockEntity extends ModularBlockEntity implements MachineUpgradeHolder {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".tree_decapitator");
    private final EnergyModule energy;
    private final InventoryModule inventory;
    private final List<BlockPos> treePositions = new ArrayList<>();

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            if (pIndex == 0) {
                return TreeDecapitatorBlockEntity.this.energy.getCapabilityInstance().getEnergyStored();
            } else if (pIndex == 1) {
                return TreeDecapitatorBlockEntity.this.energy.getCapabilityInstance().getMaxEnergyStored();
            }

            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {
            if (pIndex == 0) {
                TreeDecapitatorBlockEntity.this.energy.getCapabilityInstance().setEnergy(pValue);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public TreeDecapitatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.TREE_DECAPITATOR.get(), pos, state);
        this.energy = addModule(new EnergyModule(this));
        this.inventory = addModule(new InventoryModule(this, 16));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide) return;

        if (!this.treePositions.isEmpty()) {
            int energyToUse = getEnergyCost();
            int amountPerTick = getAmountPerTick();

            if (this.energy.getCapabilityInstance().getEnergyStored() < (energyToUse * amountPerTick)) return;

            int counter = 0;
            while (!this.treePositions.isEmpty() && counter < amountPerTick) {
                BlockPos pos = this.treePositions.get(this.treePositions.size() - 1);
                if (destroyBlock(pos, !hasUpgrade(MachineUpgradeInit.COLLECTION.get()))) {
                    this.treePositions.remove(this.treePositions.size() - 1);
                    this.energy.getCapabilityInstance().extractEnergy(energyToUse, false);
                    counter++;
                } else {
                    break;
                }
            }

            return;
        }

        if (this.energy.getCapabilityInstance().getEnergyStored() <= 0) return;

        BlockPos facingPos = this.worldPosition.relative(this.getBlockState().getValue(TreeDecapitatorBlock.FACING));
        BlockState facingState = this.level.getBlockState(facingPos);
        if (shouldBreak(facingState)) {
            this.level.setBlock(facingPos, Blocks.AIR.defaultBlockState(), 0);
            this.treePositions.add(facingPos);
            this.treePositions.addAll(locateTree(this.level, facingPos, facingPos, new HashMap<>()));
            this.level.setBlock(facingPos, facingState, 0);

            this.treePositions.sort(Comparator.comparingInt(Vec3i::getY));
        }
    }

    private boolean canInsert(ItemStack item) {
        if (item.isEmpty()) return true;
        for (int index = 0; index < this.inventory.getCapabilityInstance().getSlots() - 4; index++) {
            ItemStack copy = this.inventory.getCapabilityInstance().insertItem(index, item, true);
            if (copy.isEmpty()) return true;

            item = copy;
        }

        return false;
    }

    private void insertItem(ItemStack item) {
        if (item.isEmpty()) return;

        for (int index = 0; index < this.inventory.getCapabilityInstance().getSlots() - 4; index++) {
            ItemStack stack = this.inventory.getCapabilityInstance().insertItem(index, item, false);
            if (stack.isEmpty()) return;

            item = stack;
        }
    }

    private boolean destroyBlock(BlockPos pos, boolean dropItems) {
        if (!shouldBreak(this.level.getBlockState(pos))) return true;

        boolean success = true;

        List<ItemStack> drops = getDrops(pos);
        if (!dropItems) {
            if (drops.stream().allMatch(this::canInsert) && this.level != null) {
                drops.forEach(this::insertItem);
            } else {
                success = false;
            }
        }

        if (success && this.level != null) {
            this.level.destroyBlock(pos, false);
            if (dropItems) drops.forEach(drop -> Block.popResource(this.level, pos, drop));
        }

        return success;
    }

    private ItemStack getAxe() {
        ItemStack axe = Items.NETHERITE_AXE.getDefaultInstance();
        if (hasUpgrade(MachineUpgradeInit.SILK_TOUCH.get())) {
            EnchantmentHelper.setEnchantments(Map.of(Enchantments.SILK_TOUCH, 1), axe);
        }

        return axe;
    }

    private List<ItemStack> getDrops(BlockPos pos) {
        List<ItemStack> drops = new ArrayList<>();
        if (level == null || level.isClientSide()) return drops;

        BlockState state = this.level.getBlockState(pos);
        drops.addAll(
                Block.getDrops(state, (ServerLevel) this.level, pos, this.level.getBlockEntity(pos), null, getAxe()));
        return drops;
    }

    private List<ItemStack> getUpgradeSlots() {
        List<ItemStack> upgrades = new ArrayList<>();
        for (int index = 12; index < this.inventory.getCapabilityInstance().getSlots(); index++) {
            upgrades.add(this.inventory.getCapabilityInstance().getStackInSlot(index));
        }

        return upgrades;
    }

    private boolean hasUpgrade(MachineUpgrade upgrade) {
        return getUpgradeSlots().stream().filter(stack -> !stack.isEmpty()).map(ItemStack::getItem)
                .filter(MachineUpgradeItem.class::isInstance).map(MachineUpgradeItem.class::cast)
                .map(MachineUpgradeItem::getUpgrade).anyMatch(upgrade::equals);
    }

    private int getMaxRange() {
        int range = 16;
        if (hasUpgrade(MachineUpgradeInit.RANGE_1.get())) range += 16;
        if (hasUpgrade(MachineUpgradeInit.RANGE_2.get())) range += 16;
        if (hasUpgrade(MachineUpgradeInit.RANGE_3.get())) range += 16;
        if (hasUpgrade(MachineUpgradeInit.RANGE_ULTIMATE.get())) range = 256;

        return range;
    }

    private int getMaxCount() {
        int count = 64;
        if (hasUpgrade(MachineUpgradeInit.COUNT_1.get())) count += 64;
        if (hasUpgrade(MachineUpgradeInit.COUNT_2.get())) count += 64;
        if (hasUpgrade(MachineUpgradeInit.COUNT_3.get())) count += 64;
        if (hasUpgrade(MachineUpgradeInit.COUNT_ULTIMATE.get())) count = 2056;

        return count;
    }

    private int getEnergyCost() {
        int cost = 100;
        if (hasUpgrade(MachineUpgradeInit.EFFICIENCY_1.get())) cost -= 10;
        if (hasUpgrade(MachineUpgradeInit.EFFICIENCY_2.get())) cost -= 10;
        if (hasUpgrade(MachineUpgradeInit.EFFICIENCY_3.get())) cost -= 10;
        if (hasUpgrade(MachineUpgradeInit.EFFICIENCY_ULTIMATE.get())) cost = 10;

        return cost;
    }

    private int getAmountPerTick() {
        int amount = 1;
        if (hasUpgrade(MachineUpgradeInit.SPEED_1.get())) amount += 1;
        if (hasUpgrade(MachineUpgradeInit.SPEED_2.get())) amount += 1;
        if (hasUpgrade(MachineUpgradeInit.SPEED_3.get())) amount += 1;
        if (hasUpgrade(MachineUpgradeInit.SPEED_ULTIMATE.get())) amount = 10;

        return amount;
    }

    private List<BlockPos> locateTree(Level level, BlockPos start, BlockPos pos, Map<BlockPos, BlockState> positions) {
        cardinals:
        for (Direction direction : Direction.values()) {
            BlockPos facingPos = pos.relative(direction);
            if (positions.containsKey(facingPos)) continue;
            if (start.distSqr(facingPos) > getMaxRange()) continue;

            BlockState facingState = level.getBlockState(facingPos);
            if (shouldBreak(facingState)) {
                positions.put(facingPos, facingState);
                level.setBlock(facingPos, Blocks.AIR.defaultBlockState(), 0);
                if (positions.size() >= getMaxCount()) break;

                locateTree(level, start, facingPos, positions);
            }

            for (Direction direction2 : Direction.values()) {
                facingPos = pos.relative(direction).relative(direction2);
                if (positions.containsKey(facingPos)) continue;
                if (start.distSqr(facingPos) > getMaxRange()) continue;

                facingState = level.getBlockState(facingPos);
                if (shouldBreak(facingState)) {
                    positions.put(facingPos, facingState);
                    level.setBlock(facingPos, Blocks.AIR.defaultBlockState(), 0);
                    if (positions.size() >= getMaxCount()) break cardinals;

                    locateTree(level, start, facingPos, positions);
                }
            }
        }

        positions.forEach((blockPos, blockState) -> level.setBlock(blockPos, blockState, 0));
        return positions.keySet().stream().toList();
    }

    private static boolean shouldBreak(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES) || state.is(BlockTags.CAVE_VINES) || state.is(
                Blocks.MANGROVE_ROOTS) || state.is(BlockTags.BEEHIVES) || state.is(BlockTags.WART_BLOCKS) || state.is(
                BlockTags.CRIMSON_STEMS) || state.is(BlockTags.WARPED_STEMS) || state.is(Blocks.SHROOMLIGHT);
    }

    public InventoryModule getInventory() {
        return this.inventory;
    }

    public ContainerData getContainerData() {
        return this.containerData;
    }

    public List<BlockPos> getPositions() {
        return this.treePositions;
    }

    @Override
    public List<Supplier<MachineUpgrade>> getValidUpgrades() {
        return List.of(MachineUpgradeInit.SPEED_1, MachineUpgradeInit.SPEED_2, MachineUpgradeInit.SPEED_3,
                MachineUpgradeInit.SPEED_ULTIMATE, MachineUpgradeInit.EFFICIENCY_1, MachineUpgradeInit.EFFICIENCY_2,
                MachineUpgradeInit.EFFICIENCY_3, MachineUpgradeInit.EFFICIENCY_ULTIMATE, MachineUpgradeInit.RANGE_1,
                MachineUpgradeInit.RANGE_2, MachineUpgradeInit.RANGE_3, MachineUpgradeInit.RANGE_ULTIMATE,
                MachineUpgradeInit.COUNT_1, MachineUpgradeInit.COUNT_2, MachineUpgradeInit.COUNT_3,
                MachineUpgradeInit.COUNT_ULTIMATE, MachineUpgradeInit.SILK_TOUCH, MachineUpgradeInit.COLLECTION);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        this.treePositions.clear();
        nbt.getList("TreePositions", Tag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).map(NbtUtils::readBlockPos)
                .forEach(this.treePositions::add);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        List<Tag> list = this.treePositions.stream().map(NbtUtils::writeBlockPos).map(Tag.class::cast).toList();
        nbt.put("TreePositions", new ListTag(list, Tag.TAG_COMPOUND));
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        List<Consumer<CompoundTag>> readData = super.getReadSyncData();

        readData.add(nbt -> {
            this.treePositions.clear();
            nbt.getList("TreePositions", Tag.TAG_COMPOUND).stream().map(CompoundTag.class::cast)
                    .map(NbtUtils::readBlockPos).forEach(this.treePositions::add);
        });

        return readData;
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        List<Consumer<CompoundTag>> writeData = super.getWriteSyncData();

        writeData.add(nbt -> {
            List<Tag> list = this.treePositions.stream().map(NbtUtils::writeBlockPos).map(Tag.class::cast).toList();
            nbt.put("TreePositions", new ListTag(list, Tag.TAG_COMPOUND));
        });

        return writeData;
    }
}
