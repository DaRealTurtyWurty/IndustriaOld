package dev.turtywurty.industria.client.rendertypes;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import dev.turtywurty.industria.init.ShaderInit;
import net.minecraft.client.renderer.RenderType;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

public class AgitatorRenderType extends RenderType {
    public static final VertexFormatElement ELEMENT_BLEND_FACTOR = new VertexFormatElement(0,
            VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1);

    public static final VertexFormat VERTEX_FORMAT = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION)
                    .put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2)
                    .put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING)
                    .put("BlendFactor", ELEMENT_BLEND_FACTOR).build());

    private AgitatorRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    private static RenderType.CompositeState getCompositeState() {
        return RenderType.CompositeState.builder().setLightmapState(RenderType.LIGHTMAP)
                .setShaderState(ShaderInit.AGITATOR_SHADER.getShard()).setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET)
                .createCompositeState(true);
    }

    public static final RenderType RENDER_TYPE = create("agitator", VERTEX_FORMAT, VertexFormat.Mode.QUADS, 2_097_152,
            true, true, getCompositeState());
}