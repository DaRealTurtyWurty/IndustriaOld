package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.ClientLoaderListener;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SBlockEntityLoadPacket extends Packet {
    private final BlockPos pos;

    public SBlockEntityLoadPacket(BlockPos pos) {
        this.pos = pos;
    }

    public SBlockEntityLoadPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (player.getLevel().getBlockEntity(pos) instanceof ClientLoaderListener listener)
                listener.onServerClientLoad();

            context.setPacketHandled(true);
        });
    }
}
