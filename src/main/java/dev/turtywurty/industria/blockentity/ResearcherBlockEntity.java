package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.data.ResearchDataOld;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.items.ResearchAdvancer;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

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
                case 2 -> ResearcherBlockEntity.this.energy.getCapabilityInstance().getEnergyStored();
                case 3 -> ResearcherBlockEntity.this.energy.getCapabilityInstance().getMaxEnergyStored();
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
    public void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide)
            return;

        if(this.progress != 0) {
            if(this.progress >= this.maxProgress) {
                this.progress = 0;
                this.maxProgress = 0;
                // TODO: Modify projector item
            } else {
                this.progress++;
            }
        } else if(this.maxProgress > 0) {
            this.progress++;
        }
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

    public boolean startResearch(ResearchDataOld researchData) {
        if (this.progress != 0) return false;
        if (!(this.inventory.getCapabilityInstance().getStackInSlot(0).getItem() instanceof ResearchAdvancer advancer))
            return false;

        ResourceLocation input = ResourceLocation.tryParse(researchData.getInputRegistryName());
        Item inputItem = ForgeRegistries.ITEMS.getValue(input);
        if (inputItem != advancer.getResearchItem()) return false;

        this.maxProgress = researchData.getRequiredEnergy();
        this.progress = 0;
        return true;
    }

    public int getProgress() {
       return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }
}
