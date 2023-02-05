package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.blockentity.TreeDecapitatorBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TreeDecapitatorBlockEntityRenderer implements BlockEntityRenderer<TreeDecapitatorBlockEntity> {
    private static final int FULL_LIGHT = 15728880;

    private final BlockEntityRendererProvider.Context context;

    public TreeDecapitatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(TreeDecapitatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.getPositions().isEmpty() || !pBlockEntity.hasLevel()) return;

        BlockRenderDispatcher dispatcher = this.context.getBlockRenderDispatcher();

        float maxDistance = (float) pBlockEntity.getPositions().stream()
                .mapToDouble(p -> pBlockEntity.getBlockPos().distSqr(p)).max().orElse(0) / 4f;

        BlockPos pos = pBlockEntity.getBlockPos();
        Level level = pBlockEntity.getLevel();
        if (level == null) return;

        BlockPos above = pos.above();
        int light = level.getBlockState(above).isSolidRender(level, above) ? FULL_LIGHT : LevelRenderer.getLightColor(
                level, above);

        for (BlockPos position : pBlockEntity.getPositions()) {
            pPoseStack.pushPose();

            float scale = 0.5F / maxDistance;
            float mappedX = (float) (position.getX() - pos.getX()) / maxDistance / 2f + 0.5f - (scale / 2f);
            float mappedY = (float) (position.getY() - pos.getY()) / maxDistance / 2f + 0.5f - (scale / 2f);
            float mappedZ = (float) (position.getZ() - pos.getZ()) / maxDistance / 2f + 0.5f - (scale / 2f);

            pPoseStack.translate(mappedX, mappedY - 0.25D, mappedZ);
            pPoseStack.scale(scale, scale, scale);

            dispatcher.renderSingleBlock(level.getBlockState(position), pPoseStack, pBufferSource, light,
                    pPackedOverlay);

            pPoseStack.popPose();
        }
    }
}
