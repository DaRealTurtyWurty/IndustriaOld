package dev.turtywurty.industria.events.industria;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.event.entity.EntityEvent;

public class BeeStingEvent extends EntityEvent {
    private final Bee bee;

    public BeeStingEvent(Bee bee, Entity entity) {
        super(entity);
        this.bee = bee;
    }

    public Bee getBee() {
        return this.bee;
    }
}
