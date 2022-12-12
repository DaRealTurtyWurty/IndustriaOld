package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.entity.ChestWoodBoat;
import dev.turtywurty.industria.entity.WoodBoat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, Industria.MODID);

    public static final RegistryObject<EntityType<WoodBoat>> BOAT = ENTITY_TYPES.register("boat",
            () -> EntityType.Builder.<WoodBoat>of(WoodBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F)
                    .clientTrackingRange(10).build(new ResourceLocation(Industria.MODID, "boat").toString()));

    public static final RegistryObject<EntityType<ChestWoodBoat>> CHEST_BOAT = ENTITY_TYPES.register("chest_boat",
            () -> EntityType.Builder.<ChestWoodBoat>of(ChestWoodBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F)
                    .clientTrackingRange(10).build(new ResourceLocation(Industria.MODID, "chest_boat").toString()));
}
