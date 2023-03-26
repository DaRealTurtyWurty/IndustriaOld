package dev.turtywurty.industria.client.model.baked;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ReplacedTextureColorModel implements BakedModel {
    private final BakedModel model;
    private final List<BakedQuad> generalQuads;
    private final Map<Direction, List<BakedQuad>> sideQuads = new EnumMap<>(Direction.class);

    public ReplacedTextureColorModel(BakedModel model, TextureAtlasSprite sprite, int color, RandomSource random) {
        this.model = model;

        this.generalQuads = this.model.getQuads(null, null, random);
        Arrays.stream(Direction.values())
                .forEach(direction -> this.sideQuads.put(direction, this.model.getQuads(null, direction, random)));

        for (int quadIndex = 0; quadIndex < this.generalQuads.size(); quadIndex++) {
            BakedQuad quad = this.generalQuads.get(quadIndex);
            this.generalQuads.set(quadIndex, replaceUVColor(sprite, color,
                    new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(),
                            quad.isShade(), quad.hasAmbientOcclusion())));
        }

        for (List<BakedQuad> quadSides : this.sideQuads.values()) {
            for (int quadIndex = 0; quadIndex < quadSides.size(); quadIndex++) {
                BakedQuad quad = quadSides.get(quadIndex);
                quadSides.set(quadIndex, replaceUVColor(sprite, color,
                        new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(),
                                quad.isShade(), quad.hasAmbientOcclusion())));
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
        return pDirection == null ? this.generalQuads : this.sideQuads.get(pDirection);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.model.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.model.getOverrides();
    }

    private static BakedQuad replaceUVColor(TextureAtlasSprite sprite, int color, BakedQuad oldQuad) {
        if (sprite.equals(oldQuad.getSprite())) return oldQuad;

        int[] vertexData = oldQuad.getVertices();
        int vertexCount = vertexData.length / 8;

        TextureAtlasSprite oldSprite = oldQuad.getSprite();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            int uOffset = vertex * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
            int vOffset = uOffset + 1;

            float u = Float.intBitsToFloat(vertexData[uOffset]);
            float v = Float.intBitsToFloat(vertexData[vOffset]);
            float unscaledU = u - oldSprite.getU0();
            float unscaledV = v - oldSprite.getV0();
            vertexData[uOffset] = Float.floatToIntBits(sprite.getU0() + unscaledU);
            vertexData[vOffset] = Float.floatToIntBits(sprite.getV0() + unscaledV);

            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;
            int alpha = (color >> 24) & 0xFF;

            int newColor = (alpha << 24) | (blue << 16) | (green << 8) | red;

            int colorOffset = vertex * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
            vertexData[colorOffset] = newColor == -1 ? 0xFFFFFFFF : newColor;
        }

        return new BakedQuad(vertexData, oldQuad.getTintIndex(), oldQuad.getDirection(), sprite, oldQuad.isShade(),
                oldQuad.hasAmbientOcclusion());
    }
}
