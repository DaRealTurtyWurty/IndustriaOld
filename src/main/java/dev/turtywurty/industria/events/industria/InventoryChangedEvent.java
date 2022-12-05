package dev.turtywurty.industria.events.industria;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class InventoryChangedEvent extends PlayerEvent {
    private final AbstractContainerMenu menu;
    private final int slotIndex;
    private final ItemStack stack;

    public InventoryChangedEvent(Player player, AbstractContainerMenu menu, int slotIndex, ItemStack stack) {
        super(player);
        this.menu = menu;
        this.slotIndex = slotIndex;
        this.stack = stack;
    }

    public AbstractContainerMenu getMenu() {
        return this.menu;
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
