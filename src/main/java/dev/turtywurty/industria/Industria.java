package dev.turtywurty.industria;

import dev.turtywurty.industria.client.ProjectorModels;
import dev.turtywurty.industria.init.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.slf4j.Log4jLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Industria.MODID)
public class Industria {
    public static final String MODID = "industria";
    public static final Logger LOGGER = LoggerFactory.getLogger(Industria.class);

    public Industria() {
        ProjectorModels.unlock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(bus);
        BlockInit.BLOCKS.register(bus);
        BlockEntityInit.BLOCK_ENTITIES.register(bus);
        MenuInit.MENUS.register(bus);
        MultiblockInit.MULTIBLOCKS.register(bus);
        RecipeInit.RECIPE_SERIALIZERS.register(bus);
        RecipeInit.RECIPE_TYPES.register(bus);
        ResearchDataInit.RESEARCH_DATA.register(bus);
    }

    public static final CreativeModeTab TAB = new CreativeModeTab(Industria.MODID) {
        @Override
        public ItemStack makeIcon() {
            return ItemInit.CRUSHER.get().getDefaultInstance();
        }
    };
}