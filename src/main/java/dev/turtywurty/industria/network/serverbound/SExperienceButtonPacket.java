package dev.turtywurty.industria.network.serverbound;

import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class SExperienceButtonPacket extends Packet {
    private final Type type;
    private final BlockPos pos;

    public SExperienceButtonPacket(Type type, BlockPos pos) {
        this.type = type;
        this.pos = pos;
    }

    public SExperienceButtonPacket(FriendlyByteBuf buf) {
        this.type = buf.readEnum(Type.class);
        this.pos = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.type);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) return;

            BlockEntity be = context.getSender().level.getBlockEntity(this.pos);
            if (be == null) return;

            if (be instanceof EntityInteractorBlockEntity blockEntity) {
                switch (this.type) {
                    case ADD_LEVEL -> {
                        int levels = context.getSender().experienceLevel;
                        if (levels > 0) {
                            context.getSender().giveExperienceLevels(-1);
                            blockEntity.getPlayer().giveExperienceLevels(1);
                            blockEntity.update();
                        }
                    }
                    case REMOVE_LEVEL -> {
                        int levels = blockEntity.getPlayer().experienceLevel;
                        if (levels > 0) {
                            blockEntity.getPlayer().giveExperienceLevels(-1);
                            context.getSender().giveExperienceLevels(1);
                            blockEntity.update();
                        }
                    }
                    case ADD_EXP -> {
                        double exp = totalExperience(context.getSender());
                        if (exp > 0) {
                            context.getSender().giveExperiencePoints(-1);
                            blockEntity.getPlayer().giveExperiencePoints(1);
                            blockEntity.update();
                        }
                    }
                    case REMOVE_EXP -> {
                        double exp = totalExperience(blockEntity.getPlayer());
                        if (exp > 0) {
                            blockEntity.getPlayer().giveExperiencePoints(-1);
                            context.getSender().giveExperiencePoints(1);
                            blockEntity.update();
                        }
                    }
                }
            }

            context.setPacketHandled(true);
        });
    }

    public static double totalExperience(Player player) {
        double total;
        int level = player.experienceLevel;
        if (level < 17) {
            total = (level * level) + 6 * level;
        } else if (level < 32) {
            total = 2.5 * (level * level) - 40.5 * level + 360;
        } else {
            total = 4.5 * (level * level) - 162.5 * level + 2220;
        }

        return total + player.experienceProgress * player.getXpNeededForNextLevel();
    }

    public enum Type {
        ADD_LEVEL("+1"), REMOVE_LEVEL("-1"), ADD_EXP("+1"), REMOVE_EXP("-1");

        private final String text;

        Type(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }
}
