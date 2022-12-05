package dev.turtywurty.industria.network.clientbound;

import dev.turtywurty.industria.client.screens.ResearcherScreen;
import dev.turtywurty.industria.network.EmptyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CResearchFailedPacket extends EmptyPacket {
    public CResearchFailedPacket() {
        super();
    }

    public CResearchFailedPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
           Screen screen = Minecraft.getInstance().screen;
           if(screen instanceof ResearcherScreen researcher) {
               researcher.setResearchFailed();
           }

           context.setPacketHandled(true);
        });
    }
}
