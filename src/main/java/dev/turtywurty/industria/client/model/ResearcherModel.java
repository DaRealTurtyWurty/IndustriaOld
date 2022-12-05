package dev.turtywurty.industria.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import dev.turtywurty.industria.Industria;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ResearcherModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Industria.MODID, "researcher"), "main");
    private final ModelPart internalFrame;
    private final ModelPart frame;
    private final ModelPart base;

    public ResearcherModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.internalFrame = root.getChild("internalFrame");
        this.frame = root.getChild("frame");
        this.base = root.getChild("base");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition internalFrame = partDefinition.addOrReplaceChild("internalFrame",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(6.0F, -8.0F, -6.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(7.0F, -8.0F, -9.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(5.0F, -8.0F, -11.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(10.0F, -8.0F, -11.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(6.0F, -8.0F, -11.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.0F, 16.0F, 8.0F));

        PartDefinition diagonal0 = internalFrame.addOrReplaceChild("diagonal0", CubeListBuilder.create(),
                PartPose.offset(8.0F, 8.0F, -8.0F));

        diagonal0.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.5F, -0.65F, -3.6213F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -15.25F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition diagonal1 = internalFrame.addOrReplaceChild("diagonal1", CubeListBuilder.create(),
                PartPose.offset(8.0F, 8.0F, -8.0F));

        diagonal1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.5F, -0.65F, 0.3787F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -15.25F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition diagonal2 = internalFrame.addOrReplaceChild("diagonal2", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        diagonal2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.5F, -0.65F, 0.6213F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.0F, -7.25F, -8.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition diagonal3 = internalFrame.addOrReplaceChild("diagonal3", CubeListBuilder.create(),
                PartPose.offset(1.5F, -7.5F, -1.5F));

        diagonal3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.5F, -0.65F, -3.6213F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.5F, 0.25F, -6.5F, 0.0F, 0.7854F, 0.0F));

        partDefinition.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(26, 0)
                        .addBox(1.0F, -8.0F, -1.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(24, 24)
                        .addBox(0.0F, -8.0F, -15.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(0, 42)
                        .addBox(0.0F, -8.0F, -1.0F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(26, 18)
                        .addBox(1.0F, -8.0F, -16.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 42)
                        .addBox(0.0F, -8.0F, -16.0F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(26, 3)
                        .addBox(15.0F, -8.0F, -15.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(8, 42)
                        .addBox(15.0F, -8.0F, -1.0F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 42)
                        .addBox(15.0F, -8.0F, -16.0F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.0F, 16.0F, 8.0F));

        partDefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-8.0F, -4.0F, -6.0F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(6.0F, -4.0F, -6.0F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(7.0F, -4.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(7.0F, -4.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-8.0F, -4.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-8.0F, -4.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-7.0F, -4.0F, -8.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-7.0F, -4.0F, 6.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-8.0F, -3.0F, -8.0F, 16.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        internalFrame.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        frame.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}