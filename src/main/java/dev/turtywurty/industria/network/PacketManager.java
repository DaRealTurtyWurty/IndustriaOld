package dev.turtywurty.industria.network;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.network.clientbound.*;
import dev.turtywurty.industria.network.serverbound.SRequestResearchDataPacket;
import dev.turtywurty.industria.network.serverbound.SStartResearchPacket;
import dev.turtywurty.industria.network.serverbound.SSwitchAgitatorIOTypePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public final class PacketManager {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Industria.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int NUM_PACKETS = 0;

    private PacketManager() {
    }

    public static void init() {
        register(CSyncResearchPacket.class, CSyncResearchPacket::new);
        register(CSyncNewResearchPacket.class, CSyncNewResearchPacket::new);
        register(CSyncResearchDataPacket.class, CSyncResearchDataPacket::new);
        register(SRequestResearchDataPacket.class, SRequestResearchDataPacket::new);
        register(SStartResearchPacket.class, SStartResearchPacket::new);
        register(CResearchFailedPacket.class, CResearchFailedPacket::new);
        register(SSwitchAgitatorIOTypePacket.class, SSwitchAgitatorIOTypePacket::new);
        Industria.LOGGER.info("Registered {} packets", NUM_PACKETS);
    }

    public static void sendToClient(Packet packet, Player player) {
        if (!player.level.isClientSide()) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), packet);
        } else {
            throw new IllegalArgumentException("Player must be a server player!");
        }
    }

    public static void sendToAllClients(Packet packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToServer(Packet packet) {
        CHANNEL.sendToServer(packet);
    }

    public static <MSG extends Packet> void register(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> decoder) {
        CHANNEL.messageBuilder(clazz, NUM_PACKETS++).encoder(Packet::encode).decoder(decoder)
                .consumerMainThread((msg, ctx) -> msg.handle(ctx.get())).add();
    }
}
