package dev.turtywurty.industria.network.clientbound;

import com.mojang.authlib.GameProfile;
import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.entity.FakeClientPlayer;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CFakePlayerCreatedPacket extends Packet {
    private final GameProfile profile;
    private final BlockPos pos;

    public CFakePlayerCreatedPacket(GameProfile profile, BlockPos pos) {
        this.profile = profile;
        this.pos = pos;
    }

    public CFakePlayerCreatedPacket(FriendlyByteBuf buf) {
        this.profile = buf.readGameProfile();
        this.pos = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeGameProfile(this.profile);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (this.profile == null || this.pos == null) return;

            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            if(level.getBlockEntity(this.pos) instanceof EntityInteractorBlockEntity blockEntity) {
                var player = new FakeClientPlayer(level, this.profile);
                player.setPos(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D);
                level.addPlayer(player.getId(), player);

                blockEntity.setPlayer(player);

                context.setPacketHandled(true);
            }
        });
    }
}
