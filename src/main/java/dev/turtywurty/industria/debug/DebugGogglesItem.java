package dev.turtywurty.industria.debug;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;

public class DebugGogglesItem extends Item implements Equipable {
    public DebugGogglesItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
