package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.util.FluidRegistryContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidInit {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            ForgeRegistries.Keys.FLUID_TYPES, Industria.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
            Industria.MODID);

    public static final FluidRegistryContainer LATEX = new FluidRegistryContainer("latex",
            FluidType.Properties.create().canSwim(false).canDrown(true).canPushEntity(true).supportsBoating(false)
                    .canConvertToSource(false).density(4300).viscosity(4600).temperature(285),
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(Industria.MODID, "latex").overlay("latex")),
            BlockBehaviour.Properties.copy(Blocks.WATER), new Item.Properties().stacksTo(1));
}
