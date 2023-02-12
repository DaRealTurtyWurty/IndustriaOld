package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.menu.*;
import dev.turtywurty.industria.registry.MachineUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    public static final RegistryObject<MenuType<TreeDecapitatorMenu>> TREE_DECAPITATOR = MENUS.register(
            "tree_decapitator", () -> IForgeMenuType.create((windowId, inv, data) -> {
                List<Supplier<MachineUpgrade>> upgrades = new ArrayList<>();

                ResourceLocation upgrade;
                do {
                    try {
                        upgrade = data.readResourceLocation();

                        final ResourceLocation finalUpgrade = upgrade;
                        upgrades.add(() -> MachineUpgradeInit.REGISTRY.get().getValue(finalUpgrade));
                    } catch (Exception exception) {
                        upgrade = null;
                    }
                } while (upgrade != null);

                return TreeDecapitatorMenu.getClientMenu(windowId, inv, upgrades);
            }));

    public static final RegistryObject<MenuType<EntityInteractorMenu>> ENTITY_INTERACTOR = MENUS.register(
            "entity_interactor", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();

                BlockEntity blockEntity = inv.player.level.getBlockEntity(pos);
                Player player = null;
                if (blockEntity instanceof EntityInteractorBlockEntity interactorBlockEntity) {
                    player = interactorBlockEntity.getPlayer();
                }

                return EntityInteractorMenu.getClientMenu(windowId, inv, player, pos);
            }));

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
