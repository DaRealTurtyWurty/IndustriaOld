package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.items.ResearchAdvancer;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.security.DrbgParameters;
import java.util.List;

public class ResearcherBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".researcher");

    private final EnergyModule energy;
    private final InventoryModule inventory;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> ResearcherBlockEntity.this.progress;
                case 1 -> ResearcherBlockEntity.this.maxProgress;
                case 2 -> ResearcherBlockEntity.this.energy.getCapability().getEnergyStored();
                case 3 -> ResearcherBlockEntity.this.energy.getCapability().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> ResearcherBlockEntity.this.progress = pValue;
                case 1 -> ResearcherBlockEntity.this.maxProgress = pValue;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private int progress = 0;
    private int maxProgress = 0;

    public ResearcherBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.RESEARCHER.get(), pos, state);
        this.energy = this.addModule(new EnergyModule(this));
        this.inventory = this.addModule(new InventoryModule(this, 1));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Progress", this.progress);
        nbt.putInt("MaxProgress", this.maxProgress);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getInt("Progress");
        this.maxProgress = nbt.getInt("MaxProgress");
    }

    public ContainerData getContainerData() {
        return this.data;
    }

    public InventoryModule getInventory() {
        return this.inventory;
    }
}
