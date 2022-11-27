package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import io.github.darealturtywurty.turtylib.TurtyLib;
import io.github.darealturtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraftforge.registries.DeferredRegister;

public class MultiblockInit {
    public static final DeferredRegister<Multiblock> MULTIBLOCKS = DeferredRegister.create(
            TurtyLib.MULTIBLOCK_REGISTRY_KEY, Industria.MODID);
}
