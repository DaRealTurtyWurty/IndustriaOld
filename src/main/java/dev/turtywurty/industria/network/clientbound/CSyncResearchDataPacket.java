package dev.turtywurty.industria.network.clientbound;

import dev.turtywurty.industria.client.screens.ResearcherScreen;
import dev.turtywurty.industria.data.ResearchData;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

public class CSyncResearchDataPacket extends Packet {
    private final List<ResearchData> researchData;

    public CSyncResearchDataPacket(List<ResearchData> researchData) {
        this.researchData = researchData;
    }

    public CSyncResearchDataPacket(FriendlyByteBuf buf) {
        this.researchData = buf.readList(ResearchData::fromBuffer);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(this.researchData, (buffer, data) -> {
            buffer.writeUtf(data.getInputRegistryName());
            buffer.writeInt(data.getRequiredEnergy());
            buffer.writeUtf(data.getResultRegistryName());
        });
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof ResearcherScreen researcherScreen) {
                researcherScreen.researchData.clear();
                researcherScreen.researchData.addAll(this.researchData);
                researcherScreen.rebuildWidgets();
            }

            context.setPacketHandled(true);
        });
    }
}
