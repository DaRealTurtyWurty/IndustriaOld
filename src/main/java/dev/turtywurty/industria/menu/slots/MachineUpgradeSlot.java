package dev.turtywurty.industria.menu.slots;

import dev.turtywurty.industria.items.MachineUpgradeItem;
import dev.turtywurty.industria.registry.MachineUpgrade;
import io.github.darealturtywurty.turtylib.common.container.slot.SlotWithRestriction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class MachineUpgradeSlot extends SlotWithRestriction {
    private final List<Supplier<MachineUpgrade>> upgrades;
    private final int[] validSlots;

    public MachineUpgradeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,
            List<Supplier<MachineUpgrade>> upgrades, int... validSlots) {
        super(itemHandler, index, xPosition, yPosition, stack -> stack.getItem() instanceof MachineUpgradeItem);
        this.upgrades = upgrades;
        this.validSlots = validSlots;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return super.mayPlace(stack) && canAddItem(stack);
    }

    private boolean canAddItem(ItemStack stack) {
        if (stack.getItem() instanceof MachineUpgradeItem upgradeItem) {
            if (this.upgrades.stream().map(Supplier::get).noneMatch(upgradeItem.getUpgrade()::equals))
                return false;

            for (int slot : validSlots) {
                ItemStack inSlot = getItemHandler().getStackInSlot(slot);
                if (inSlot.getItem() instanceof MachineUpgradeItem upgradeItemInSlot) {
                    if (upgradeItemInSlot.getUpgrade().equals(upgradeItem.getUpgrade())) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }
}
