package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.init.ReloadListenerInit;
import dev.turtywurty.industria.network.EmptyPacket;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CSyncResearchDataPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SRequestResearchDataPacket extends EmptyPacket {
    public SRequestResearchDataPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public SRequestResearchDataPacket() {
        super();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            PacketManager.sendToClient(new CSyncResearchDataPacket(ReloadListenerInit.RESEARCH_DATA.getDataList()), context.getSender());

            context.setPacketHandled(true);
        });
    }
}
