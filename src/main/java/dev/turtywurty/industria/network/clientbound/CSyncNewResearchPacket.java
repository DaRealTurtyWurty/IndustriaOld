package dev.turtywurty.industria.network.clientbound;

import dev.turtywurty.industria.capability.ResearchCapability;
import dev.turtywurty.industria.items.ResearchAdvancer;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CSyncNewResearchPacket extends Packet {
    private final ResearchAdvancer researchAdvancer;

    public CSyncNewResearchPacket(ResearchAdvancer researchAdvancer) {
        this.researchAdvancer = researchAdvancer;
    }

    public CSyncNewResearchPacket(FriendlyByteBuf buf) {
        Item item = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
        if (item instanceof ResearchAdvancer researchAdvancer) {
            this.researchAdvancer = researchAdvancer;
        } else {
            this.researchAdvancer = null;
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(ForgeRegistries.ITEMS.getKey(this.researchAdvancer.getResearchItem()));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(ResearchCapability.INSTANCE)
                        .ifPresent(research -> research.addAdvancer(this.researchAdvancer));
                player.sendSystemMessage(Component.literal(this.researchAdvancer.getResearchTitle()));
                // TODO: Add a notification to the player that they have unlocked a new research
            }
        });

        context.setPacketHandled(true);
    }
}
