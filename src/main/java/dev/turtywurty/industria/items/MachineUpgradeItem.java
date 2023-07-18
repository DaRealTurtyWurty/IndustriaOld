package dev.turtywurty.industria.items;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.registry.MachineUpgrade;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class MachineUpgradeItem extends Item {
    private final Supplier<MachineUpgrade> upgrade;

    public MachineUpgradeItem(Supplier<MachineUpgrade> upgrade) {
        super(new Item.Properties().stacksTo(8));
        this.upgrade = upgrade;
    }

    public MachineUpgrade getUpgrade() {
        return upgrade.get();
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return this.upgrade.get().rarity();
    }
}
