package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.BiomassGeneratorBlockEntity;
import dev.turtywurty.industria.blockentity.CrusherBlockEntity;
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
            "coal_generator",
            () -> BlockEntityType.Builder.of(BiomassGeneratorBlockEntity::new, BlockInit.BIOMASS_GENERATOR.get())
                    .build(null));
}
