package dev.turtywurty.industria.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class EmptyPacket extends Packet {

    public EmptyPacket(FriendlyByteBuf buf) {}
    public EmptyPacket() {}

    @Override
    public void encode(FriendlyByteBuf buf) {}
}
