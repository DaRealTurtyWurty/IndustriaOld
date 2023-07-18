package dev.turtywurty.industria.client.entityrenderers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.client.model.WoodBoatModel;
import dev.turtywurty.industria.entity.WoodBoat;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.stream.Stream;

public class WoodBoatRenderer extends EntityRenderer<WoodBoat> {
    private final Map<WoodBoat.Type, Pair<ResourceLocation, WoodBoatModel>> boatResources;

    public WoodBoatRenderer(EntityRendererProvider.Context context, boolean withChest) {
        super(context);
        this.shadowRadius = 0.8F;
        this.boatResources = Stream.of(WoodBoat.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type,
                type -> Pair.of(new ResourceLocation(getTextureLocation(type, withChest)),
                        this.createWoodBoatModel(context, type, withChest))));
    }

    private WoodBoatModel createWoodBoatModel(EntityRendererProvider.Context context, WoodBoat.Type type, boolean withChest) {
        ModelLayerLocation modellayerlocation = withChest ? createChestBoatModelName(type) : createBoatModelName(type);
        return new WoodBoatModel(context.bakeLayer(modellayerlocation), withChest);
    }

    private static String getTextureLocation(WoodBoat.Type type, boolean withChest) {
        return Industria.MODID + ":" + (withChest ? "textures/entity/chest_boat/" + type.getName() + ".png" : "textures/entity/boat/" + type.getName() + ".png");
    }

    public void render(WoodBoat pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.0D, 0.375D, 0.0D);
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - pEntityYaw));
        float hurt = (float) pEntity.getHurtTime() - pPartialTicks;
        float damage = pEntity.getDamage() - pPartialTicks;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurt > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.sin(hurt) * hurt * damage / 10.0F * (float) pEntity.getHurtDir()));
        }

        float bubbleAngle = pEntity.getBubbleAngle(pPartialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            pMatrixStack.mulPose((new Quaternionf()).setAngleAxis(pEntity.getBubbleAngle(pPartialTicks) * ((float)Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }

        Pair<ResourceLocation, WoodBoatModel> pair = getModelWithLocation(pEntity);
        ResourceLocation texture = pair.getFirst();
        WoodBoatModel model = pair.getSecond();
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        model.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(texture));
        model.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                1.0F);
        if (!pEntity.isUnderWater()) {
            VertexConsumer vertexConsumer1 = pBuffer.getBuffer(RenderType.waterMask());
            model.waterPatch().render(pMatrixStack, vertexConsumer1, pPackedLight, OverlayTexture.NO_OVERLAY);
        }

        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     *
     * @deprecated Forge: use {@link #getModelWithLocation(WoodBoat)} instead
     */
    @Deprecated
    public ResourceLocation getTextureLocation(WoodBoat pEntity) {
        return getModelWithLocation(pEntity).getFirst();
    }

    public Pair<ResourceLocation, WoodBoatModel> getModelWithLocation(WoodBoat boat) {
        return this.boatResources.get(boat.getBoatType());
    }

    public static ModelLayerLocation createBoatModelName(WoodBoat.Type pType) {
        return createLocation("boat/" + pType.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(WoodBoat.Type type) {
        return createLocation("chest_boat/" + type.getName(), "main");
    }

    private static ModelLayerLocation createLocation(String pPath, String pModel) {
        return new ModelLayerLocation(new ResourceLocation(Industria.MODID, pPath), pModel);
    }
}