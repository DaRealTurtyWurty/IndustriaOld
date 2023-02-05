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

public class AgitatorModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Industria.MODID, "agitator"), "main");

    private final AgitatorParts parts;

    public AgitatorModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.parts = AgitatorParts.create(root.getChild("main"));
    }

    public static LayerDefinition createMainLayer() {
        var meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition structure = main.addOrReplaceChild("structure", CubeListBuilder.create().texOffs(60, 8)
                        .addBox(-4.0F, -7.5217F, -6.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(57, 58)
                        .addBox(-4.0F, 7.4783F, -8.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(38, 58)
                        .addBox(-4.0F, 7.4783F, 7.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(-8.0F, 7.4783F, -4.0F, 16.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(52, 0)
                        .addBox(-5.0F, 7.4783F, 6.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(30, 23)
                        .addBox(-7.0F, 7.4783F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(30, 20)
                        .addBox(-7.0F, 7.4783F, 4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 51)
                        .addBox(-5.0F, 7.4783F, -7.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(41, 16)
                        .addBox(-6.0F, 7.4783F, 5.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(33, 13)
                        .addBox(-6.0F, 7.4783F, -6.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(41, 53)
                        .addBox(-4.0F, 4.4783F, -8.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(30, 39)
                        .addBox(7.0F, 4.4783F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(52, 34)
                        .addBox(-4.0F, 4.4783F, 7.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 35)
                        .addBox(-8.0F, 4.4783F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(52, 29)
                        .addBox(-4.0F, -4.5217F, -8.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 35)
                        .addBox(7.0F, -4.5217F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(52, 3)
                        .addBox(-4.0F, -4.5217F, 7.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(30, 27)
                        .addBox(-8.0F, -4.5217F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(41, 31)
                        .addBox(6.0F, -6.5217F, -4.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 57)
                        .addBox(-4.0F, -6.5217F, -7.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(41, 2)
                        .addBox(-7.0F, -6.5217F, -4.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(18, 54)
                        .addBox(-4.0F, -6.5217F, 6.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(60, 61)
                        .addBox(-6.0F, -4.5217F, -6.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(55, 61)
                        .addBox(-5.0F, -4.5217F, -7.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(50, 61)
                        .addBox(-7.0F, -4.5217F, -5.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(45, 61)
                        .addBox(5.0F, -4.5217F, -6.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(40, 61)
                        .addBox(4.0F, -4.5217F, -7.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(35, 61)
                        .addBox(6.0F, -4.5217F, -5.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(30, 61)
                        .addBox(6.0F, -4.5217F, 4.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(25, 61)
                        .addBox(-5.0F, -4.5217F, 6.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 24)
                        .addBox(5.0F, -6.5217F, 4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(22, 23)
                        .addBox(-5.0F, -6.5217F, 5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(18, 20)
                        .addBox(-5.0F, -6.5217F, -6.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 20)
                        .addBox(5.0F, -6.5217F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(20, 61)
                        .addBox(5.0F, -4.5217F, 5.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(15, 61)
                        .addBox(-6.0F, -4.5217F, 5.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 14)
                        .addBox(4.0F, -6.5217F, 5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 10)
                        .addBox(-6.0F, -6.5217F, 4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 4)
                        .addBox(-6.0F, -6.5217F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
                        .addBox(4.0F, -6.5217F, -6.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(10, 61)
                        .addBox(4.0F, -4.5217F, 6.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(5, 61)
                        .addBox(-7.0F, -4.5217F, 4.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 10)
                        .addBox(-6.0F, -7.5217F, -4.0F, 12.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(48, 50)
                        .addBox(-5.0F, -7.5217F, -5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(41, 26)
                        .addBox(-5.0F, -7.5217F, 4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(19, 58)
                        .addBox(-4.0F, -7.5217F, 5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -8.4783F, 0.0F));

        structure.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(19, 20)
                        .addBox(-8.0F, -2.5F, -19.0F, 1.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(49, 42)
                        .addBox(-4.0F, -2.5F, -23.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 20)
                        .addBox(7.0F, -2.5F, -19.0F, 1.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 47)
                        .addBox(-4.0F, -2.5F, -8.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.9783F, 15.0F));

        main.addOrReplaceChild("rotor", CubeListBuilder.create().texOffs(67, 35)
                        .addBox(-0.5F, 3.6538F, -5.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(68, 17)
                        .addBox(0.5F, 3.6538F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(67, 11)
                        .addBox(-0.5F, 3.6538F, 0.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(65, 67)
                        .addBox(-5.0F, 3.6538F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 61)
                        .addBox(-0.5F, -8.3462F, -0.5F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(65, 61)
                        .addBox(-0.5F, -0.3462F, -5.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(64, 25)
                        .addBox(0.5F, -0.3462F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(60, 53)
                        .addBox(-5.0F, -0.3462F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(61, 19)
                        .addBox(-0.5F, -0.3462F, 0.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(60, 11)
                        .addBox(0.5F, -4.3462F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(11, 35)
                        .addBox(-0.5F, -4.3462F, -5.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(19, 47)
                        .addBox(-5.0F, -4.3462F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(11, 20)
                        .addBox(-0.5F, -4.3462F, 0.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -6.6538F, 0.0F));

        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.parts.structure().render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public AgitatorParts getParts() {
        return this.parts;
    }

    public record AgitatorParts(ModelPart structure, ModelPart glass, ModelPart rotor) {
        public static AgitatorParts create(ModelPart root) {
            return new AgitatorParts(root.getChild("structure"), root.getChild("structure").getChild("glass"),
                    root.getChild("rotor"));
        }
    }
}