package dev.turtywurty.industria.mixins;

import dev.turtywurty.industria.events.industria.BeeStingEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.class)
public class BeeMixin {
    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Bee;setHasStung(Z)V"))
    private void industria$doHurtTarget(Entity pEntity, CallbackInfoReturnable<Boolean> callback) {
        MinecraftForge.EVENT_BUS.post(new BeeStingEvent((Bee) (Object) this, pEntity));
    }
}
