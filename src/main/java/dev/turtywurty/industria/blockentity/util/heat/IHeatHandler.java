package dev.turtywurty.industria.blockentity.util.heat;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * This interface is used to define a heat handler.
 *
 * @apiNote The heat values are measured in Kelvin.
 */
@AutoRegisterCapability
public interface IHeatHandler extends INBTSerializable<CompoundTag> {
    Capability<IHeatHandler> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    float getHeat();
    float getMaxHeat();
    float getHeatTransferRate();

    float receiveHeat(float heat, boolean simulate);
    float receiveHeat(float heat, float maxTransfer, boolean simulate);

    float extractHeat(float heat, boolean simulate);
    float extractHeat(float heat, float maxTransfer, boolean simulate);

    void setHeat(float heat);
    default void onChanged(float oldHeat, float newHeat) {}

    default float getAsCelsius() {
        return getHeat() - 273.15f;
    }
    default float getAsFahrenheit() {
        return getAsCelsius() * (9f/5f) + 32;
    }
}
