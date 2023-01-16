package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.menu.AgitatorMenu;
import dev.turtywurty.industria.menu.BiomassGeneratorMenu;
import dev.turtywurty.industria.menu.CrusherMenu;
import dev.turtywurty.industria.menu.ResearcherMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            Industria.MODID);

    public static final RegistryObject<MenuType<CrusherMenu>> CRUSHER = MENUS.register("crusher",
            () -> new MenuType<>(CrusherMenu::getClientMenu));
    public static final RegistryObject<MenuType<BiomassGeneratorMenu>> BIOMASS_GENERATOR = MENUS.register(
            "biomass_generator", () -> new MenuType<>(BiomassGeneratorMenu::getClientMenu));

    public static final RegistryObject<MenuType<ResearcherMenu>> RESEARCHER = MENUS.register("researcher",
            () -> new MenuType<>(ResearcherMenu::getClientMenu));

    public static final RegistryObject<MenuType<AgitatorMenu>> AGITATOR = MENUS.register("agitator",
            () -> createPositionedMenu(AgitatorMenu::getClientMenu));

    private static <T extends AbstractContainerMenu> MenuType<T> createPositionedMenu(ClientPositionedMenuConstructor<T> constructor) {
        return IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();

            return constructor.create(windowId, inv, pos);
        });
    }

    @FunctionalInterface
    interface ClientPositionedMenuConstructor<T extends AbstractContainerMenu> {
        T create(int id, Inventory playerInv, BlockPos pos);
    }
}
