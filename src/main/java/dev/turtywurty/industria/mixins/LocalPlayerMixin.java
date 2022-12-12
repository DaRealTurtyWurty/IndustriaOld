package dev.turtywurty.industria.mixins;

import com.mojang.authlib.GameProfile;
import dev.turtywurty.industria.entity.WoodBoat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    private LocalPlayerMixin(ClientLevel pClientLevel, GameProfile pGameProfile, @Nullable ProfilePublicKey pProfilePublicKey) {
        super(pClientLevel, pGameProfile, pProfilePublicKey);
    }

    @Shadow
    public Input input;

    @Shadow
    private boolean handsBusy;

    @Inject(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;" + "getVehicle()Lnet/minecraft/world/entity/Entity;", ordinal = 0, shift = At.Shift.BEFORE))
    private void industria$rideTick(CallbackInfo callback) {
        if (getVehicle() instanceof WoodBoat boat) {
            boat.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
            this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
        }
    }
}
