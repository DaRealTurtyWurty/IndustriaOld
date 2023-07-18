package dev.turtywurty.industria.entity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.EntityDataSerializerInit;
import dev.turtywurty.industria.init.EntityInit;
import dev.turtywurty.industria.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RopeEntity extends HangingEntity {
    public static final float THICKNESS = 0.075F;

    private static final Lazy<EntityDataAccessor<List<RopeEnd>>> ROPE_ENDS = Lazy.concurrentOf(
            () -> SynchedEntityData.defineId(RopeEntity.class, EntityDataSerializerInit.ROPE_ENDS.get()));
    private final Map<RopeEnd, Calculation> calculations = new HashMap<>();

    public RopeEntity(EntityType<? extends RopeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public RopeEntity(Level pLevel, BlockPos pPos) {
        super(EntityInit.ROPE.get(), pLevel, pPos);
        setPos(pPos.getX(), pPos.getY(), pPos.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROPE_ENDS.get(), new ArrayList<>());
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public void dropItem(@Nullable Entity pBrokenEntity) {
        playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    @Override
    public void playPlacementSound() {
        playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return 0.0625F;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 1024.0D;
    }

    @Override
    public boolean survives() {
        return this.level.getBlockState(this.pos).is(BlockTags.FENCES);
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return ItemInit.ROPE.get().getDefaultInstance();
    }

    public Vec3 getRopeHoldPosition(float pPartialTicks) {
        return getPosition(pPartialTicks).add(0.0D, 0.2D, 0.0D);
    }

    @Override
    protected void recalculateBoundingBox() {
        setPosRaw(this.pos.getX() + 0.5D, this.pos.getY() + 0.375D, this.pos.getZ() + 0.5D);
        double r = getType().getWidth() / 2.0D;
        double h = getType().getHeight();
        var aabb = new AABB(getX() - r, getY(), getZ() - r, getX() + r, getY() + h, getZ() + r);
        setBoundingBox(aabb);
    }

    @Override
    protected void setDirection(Direction pFacingDirection) {
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (this.level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.discard();
            getRopeEntity(pPlayer.level, this.pos).map(RopeEntity::getEnds).ifPresent(ends -> {
                for (RopeEnd end : ends) {
                    if (!end.position().equals(this.pos)) {
                        getRopeEntity(pPlayer.level, end.position()).ifPresent(rope -> rope.removeEnd(this.pos));
                    }
                }
            });

            if (pPlayer.getAbilities().instabuild) {
                Containers.dropItemStack(this.level, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                        getPickResult());
            }

            return InteractionResult.CONSUME;
        }
    }

    public List<RopeEnd> getEnds() {
        return this.entityData.get(ROPE_ENDS.get());
    }

    public void addEnd(BlockPos otherEnd) {
        if (!getEnds().contains(otherEnd)) {
            List<RopeEnd> ends = new ArrayList<>(getEnds());

            ends.add(new RopeEnd(Calculation.from(getRopeEntity(this.level, otherEnd).orElseThrow(
                    () -> new IllegalStateException("Rope entity not found at " + otherEnd)), this, 0), otherEnd,
                    false));
            this.entityData.set(ROPE_ENDS.get(), ends);
        }
    }

    public void addEnd(RopeEnd otherEnd) {
        if (!getEnds().contains(otherEnd)) {
            getEnds().add(otherEnd);
        }
    }

    public void removeEnd(BlockPos otherEnd) {
        List<RopeEnd> ends = new ArrayList<>(getEnds());
        ends.removeIf(end -> end.position().equals(otherEnd));
        this.entityData.set(ROPE_ENDS.get(), ends);

        if (getEnds().isEmpty()) {
            this.discard();
        }
    }

    public void setShouldRender(BlockPos position) {
        getEnds().stream().filter(end -> end.position().equals(position) && !end.shouldRender()).findFirst()
                .ifPresent(end -> {
                    int index = getEnds().indexOf(end);
                    var replacement = new RopeEnd(end.calculation(), end.position(), true);

                    List<RopeEnd> ends = new ArrayList<>(getEnds());
                    ends.set(index, replacement);
                    this.entityData.set(ROPE_ENDS.get(), ends);
                });
    }

    public static @Nullable RopeEntity handleClientSpawn(PlayMessages.SpawnEntity pSpawnEntity, Level pLevel) {
        return new RopeEntity(pLevel,
                new BlockPos((int) pSpawnEntity.getPosX(), (int) pSpawnEntity.getPosY(), (int) pSpawnEntity.getPosZ()));
    }

    public static Optional<RopeEntity> getRopeEntity(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(RopeEntity.class, new AABB(pos), entity -> !entity.getEnds().isEmpty()).stream()
                .findFirst();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        CompoundTag industria = pCompound.getCompound(Industria.MODID);
        ListTag ends = industria.getList("Ends", Tag.TAG_COMPOUND);
        for (Tag tag : ends) {
            if (tag instanceof CompoundTag compound) {
                addEnd(RopeEnd.fromNBT(compound));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        var industria = new CompoundTag();
        var ends = new ListTag();
        for (RopeEnd end : getEnds()) {
            ends.add(end.toNBT());
        }

        industria.put("Ends", ends);
        pCompound.put(Industria.MODID, industria);
    }

    public Optional<RopeEnd> getUsingEntity(RopeEntity end) {
        return getEnds().stream().filter(ropeEnd -> ropeEnd.position().equals(end.position())).findFirst();
    }

    public record RopeEnd(Calculation calculation, BlockPos position, boolean shouldRender) {
        public CompoundTag toNBT() {
            var nbt = new CompoundTag();
            nbt.put("Calculation", calculation.toNBT());
            nbt.put("Position", NbtUtils.writeBlockPos(position));
            nbt.putBoolean("ShouldRender", shouldRender);
            return nbt;
        }

        public static @NotNull RopeEnd fromNBT(@NotNull CompoundTag nbt) {
            return new RopeEnd(Calculation.fromNBT(nbt), NbtUtils.readBlockPos(nbt.getCompound("Position")),
                    nbt.getBoolean("ShouldRender"));
        }

        public static void toBuffer(FriendlyByteBuf buffer, RopeEnd end) {
            end.calculation().toBuffer(buffer);
            buffer.writeBlockPos(end.position());
            buffer.writeBoolean(end.shouldRender());
        }

        public static @NotNull RopeEnd fromBuffer(FriendlyByteBuf buffer) {
            return new RopeEnd(Calculation.fromBuffer(buffer), buffer.readBlockPos(), buffer.readBoolean());
        }

        public static class Serializer implements EntityDataSerializer<List<RopeEnd>> {
            @Override
            public void write(FriendlyByteBuf pBuffer, List<RopeEnd> pValue) {
                pBuffer.writeCollection(pValue, RopeEnd::toBuffer);
            }

            @Override
            public List<RopeEnd> read(FriendlyByteBuf pBuffer) {
                return pBuffer.readList(RopeEnd::fromBuffer);
            }

            @Override
            public List<RopeEnd> copy(List<RopeEnd> pValue) {
                return List.copyOf(pValue);
            }
        }
    }

    public record Calculation(Vec3 end, double angle, Vec3 ropeOffset, Vec2D offset, Vec3 start, Vec3f size,
                              float thickness, Vec2 normal, BlockPos startPos, BlockPos endPos, Vec2i brightness,
                              int segmentCount) {
        public static Calculation from(@NotNull RopeEntity start, @NotNull RopeEntity end, float partialTicks) {
            Vec3 endVec = end.getRopeHoldPosition(partialTicks);
            double angle = Math.PI;
            Vec3 leashOffset = start.getLeashOffset();
            double offsetX = Math.cos(angle) * leashOffset.z + Math.sin(angle) * leashOffset.x;
            double offsetZ = Math.sin(angle) * leashOffset.z - Math.cos(angle) * leashOffset.x;
            double startX = Mth.lerp(partialTicks, start.xo, start.getX()) + offsetX;
            double startY = Mth.lerp(partialTicks, start.yo, start.getY()) + leashOffset.y;
            double startZ = Mth.lerp(partialTicks, start.zo, start.getZ()) + offsetZ;
            float length = (float) (endVec.x - startX);
            float height = (float) (endVec.y - startY);
            float depth = (float) (endVec.z - startZ);
            float thickness = Mth.invSqrt(length * length + depth * depth) * THICKNESS / 2F;
            float normX = depth * thickness;
            float normY = length * thickness;
            Vec3 startEyePos = start.getEyePosition(partialTicks);
            Vec3 endEyePos = end.getEyePosition(partialTicks);
            var startPos = new BlockPos(new Vec3i((int) startEyePos.x(), (int) startEyePos.y(), (int) startEyePos.z()));
            var endPos = new BlockPos(new Vec3i((int) endEyePos.x(), (int) endEyePos.y(), (int) endEyePos.z()));
            int startBrightness = start.level.getBrightness(LightLayer.SKY, startPos);
            int endBrightness = start.level.getBrightness(LightLayer.SKY, endPos);
            int segmentCount = (int) Math.floor(startPos.distSqr(endPos));

            return new Calculation(endVec, angle, leashOffset, new Vec2D(offsetX, offsetZ),
                    new Vec3(startX, startY, startZ), new Vec3f(length, height, depth), thickness,
                    new Vec2(normX, normY), startPos, endPos, new Vec2i(startBrightness, endBrightness), segmentCount);
        }

        public CompoundTag toNBT() {
            var nbt = new CompoundTag();
            nbt.put("End", toNBT(end));
            nbt.putDouble("Angle", angle);
            nbt.put("RopeOffset", toNBT(ropeOffset));
            nbt.put("Offset", offset.toNBT());
            nbt.put("Start", toNBT(start));
            nbt.put("Size", size.toNBT());
            nbt.putFloat("Thickness", thickness);
            nbt.put("Normal", toNBT(normal));
            nbt.put("StartPos", NbtUtils.writeBlockPos(startPos));
            nbt.put("EndPos", NbtUtils.writeBlockPos(endPos));
            nbt.put("Brightness", brightness.toNBT());
            nbt.putInt("SegmentCount", segmentCount);
            return nbt;
        }

        public static @NotNull Calculation fromNBT(@NotNull CompoundTag nbt) {
            return new Calculation(vec3FromNBT(nbt.getCompound("End")), nbt.getDouble("Angle"),
                    vec3FromNBT(nbt.getCompound("RopeOffset")), Vec2D.fromNBT(nbt.getCompound("Offset")),
                    vec3FromNBT(nbt.getCompound("Start")), Vec3f.fromNBT(nbt.getCompound("Size")),
                    nbt.getFloat("Thickness"), vec2FromNBT(nbt.getCompound("Normal")),
                    NbtUtils.readBlockPos(nbt.getCompound("StartPos")),
                    NbtUtils.readBlockPos(nbt.getCompound("EndPos")), Vec2i.fromNBT(nbt.getCompound("Brightness")),
                    nbt.getInt("SegmentCount"));
        }

        public void toBuffer(FriendlyByteBuf buf) {
            toBuffer(buf, end);
            buf.writeDouble(angle);
            toBuffer(buf, ropeOffset);
            Vec2D.toBuffer(buf, offset);
            toBuffer(buf, start);
            Vec3f.toBuffer(buf, size);
            buf.writeFloat(thickness);
            toBuffer(buf, normal);
            buf.writeBlockPos(startPos);
            buf.writeBlockPos(endPos);
            Vec2i.toBuffer(buf, brightness);
            buf.writeInt(segmentCount);
        }

        public static @NotNull Calculation fromBuffer(FriendlyByteBuf buf) {
            return new Calculation(vec3FromBuffer(buf), buf.readDouble(), vec3FromBuffer(buf), Vec2D.fromBuffer(buf),
                    vec3FromBuffer(buf), Vec3f.fromBuffer(buf), buf.readFloat(), vec2FromBuffer(buf),
                    buf.readBlockPos(),
                    buf.readBlockPos(), Vec2i.fromBuffer(buf), buf.readInt());
        }

        public record Vec2D(double x, double y) {
            public CompoundTag toNBT() {
                var nbt = new CompoundTag();
                nbt.putDouble("X", x);
                nbt.putDouble("Y", y);
                return nbt;
            }

            public static @NotNull Vec2D fromNBT(@NotNull CompoundTag nbt) {
                return new Vec2D(nbt.getDouble("X"), nbt.getDouble("Y"));
            }

            public static void toBuffer(FriendlyByteBuf buffer, Vec2D vec) {
                buffer.writeDouble(vec.x());
                buffer.writeDouble(vec.y());
            }

            public static @NotNull Vec2D fromBuffer(FriendlyByteBuf buffer) {
                return new Vec2D(buffer.readDouble(), buffer.readDouble());
            }
        }

        public record Vec2i(int x, int y) {
            public CompoundTag toNBT() {
                var nbt = new CompoundTag();
                nbt.putInt("X", x);
                nbt.putInt("Y", y);
                return nbt;
            }

            public static @NotNull Vec2i fromNBT(@NotNull CompoundTag nbt) {
                return new Vec2i(nbt.getInt("X"), nbt.getInt("Y"));
            }

            public static void toBuffer(FriendlyByteBuf buffer, Vec2i vec) {
                buffer.writeInt(vec.x());
                buffer.writeInt(vec.y());
            }

            public static @NotNull Vec2i fromBuffer(FriendlyByteBuf buffer) {
                return new Vec2i(buffer.readInt(), buffer.readInt());
            }
        }

        public record Vec3f(float x, float y, float z) {
            public CompoundTag toNBT() {
                var nbt = new CompoundTag();
                nbt.putFloat("X", x);
                nbt.putFloat("Y", y);
                nbt.putFloat("Z", z);
                return nbt;
            }

            public static @NotNull Vec3f fromNBT(@NotNull CompoundTag nbt) {
                return new Vec3f(nbt.getFloat("X"), nbt.getFloat("Y"), nbt.getFloat("Z"));
            }

            public static void toBuffer(FriendlyByteBuf buffer, Vec3f vec) {
                buffer.writeFloat(vec.x());
                buffer.writeFloat(vec.y());
                buffer.writeFloat(vec.z());
            }

            public static @NotNull Vec3f fromBuffer(FriendlyByteBuf buffer) {
                return new Vec3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        }

        public static CompoundTag toNBT(Vec2 vec) {
            var nbt = new CompoundTag();
            nbt.putDouble("X", vec.x);
            nbt.putDouble("Y", vec.y);
            return nbt;
        }

        public static CompoundTag toNBT(Vec3 vec) {
            var nbt = new CompoundTag();
            nbt.putDouble("X", vec.x);
            nbt.putDouble("Y", vec.y);
            nbt.putDouble("Z", vec.z);
            return nbt;
        }

        public static Vec2 vec2FromNBT(CompoundTag nbt) {
            return new Vec2(nbt.getFloat("X"), nbt.getFloat("Y"));
        }

        public static Vec3 vec3FromNBT(CompoundTag nbt) {
            return new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }

        public static void toBuffer(FriendlyByteBuf buffer, Vec3 vec) {
            buffer.writeDouble(vec.x());
            buffer.writeDouble(vec.y());
            buffer.writeDouble(vec.z());
        }

        public static @NotNull Vec3 vec3FromBuffer(FriendlyByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        public static void toBuffer(FriendlyByteBuf buffer, Vec2 vec) {
            buffer.writeFloat(vec.x);
            buffer.writeFloat(vec.y);
        }

        public static @NotNull Vec2 vec2FromBuffer(FriendlyByteBuf buffer) {
            return new Vec2(buffer.readFloat(), buffer.readFloat());
        }
    }
}
