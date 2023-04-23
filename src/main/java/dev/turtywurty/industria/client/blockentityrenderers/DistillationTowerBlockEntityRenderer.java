package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.DistillationTowerBlockEntity;
import dev.turtywurty.industria.client.model.DistillationTowerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DistillationTowerBlockEntityRenderer implements BlockEntityRenderer<DistillationTowerBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/blocks/distillation_tower.png");
    private final BlockEntityRendererProvider.Context context;
    private final DistillationTowerModel model;

    public DistillationTowerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.model = new DistillationTowerModel(context.bakeLayer(DistillationTowerModel.LAYER_LOCATION));
    }

    @Override
    public void render(DistillationTowerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.scale(1, -1, -1);
        pPoseStack.translate(2, -1.5, -2);
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180f));
        this.model.renderToBuffer(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), pPackedLight,
                pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
}
