package dev.turtywurty.industria.debug;

import dev.turtywurty.industria.Industria;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import org.jetbrains.annotations.Nullable;

public class DebugGogglesItem extends Item implements Wearable {
    public DebugGogglesItem() {
        super(new Item.Properties().tab(Industria.TAB).stacksTo(1));
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }
}
