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

public class BiomassGeneratorModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Industria.MODID, "biomass_generator"), "main");
    private final BiomassGeneratorParts parts;
    private final float pistonPlateStartX, pistonArmStartX, pistonArmStartSizeX;

    public BiomassGeneratorModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.parts = BiomassGeneratorParts.create(root);
        this.pistonPlateStartX = this.parts.pistonPlate().x;
        this.pistonArmStartX = this.parts.pistonArm().x;
        this.pistonArmStartSizeX = this.parts.pistonArm().xScale;
    }

    public static LayerDefinition createMainLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(),
                PartPose.offset(8.0F, 24.0F, -8.0F));

        PartDefinition structure = main.addOrReplaceChild("structure", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.0F, -0.5F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.0F, -0.5F, 8.0F));

        PartDefinition bucket = structure.addOrReplaceChild("bucket", CubeListBuilder.create().texOffs(38, 58)
                        .addBox(-4.0F, -3.7F, 4.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(23, 28)
                        .addBox(4.0F, -3.7F, -5.0F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 16)
                        .addBox(-5.0F, 2.3F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(19, 56)
                        .addBox(-4.0F, -3.7F, -5.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 28)
                        .addBox(-5.0F, -3.7F, -5.0F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -11.8F, 0.0F));

        PartDefinition supports = structure.addOrReplaceChild("supports", CubeListBuilder.create().texOffs(11, 53)
                        .addBox(-4.0F, 0.9545F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(36, 34)
                        .addBox(-4.0F, 0.9545F, 4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 52)
                        .addBox(-5.0F, 0.9545F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(47, 48)
                        .addBox(-2.0F, 0.9545F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(36, 46)
                        .addBox(1.0F, 0.9545F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(13, 28)
                        .addBox(4.0F, 0.9545F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(5, 62)
                        .addBox(4.0F, -5.0455F, 4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 62)
                        .addBox(-5.0F, -5.0455F, 4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(59, 34)
                        .addBox(-5.0F, -5.0455F, -5.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(57, 58)
                        .addBox(4.0F, -5.0455F, -5.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(41, 22)
                        .addBox(-2.0F, -5.0455F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.4545F, 0.0F));

        PartDefinition engine = structure.addOrReplaceChild("engine", CubeListBuilder.create().texOffs(36, 28)
                        .addBox(-5.8333F, 1.5556F, -2.1667F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(43, 7)
                        .addBox(-5.8333F, -2.4444F, -3.1667F, 12.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(43, 0)
                        .addBox(-5.8333F, -2.4444F, 1.8333F, 12.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(31, 16)
                        .addBox(-5.8333F, -2.4444F, -2.1667F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 16)
                        .addBox(6.1667F, -1.4444F, -2.1667F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(5, 5)
                        .addBox(-5.8333F, -1.4444F, -2.1667F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(5, 16)
                        .addBox(-0.8333F, -1.4444F, -0.1667F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(9, 0)
                        .addBox(-0.8333F, -1.4444F, 0.8333F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 9)
                        .addBox(-0.8333F, -1.4444F, -1.1667F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.1667F, -5.0556F, 0.1667F));

        PartDefinition crusher = main.addOrReplaceChild("crusher", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, -2.25F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(19, 46)
                        .addBox(-3.0F, 1.75F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 45)
                        .addBox(-3.0F, -0.25F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(40, 39)
                        .addBox(-3.0F, -2.25F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.0F, -13.75F, 8.0F));

        PartDefinition engineInner = main.addOrReplaceChild("engineInner", CubeListBuilder.create(),
                PartPose.offset(-8.0F, 0.0F, 8.0F));

        PartDefinition piston = engineInner.addOrReplaceChild("piston", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition plate = piston.addOrReplaceChild("plate", CubeListBuilder.create().texOffs(58, 46)
                        .addBox(-0.5F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.5F, -5.5F, 0.5F));

        PartDefinition arm = piston.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(13, 28)
                        .addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.5F, -5.5F, 0.5F));

        PartDefinition gears = engineInner.addOrReplaceChild("gears", CubeListBuilder.create(),
                PartPose.offset(-3.5F, -5.5F, 0.5F));

        PartDefinition secondGear = gears.addOrReplaceChild("secondGear", CubeListBuilder.create().texOffs(24, 28)
                        .addBox(-1.5F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition firstGear = gears.addOrReplaceChild("firstGear", CubeListBuilder.create().texOffs(0, 28)
                        .addBox(0.5F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.parts.structure().render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public BiomassGeneratorParts getParts() {
        return this.parts;
    }

    public float getPistonPlateStartX() {
        return this.pistonPlateStartX;
    }

    public float getPistonArmStartX() {
        return this.pistonArmStartX;
    }

    public float getPistonArmStartSizeX() {
        return this.pistonArmStartSizeX;
    }

    public record BiomassGeneratorParts(ModelPart structure, ModelPart crusher, ModelPart engineInner, ModelPart piston,
                                        ModelPart pistonPlate, ModelPart pistonArm, ModelPart engineInnerGears,
                                        ModelPart secondGear, ModelPart firstGear) {
        public static BiomassGeneratorParts create(ModelPart modelPart) {
            ModelPart root = modelPart.getChild("main");
            ModelPart engineInner = root.getChild("engineInner");
            return new BiomassGeneratorParts(root.getChild("structure"), root.getChild("crusher"), engineInner,
                    engineInner.getChild("piston"), engineInner.getChild("piston").getChild("plate"),
                    engineInner.getChild("piston").getChild("arm"), engineInner.getChild("gears"),
                    engineInner.getChild("gears").getChild("secondGear"),
                    engineInner.getChild("gears").getChild("firstGear"));
        }
    }
}