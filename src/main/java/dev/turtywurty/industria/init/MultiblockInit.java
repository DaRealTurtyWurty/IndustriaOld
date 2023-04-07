package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.block.DistillationUnitBlock;
import dev.turtywurty.turtylib.TurtyLib;
import dev.turtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MultiblockInit {
    public static final DeferredRegister<Multiblock> MULTIBLOCKS = DeferredRegister.create(
            TurtyLib.MULTIBLOCK_REGISTRY_KEY, Industria.MODID);

    public static final RegistryObject<Multiblock> DISTILLATION_UNIT = MULTIBLOCKS.register("distillation_unit",
            () -> new Multiblock(
                    Multiblock.Builder.start()
                            .aisle("IIII", "IIII", "IIII", "IIII")
                            .aisle("AAAA", "AIIA", "AIIA", "AAAA")
                            .aisle("AAAA", "AIIA", "AIIA", "AAAA")
                            .aisle("AAAA", "AIIA", "AIIA", "AAAA")
                            .where('I', BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))
                            .where('A', BlockBehaviour.BlockStateBase::isAir)
                            .finish()
                            .controller(0, 0, 0, BlockInit.DISTILLATION_UNIT.get().defaultBlockState())
                            .useFunction((($0, level, blockPos, player, $1, $2, $3) -> DistillationUnitBlock.use(level, blockPos, player)))
            ));

}
