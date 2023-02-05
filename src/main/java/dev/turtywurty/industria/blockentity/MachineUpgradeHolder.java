package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.registry.MachineUpgrade;

import java.util.List;
import java.util.function.Supplier;

public interface MachineUpgradeHolder {
    List<Supplier<MachineUpgrade>> getValidUpgrades();
}
