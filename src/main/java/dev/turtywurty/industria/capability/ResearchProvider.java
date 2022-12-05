package dev.turtywurty.industria.capability;

import dev.turtywurty.industria.Industria;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class ResearchProvider implements ICapabilitySerializable<ListTag> {
    public static final ResourceLocation ID = new ResourceLocation(Industria.MODID, "research");

    private final Research backend = new ResearchCapability();
    private final LazyOptional<Research> optional = LazyOptional.of(() -> this.backend);

    @Override
    public ListTag serializeNBT() {
        return this.backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        this.backend.deserializeNBT(nbt);
    }

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
        return ResearchCapability.INSTANCE.orEmpty(cap, this.optional);
    }

    public void invalidate() {
        this.optional.invalidate();
    }

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        var provider = new ResearchProvider();
        event.addCapability(ID, provider);
    }
}
