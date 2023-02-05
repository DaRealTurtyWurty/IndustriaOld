package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.registry.MachineUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MachineUpgradeInit {
    private static final ResourceLocation REGISTRY_KEY = new ResourceLocation(Industria.MODID, "machine_upgrade");
    public static final DeferredRegister<MachineUpgrade> MACHINE_UPGRADES = DeferredRegister.create(REGISTRY_KEY,
            Industria.MODID);
    public static final Supplier<IForgeRegistry<MachineUpgrade>> REGISTRY = MACHINE_UPGRADES.makeRegistry(
            RegistryBuilder::new);

    public static final RegistryObject<MachineUpgrade> COLLECTION = MACHINE_UPGRADES.register("collection",
            () -> new MachineUpgrade(ItemInit.COLLECTION_UPGRADE, Rarity.COMMON));

    public static final RegistryObject<MachineUpgrade> SPEED_1 = MACHINE_UPGRADES.register("speed_1",
            () -> new MachineUpgrade(ItemInit.SPEED_1_UPGRADE, Rarity.COMMON));

    public static final RegistryObject<MachineUpgrade> SPEED_2 = MACHINE_UPGRADES.register("speed_2",
            () -> new MachineUpgrade(ItemInit.SPEED_2_UPGRADE, Rarity.UNCOMMON));

    public static final RegistryObject<MachineUpgrade> SPEED_3 = MACHINE_UPGRADES.register("speed_3",
            () -> new MachineUpgrade(ItemInit.SPEED_3_UPGRADE, Rarity.RARE));

    public static final RegistryObject<MachineUpgrade> SPEED_ULTIMATE = MACHINE_UPGRADES.register("speed_ultimate",
            () -> new MachineUpgrade(ItemInit.SPEED_ULTIMATE_UPGRADE, Rarity.EPIC));

    public static final RegistryObject<MachineUpgrade> RANGE_1 = MACHINE_UPGRADES.register("range_1",
            () -> new MachineUpgrade(ItemInit.RANGE_1_UPGRADE, Rarity.COMMON));

    public static final RegistryObject<MachineUpgrade> RANGE_2 = MACHINE_UPGRADES.register("range_2",
            () -> new MachineUpgrade(ItemInit.RANGE_2_UPGRADE, Rarity.UNCOMMON));

    public static final RegistryObject<MachineUpgrade> RANGE_3 = MACHINE_UPGRADES.register("range_3",
            () -> new MachineUpgrade(ItemInit.RANGE_3_UPGRADE, Rarity.RARE));

    public static final RegistryObject<MachineUpgrade> RANGE_ULTIMATE = MACHINE_UPGRADES.register("range_ultimate",
            () -> new MachineUpgrade(ItemInit.RANGE_ULTIMATE_UPGRADE, Rarity.EPIC));

    public static final RegistryObject<MachineUpgrade> EFFICIENCY_1 = MACHINE_UPGRADES.register("efficiency_1",
            () -> new MachineUpgrade(ItemInit.EFFICIENCY_1_UPGRADE, Rarity.COMMON));

    public static final RegistryObject<MachineUpgrade> EFFICIENCY_2 = MACHINE_UPGRADES.register("efficiency_2",
            () -> new MachineUpgrade(ItemInit.EFFICIENCY_2_UPGRADE, Rarity.UNCOMMON));

    public static final RegistryObject<MachineUpgrade> EFFICIENCY_3 = MACHINE_UPGRADES.register("efficiency_3",
            () -> new MachineUpgrade(ItemInit.EFFICIENCY_3_UPGRADE, Rarity.RARE));

    public static final RegistryObject<MachineUpgrade> EFFICIENCY_ULTIMATE = MACHINE_UPGRADES.register("efficiency_ultimate",
            () -> new MachineUpgrade(ItemInit.EFFICIENCY_ULTIMATE_UPGRADE, Rarity.EPIC));

    public static final RegistryObject<MachineUpgrade> SILK_TOUCH = MACHINE_UPGRADES.register("silk_touch",
            () -> new MachineUpgrade(ItemInit.SILK_TOUCH_UPGRADE, Rarity.COMMON));

    public static final RegistryObject<MachineUpgrade> COUNT_1 = MACHINE_UPGRADES.register("count_1",
            () -> new MachineUpgrade(ItemInit.COUNT_1_UPGRADE, Rarity.COMMON));

    public static final RegistryObject<MachineUpgrade> COUNT_2 = MACHINE_UPGRADES.register("count_2",
            () -> new MachineUpgrade(ItemInit.COUNT_2_UPGRADE, Rarity.UNCOMMON));

    public static final RegistryObject<MachineUpgrade> COUNT_3 = MACHINE_UPGRADES.register("count_3",
            () -> new MachineUpgrade(ItemInit.COUNT_3_UPGRADE, Rarity.RARE));

    public static final RegistryObject<MachineUpgrade> COUNT_ULTIMATE = MACHINE_UPGRADES.register("count_ultimate",
            () -> new MachineUpgrade(ItemInit.COUNT_ULTIMATE_UPGRADE, Rarity.EPIC));
}
