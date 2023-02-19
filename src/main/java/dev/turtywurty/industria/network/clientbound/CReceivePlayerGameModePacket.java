package dev.turtywurty.industria.network.clientbound;

import dev.turtywurty.industria.client.screens.EntityInteractorScreen;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

public class CReceivePlayerGameModePacket extends Packet {
    private final GameType gameType;

    public CReceivePlayerGameModePacket(GameType gameType) {
        this.gameType = gameType;
    }

    public CReceivePlayerGameModePacket(FriendlyByteBuf buf) {
        this.gameType = GameType.byId(buf.readVarInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.gameType.getId());
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            System.out.println("Received game mode: " + this.gameType);
            if(Minecraft.getInstance().screen instanceof EntityInteractorScreen screen) {
                screen.receivePlayerGameMode(this.gameType);
            }

            context.setPacketHandled(true);
        });
    }
}
