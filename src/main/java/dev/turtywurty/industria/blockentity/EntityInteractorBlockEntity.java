package dev.turtywurty.industria.blockentity;

import com.mojang.authlib.GameProfile;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CFakePlayerCreatedPacket;
import dev.turtywurty.industria.network.serverbound.SBlockEntityLoadPacket;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class EntityInteractorBlockEntity extends ModularBlockEntity implements ClientLoaderListener {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".entity_interactor");
    private Player player;

    private UUID playerUUID;
    private int interactRate = 20;
    private int ticks = 0;
    private CompoundTag playerData;

    private final EnergyModule energy;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            if (pIndex == 0) return energy.getCapabilityInstance().getEnergyStored();
            if (pIndex == 1) return energy.getCapabilityInstance().getMaxEnergyStored();
            if (pIndex == 2) return interactRate;
            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {
            if (pIndex == 0) energy.getCapabilityInstance().setEnergy(pValue);
            if (pIndex == 2) interactRate = pValue;
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public EntityInteractorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.ENTITY_INTERACTOR.get(), pos, state);

        this.energy = addModule(new EnergyModule(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide) return;

        this.ticks++;
        if (this.ticks % 50 == 0) update();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.level != null && this.level.isClientSide) {
            PacketManager.sendToServer(new SBlockEntityLoadPacket(this.worldPosition));
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        if (nbt.contains("PlayerUUID")) {
            this.playerUUID = nbt.getUUID("PlayerUUID");
        }

        if (nbt.contains("InteractRate")) {
            this.interactRate = nbt.getInt("InteractRate");
        }

        if (nbt.contains("PlayerData")) {
            this.playerData = nbt.getCompound("PlayerData");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (this.player != null) {
            nbt.putUUID("PlayerUUID",
                    this.playerUUID == null ? this.playerUUID = this.player.getUUID() : this.playerUUID);

            nbt.put("PlayerData", this.player.saveWithoutId(new CompoundTag()));
        }

        nbt.putInt("InteractRate", this.interactRate);
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        List<Consumer<CompoundTag>> readData = super.getReadSyncData();
        readData.add(nbt -> {
            if (nbt.contains("PlayerUUID")) {
                this.playerUUID = nbt.getUUID("PlayerUUID");
            }

            if (nbt.contains("InteractRate")) {
                this.interactRate = nbt.getInt("InteractRate");
            }

            if (this.player != null && nbt.contains("PlayerData")) {
                this.player.load(nbt.getCompound("PlayerData"));
            }
        });

        return readData;
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        List<Consumer<CompoundTag>> writeData = super.getWriteSyncData();
        writeData.add(nbt -> {
            if (this.player != null) {
                nbt.putUUID("PlayerUUID",
                        this.playerUUID == null ? this.playerUUID = this.player.getUUID() : this.playerUUID);

                nbt.put("PlayerData", this.player.saveWithoutId(new CompoundTag()));
            }

            nbt.putInt("InteractRate", this.interactRate);
        });

        return writeData;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (this.player != null) {
            this.player.remove(Entity.RemovalReason.KILLED);
            this.player = null;
        }

        this.ticks = 0;
        this.playerUUID = null;
    }

    public EnergyModule getEnergy() {
        return this.energy;
    }

    public ContainerData getContainerData() {
        return this.containerData;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayer(Player player) {
        this.player = player;

        if(this.level.isClientSide) {
            this.player.load(this.playerData);
        }
    }

    private void tryCreatePlayer() {
        if (this.level != null && !this.level.isClientSide && this.player == null) {
            final UUID uuid = this.playerUUID == null ? this.playerUUID = UUID.randomUUID() : this.playerUUID;
            ServerPlayer sPlayer = this.level.getServer().getPlayerList().getPlayer(uuid);
            if (sPlayer != null) {
                this.player = sPlayer;
                this.player.load(this.playerData);
                return;
            }

            var profile = new GameProfile(uuid, "");
            this.player = new FakePlayer((ServerLevel) this.level, profile) {
                @Override
                public Packet<?> getAddEntityPacket() {
                    return PacketManager.CHANNEL.toVanillaPacket(
                            new CFakePlayerCreatedPacket(profile, EntityInteractorBlockEntity.this.worldPosition),
                            NetworkDirection.PLAY_TO_CLIENT);
                }
            };

            ((ServerLevel) this.level).addNewPlayer((ServerPlayer) this.player);
            this.player.load(this.playerData);
        }
    }

    @Override
    public void onServerClientLoad() {
        tryCreatePlayer();
    }
}
