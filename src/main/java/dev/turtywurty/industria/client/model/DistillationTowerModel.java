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

public class DistillationTowerModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Industria.MODID, "distillation_tower"), "main");
    private final ModelPart main;

    public DistillationTowerModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.main = root.getChild("main");
    }

    public static LayerDefinition createMainLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-32.0F, -8.0F, -32.0F, 64.0F, 8.0F, 64.0F, new CubeDeformation(0.0F)).texOffs(120, 119)
                        .addBox(-6.0F, -10.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(83, 116)
                        .addBox(-6.0F, -64.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(0, 17)
                        .addBox(-7.0F, -12.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-7.0F, -62.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(0, 73)
                        .addBox(-8.0F, -60.0F, -8.0F, 16.0F, 48.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(90, 82)
                        .addBox(8.0F, -32.0F, -2.0F, 24.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(83, 98)
                        .addBox(8.0F, -16.0F, -2.0F, 24.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(90, 73)
                        .addBox(8.0F, -60.0F, -2.0F, 24.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(83, 107)
                        .addBox(-32.0F, -40.0F, -2.0F, 24.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        main.addOrReplaceChild("support", CubeListBuilder.create().texOffs(0, 34)
                        .addBox(10.0F, -36.0F, -10.0F, 2.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(83, 134)
                        .addBox(-12.0F, -36.0F, 10.0F, 24.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(110, 91)
                        .addBox(-12.0F, -36.0F, -12.0F, 24.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(9, 138)
                        .addBox(10.0F, -32.0F, -12.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(18, 138)
                        .addBox(10.0F, -32.0F, 10.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(45, 34)
                        .addBox(-12.0F, -32.0F, -12.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(54, 34)
                        .addBox(-12.0F, -32.0F, 10.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(43, 24)
                        .addBox(-1.0F, -36.0F, -10.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(43, 17)
                        .addBox(-1.0F, -36.0F, 8.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(43, 7)
                        .addBox(8.0F, -36.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(43, 0)
                        .addBox(-10.0F, -36.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(65, 73)
                        .addBox(-12.0F, -36.0F, -10.0F, 2.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(25, 41)
                        .addBox(21.0F, -12.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(74, 98)
                        .addBox(21.0F, -62.0F, -4.0F, 2.0F, 54.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(65, 98)
                        .addBox(21.0F, -62.0F, 2.0F, 2.0F, 54.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(136, 134)
                        .addBox(-23.0F, -42.0F, 2.0F, 2.0F, 34.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 138)
                        .addBox(-23.0F, -42.0F, -4.0F, 2.0F, 34.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 41)
                        .addBox(21.0F, -18.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 34)
                        .addBox(21.0F, -34.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(25, 34)
                        .addBox(21.0F, -28.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 17)
                        .addBox(21.0F, -62.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 24)
                        .addBox(21.0F, -56.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-23.0F, -42.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 7)
                        .addBox(-23.0F, -36.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 256, 256);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}