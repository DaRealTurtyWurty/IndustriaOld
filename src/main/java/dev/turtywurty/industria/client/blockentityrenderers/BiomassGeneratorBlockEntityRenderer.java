package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.BiomassGeneratorBlockEntity;
import dev.turtywurty.industria.client.model.BiomassGeneratorModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BiomassGeneratorBlockEntityRenderer implements BlockEntityRenderer<BiomassGeneratorBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/blocks/biomass_generator.png");

    private final BiomassGeneratorModel model;

    public BiomassGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new BiomassGeneratorModel(context.bakeLayer(BiomassGeneratorModel.LAYER_LOCATION));
    }

    @Override
    public void render(BiomassGeneratorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(1, 0, 1);
        pPoseStack.scale(1, -1, -1);

        if (pBlockEntity.getRunningTicks() > 0) {
            float maxSpeed = (float) Math.toRadians(
                    360 * pBlockEntity.getRunningTicks() / 5f);

            float speed = Mth.clamp((pBlockEntity.getRunningTicks() / 25f) * maxSpeed, 0, maxSpeed);
            this.model.getParts().crusher().yRot = speed;

            this.model.getParts().firstGear().xRot = speed / 2f;
            this.model.getParts().secondGear().xRot = -speed / 2f;

            if(this.model.getParts().pistonPlate().x > this.model.getPistonPlateStartX() - 4 && pBlockEntity.isPistonExtending()) {
                this.model.getParts().pistonPlate().x -= 0.1f;
            } else if(this.model.getParts().pistonPlate().x < this.model.getPistonPlateStartX()) {
                pBlockEntity.setPistonReverse(true);
                this.model.getParts().pistonPlate().x += 0.1f;
                if(this.model.getParts().pistonPlate().x >= this.model.getPistonPlateStartX()) {
                    pBlockEntity.setPistonReverse(false);
                }
            }

            if(pBlockEntity.isPistonExtending() && this.model.getParts().pistonArm().xScale <= this.model.getPistonArmStartSizeX() + 4f) {
                this.model.getParts().pistonArm().xScale += 0.1f;
                this.model.getParts().pistonArm().x -= 0.05f;
            } else if(this.model.getParts().pistonArm().xScale > this.model.getPistonArmStartSizeX()) {
                this.model.getParts().pistonArm().xScale -= 0.1f;
                this.model.getParts().pistonArm().x += 0.05f;
            }
        } else {
            this.model.getParts().crusher().yRot = 0;
            this.model.getParts().firstGear().xRot = 0;
            this.model.getParts().secondGear().xRot = 0;
            this.model.getParts().pistonPlate().x = this.model.getPistonPlateStartX();
            this.model.getParts().pistonArm().x = this.model.getPistonArmStartX();
            this.model.getParts().pistonArm().xScale = this.model.getPistonArmStartSizeX();
            pBlockEntity.setPistonReverse(false);
        }

        VertexConsumer vertexConsumer = pBufferSource.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        this.model.getParts().crusher().render(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay);
        this.model.getParts().engineInner().render(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay);

        pPoseStack.popPose();
    }
}