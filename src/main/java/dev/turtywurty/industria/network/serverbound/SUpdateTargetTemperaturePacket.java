package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.ElectricHeaterBlockEntity;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class SUpdateTargetTemperaturePacket extends Packet {
    private final BlockPos position;
    private final int targetTemperature;

    public SUpdateTargetTemperaturePacket(BlockPos position, int targetTemperature) {
        this.position = position;
        this.targetTemperature = targetTemperature;
    }

    public SUpdateTargetTemperaturePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.position);
        buf.writeInt(this.targetTemperature);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if(context.getSender() == null) return;

            Level level = context.getSender().level;

            if(level.getBlockEntity(this.position) instanceof ElectricHeaterBlockEntity blockEntity) {
                blockEntity.setTargetTemperature(this.targetTemperature);
                blockEntity.update();
            }
        });

        context.setPacketHandled(true);
    }
}
