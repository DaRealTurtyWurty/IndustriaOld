package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.blockentity.ResearcherBlockEntity;
import dev.turtywurty.industria.client.model.ResearcherModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ResearcherBlockEntityRenderer implements BlockEntityRenderer<ResearcherBlockEntity> {
    private final ResearcherModel model;

    public ResearcherBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ResearcherModel(context.bakeLayer(ResearcherModel.LAYER_LOCATION));
    }

    @Override
    public void render(ResearcherBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 1.5, 0.5);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);
        this.model.renderToBuffer(pPoseStack,
                pBufferSource.getBuffer(this.model.renderType(ResearcherModel.LAYER_LOCATION.getModel())), pPackedLight,
                pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();

        int progress = pBlockEntity.getProgress();
        int maxProgress = pBlockEntity.getMaxProgress();
        float progressPercent = (float) progress / (float) maxProgress;
        float progressAngle = progressPercent * 360.0F;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 1.5, 0.5);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);
        this.model.renderToBuffer(pPoseStack,
                pBufferSource.getBuffer(this.model.renderType(ResearcherModel.LAYER_LOCATION.getModel())), pPackedLight,
                pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
}