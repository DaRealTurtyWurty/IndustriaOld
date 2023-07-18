package dev.turtywurty.industria;

import dev.turtywurty.industria.client.ProjectorModels;
import dev.turtywurty.industria.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Industria.MODID)
public class Industria {
    public static final String MODID = "industria";
    public static final Logger LOGGER = LoggerFactory.getLogger(Industria.class);

    public Industria() {
        ProjectorModels.unlock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        TagInit.Blocks.init();
        TagInit.Items.init();

        ItemInit.ITEMS.register(bus);
        MachineUpgradeInit.MACHINE_UPGRADES.register(bus);
        BlockInit.BLOCKS.register(bus);
        BlockEntityInit.BLOCK_ENTITIES.register(bus);
        MenuInit.MENUS.register(bus);
        MultiblockInit.MULTIBLOCKS.register(bus);
        RecipeInit.RECIPE_SERIALIZERS.register(bus);
        RecipeInit.RECIPE_TYPES.register(bus);
        ResearchDataInit.RESEARCH_DATA.register(bus);
        EntityInit.ENTITY_TYPES.register(bus);
        EntityDataSerializerInit.ENTITY_DATA_SERIALIZERS.register(bus);
        FluidInit.FLUIDS.register(bus);
        FluidInit.FLUID_TYPES.register(bus);
        TrunkPlacerInit.TRUNK_PLACER_TYPES.register(bus);
        WoodSetInit.init();
    }
}