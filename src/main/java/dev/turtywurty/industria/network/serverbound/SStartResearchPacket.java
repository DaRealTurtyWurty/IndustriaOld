package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.ResearcherBlockEntity;
import dev.turtywurty.industria.data.ResearchDataOld;
import dev.turtywurty.industria.menu.ResearcherMenu;
import dev.turtywurty.industria.network.Packet;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CResearchFailedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SStartResearchPacket extends Packet {
    private final ResearchDataOld researchData;

    public SStartResearchPacket(ResearchDataOld researchData) {
        this.researchData = researchData;
    }

    public SStartResearchPacket(FriendlyByteBuf buffer) {
        this.researchData = ResearchDataOld.fromBuffer(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.researchData.getInputRegistryName());
        buf.writeInt(this.researchData.getRequiredEnergy());
        buf.writeUtf(this.researchData.getResultRegistryName());
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        var result = new CompletableFuture<Boolean>();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof ResearcherMenu menu)) {
                result.complete(false);
                return;
            }

            BlockPos pos = menu.getPos();
            if (player.level.getBlockEntity(pos) instanceof ResearcherBlockEntity researcher) {
                result.complete(researcher.startResearch(this.researchData));
                return;
            }

            result.complete(false);
        });

        result.thenAccept(res -> {
            if (!res) {
                PacketManager.sendToClient(new CResearchFailedPacket(), Objects.requireNonNull(context.getSender()));
            }

            context.setPacketHandled(true);
        });
    }
}
