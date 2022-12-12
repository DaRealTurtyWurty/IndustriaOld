package dev.turtywurty.industria.init.util;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.entity.WoodBoat;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.ItemInit;
import dev.turtywurty.industria.items.WoodBoatItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WoodRegistrySet {
    private static final List<WoodRegistrySet> WOOD_SETS = new ArrayList<>();

    private final String name;
    private final AbstractTreeGrower tree;

    public final WoodType woodType;
    public final RegistryObject<RotatedPillarBlock> log;
    public final RegistryObject<RotatedPillarBlock> strippedLog;
    public final RegistryObject<RotatedPillarBlock> strippedWood;
    public final RegistryObject<RotatedPillarBlock> wood;
    public final RegistryObject<Block> leaves;
    public final RegistryObject<SaplingBlock> sapling;
    public final RegistryObject<Block> planks;
    public final RegistryObject<StairBlock> stairs;
    public final RegistryObject<SlabBlock> slab;
    public final RegistryObject<FenceBlock> fence;
    public final RegistryObject<FenceGateBlock> fenceGate;
    public final RegistryObject<DoorBlock> door;
    public final RegistryObject<TrapDoorBlock> trapDoor;
    public final RegistryObject<PressurePlateBlock> pressurePlate;
    public final RegistryObject<ButtonBlock> button;
    public final RegistryObject<SignBlock> sign;
    public final RegistryObject<WallSignBlock> wallSign;

    public final RegistryObject<BlockItem> logItem;
    public final RegistryObject<BlockItem> strippedLogItem;
    public final RegistryObject<BlockItem> strippedWoodItem;
    public final RegistryObject<BlockItem> woodItem;
    public final RegistryObject<BlockItem> leavesItem;
    public final RegistryObject<BlockItem> saplingItem;
    public final RegistryObject<BlockItem> planksItem;
    public final RegistryObject<BlockItem> stairsItem;
    public final RegistryObject<BlockItem> slabItem;
    public final RegistryObject<BlockItem> fenceItem;
    public final RegistryObject<BlockItem> fenceGateItem;
    public final RegistryObject<BlockItem> doorItem;
    public final RegistryObject<BlockItem> trapDoorItem;
    public final RegistryObject<BlockItem> pressurePlateItem;
    public final RegistryObject<BlockItem> buttonItem;
    public final RegistryObject<SignItem> signItem;
    public final RegistryObject<WoodBoatItem> boatItem;
    public final RegistryObject<WoodBoatItem> chestBoatItem;

    public WoodRegistrySet(Builder builder) {
        WOOD_SETS.add(this);

        this.name = builder.name;
        this.tree = builder.tree;

        this.woodType = builder.woodType != null ? builder.woodType : WoodType.create(
                Industria.MODID + ":" + this.name);
        WoodType.register(this.woodType);

        this.log = builder.log != null ? builder.log : BlockInit.BLOCKS.register(this.name + "_log",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.OAK_LOG)));
        this.strippedLog = builder.strippedLog != null ? builder.strippedLog : BlockInit.BLOCKS.register(
                this.name + "_stripped_log",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
        this.strippedWood = builder.strippedWood != null ? builder.strippedWood : BlockInit.BLOCKS.register(
                this.name + "_stripped_wood",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
        this.wood = builder.wood != null ? builder.wood : BlockInit.BLOCKS.register(this.name + "_wood",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.OAK_WOOD)));
        this.leaves = builder.leaves != null ? builder.leaves : BlockInit.BLOCKS.register(this.name + "_leaves",
                () -> new LeavesBlock(Block.Properties.copy(Blocks.OAK_LEAVES)));
        this.sapling = builder.sapling != null ? builder.sapling : BlockInit.BLOCKS.register(this.name + "_sapling",
                () -> new SaplingBlock(this.tree, Block.Properties.copy(Blocks.OAK_SAPLING)));
        this.planks = builder.planks != null ? builder.planks : BlockInit.BLOCKS.register(this.name + "_planks",
                () -> new Block(Block.Properties.copy(Blocks.OAK_PLANKS)));
        this.stairs = builder.stairs != null ? builder.stairs : BlockInit.BLOCKS.register(this.name + "_stairs",
                () -> new StairBlock(() -> this.planks.get().defaultBlockState(),
                        Block.Properties.copy(Blocks.OAK_STAIRS)));
        this.slab = builder.slab != null ? builder.slab : BlockInit.BLOCKS.register(this.name + "_slab",
                () -> new SlabBlock(Block.Properties.copy(Blocks.OAK_SLAB)));
        this.fence = builder.fence != null ? builder.fence : BlockInit.BLOCKS.register(this.name + "_fence",
                () -> new FenceBlock(Block.Properties.copy(Blocks.OAK_FENCE)));
        this.fenceGate = builder.fenceGate != null ? builder.fenceGate : BlockInit.BLOCKS.register(
                this.name + "_fence_gate", () -> new FenceGateBlock(Block.Properties.copy(Blocks.OAK_FENCE_GATE)));
        this.door = builder.door != null ? builder.door : BlockInit.BLOCKS.register(this.name + "_door",
                () -> new DoorBlock(Block.Properties.copy(Blocks.OAK_DOOR)));
        this.trapDoor = builder.trapDoor != null ? builder.trapDoor : BlockInit.BLOCKS.register(this.name + "_trapdoor",
                () -> new TrapDoorBlock(Block.Properties.copy(Blocks.OAK_TRAPDOOR)));
        this.pressurePlate = builder.pressurePlate != null ? builder.pressurePlate : BlockInit.BLOCKS.register(
                this.name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                        Block.Properties.copy(Blocks.OAK_PRESSURE_PLATE)));
        this.button = builder.button != null ? builder.button : BlockInit.BLOCKS.register(this.name + "_button",
                () -> new WoodButtonBlock(Block.Properties.copy(Blocks.OAK_BUTTON)));
        this.sign = builder.sign != null ? builder.sign : BlockInit.BLOCKS.register(this.name + "_sign",
                () -> new StandingSignBlock(Block.Properties.copy(Blocks.OAK_SIGN), this.woodType));
        this.wallSign = builder.wallSign != null ? builder.wallSign : BlockInit.BLOCKS.register(
                this.name + "_wall_sign",
                () -> new WallSignBlock(Block.Properties.copy(Blocks.OAK_WALL_SIGN), this.woodType));

        this.logItem = builder.logItem != null ? builder.logItem : ItemInit.registerBlockItem(this.name + "_log",
                this.log);
        this.strippedLogItem = builder.strippedLogItem != null ? builder.strippedLogItem : ItemInit.registerBlockItem(
                this.name + "_stripped_log", this.strippedLog);
        this.strippedWoodItem = builder.strippedWoodItem != null ? builder.strippedWoodItem : ItemInit.registerBlockItem(
                this.name + "_stripped_wood", this.strippedWood);
        this.woodItem = builder.woodItem != null ? builder.woodItem : ItemInit.registerBlockItem(this.name + "_wood",
                this.wood);
        this.leavesItem = builder.leavesItem != null ? builder.leavesItem : ItemInit.registerBlockItem(
                this.name + "_leaves", this.leaves);
        this.saplingItem = builder.saplingItem != null ? builder.saplingItem : ItemInit.registerBlockItem(
                this.name + "_sapling", this.sapling);
        this.planksItem = builder.planksItem != null ? builder.planksItem : ItemInit.registerBlockItem(
                this.name + "_planks", this.planks);
        this.stairsItem = builder.stairsItem != null ? builder.stairsItem : ItemInit.registerBlockItem(
                this.name + "_stairs", this.stairs);
        this.slabItem = builder.slabItem != null ? builder.slabItem : ItemInit.registerBlockItem(this.name + "_slab",
                this.slab);
        this.fenceItem = builder.fenceItem != null ? builder.fenceItem : ItemInit.registerBlockItem(
                this.name + "_fence", this.fence);
        this.fenceGateItem = builder.fenceGateItem != null ? builder.fenceGateItem : ItemInit.registerBlockItem(
                this.name + "_fence_gate", this.fenceGate);
        this.doorItem = builder.doorItem != null ? builder.doorItem : ItemInit.registerBlockItem(this.name + "_door",
                this.door);
        this.trapDoorItem = builder.trapDoorItem != null ? builder.trapDoorItem : ItemInit.registerBlockItem(
                this.name + "_trapdoor", this.trapDoor);
        this.pressurePlateItem = builder.pressurePlateItem != null ? builder.pressurePlateItem : ItemInit.registerBlockItem(
                this.name + "_pressure_plate", this.pressurePlate);
        this.buttonItem = builder.buttonItem != null ? builder.buttonItem : ItemInit.registerBlockItem(
                this.name + "_button", this.button);
        this.signItem = builder.signItem != null ? builder.signItem : ItemInit.registerItem(this.name + "_sign",
                () -> new SignItem(new Item.Properties().tab(Industria.TAB), this.sign.get(), this.wallSign.get()));
        this.boatItem = builder.boatItem != null ? builder.boatItem : ItemInit.registerItem(this.name + "_boat",
                () -> new WoodBoatItem(false, WoodBoat.Type.RUBBER,
                        new Item.Properties().tab(Industria.TAB).stacksTo(1)));
        this.chestBoatItem = builder.chestBoatItem != null ? builder.chestBoatItem : ItemInit.registerItem(
                this.name + "_chest_boat", () -> new WoodBoatItem(true, WoodBoat.Type.RUBBER,
                        new Item.Properties().tab(Industria.TAB).stacksTo(1)));
    }

    public String getName() {
        return name;
    }

    public AbstractTreeGrower getTree() {
        return tree;
    }

    @Override
    public String toString() {
        return "WoodRegistrySet [name=" + name + ", tree=" + tree + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        var that = (WoodRegistrySet) obj;
        return name.equals(that.name) && tree.equals(that.tree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tree);
    }

    public static List<WoodRegistrySet> getWoodSets() {
        return WOOD_SETS;
    }

    public static class Builder {
        private final String name;
        private final AbstractTreeGrower tree;

        private WoodType woodType;
        private RegistryObject<RotatedPillarBlock> log;
        private RegistryObject<RotatedPillarBlock> strippedLog;
        private RegistryObject<RotatedPillarBlock> strippedWood;
        private RegistryObject<RotatedPillarBlock> wood;
        private RegistryObject<Block> leaves;
        private RegistryObject<SaplingBlock> sapling;
        private RegistryObject<Block> planks;
        private RegistryObject<StairBlock> stairs;
        private RegistryObject<SlabBlock> slab;
        private RegistryObject<FenceBlock> fence;
        private RegistryObject<FenceGateBlock> fenceGate;
        private RegistryObject<DoorBlock> door;
        private RegistryObject<TrapDoorBlock> trapDoor;
        private RegistryObject<PressurePlateBlock> pressurePlate;
        private RegistryObject<ButtonBlock> button;
        private RegistryObject<SignBlock> sign;
        private RegistryObject<WallSignBlock> wallSign;

        private RegistryObject<BlockItem> logItem;
        private RegistryObject<BlockItem> strippedLogItem;
        private RegistryObject<BlockItem> strippedWoodItem;
        private RegistryObject<BlockItem> woodItem;
        private RegistryObject<BlockItem> leavesItem;
        private RegistryObject<BlockItem> saplingItem;
        private RegistryObject<BlockItem> planksItem;
        private RegistryObject<BlockItem> stairsItem;
        private RegistryObject<BlockItem> slabItem;
        private RegistryObject<BlockItem> fenceItem;
        private RegistryObject<BlockItem> fenceGateItem;
        private RegistryObject<BlockItem> doorItem;
        private RegistryObject<BlockItem> trapDoorItem;
        private RegistryObject<BlockItem> pressurePlateItem;
        private RegistryObject<BlockItem> buttonItem;
        private RegistryObject<SignItem> signItem;
        private RegistryObject<WoodBoatItem> boatItem;
        private RegistryObject<WoodBoatItem> chestBoatItem;

        public Builder(String name, AbstractTreeGrower tree) {
            this.name = name;
            this.tree = tree;
        }

        public Builder woodType(WoodType woodType) {
            this.woodType = woodType;
            return this;
        }

        public Builder log(RegistryObject<RotatedPillarBlock> log) {
            this.log = log;
            return this;
        }

        public Builder strippedLog(RegistryObject<RotatedPillarBlock> strippedLog) {
            this.strippedLog = strippedLog;
            return this;
        }

        public Builder strippedWood(RegistryObject<RotatedPillarBlock> strippedWood) {
            this.strippedWood = strippedWood;
            return this;
        }

        public Builder wood(RegistryObject<RotatedPillarBlock> wood) {
            this.wood = wood;
            return this;
        }

        public Builder leaves(RegistryObject<Block> leaves) {
            this.leaves = leaves;
            return this;
        }

        public Builder sapling(RegistryObject<SaplingBlock> sapling) {
            this.sapling = sapling;
            return this;
        }

        public Builder planks(RegistryObject<Block> planks) {
            this.planks = planks;
            return this;
        }

        public Builder stairs(RegistryObject<StairBlock> stairs) {
            this.stairs = stairs;
            return this;
        }

        public Builder slab(RegistryObject<SlabBlock> slab) {
            this.slab = slab;
            return this;
        }

        public Builder fence(RegistryObject<FenceBlock> fence) {
            this.fence = fence;
            return this;
        }

        public Builder fenceGate(RegistryObject<FenceGateBlock> fenceGate) {
            this.fenceGate = fenceGate;
            return this;
        }

        public Builder door(RegistryObject<DoorBlock> door) {
            this.door = door;
            return this;
        }

        public Builder trapDoor(RegistryObject<TrapDoorBlock> trapDoor) {
            this.trapDoor = trapDoor;
            return this;
        }

        public Builder pressurePlate(RegistryObject<PressurePlateBlock> pressurePlate) {
            this.pressurePlate = pressurePlate;
            return this;
        }

        public Builder button(RegistryObject<ButtonBlock> button) {
            this.button = button;
            return this;
        }

        public Builder sign(RegistryObject<SignBlock> sign) {
            this.sign = sign;
            return this;
        }

        public Builder wallSign(RegistryObject<WallSignBlock> wallSign) {
            this.wallSign = wallSign;
            return this;
        }

        public Builder logItem(RegistryObject<BlockItem> logItem) {
            this.logItem = logItem;
            return this;
        }

        public Builder strippedLogItem(RegistryObject<BlockItem> strippedLogItem) {
            this.strippedLogItem = strippedLogItem;
            return this;
        }

        public Builder strippedWoodItem(RegistryObject<BlockItem> strippedWoodItem) {
            this.strippedWoodItem = strippedWoodItem;
            return this;
        }

        public Builder woodItem(RegistryObject<BlockItem> woodItem) {
            this.woodItem = woodItem;
            return this;
        }

        public Builder leavesItem(RegistryObject<BlockItem> leavesItem) {
            this.leavesItem = leavesItem;
            return this;
        }

        public Builder saplingItem(RegistryObject<BlockItem> saplingItem) {
            this.saplingItem = saplingItem;
            return this;
        }

        public Builder planksItem(RegistryObject<BlockItem> planksItem) {
            this.planksItem = planksItem;
            return this;
        }

        public Builder stairsItem(RegistryObject<BlockItem> stairsItem) {
            this.stairsItem = stairsItem;
            return this;
        }

        public Builder slabItem(RegistryObject<BlockItem> slabItem) {
            this.slabItem = slabItem;
            return this;
        }

        public Builder fenceItem(RegistryObject<BlockItem> fenceItem) {
            this.fenceItem = fenceItem;
            return this;
        }

        public Builder fenceGateItem(RegistryObject<BlockItem> fenceGateItem) {
            this.fenceGateItem = fenceGateItem;
            return this;
        }

        public Builder doorItem(RegistryObject<BlockItem> doorItem) {
            this.doorItem = doorItem;
            return this;
        }

        public Builder trapDoorItem(RegistryObject<BlockItem> trapDoorItem) {
            this.trapDoorItem = trapDoorItem;
            return this;
        }

        public Builder pressurePlateItem(RegistryObject<BlockItem> pressurePlateItem) {
            this.pressurePlateItem = pressurePlateItem;
            return this;
        }

        public Builder buttonItem(RegistryObject<BlockItem> buttonItem) {
            this.buttonItem = buttonItem;
            return this;
        }

        public Builder signItem(RegistryObject<SignItem> signItem) {
            this.signItem = signItem;
            return this;
        }

        public Builder boatItem(RegistryObject<WoodBoatItem> boatItem) {
            this.boatItem = boatItem;
            return this;
        }

        public Builder chestBoatItem(RegistryObject<WoodBoatItem> chestBoatItem) {
            this.chestBoatItem = chestBoatItem;
            return this;
        }
    }

    public static WoodRegistrySet createDefault(String name, AbstractTreeGrower tree) {
        return new WoodRegistrySet(new Builder(name, tree));
    }
}
