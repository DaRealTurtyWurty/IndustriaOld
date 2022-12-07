package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.block.ProjectorBlock;
import dev.turtywurty.industria.block.BiomassGeneratorBlock;
import dev.turtywurty.industria.block.CrusherBlock;
import dev.turtywurty.industria.block.ResearcherBlock;
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

    public static final RegistryObject<ProjectorBlock> PROJECTOR = BLOCKS.register("projector",
            ProjectorBlock::new);
}
