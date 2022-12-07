package dev.turtywurty.industria.mixins;

import dev.turtywurty.industria.init.BlockInit;
import dev.turtywurty.industria.init.util.BoatTypes;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(Boat.Type.class)
public class BoatTypeMixin {
    @Invoker("<init>")
    private static Boat.Type create(String internalName, int internalId, Block planks, String name) {
        throw new AssertionError();
    }

    @Shadow
    @Final
    @Mutable
    private static Boat.Type[] $VALUES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/world/entity/vehicle/Boat$Type;$VALUES:[Lnet/minecraft/world/entity/vehicle/Boat$Type;", shift = At.Shift.AFTER))
    private static void addBoatType(CallbackInfo callback) {
        List<Boat.Type> variants = new ArrayList<>(Arrays.asList($VALUES));
        Boat.Type last = variants.get(variants.size() - 1);
        Boat.Type rubber = create("RUBBER", last.ordinal() + 1, BlockInit.RUBBER_WOOD_SET.getPlanks().get(), "rubber");
        BoatTypes.RUBBER = rubber;
        variants.add(rubber);
        $VALUES = variants.toArray(new Boat.Type[0]);
    }
}
