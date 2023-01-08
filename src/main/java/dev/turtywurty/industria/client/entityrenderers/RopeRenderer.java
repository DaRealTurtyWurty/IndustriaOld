package dev.turtywurty.industria.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.entity.RopeEntity;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

import static dev.turtywurty.industria.entity.RopeEntity.THICKNESS;

public class RopeRenderer extends EntityRenderer<RopeEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID, "textures/entity/rope.png");
    private static final int NUMBER_OF_SEGMENTS = 24;

    private final LeashKnotModel model;

    public RopeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new LeashKnotModel<>(pContext.bakeLayer(ModelLayers.LEASH_KNOT));
    }

    @Override
    public void render(RopeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.scale(-1F, -1F, 1F);
        this.model.setupAnim(pEntity, 0F, 0F, 0F, 0F, 0F);
        VertexConsumer vertexConsumer = pBuffer.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        for (RopeEntity.RopeEnd end : pEntity.getEnds()) {
            if (!end.shouldRender()) continue;

            renderRope(pEntity, pPoseStack, pBuffer,
                    RopeEntity.getRopeEntity(pEntity.level, end.position()).orElse(null));
        }
    }

    private void renderRope(RopeEntity start, PoseStack pMatrixStack, MultiBufferSource pBuffer, @Nullable RopeEntity end) {
        if (end == null) return;

        RopeEntity.RopeEnd ropeEnd = start.getUsingEntity(end).orElse(null);
        if (ropeEnd == null) return;

        RopeEntity.Calculation calculation = ropeEnd.calculation();

        pMatrixStack.pushPose();
        pMatrixStack.translate(calculation.offset().x(), calculation.ropeOffset().y(), calculation.offset().y());
        VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.leash());
        Matrix4f matrix = pMatrixStack.last().pose();
        int startLight = getBlockLightLevel(start, calculation.startPos());
        int endLight = getBlockLightLevel(end, calculation.endPos());

        for (int segment = 0; segment <= calculation.segmentCount(); ++segment) {
            addVertexPair(vertexConsumer, matrix, calculation.size().x(), calculation.size().y(),
                    calculation.size().z(), startLight, endLight, calculation.brightness().x(),
                    calculation.brightness().y(), THICKNESS, THICKNESS, calculation.normal().x, calculation.normal().y,
                    segment, calculation.segmentCount(), false);
        }

        addVertexPair(vertexConsumer, matrix, calculation.size().x(), calculation.size().y() + THICKNESS,
                calculation.size().z(), startLight, endLight, calculation.brightness().x(),
                calculation.brightness().y(), THICKNESS, THICKNESS / 2, calculation.normal().x, calculation.normal().y,
                calculation.segmentCount() + 1, calculation.segmentCount() + 1, false);

        for (int segment = calculation.segmentCount(); segment >= 0; --segment) {
            addVertexPair(vertexConsumer, matrix, calculation.size().x(), calculation.size().y(),
                    calculation.size().z(), startLight, endLight, calculation.brightness().x(),
                    calculation.brightness().y(), THICKNESS, 0, calculation.normal().x, calculation.normal().y, segment,
                    calculation.segmentCount() + 1, true);
        }

        pMatrixStack.popPose();
    }

    private static Vector3f addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix, float length, float height, float depth, int startLight, int endLight, int startBrightness, int endBrightness, float xThickness, float yThickness, float xOffset, float zOffset, int segment, float segmentCount, boolean isInverted) {
        float progress = (float) segment / segmentCount;
        int light = (int) Mth.lerp(progress, (float) startLight, (float) endLight);
        int brightness = (int) Mth.lerp(progress, (float) startBrightness, (float) endBrightness);
        int packedLight = LightTexture.pack(light, brightness);
        float yColorOffset = segment % 2 == (isInverted ? 1 : 0) ? 0.7F : 1.25F;
        float red = 0.5F * yColorOffset;
        float green = 0.4F * yColorOffset;
        float blue = 0.3F * yColorOffset;
        float xPos = length * progress;
        float yPos = height > 0.0F ? height * progress * progress : height - height * (1.0F - progress) * (1.0F - progress);
        float zPos = depth * progress;

        float x0 = xPos - xOffset;
        float y0 = yPos + yThickness;
        float z0 = zPos + zOffset;
        float x1 = xPos + xOffset;
        float y1 = yPos + xThickness - yThickness;
        float z1 = zPos - zOffset;
        vertex(vertexConsumer, matrix, x0, y0, z0, red, green, blue, 1F, packedLight);
        vertex(vertexConsumer, matrix, x1, y1, z1, red, green, blue, 1F, packedLight);

        return new Vector3f(x1, y1, z1);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix, float x, float y, float z, float red, float green, float blue, float alpha, int packedLight) {
        vertexConsumer.vertex(matrix, x, y, z).color(red, green, blue, alpha).uv2(packedLight).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(RopeEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(RopeEntity rope, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        RopeEntity furthestRope = rope.getEnds().stream().map(RopeEntity.RopeEnd::position)
                .map(pos -> RopeEntity.getRopeEntity(rope.level, pos).orElse(null)).filter(Objects::nonNull)
                .max(Comparator.comparingDouble(rope::distanceToSqr)).orElse(null);
        if (furthestRope == null) return false;

        AABB aabb = rope.getBoundingBoxForCulling().inflate(0.5D).inflate(rope.distanceToSqr(furthestRope));
        if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
            aabb = new AABB(rope.getX() - 2.0D, rope.getY() - 2.0D, rope.getZ() - 2.0D, rope.getX() + 2.0D,
                    rope.getY() + 2.0D, rope.getZ() + 2.0D);
        }

        return pCamera.isVisible(aabb);
    }
}
