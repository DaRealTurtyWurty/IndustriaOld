package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.registry.ResearchData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ResearchDataInit {
    private static final ResourceLocation REGISTRY_KEY = new ResourceLocation(Industria.MODID, "research_data");
    public static final DeferredRegister<ResearchData> RESEARCH_DATA = DeferredRegister.create(REGISTRY_KEY,
            Industria.MODID);
    private static final Supplier<IForgeRegistry<ResearchData>> REGISTRY = RESEARCH_DATA.makeRegistry(
            RegistryBuilder::new);

    public static final RegistryObject<ResearchData> TEST_0 = RESEARCH_DATA.register("test_0", () -> new ResearchData(
            new ResearchData.Builder().title("Test 0").description("This is a test").input(() -> Items.IRON_INGOT)
                    .requiredEnergy(100).result(Items.APPLE::getDefaultInstance)
                    .icon(Items.APPLE::getDefaultInstance).rarity(Rarity.COMMON)));
}
