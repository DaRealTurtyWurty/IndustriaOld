package dev.turtywurty.industria.menu.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class ToggleSlot extends Slot {
    protected boolean active = true;

    public ToggleSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
