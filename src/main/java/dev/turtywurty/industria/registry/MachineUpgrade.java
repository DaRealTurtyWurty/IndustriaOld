package dev.turtywurty.industria.registry;

import dev.turtywurty.industria.items.MachineUpgradeItem;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public record MachineUpgrade(Supplier<? extends MachineUpgradeItem> input, Rarity rarity) {
}
