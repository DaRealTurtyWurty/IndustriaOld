package dev.turtywurty.industria.client.model;

import com.google.common.collect.ImmutableList;
import dev.turtywurty.industria.entity.WoodBoat;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WoodBoatModel extends ListModel<WoodBoat> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ModelPart waterPatch;
    private final ImmutableList<ModelPart> parts;

    public WoodBoatModel(ModelPart pRoot, boolean pWithChest) {
        this.leftPaddle = pRoot.getChild("left_paddle");
        this.rightPaddle = pRoot.getChild("right_paddle");
        this.waterPatch = pRoot.getChild("water_patch");
        ImmutableList.Builder<ModelPart> builder = new ImmutableList.Builder<>();
        builder.add(pRoot.getChild("bottom"), pRoot.getChild("back"), pRoot.getChild("front"), pRoot.getChild("right"),
                pRoot.getChild("left"), this.leftPaddle, this.rightPaddle);
        if (pWithChest) {
            builder.add(pRoot.getChild("chest_bottom"));
            builder.add(pRoot.getChild("chest_lid"));
            builder.add(pRoot.getChild("chest_lock"));
        }

        this.parts = builder.build();
    }

    public static LayerDefinition createBodyModel(boolean withChest) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, ((float) Math.PI / 2F), 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("back",
                CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(-15.0F, 4.0F, 4.0F, 0.0F, ((float) Math.PI * 1.5F), 0.0F));
        partdefinition.addOrReplaceChild("front",
                CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(15.0F, 4.0F, 0.0F, 0.0F, ((float) Math.PI / 2F), 0.0F));
        partdefinition.addOrReplaceChild("right",
                CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, -9.0F, 0.0F, (float) Math.PI, 0.0F));
        partdefinition.addOrReplaceChild("left",
                CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
                PartPose.offset(0.0F, 4.0F, 9.0F));
        if (withChest) {
            partdefinition.addOrReplaceChild("chest_bottom",
                    CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F),
                    PartPose.offsetAndRotation(-2.0F, -5.0F, -6.0F, 0.0F, (-(float) Math.PI / 2F), 0.0F));
            partdefinition.addOrReplaceChild("chest_lid",
                    CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F),
                    PartPose.offsetAndRotation(-2.0F, -9.0F, -6.0F, 0.0F, (-(float) Math.PI / 2F), 0.0F));
            partdefinition.addOrReplaceChild("chest_lock",
                    CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F),
                    PartPose.offsetAndRotation(-1.0F, -6.0F, -1.0F, 0.0F, (-(float) Math.PI / 2F), 0.0F));
        }

        partdefinition.addOrReplaceChild("left_paddle",
                CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F)
                        .addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
                PartPose.offsetAndRotation(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, 0.19634955F));
        partdefinition.addOrReplaceChild("right_paddle",
                CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F)
                        .addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
                PartPose.offsetAndRotation(3.0F, -5.0F, -9.0F, 0.0F, (float) Math.PI, 0.19634955F));
        partdefinition.addOrReplaceChild("water_patch",
                CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, ((float) Math.PI / 2F), 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, withChest ? 128 : 64);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setupAnim(WoodBoat pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        animatePaddle(pEntity, 0, this.leftPaddle, pLimbSwing);
        animatePaddle(pEntity, 1, this.rightPaddle, pLimbSwing);
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    private static void animatePaddle(WoodBoat pBoat, int pSide, ModelPart pPaddle, float pLimbSwing) {
        float f = pBoat.getRowingTime(pSide, pLimbSwing);
        pPaddle.xRot = Mth.clampedLerp((-(float) Math.PI / 3F), -0.2617994F, (Mth.sin(-f) + 1.0F) / 2.0F);
        pPaddle.yRot = Mth.clampedLerp((-(float) Math.PI / 4F), ((float) Math.PI / 4F),
                (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);
        if (pSide == 1) {
            pPaddle.yRot = (float) Math.PI - pPaddle.yRot;
        }
    }
}