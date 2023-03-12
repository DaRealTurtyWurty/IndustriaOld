package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockButtonIcon implements RenderableButtonIcon {
    private final SimpleButton self;
    private final int x, y;
    private final BlockState state;

    private BlockButtonIcon(SimpleButton self, int x, int y, BlockState state) {
        this.self = self;
        this.x = x;
        this.y = y;
        this.state = state;
    }

    @Override
    public void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks) {
        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(this.state, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(),
                        15728880, OverlayTexture.NO_OVERLAY);
    }

    public static class Builder {
        private final SimpleButton self;
        private int x, y;
        private BlockState state;

        public Builder(SimpleButton self) {
            this.self = self;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder state(BlockState state) {
            this.state = state;
            return this;
        }

        public Builder state(Block block) {
            this.state = block.defaultBlockState();
            return this;
        }

        public BlockButtonIcon build() {
            return new BlockButtonIcon(this.self, this.x, this.y, this.state);
        }
    }
}
