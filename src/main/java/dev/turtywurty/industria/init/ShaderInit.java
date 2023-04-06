package dev.turtywurty.industria.init;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.client.rendertypes.AgitatorRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ShaderInit {
    public static final ShaderTracker AGITATOR_SHADER = new ShaderTracker();

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            registerShader(event, "agitator", AgitatorRenderType.VERTEX_FORMAT, AGITATOR_SHADER);
        } catch (IOException exception) {
            Industria.LOGGER.error("Failed to load shaders", exception);
        }
    }

    private static void registerShader(RegisterShadersEvent event, String name, VertexFormat vertexFormat, ShaderTracker tracker) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(Industria.MODID, name),
                vertexFormat), tracker::setInstance);
    }

    private static void registerShader(RegisterShadersEvent event, ShaderInstance shader, ShaderTracker tracker) {
        event.registerShader(shader, tracker::setInstance);
    }

    public static class ShaderTracker implements Supplier<ShaderInstance> {
        private final RenderStateShard.ShaderStateShard shard = new RenderStateShard.ShaderStateShard(this);

        @Nullable
        private ShaderInstance instance;

        public void setInstance(ShaderInstance instance) {
            this.instance = instance;
        }

        @Override
        public ShaderInstance get() {
            return this.instance;
        }

        public RenderStateShard.ShaderStateShard getShard() {
            return this.shard;
        }
    }
}
