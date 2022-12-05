package dev.turtywurty.industria.mixins;

import dev.turtywurty.industria.events.industria.InventoryChangedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ServerPlayer$2")
public class ServerPlayerMixin {
    @Final
    @Shadow(aliases = "this$0")
    private ServerPlayer player;

    @Inject(method = "slotChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/InventoryChangeTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V"))
    public void industria$slotAdded(AbstractContainerMenu container, int slotIndex, ItemStack stack, CallbackInfo callback) {
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(this.player, container, slotIndex, stack));
    }
}
