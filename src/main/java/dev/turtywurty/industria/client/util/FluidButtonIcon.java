package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.client.util.ClientUtils;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidButtonIcon implements RenderableButtonIcon {
    private final SimpleButton self;
    private final int x, y, width, height;
    private final boolean renderIn3d;
    private final FluidStack fluid;
    private final IClientFluidTypeExtensions fluidTypeExtensions;

    public FluidButtonIcon(SimpleButton self, int x, int y, int width, int height, boolean renderIn3d, FluidStack fluid) {
        this.self = self;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.renderIn3d = renderIn3d;
        this.fluid = fluid;
        this.fluidTypeExtensions = ClientUtils.getClientFluidExtensions(fluid);
    }

    @Override
    public void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks) {
        if (this.renderIn3d) {
            Minecraft.getInstance().getBlockRenderer()
                    .renderSingleBlock(this.fluid.getFluid().defaultFluidState().createLegacyBlock(), poseStack,
                            Minecraft.getInstance().renderBuffers().bufferSource(), 15728880,
                            OverlayTexture.NO_OVERLAY);
        } else {
            GuiUtils.renderFluid(poseStack, this.fluid, this.fluidTypeExtensions, false, this.x, this.y, this.width,
                    this.height);
        }
    }

    public static class Builder {
        private final SimpleButton self;

        private int x, y, width, height;
        private boolean renderIn3d;
        private FluidStack fluid;

        public Builder(SimpleButton self) {
            this.self = self;
            this.x = self.getX();
            this.y = self.getY();
            this.width = self.getWidth();
            this.height = self.getHeight();
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder renderIn3d() {
            this.renderIn3d = true;
            return this;
        }

        public Builder fluid(FluidStack fluidStack) {
            this.fluid = fluidStack;
            return this;
        }

        public Builder fluid(Fluid fluid) {
            this.fluid = new FluidStack(fluid, 1000);
            return this;
        }

        public FluidButtonIcon build() {
            return new FluidButtonIcon(this.self, this.x, this.y, this.width, this.height, this.renderIn3d, this.fluid);
        }
    }
}
