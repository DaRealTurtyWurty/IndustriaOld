package dev.turtywurty.industria.cables;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.EnergyModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CableBlockEntity extends ModularBlockEntity {
    private EnergyGraph network;
    private boolean isEndpoint;

    private final EnergyModule energyModule;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(Initialization.CABLE_BLOCK_ENTITY.get(), pos, state);

        this.network = new EnergyGraph(pos);
        this.isEndpoint = false;
        this.energyModule = addModule(new EnergyModule(this));
    }

    public EnergyGraph getNetwork() {
        return this.network;
    }

    public boolean isEndpoint() {
        return this.isEndpoint;
    }

    public void setEndpoint(boolean isEndpoint) {
        this.isEndpoint = isEndpoint;
        update();
    }

    public int getEnergy() {
        return this.energyModule.getCapabilityInstance().getEnergyStored();
    }

    public int getMaxEnergy() {
        return this.energyModule.getCapabilityInstance().getMaxEnergyStored();
    }

    public void setEnergy(int energy) {
        this.energyModule.getCapabilityInstance().setEnergy(energy);
    }

    public void transferEnergy() {
        if(this.level == null) return;

        EnergyGraph network = getNetwork();
        if (network != null) {
            int totalEnergy = getEnergy();
            if (totalEnergy <= 0) return;

            Map<BlockPos, IEnergyStorage> machineEndPoints = new HashMap<>();
            for (BlockPos pos : network.getEndPoints()) {
                for (Direction direction : Direction.values()) {
                    BlockPos offset = pos.relative(direction);
                    BlockEntity blockEntity = this.level.getBlockEntity(offset);
                    if (blockEntity == null || blockEntity instanceof CableBlockEntity) continue;

                    IEnergyStorage energyStorage = blockEntity.getCapability(ForgeCapabilities.ENERGY,
                            direction.getOpposite()).orElse(null);
                    if (energyStorage != null && energyStorage.canReceive()) {
                        machineEndPoints.put(offset, energyStorage);
                        break;
                    }
                }
            }

            if(machineEndPoints.isEmpty()) return;

            int energyPerMachine = totalEnergy / machineEndPoints.size();
            for (Map.Entry<BlockPos, IEnergyStorage> endPoint : machineEndPoints.entrySet()) {
                int energy = endPoint.getValue().receiveEnergy(energyPerMachine, false);
                setEnergy(getEnergy() - energy);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide) return;

        transferEnergy();
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        List<Consumer<CompoundTag>> list = super.getReadSyncData();
        list.add(tag -> {
            this.network = new EnergyGraph(this.worldPosition);
            this.network.deserializeNBT(tag.getCompound("Network"));
        });
        return list;
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        List<Consumer<CompoundTag>> list = super.getWriteSyncData();
        list.add(tag -> tag.put("Network", this.network.serializeNBT()));
        return list;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.isEndpoint = nbt.getBoolean("IsEndpoint");
        this.network = new EnergyGraph(this.worldPosition);
        this.network.deserializeNBT(nbt.getCompound("Network"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("IsEndpoint", this.isEndpoint);
        nbt.put("Network", this.network.serializeNBT());
    }

    public void setNetwork(EnergyGraph network) {
        this.network = network;
        update();
    }
}