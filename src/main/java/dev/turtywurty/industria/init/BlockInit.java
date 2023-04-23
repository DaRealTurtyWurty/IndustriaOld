package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Industria.MODID);

    public static final RegistryObject<CrusherBlock> CRUSHER = BLOCKS.register("crusher", CrusherBlock::new);
    public static final RegistryObject<BiomassGeneratorBlock> BIOMASS_GENERATOR = BLOCKS.register("biomass_generator",
            BiomassGeneratorBlock::new);
    public static final RegistryObject<ResearcherBlock> RESEARCHER = BLOCKS.register("researcher",
            ResearcherBlock::new);
    public static final RegistryObject<ProjectorBlock> PROJECTOR = BLOCKS.register("projector", ProjectorBlock::new);

    public static final RegistryObject<TreeTapBlock> TREE_TAP = BLOCKS.register("tree_tap", TreeTapBlock::new);

    public static final RegistryObject<AgitatorBlock> AGITATOR = BLOCKS.register("agitator", AgitatorBlock::new);

    public static final RegistryObject<TreeDecapitatorBlock> TREE_DECAPITATOR = BLOCKS.register("tree_decapitator",
            TreeDecapitatorBlock::new);

    public static final RegistryObject<EntityInteractorBlock> ENTITY_INTERACTOR = BLOCKS.register("entity_interactor",
            EntityInteractorBlock::new);

    public static final RegistryObject<DistillationTowerBlock> DISTILLATION_TOWER = BLOCKS.register(
            "distillation_tower", DistillationTowerBlock::new);

    public static final RegistryObject<ElectricHeaterBlock> ELECTRIC_HEATER = BLOCKS.register("electric_heater",
            ElectricHeaterBlock::new);

    public static final RegistryObject<Block> TRONA_ORE = BLOCKS.register("trona_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> NAHCOLITE_ORE = BLOCKS.register("nahcolite_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> TRONA_BLOCK = BLOCKS.register("trona_block",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> NAHCOLITE_BLOCK = BLOCKS.register("nahcolite_block",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> PHOSPHATE_ORE = BLOCKS.register("phosphate_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ZINC_ORE = BLOCKS.register("zinc_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ZINC_BLOCK = BLOCKS.register("zinc_block",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> SILVER_ORE = BLOCKS.register("silver_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SILVER_BLOCK = BLOCKS.register("silver_block",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> PLATINUM_ORE = BLOCKS.register("platinum_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> PLATINUM_BLOCK = BLOCKS.register("platinum_block",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> PYRITE_ORE = BLOCKS.register("pyrite_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> VANADINITE_ORE = BLOCKS.register("vanadinite_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CARNOTITE_ORE = BLOCKS.register("carnotite_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> PATRONITE_ORE = BLOCKS.register("patronite_ore",
            () -> new Block(Block.Properties.copy(Blocks.STONE)));
}