package dev.turtywurty.industria.network.clientbound;

import dev.turtywurty.industria.blockentity.AgitatorBlockEntity;
import dev.turtywurty.industria.client.screens.AgitatorScreen;
import dev.turtywurty.industria.menu.AgitatorMenu;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

public class CAgitatorFluidUpdatePacket extends Packet {
    private final BlockPos pos;
    private final List<FluidStack> fluids;

    public CAgitatorFluidUpdatePacket(BlockPos pos, List<FluidStack> fluids) {
        this.pos = pos;
        this.fluids = fluids;
    }

    public CAgitatorFluidUpdatePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readList(FluidStack::readFromPacket));
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeCollection(this.fluids, (friendlyByteBuf, fluidStack) -> fluidStack.writeToPacket(friendlyByteBuf));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if(player == null) return;

            if (player.containerMenu instanceof AgitatorMenu menu && menu.getPos().equals(this.pos)) {
                menu.getFluids().clear();
                menu.getFluids().addAll(this.fluids);

                if (Minecraft.getInstance().screen instanceof AgitatorScreen screen) {
                    screen.updateFluids(this.fluids);
                }
            }

            BlockEntity blockEntity = player.level.getBlockEntity(this.pos);
            if (blockEntity instanceof AgitatorBlockEntity agitatorBlockEntity && agitatorBlockEntity.hasBERFluidUpdateCallback()) {
                agitatorBlockEntity.getBERFluidUpdateCallback().accept(this.fluids);
            }
        });

        context.setPacketHandled(true);
    }
}
