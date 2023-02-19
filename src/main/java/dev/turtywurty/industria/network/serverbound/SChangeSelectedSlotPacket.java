package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class SChangeSelectedSlotPacket extends Packet {
    private final int slot;
    private final BlockPos pos;

    public SChangeSelectedSlotPacket(int slot, BlockPos pos) {
        this.slot = slot;
        this.pos = pos;
    }

    public SChangeSelectedSlotPacket(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) return;

            BlockEntity be = context.getSender().level.getBlockEntity(this.pos);
            if (be instanceof EntityInteractorBlockEntity blockEntity) {
                blockEntity.getPlayer().getInventory().selected = this.slot;
                blockEntity.update();
            }

            context.setPacketHandled(true);
        });
    }
}
