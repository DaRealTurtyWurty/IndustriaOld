package dev.turtywurty.industria.client.blockentityrenderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.AgitatorBlockEntity;
import dev.turtywurty.industria.blockentity.util.directional.MultiDirectionalFluidTank;
import dev.turtywurty.industria.client.model.AgitatorModel;
import dev.turtywurty.industria.client.model.baked.ReplacedTextureColorModel;
import dev.turtywurty.industria.client.rendertypes.AgitatorRenderType;
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
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.List;

public class AgitatorBlockEntityRenderer implements BlockEntityRenderer<AgitatorBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/blocks/agitator.png");

    private final BlockEntityRendererProvider.Context context;
    private final AgitatorModel model;
    private final BakedModel bakedModel = Minecraft.getInstance().getModelManager()
            .getModel(new ResourceLocation(Industria.MODID, "block/agitator_fluid"));

    public AgitatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.model = new AgitatorModel(this.context.bakeLayer(AgitatorModel.LAYER_LOCATION));
    }

    @Override
    public void render(AgitatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        List<FluidStack> fluids = pBlockEntity.getFluidInventory().getFluidHandler().getFluidTanks().stream()
                .map(MultiDirectionalFluidTank::getFluid).filter(fluid -> !fluid.isEmpty())
                .sorted(Comparator.comparingInt(FluidStack::getAmount).reversed()).toList();

        if (!fluids.isEmpty()) {
            int fluidIndex = 0;
            for (FluidStack fluid : fluids) {
                if (fluid.isEmpty()) continue;

                IClientFluidTypeExtensions fluidTypeExtensions = ClientUtils.getClientFluidExtensions(fluid);

                TextureAtlasSprite sprite = ClientUtils.getBlock(fluidTypeExtensions.getStillTexture(fluid));
                int color = fluidTypeExtensions.getTintColor(fluid);

                BakedModel replacedModel = new ReplacedTextureColorModel(this.bakedModel, sprite, color,
                        pBlockEntity.getLevel().random);

                RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluid.getFluid().defaultFluidState());

                float maxHeight = 0.25f;
                float amount = fluid.getAmount();
                float capacity = pBlockEntity.getFluidInventory().getFluidHandler().getTankCapacity(fluidIndex);
                System.out.println("Amount: " + amount + " Capacity: " + capacity);
                float ratio = amount / capacity;
                float height = maxHeight * ratio;

                pPoseStack.pushPose();
                pPoseStack.scale(1.0F, ratio, 1.0F);
                pPoseStack.translate(0, 0.05 + fluidIndex++ * height, 0);

                if (pBlockEntity.getProgress() <= 0) {
                    ClientUtils.renderTintedModel(pPoseStack.last(), pBufferSource.getBuffer(renderType), null,
                            replacedModel, 1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
                } else {
                    float progress = pBlockEntity.getProgress();
                    float maxProgress = pBlockEntity.getMaxProgress() / 2f;
                    float blendFactor = progress / maxProgress;

                    // get the other 2 fluid stacks if they exist
                    List<ResourceLocation> textures = fluids.stream()
                            .map(fluidStack -> ClientUtils.getClientFluidExtensions(fluidStack)
                                    .getStillTexture(fluidStack)).toList();

                    ResourceLocation first = textures.size() > 0 ? textures.get(0) : null;
                    ResourceLocation second = textures.size() > 1 ? textures.get(1) : null;

                    renderTexturedModel(pPoseStack, pBufferSource, replacedModel, pPackedLight, pPackedOverlay, first,
                            second, blendFactor);
                }

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

    private static void renderTexturedModel(PoseStack poseStack, MultiBufferSource bufferSource, BakedModel model, int packedLight, int packedOverlay, ResourceLocation texture3, ResourceLocation texture4, float blendFactor) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(AgitatorRenderType.RENDER_TYPE);

        RenderSystem.setShaderTexture(3, texture3);
        RenderSystem.setShaderTexture(4, texture4);

        renderTintedModel(poseStack.last(), vertexConsumer, null, model, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay,
                ModelData.EMPTY, AgitatorRenderType.RENDER_TYPE, blendFactor);
    }

    private static void renderTintedModel(PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, float pRed, float pGreen, float pBlue, int pPackedLight, int pPackedOverlay, ModelData modelData, RenderType renderType, float blendFactor) {
        var randomsource = RandomSource.create();
        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue,
                    pModel.getQuads(pState, direction, randomsource, modelData, renderType), pPackedLight,
                    pPackedOverlay, blendFactor);
        }

        randomsource.setSeed(42L);
        renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue,
                pModel.getQuads(pState, null, randomsource, modelData, renderType), pPackedLight, pPackedOverlay,
                blendFactor);
    }

    private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay, float blendFactor) {
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

            putBulkData(pPose, pConsumer, quad, red, green, blue, pPackedLight, pPackedOverlay, blendFactor);
        }
    }

    private static void putBulkData(PoseStack.Pose pPose, VertexConsumer pConsumer, BakedQuad quad, float red, float green, float blue, int pPackedLight, int pPackedOverlay, float blendFactor) {
        if (pConsumer instanceof BufferVertexConsumer bufferVertexConsumer) {
            int[] lighting = new int[]{pPackedLight, pPackedLight, pPackedLight, pPackedLight};
            int[] vertices = quad.getVertices();
            Vec3i quadNormal = quad.getDirection().getNormal();
            Vector3f normals = new Vector3f((float) quadNormal.getX(), (float) quadNormal.getY(),
                    (float) quadNormal.getZ());
            Matrix4f poseMatrix = pPose.pose();
            normals.transform(pPose.normal());
            int vertexCount = vertices.length / 8;
            MemoryStack memoryStack = MemoryStack.stackPush();

            try {
                ByteBuffer buffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
                IntBuffer intBuffer = buffer.asIntBuffer();

                for (int vertex = 0; vertex < vertexCount; ++vertex) {
                    intBuffer.clear();
                    intBuffer.put(vertices, vertex * 8, 8);
                    float x = buffer.getFloat(0);
                    float y = buffer.getFloat(4);
                    float z = buffer.getFloat(8);

                    float bufRed = (float) (buffer.get(12) & 255) / 255.0F;
                    float bufGreen = (float) (buffer.get(13) & 255) / 255.0F;
                    float bufBlue = (float) (buffer.get(14) & 255) / 255.0F;
                    float vertexRed = bufRed * red;
                    float vertexGreen = bufGreen * green;
                    float vertexBlue = bufBlue * blue;
                    float vertexAlpha = (float) (buffer.get(15) & 255) / 255.0F;

                    int light = bufferVertexConsumer.applyBakedLighting(lighting[vertex], buffer);
                    float u = buffer.getFloat(16);
                    float v = buffer.getFloat(20);

                    Vector4f positions = new Vector4f(x, y, z, 1.0F);
                    positions.transform(poseMatrix);
                    bufferVertexConsumer.applyBakedNormals(normals, buffer, pPose.normal());

                    bufferVertexConsumer.vertex(positions.x(), positions.y(), positions.z())
                            .color(vertexRed, vertexGreen, vertexBlue, vertexAlpha).uv(u, v)
                            .overlayCoords(pPackedOverlay).uv2(light).normal(normals.x(), normals.y(), normals.z());

                    bufferVertexConsumer.putFloat(0, blendFactor);
                    bufferVertexConsumer.nextElement();
                    bufferVertexConsumer.endVertex();
                }
            } catch (Throwable throwable1) {
                try {
                    memoryStack.close();
                } catch (Throwable throwable) {
                    throwable1.addSuppressed(throwable);
                }

                throw throwable1;
            }

            memoryStack.close();
        }
    }

    private record RenderData(BakedModel model, IClientFluidTypeExtensions fluidTypeExtensions, RenderType renderType) {
    }
}
