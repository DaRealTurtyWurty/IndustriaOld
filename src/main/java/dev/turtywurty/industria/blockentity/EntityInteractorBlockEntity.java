package dev.turtywurty.industria.blockentity;

import com.mojang.authlib.GameProfile;
import dev.turtywurty.industria.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class EntityInteractorBlockEntity extends ModularBlockEntity {
    private FakePlayer player;
    private UUID playerUUID;
    private int interactRate = 20;

    private final EnergyModule energy;

    public EntityInteractorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.ENTITY_INTERACTOR.get(), pos, state);

        this.energy = addModule(new EnergyModule(this));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide && this.player == null) {
            final UUID uuid = this.playerUUID == null ? this.playerUUID = UUID.randomUUID() : this.playerUUID;
            this.player = FakePlayerFactory.get((ServerLevel) this.level, new GameProfile(uuid, ""));
            ((ServerLevel) this.level).addNewPlayer(this.player);
        }
    }

    public FakePlayer getPlayer() {
        return this.player;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.playerUUID = nbt.getUUID("PlayerUUID");
        this.interactRate = nbt.getInt("InteractRate");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.player != null) {
            nbt.putUUID("PlayerUUID",
                    this.playerUUID == null ? this.playerUUID = this.player.getUUID() : this.playerUUID);
        }

        nbt.putInt("InteractRate", this.interactRate);
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        List<Consumer<CompoundTag>> readData = super.getReadSyncData();
        readData.add(nbt -> {
            this.playerUUID = nbt.getUUID("PlayerUUID");
            this.interactRate = nbt.getInt("InteractRate");
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
            }

            nbt.putInt("InteractRate", this.interactRate);
        });

        return writeData;
    }

    public EnergyModule getEnergy() {
        return this.energy;
    }

    public void openInventory(ServerPlayer pPlayer) {
        if (this.player != null) {
            var provider = new SimpleMenuProvider(
                    (pContainerId, pPlayerInventory, pPlayer1) -> this.player.inventoryMenu,
                    this.player.getDisplayName());
            NetworkHooks.openScreen(pPlayer, provider, this.worldPosition);
        }
    }
}
