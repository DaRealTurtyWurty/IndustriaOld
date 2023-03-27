package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.AgitatorBlockEntity;
import dev.turtywurty.industria.blockentity.util.directional.MultiDirectionalFluidTank;
import dev.turtywurty.industria.client.model.AgitatorModel;
import dev.turtywurty.industria.client.model.baked.ReplacedTextureColorModel;
import dev.turtywurty.turtylib.client.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        List<FluidStack> fluids = pBlockEntity.getFluidInventory().getFluidHandler().getFluidTanks().stream()
                .map(MultiDirectionalFluidTank::getFluid).toList();
        if (!fluids.isEmpty()) {
            int fluidIndex = 0;
            float combinedHeight = 0.05f;
            for (FluidStack fluid : fluids) {
                if (fluid.isEmpty()) continue;

                IClientFluidTypeExtensions fluidTypeExtensions = ClientUtils.getClientFluidExtensions(fluid);
                TextureAtlasSprite sprite = ClientUtils.getBlock(fluidTypeExtensions.getStillTexture(fluid));

                int color = fluidTypeExtensions.getTintColor(fluid);

                BakedModel bakedModel = Minecraft.getInstance().getModelManager()
                        .getModel(new ResourceLocation(Industria.MODID, "block/agitator_fluid"));
                BakedModel replacedModel = new ReplacedTextureColorModel(bakedModel, sprite, color,
                        pBlockEntity.getLevel().random);

                RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluid.getFluid().defaultFluidState());

                pPoseStack.pushPose();
                final double maxHeight = 0.25;
                int amount = fluid.getAmount();
                int capacity = pBlockEntity.getFluidInventory().getFluidHandler().getTankCapacity(fluidIndex);
                double ratio = (double) amount / (double) capacity;
                pPoseStack.scale(1.0F, (float) ratio, 1.0F);

                double height = maxHeight * ratio;
                combinedHeight += height;

                pPoseStack.translate(0, combinedHeight, 0);
                renderTintedModel(pPoseStack.last(), pBufferSource.getBuffer(renderType), null, replacedModel,
                        1.0F,
                        1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
                pPoseStack.popPose();
            }
        }

        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.343745, 0.5);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);


        if (pBlockEntity.getProgress() > 0) {
            this.model.getParts().rotor().yRot += 0.01F;
            this.model.getParts().rotor().translateAndRotate(pPoseStack);
        } else {
            float yRot = this.model.getParts().rotor().yRot;
            if (yRot != 0) {
                this.model.getParts().rotor().yRot = Mth.lerp(yRot, 360, 0);
                this.model.getParts().rotor().translateAndRotate(pPoseStack);
            }
        }

        this.model.getParts().rotor()
                .render(pPoseStack, pBufferSource.getBuffer(this.model.renderType(TEXTURE)), pPackedLight,
                        pPackedOverlay);
        pPoseStack.popPose();
    }

    private static void renderTintedModel(PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, float redMul, float greenMul, float blueMul, float alphaMul, float pRed, float pGreen, float pBlue, int pPackedLight, int pPackedOverlay, ModelData modelData, RenderType renderType) {
        var randomsource = RandomSource.create();
        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(pPose, pConsumer, redMul, greenMul, blueMul, alphaMul, pRed, pGreen, pBlue,
                    pModel.getQuads(pState, direction, randomsource, modelData, renderType), pPackedLight,
                    pPackedOverlay);
        }

        randomsource.setSeed(42L);
        renderQuadList(pPose, pConsumer, redMul, greenMul, blueMul, alphaMul, pRed, pGreen, pBlue,
                pModel.getQuads(pState, null, randomsource, modelData, renderType), pPackedLight, pPackedOverlay);
    }

    private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float redMul, float greenMul, float blueMul, float alphaMul, float pRed, float pGreen, float pBlue, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
        for (BakedQuad quad : pQuads) {
            float red;
            float green;
            float blue;
            if (quad.isTinted()) {
                red = Mth.clamp(pRed, 0.0F, 1.0F);
                green = Mth.clamp(pGreen, 0.0F, 1.0F);
                blue = Mth.clamp(pBlue, 0.0F, 1.0F);
            } else {
                red = 1.0F;
                green = 1.0F;
                blue = 1.0F;
            }

            pConsumer.putBulkData(pPose, quad, new float[]{redMul, greenMul, blueMul, alphaMul}, red, green, blue,
                    new int[]{pPackedLight, pPackedLight, pPackedLight, pPackedLight}, pPackedOverlay, true);
        }
    }
}
