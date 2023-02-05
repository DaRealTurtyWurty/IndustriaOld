package dev.turtywurty.industria.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.turtywurty.industria.Industria;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CrusherModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Industria.MODID, "crusher"), "main");

    private final CrusherParts parts;

    public CrusherModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        ModelPart main = root.getChild("main");
        ModelPart structure = main.getChild("structure");
        ModelPart left = main.getChild("left");
        ModelPart right = main.getChild("right");
        this.parts = new CrusherParts(main, structure, left, right, left.getChild("bottomLeft"),
                right.getChild("bottomRight"), left.getChild("topLeft"), right.getChild("topRight"),
                structure.getChild("feet"), structure.getChild("funnel"), structure.getChild("bars"));
    }

    public static LayerDefinition createMainLayer() {
        var meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(),
                PartPose.offset(0.0F, 20.3889F, 0.0F));

        PartDefinition structure = main.addOrReplaceChild("structure", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-8.0F, 0.75F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(19, 26)
                        .addBox(7.0F, -6.25F, -8.0F, 1.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(0, 18)
                        .addBox(-8.0F, -6.25F, -8.0F, 1.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(59, 35)
                        .addBox(-7.0F, -6.25F, -8.0F, 14.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(57, 18)
                        .addBox(-7.0F, -6.25F, 7.0F, 14.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -0.1389F, 0.0F));

        structure.addOrReplaceChild("bars", CubeListBuilder.create().texOffs(36, 53)
                        .addBox(4.0F, -8.0F, -7.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(49, 0)
                        .addBox(-3.0F, -5.0F, -7.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(53, 55)
                        .addBox(-5.0F, -8.0F, -7.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(19, 50)
                        .addBox(2.0F, -5.0F, -7.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 3.75F, 0.0F));

        PartDefinition funnel = structure.addOrReplaceChild("funnel", CubeListBuilder.create().texOffs(0, 42)
                        .addBox(-1.0F, -1.4616F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -4.7884F, 0.0F));

        funnel.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 36)
                        .addBox(-1.5F, -1.5F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.5F, 0.0384F, 0.0F, 0.0F, 0.0F, -0.6545F));

        funnel.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(38, 18)
                        .addBox(-0.5F, -1.5F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.5F, 0.0384F, 0.0F, 0.0F, 0.0F, 0.6545F));

        structure.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(66, 5)
                        .addBox(6.0F, 1.75F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(66, 0)
                        .addBox(6.0F, 1.75F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 64)
                        .addBox(-8.0F, 1.75F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(9, 64)
                        .addBox(-8.0F, 1.75F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left = main.addOrReplaceChild("left", CubeListBuilder.create(),
                PartPose.offset(-2.5F, -0.8889F, 0.0F));

        left.addOrReplaceChild("bottomLeft", CubeListBuilder.create().texOffs(0, 47)
                        .addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 42)
                        .addBox(-1.5F, -1.5F, 6.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(38, 35)
                        .addBox(-1.5F, -1.5F, 4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(38, 23)
                        .addBox(-1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 6)
                        .addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(37, 18)
                        .addBox(-1.5F, -1.5F, -7.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 28)
                        .addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        left.addOrReplaceChild("topLeft", CubeListBuilder.create().texOffs(0, 59)
                        .addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(59, 44)
                        .addBox(-1.5F, -1.5F, 5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(18, 59)
                        .addBox(-1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(9, 59)
                        .addBox(-1.5F, -1.5F, 1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(36, 58)
                        .addBox(-1.5F, -1.5F, -6.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(53, 58)
                        .addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, -3.0F, 0.0F));

        PartDefinition right = main.addOrReplaceChild("right", CubeListBuilder.create(),
                PartPose.offset(4.5F, -3.8889F, 0.0F));

        right.addOrReplaceChild("bottomRight", CubeListBuilder.create().texOffs(0, 18)
                        .addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 18)
                        .addBox(-1.5F, -1.5F, 6.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 23)
                        .addBox(-1.5F, -1.5F, 4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 23)
                        .addBox(-1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 28)
                        .addBox(-1.5F, -1.5F, -7.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(28, 18)
                        .addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 3.0F, 0.0F));

        right.addOrReplaceChild("topRight", CubeListBuilder.create().texOffs(49, 0)
                        .addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(49, 5)
                        .addBox(-1.5F, -1.5F, 5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 50)
                        .addBox(-1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(36, 53)
                        .addBox(-1.5F, -1.5F, 1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(53, 53)
                        .addBox(-1.5F, -1.5F, -6.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(57, 27)
                        .addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.parts.structure.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public CrusherParts getParts() {
        return this.parts;
    }

    public record CrusherParts(ModelPart main, ModelPart structure, ModelPart left, ModelPart right,
                               ModelPart bottomLeft, ModelPart topLeft, ModelPart bottomRight, ModelPart topRight,
                               ModelPart feet, ModelPart funnel, ModelPart bars) {
    }
}