package dev.turtywurty.industria.capability;

import dev.turtywurty.industria.items.ResearchAdvancer;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface Research extends INBTSerializable<ListTag> {
    void addAdvancer(ResearchAdvancer advancer);
    boolean hasAdvancer(ResearchAdvancer advancer);
    List<ResearchAdvancer> getAdvancers();
}
