package dev.turtywurty.industria.network.clientbound;

import dev.turtywurty.industria.capability.ResearchCapability;
import dev.turtywurty.industria.items.ResearchAdvancer;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class CSyncResearchPacket extends Packet {
    private final List<ResearchAdvancer> researchAdvancers;

    public CSyncResearchPacket(List<ResearchAdvancer> researchAdvancers) {
        this.researchAdvancers = researchAdvancers;
    }

    public CSyncResearchPacket(FriendlyByteBuf buf) {
        List<ResearchAdvancer> researchAdvancers = new ArrayList<>();
        while (buf.isReadable()) {
            Item item = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
            if (item instanceof ResearchAdvancer researchAdvancer) {
                researchAdvancers.add(researchAdvancer);
            }
        }

        this.researchAdvancers = researchAdvancers;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        this.researchAdvancers.stream().map(ResearchAdvancer::getResearchItem).map(ForgeRegistries.ITEMS::getKey)
                .forEach(buf::writeResourceLocation);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(ResearchCapability.INSTANCE)
                        .ifPresent(research -> this.researchAdvancers.forEach(research::addAdvancer));
            }
        });

        context.setPacketHandled(true);
    }
}
