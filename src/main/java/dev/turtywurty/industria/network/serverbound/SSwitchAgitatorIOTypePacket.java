package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.AgitatorBlockEntity;
import dev.turtywurty.industria.client.screens.AgitatorScreen.SwitchingWidget.IOType;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SSwitchAgitatorIOTypePacket extends Packet {
    private final BlockPos pos;
    private final int index;
    private final IOType type;

    public SSwitchAgitatorIOTypePacket(BlockPos pos, int index, IOType type) {
        this.pos = pos;
        this.index = index;
        this.type = type;
    }

    public SSwitchAgitatorIOTypePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt(), buf.readEnum(IOType.class));
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.index);
        buf.writeEnum(this.type);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if(context.getSender() == null)
                return;

            if (context.getSender().level.getBlockEntity(this.pos) instanceof AgitatorBlockEntity agitator) {
                agitator.setIOType(this.index, this.type);
            }
        });

        context.setPacketHandled(true);
    }
}
