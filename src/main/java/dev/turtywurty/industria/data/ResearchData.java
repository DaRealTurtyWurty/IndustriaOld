package dev.turtywurty.industria.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ResearchData {
    // TODO: Use codecs instead
    private String inputRegistryName;
    private int requiredEnergy;

    // TODO: This is temporary until I set up the projector input. Instead, this will change to a multiblock reference
    private String resultRegistryName;

    public ResearchData(String inputRegistryName, int requiredEnergy, String resultRegistryName) {
        this.inputRegistryName = inputRegistryName;
        this.requiredEnergy = requiredEnergy;
        this.resultRegistryName = resultRegistryName;
    }

    public ResearchData() {
        this.inputRegistryName = "";
        this.requiredEnergy = 0;
        this.resultRegistryName = "";
    }

    public String getInputRegistryName() {
        return this.inputRegistryName;
    }

    public int getRequiredEnergy() {
        return this.requiredEnergy;
    }

    public String getResultRegistryName() {
        return this.resultRegistryName;
    }

    public static ItemStack getItemStack(String registryName) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(registryName)).getDefaultInstance();
    }

    public static ResearchData fromBuffer(FriendlyByteBuf buffer) {
        return new ResearchData(buffer.readUtf(), buffer.readInt(), buffer.readUtf());
    }
}
