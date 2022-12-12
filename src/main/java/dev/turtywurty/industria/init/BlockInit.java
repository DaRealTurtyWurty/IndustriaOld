package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.block.*;
import dev.turtywurty.industria.init.util.WoodRegistrySet;
import dev.turtywurty.industria.world.tree.grower.RubberTree;
import net.minecraft.world.level.block.Block;
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
}