package dev.turtywurty.industria.cables;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.ItemInit;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

public class Initialization {
    public static final RegistryObject<CableBlock> CABLE_BLOCK = BlockInit.BLOCKS.register("cable", CableBlock::new);
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE_BLOCK_ENTITY = BlockEntityInit.BLOCK_ENTITIES.register(
            "cable", () -> BlockEntityType.Builder.of(CableBlockEntity::new, CABLE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockItem> CABLE_ITEM = ItemInit.registerBlockItem("cable", CABLE_BLOCK);

    public static void init() {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Industria.MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(Initialization.CABLE_BLOCK_ENTITY.get(), CableBlockEntityRenderer::new);
        }
    }
}
