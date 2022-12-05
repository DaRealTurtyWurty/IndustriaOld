package dev.turtywurty.industria.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {
    public Packet(FriendlyByteBuf buf) {}
    protected Packet() {}

    public abstract void encode(FriendlyByteBuf buf);
    public abstract void handle(NetworkEvent.Context context);
}
