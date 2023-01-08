package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.entity.RopeEntity;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class EntityDataSerializerInit {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Industria.MODID);

    public static final RegistryObject<EntityDataSerializer<List<RopeEntity.RopeEnd>>> ROPE_ENDS = ENTITY_DATA_SERIALIZERS.register(
            "rope_end", RopeEntity.RopeEnd.Serializer::new);
}
