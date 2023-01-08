package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.FluidModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;

public class AgitatorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".agitator");

    private final InventoryModule itemInventory;
    private final FluidModule[] fluidInventories = new FluidModule[6];
    private final EnergyModule energy;
    // private final GasModule[] gasInventories = new GasModule[6];

    private int progress = 0;
    private int maxProgress = 100;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch(pIndex) {
                case 0 -> AgitatorBlockEntity.this.progress;
                case 1 -> AgitatorBlockEntity.this.maxProgress;
                case 2 -> AgitatorBlockEntity.this.energy.getCapability().getEnergyStored();
                case 3 -> AgitatorBlockEntity.this.energy.getCapability().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> AgitatorBlockEntity.this.progress = pValue;
                case 1 -> AgitatorBlockEntity.this.maxProgress = pValue;
                case 2 -> AgitatorBlockEntity.this.energy.getCapability().setEnergy(pValue);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    @Override
    public void onLoad() {

    }

    public AgitatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.AGITATOR.get(), pos, state);

        this.itemInventory = addModule(new InventoryModule(this, 6));

        for (int index = 0; index < 6; index++) {
            this.fluidInventories[index] = addModule(new FluidModule(this, 10000));
        }

        this.energy = addModule(new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxReceive(1000)));

        // for (int index = 0; index <= 6; index++) {
        //     this.gasInventories[index] = addModule(new GasModule(this, 10000));
        // }
    }

    public InventoryModule getInventory() {
        return this.itemInventory;
    }

    public FluidModule[] getFluidInventories() {
        return this.fluidInventories;
    }

    public EnergyModule getEnergy() {
        return this.energy;
    }

    // public GasModule[] getGasInventories() {
    //     return this.gasInventories;
    // }

    public ContainerData getContainerData() {
        return this.containerData;
    }
}
