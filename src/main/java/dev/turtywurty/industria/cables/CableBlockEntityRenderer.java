package dev.turtywurty.industria.cables;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Set;

public class CableBlockEntityRenderer implements BlockEntityRenderer<CableBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public CableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(CableBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(ItemInit.DEBUG_GOGGLES.get())) return;

        HitResult hitResult = player.pick(5, pPartialTick, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos hitPos = new BlockPos(hitResult.getLocation());
        if (!hitPos.equals(pBlockEntity.getBlockPos())) return;

        EnergyGraph network = pBlockEntity.getNetwork();
        if (network == null) return;

        Map<BlockPos, Set<BlockPos>> graph = network.getGraph();
        for (BlockPos fromPos : graph.keySet()) {
            pPoseStack.pushPose();
            Vec3 distance = Vec3.atBottomCenterOf(fromPos).subtract(Vec3.atBottomCenterOf(pBlockEntity.getBlockPos()));
            pPoseStack.translate(distance.x, distance.y, distance.z);
            LevelRenderer.renderLineBox(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 0.24, 0.24, 0.24, 0.76,
                    0.76, 0.76, 0, 1, 0, 1f);
            pPoseStack.popPose();

            for (BlockPos toPos : graph.get(fromPos)) {
                // render a box inbetween the two positions
                pPoseStack.pushPose();
                Vec3 distance1 = Vec3.atBottomCenterOf(fromPos).subtract(Vec3.atBottomCenterOf(pBlockEntity.getBlockPos()));
                Vec3 distance2 = Vec3.atBottomCenterOf(toPos).subtract(Vec3.atBottomCenterOf(pBlockEntity.getBlockPos()));
                pPoseStack.translate((distance1.x + distance2.x) / 2, (distance1.y + distance2.y) / 2, (distance1.z + distance2.z) / 2);
                LevelRenderer.renderLineBox(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 0.34, 0.34, 0.34,
                        0.66,
                        0.66, 0.66, 1, 1, 0, 1f);
                pPoseStack.popPose();
            }
        }
    }
}
