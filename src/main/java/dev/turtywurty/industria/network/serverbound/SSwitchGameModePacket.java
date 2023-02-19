package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class SSwitchGameModePacket extends Packet {
    private final boolean creative;
    private final BlockPos pos;

    public SSwitchGameModePacket(boolean creative, BlockPos pos) {
        this.creative = creative;
        this.pos = pos;
    }

    public SSwitchGameModePacket(FriendlyByteBuf buf) {
        this.creative = buf.readBoolean();
        this.pos = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.creative);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) return;

            BlockEntity be = context.getSender().level.getBlockEntity(this.pos);
            if (be instanceof EntityInteractorBlockEntity blockEntity && blockEntity.getPlayer() instanceof ServerPlayer player) {
                // TODO: Implement spectator and adventure mode
                player.setGameMode(this.creative ? GameType.CREATIVE : GameType.SURVIVAL);
            }

            context.setPacketHandled(true);
        });
    }
}
