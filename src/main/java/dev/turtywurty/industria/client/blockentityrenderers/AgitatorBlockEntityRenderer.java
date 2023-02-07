package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.AgitatorBlockEntity;
import dev.turtywurty.industria.client.model.AgitatorModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AgitatorBlockEntityRenderer implements BlockEntityRenderer<AgitatorBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/blocks/agitator.png");
    private final BlockEntityRendererProvider.Context context;
    private final AgitatorModel model;

    public AgitatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.model = new AgitatorModel(this.context.bakeLayer(AgitatorModel.LAYER_LOCATION));
    }

    @Override
    public void render(AgitatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);
        this.model.renderToBuffer(pPoseStack, pBufferSource.getBuffer(this.model.renderType(TEXTURE)), pPackedLight,
                pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.343745, 0.5);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);


        if(pBlockEntity.getProgress() > 0) {
            this.model.getParts().rotor().yRot += 0.01F;
            this.model.getParts().rotor().translateAndRotate(pPoseStack);
        } else {
            float yRot = this.model.getParts().rotor().yRot;
            if(yRot != 0) {
                this.model.getParts().rotor().yRot = Mth.lerp(yRot, 360, 0);
                this.model.getParts().rotor().translateAndRotate(pPoseStack);
            }
        }

        this.model.getParts().rotor()
                .render(pPoseStack, pBufferSource.getBuffer(this.model.renderType(TEXTURE)), pPackedLight,
                        pPackedOverlay);
        pPoseStack.popPose();
    }
}
