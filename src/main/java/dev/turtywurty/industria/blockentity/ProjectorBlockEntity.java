package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.data.ResearchData;
import dev.turtywurty.industria.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("gui." + Industria.MODID + ".projector");

    private ResearchData data;
    private boolean isProjecting;
    private int partsPlaced, partsNeeded;

    public ProjectorBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntityInit.PROJECTOR.get(), pPos, pState);
    }

    public @Nullable ResearchData getData() {
        return this.data;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Data")) {
            CompoundTag data = nbt.getCompound("Data");
            var inputRegistryName = data.getString("InputRegistryName");
            var requiredEnergy = data.getInt("RequiredEnergy");
            var resultRegistryName = data.getString("ResultRegistryName");
            this.data = new ResearchData(inputRegistryName, requiredEnergy, resultRegistryName);
        }

        this.isProjecting = nbt.getBoolean("IsProjecting");
        this.partsPlaced = nbt.getInt("PartsPlaced");
        this.partsNeeded = nbt.getInt("PartsNeeded");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.data != null) {
            var data = new CompoundTag();
            data.putString("InputRegistryName", this.data.getInputRegistryName());
            data.putInt("RequiredEnergy", this.data.getRequiredEnergy());
            data.putString("ResultRegistryName", this.data.getResultRegistryName());
            nbt.put("Data", data);
        }
        nbt.putBoolean("IsProjecting", this.isProjecting);
        nbt.putInt("PartsPlaced", this.partsPlaced);
        nbt.putInt("PartsNeeded", this.partsNeeded);
    }

    public boolean isProjecting() {
        return this.isProjecting;
    }

    public int getPartsPlaced() {
        return this.partsPlaced;
    }

    public int getPartsNeeded() {
        return this.partsNeeded;
    }

    public int incrementPartsPlaced() {
        if (this.partsPlaced < this.partsNeeded) {
            this.partsPlaced++;
        }

        return this.partsPlaced;
    }

    public boolean startProjecting(@NotNull ResearchData data) {
        if (this.data != null) return false;

        this.data = data;
        this.partsNeeded = data.getRequiredEnergy();
        this.partsPlaced = 0;
        this.isProjecting = true;

        return this.data != null;
    }
}
