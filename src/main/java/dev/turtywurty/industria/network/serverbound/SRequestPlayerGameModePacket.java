package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.network.Packet;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CReceivePlayerGameModePacket;
import dev.turtywurty.industria.network.clientbound.CSyncResearchPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

public class SRequestPlayerGameModePacket extends Packet {
    private final BlockPos pos;

    public SRequestPlayerGameModePacket(BlockPos pos) {
        this.pos = pos;
    }

    public SRequestPlayerGameModePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) return;

            if (context.getSender().level.getBlockEntity(
                    this.pos) instanceof EntityInteractorBlockEntity blockEntity && blockEntity.getPlayer() instanceof ServerPlayer player) {
                GameType gameType = player.gameMode.getGameModeForPlayer();
                PacketManager.sendToClient(new CReceivePlayerGameModePacket(gameType), context.getSender());
            }

            context.setPacketHandled(true);
        });
    }
}
