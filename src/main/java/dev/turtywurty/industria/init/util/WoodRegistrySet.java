package dev.turtywurty.industria.init.util;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.ItemInit;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WoodRegistrySet {
    private static final List<WoodRegistrySet> WOOD_SETS = new ArrayList<>();

    private final String name;
    private final AbstractTreeGrower tree;
    private final DeferredRegister<Block> blocks;
    private final DeferredRegister<Item> items;

    private WoodType woodType;
    private RegistryObject<RotatedPillarBlock> log;
    private RegistryObject<RotatedPillarBlock> strippedLog;
    private RegistryObject<RotatedPillarBlock> strippedWood;
    private RegistryObject<RotatedPillarBlock> wood;
    private RegistryObject<LeavesBlock> leaves;
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
    private RegistryObject<BoatItem> boatItem;

    public WoodRegistrySet(DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry, String name, AbstractTreeGrower tree) {
        this.name = name;
        this.tree = tree;
        this.blocks = blockRegistry;
        this.items = itemRegistry;

        register();
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

    public void register() {
        this.woodType = WoodType.create(this.name);
        registerBlocks();
        registerItems();
        WOOD_SETS.add(this);
    }

    protected void registerBlocks() {
        this.log = blocks.register(this.name + "_log",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.OAK_LOG)));
        this.strippedLog = blocks.register(this.name + "_stripped_log",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
        this.strippedWood = blocks.register(this.name + "_stripped_wood",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
        this.wood = blocks.register(this.name + "_wood",
                () -> new RotatedPillarBlock(Block.Properties.copy(Blocks.OAK_WOOD)));
        this.planks = blocks.register(this.name + "_planks", () -> new Block(Block.Properties.copy(Blocks.OAK_PLANKS)));
        this.slab = blocks.register(this.name + "_slab", () -> new SlabBlock(Block.Properties.copy(Blocks.OAK_SLAB)));
        this.stairs = blocks.register(this.name + "_stairs",
                () -> new StairBlock(() -> planks.get().defaultBlockState(), Block.Properties.copy(Blocks.OAK_STAIRS)));
        this.fence = blocks.register(this.name + "_fence",
                () -> new FenceBlock(Block.Properties.copy(Blocks.OAK_FENCE)));
        this.fenceGate = blocks.register(this.name + "_fence_gate",
                () -> new FenceGateBlock(Block.Properties.copy(Blocks.OAK_FENCE_GATE)));
        this.button = blocks.register(this.name + "_button",
                () -> new WoodButtonBlock(Block.Properties.copy(Blocks.OAK_BUTTON)));
        this.pressurePlate = blocks.register(this.name + "_pressure_plate",
                () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                        Block.Properties.copy(Blocks.OAK_PRESSURE_PLATE)));
        this.door = blocks.register(this.name + "_door", () -> new DoorBlock(Block.Properties.copy(Blocks.OAK_DOOR)));
        this.trapDoor = blocks.register(this.name + "_trapdoor",
                () -> new TrapDoorBlock(Block.Properties.copy(Blocks.OAK_TRAPDOOR)));
        this.leaves = blocks.register(this.name + "_leaves",
                () -> new LeavesBlock(Block.Properties.copy(Blocks.OAK_LEAVES)));
        this.sapling = blocks.register(this.name + "_sapling",
                () -> new SaplingBlock(tree, Block.Properties.copy(Blocks.OAK_SAPLING)));
        this.sign = blocks.register(this.name + "_sign",
                () -> new StandingSignBlock(Block.Properties.copy(Blocks.OAK_SIGN), this.woodType));
        this.wallSign = blocks.register(this.name + "_wall_sign",
                () -> new WallSignBlock(Block.Properties.copy(Blocks.OAK_WALL_SIGN), this.woodType));
    }

    protected void registerItems() {
        this.logItem = ItemInit.registerBlockItem(this.name + "_log", this.log);
        this.strippedLogItem = ItemInit.registerBlockItem(this.name + "_stripped_log", this.strippedLog);
        this.strippedWoodItem = ItemInit.registerBlockItem(this.name + "_stripped_wood", this.strippedWood);
        this.woodItem = ItemInit.registerBlockItem(this.name + "_wood", this.wood);
        this.planksItem = ItemInit.registerBlockItem(this.name + "_planks", this.planks);
        this.slabItem = ItemInit.registerBlockItem(this.name + "_slab", this.slab);
        this.stairsItem = ItemInit.registerBlockItem(this.name + "_stairs", this.stairs);
        this.fenceItem = ItemInit.registerBlockItem(this.name + "_fence", this.fence);
        this.fenceGateItem = ItemInit.registerBlockItem(this.name + "_fence_gate", this.fenceGate);
        this.buttonItem = ItemInit.registerBlockItem(this.name + "_button", this.button);
        this.pressurePlateItem = ItemInit.registerBlockItem(this.name + "_pressure_plate", this.pressurePlate);
        this.doorItem = ItemInit.registerBlockItem(this.name + "_door", this.door);
        this.trapDoorItem = ItemInit.registerBlockItem(this.name + "_trapdoor", this.trapDoor);
        this.leavesItem = ItemInit.registerBlockItem(this.name + "_leaves", this.leaves);
        this.saplingItem = ItemInit.registerBlockItem(this.name + "_sapling", this.sapling);
        this.signItem = ItemInit.registerItem(this.name + "_sign",
                () -> new SignItem(new Item.Properties().tab(Industria.TAB), this.sign.get(), this.wallSign.get()));
        this.boatItem = ItemInit.registerItem(this.name + "_boat",
                () -> new BoatItem(false, BoatTypes.RUBBER, new Item.Properties().tab(Industria.TAB).stacksTo(1)));
    }

    public WoodType getWoodType() {
        return woodType;
    }

    public RegistryObject<RotatedPillarBlock> getLog() {
        return log;
    }

    public RegistryObject<RotatedPillarBlock> getStrippedLog() {
        return strippedLog;
    }

    public RegistryObject<RotatedPillarBlock> getWood() {
        return wood;
    }

    public RegistryObject<LeavesBlock> getLeaves() {
        return leaves;
    }

    public RegistryObject<SaplingBlock> getSapling() {
        return sapling;
    }

    public RegistryObject<Block> getPlanks() {
        return planks;
    }

    public RegistryObject<StairBlock> getStairs() {
        return stairs;
    }

    public RegistryObject<SlabBlock> getSlab() {
        return slab;
    }

    public RegistryObject<FenceBlock> getFence() {
        return fence;
    }

    public RegistryObject<FenceGateBlock> getFenceGate() {
        return fenceGate;
    }

    public RegistryObject<DoorBlock> getDoor() {
        return door;
    }

    public RegistryObject<TrapDoorBlock> getTrapDoor() {
        return trapDoor;
    }

    public RegistryObject<PressurePlateBlock> getPressurePlate() {
        return pressurePlate;
    }

    public RegistryObject<ButtonBlock> getButton() {
        return button;
    }

    public RegistryObject<SignBlock> getSign() {
        return sign;
    }

    public RegistryObject<WallSignBlock> getWallSign() {
        return wallSign;
    }

    public RegistryObject<BlockItem> getLogItem() {
        return logItem;
    }

    public RegistryObject<BlockItem> getStrippedLogItem() {
        return strippedLogItem;
    }

    public RegistryObject<BlockItem> getWoodItem() {
        return woodItem;
    }

    public RegistryObject<BlockItem> getLeavesItem() {
        return leavesItem;
    }

    public RegistryObject<BlockItem> getSaplingItem() {
        return saplingItem;
    }

    public RegistryObject<BlockItem> getPlanksItem() {
        return planksItem;
    }

    public RegistryObject<BlockItem> getStairsItem() {
        return stairsItem;
    }

    public RegistryObject<BlockItem> getSlabItem() {
        return slabItem;
    }

    public RegistryObject<BlockItem> getFenceItem() {
        return fenceItem;
    }

    public RegistryObject<BlockItem> getFenceGateItem() {
        return fenceGateItem;
    }

    public RegistryObject<BlockItem> getDoorItem() {
        return doorItem;
    }

    public RegistryObject<BlockItem> getTrapDoorItem() {
        return trapDoorItem;
    }

    public RegistryObject<BlockItem> getPressurePlateItem() {
        return pressurePlateItem;
    }

    public RegistryObject<BlockItem> getButtonItem() {
        return buttonItem;
    }

    public RegistryObject<SignItem> getSignItem() {
        return signItem;
    }

    public RegistryObject<BoatItem> getBoatItem() {
        return boatItem;
    }

    public RegistryObject<RotatedPillarBlock> getStrippedWood() {
        return strippedWood;
    }

    public RegistryObject<BlockItem> getStrippedWoodItem() {
        return strippedWoodItem;
    }

    public static List<WoodRegistrySet> getWoodSets() {
        return WOOD_SETS;
    }
}
