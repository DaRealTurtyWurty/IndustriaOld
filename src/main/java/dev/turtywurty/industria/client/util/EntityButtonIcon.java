package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityButtonIcon implements RenderableButtonIcon {
    private final SimpleButton self;
    private final Entity entity;
    private final int x, y, width, height;
    private final float rotationSpeed;
    private final Vec3 scale, defaultRotation, offset;

    private float rotation = 135.0F;

    private EntityButtonIcon(SimpleButton self, Entity entity, int x, int y, int width, int height, float rotationSpeed, Vec3 scale, Vec3 defaultRotation, Vec3 offset) {
        this.self = self;
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotationSpeed = rotationSpeed;
        this.scale = scale;
        this.defaultRotation = defaultRotation;
        this.offset = offset;
    }

    @Override
    public void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks) {
        if (this.rotationSpeed != 0.0F) {
            this.rotation += partialTicks * this.rotationSpeed;
        }

        this.entity.tick();
        GuiUtils.renderEntity(poseStack, this.entity,
                this.rotationSpeed != 0.0F ? new Vec3(this.defaultRotation.x(), this.rotation,
                        this.defaultRotation.z()) : this.defaultRotation, this.scale, this.offset, this.x, this.y,
                partialTicks);
    }

    public static class Builder {
        private final SimpleButton self;
        private float rotationSpeed;
        private Vec3 scale = new Vec3(20.0, 20.0, 20.0), defaultRotation = new Vec3(15.0, 135.0,
                0.0), offset = new Vec3(-1.25, -1.75, 0.0);
        private Entity entity;
        private int x, y, width, height;

        public Builder(SimpleButton self) {
            this.self = self;
        }

        public Builder rotationSpeed(float rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
            return this;
        }

        public Builder scale(Vec3 scale) {
            this.scale = scale;
            return this;
        }

        public Builder scale(double x, double y, double z) {
            return scale(new Vec3(x, y, z));
        }

        public Builder defaultRotation(Vec3 defaultRotation) {
            this.defaultRotation = defaultRotation;
            return this;
        }

        public Builder defaultRotation(double x, double y, double z) {
            return defaultRotation(new Vec3(x, y, z));
        }

        public Builder offset(Vec3 offset) {
            this.offset = offset;
            return this;
        }

        public Builder offset(double x, double y, double z) {
            return offset(new Vec3(x, y, z));
        }

        public Builder entity(Entity entity) {
            this.entity = entity;
            return this;
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

        public EntityButtonIcon build() {
            return new EntityButtonIcon(this.self, this.entity, this.x, this.y, this.width, this.height,
                    this.rotationSpeed, this.scale, this.defaultRotation, this.offset);
        }
    }
}
