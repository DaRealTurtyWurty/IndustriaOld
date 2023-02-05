package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES, Industria.MODID);

    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER = BLOCK_ENTITIES.register("crusher",
            () -> BlockEntityType.Builder.of(CrusherBlockEntity::new, BlockInit.CRUSHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BiomassGeneratorBlockEntity>> BIOMASS_GENERATOR = BLOCK_ENTITIES.register(
            "biomass_generator",
            () -> BlockEntityType.Builder.of(BiomassGeneratorBlockEntity::new, BlockInit.BIOMASS_GENERATOR.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<ResearcherBlockEntity>> RESEARCHER = BLOCK_ENTITIES.register(
            "researcher",
            () -> BlockEntityType.Builder.of(ResearcherBlockEntity::new, BlockInit.RESEARCHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ProjectorBlockEntity>> PROJECTOR = BLOCK_ENTITIES.register(
            "projector",
            () -> BlockEntityType.Builder.of(ProjectorBlockEntity::new, BlockInit.PROJECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<AgitatorBlockEntity>> AGITATOR = BLOCK_ENTITIES.register(
            "agitator",
            () -> BlockEntityType.Builder.of(AgitatorBlockEntity::new, BlockInit.AGITATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<TreeDecapitatorBlockEntity>> TREE_DECAPITATOR = BLOCK_ENTITIES.register(
            "tree_decapitator",
            () -> BlockEntityType.Builder.of(TreeDecapitatorBlockEntity::new, BlockInit.TREE_DECAPITATOR.get()).build(null));
}
