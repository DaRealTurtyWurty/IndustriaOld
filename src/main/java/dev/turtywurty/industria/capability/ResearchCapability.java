package dev.turtywurty.industria.capability;

import dev.turtywurty.industria.items.ResearchAdvancer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ResearchCapability implements Research {
    public static final Capability<Research> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final List<ResearchAdvancer> advancers = new ArrayList<>();


    @Override
    public ListTag serializeNBT() {
        return new ListTag(
                this.advancers.stream().map(ResearchAdvancer::getResearchItem).map(ForgeRegistries.ITEMS::getKey)
                        .map(ResourceLocation::toString).map(StringTag::valueOf).map(Tag.class::cast).toList(),
                Tag.TAG_STRING);
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        for (Tag tag : nbt) {
            String id = tag.getAsString();
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (!ForgeRegistries.ITEMS.containsKey(location)) continue;

            if (ForgeRegistries.ITEMS.getValue(location) instanceof ResearchAdvancer advancer && !advancers.contains(
                    advancer)) {
                advancers.add(advancer);
            }
        }
    }

    @Override
    public void addAdvancer(ResearchAdvancer advancer) {
        if (!advancers.contains(advancer)) advancers.add(advancer);
    }

    @Override
    public boolean hasAdvancer(ResearchAdvancer advancer) {
        return this.advancers.contains(advancer);
    }

    @Override
    public List<ResearchAdvancer> getAdvancers() {
        return List.copyOf(this.advancers);
    }
}
