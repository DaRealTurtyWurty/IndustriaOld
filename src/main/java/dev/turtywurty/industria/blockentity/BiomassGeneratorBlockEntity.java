package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

public class BiomassGeneratorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".biomass_generator");

    private final InventoryModule inventory;
    private final EnergyModule energy;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> BiomassGeneratorBlockEntity.this.energy.getCapability().getEnergyStored();
                case 1 -> BiomassGeneratorBlockEntity.this.energy.getCapability().getMaxEnergyStored();
                case 2 ->
                        getEnergyForItem(BiomassGeneratorBlockEntity.this.inventory.getCapability().getStackInSlot(0));
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    private int getEnergyForItem(ItemStack stackInSlot) {
        return ForgeHooks.getBurnTime(stackInSlot, RecipeType.SMELTING);
    }

    public BiomassGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.COAL_GENERATOR.get(), pos, state);

        this.inventory = new InventoryModule(this, 1);
        this.energy = new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxExtract(100).maxReceive(0));
    }

    public InventoryModule getInventory() {
        return this.inventory;
    }

    public EnergyModule getEnergy() {
        return this.energy;
    }

    public ContainerData getContainerData() {
        return this.data;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) return;

        System.out.println("server");

        if (this.energy.getCapability().getEnergyStored() >= this.energy.getCapability().getMaxEnergyStored()) return;

        System.out.println("not full");

        final ItemStack stack = this.inventory.getCapability().getStackInSlot(0);
        final int energy = getEnergyForItem(stack);
        if (energy <= 0 || this.energy.getCapability().getEnergyStored() + energy >= this.energy.getCapability()
                .getMaxEnergyStored()) return;

        System.out.println("energy: " + energy);

        this.energy.getCapability().setEnergy(this.energy.getCapability().getEnergyStored() + energy);
        stack.shrink(1);

        System.out.println("energy2: " + this.energy.getCapability().getEnergyStored());
    }
}
