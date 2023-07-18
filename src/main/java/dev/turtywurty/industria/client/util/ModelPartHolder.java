package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ModelPartHolder {
    private static final Vector3d ZERO_VECTOR = new Vector3d(0, 0, 0);
    private static final Vector3f ZERO_VECTOR_F = new Vector3f(0, 0, 0);
    private static final Quaternionf ZERO_QUATERNION = new Quaternionf(0, 0, 0, 1);

    private final ModelPart part;
    private final Vector3d position = ZERO_VECTOR;
    private final Vector3f scale = ZERO_VECTOR_F;
    private final Quaternionf rotation = ZERO_QUATERNION;

    public ModelPartHolder(ModelPart part) {
        this.part = part;
    }

    public Vector3d translate(PoseStack poseStack, double x, double y, double z) {
        poseStack.translate(x, y, z);
        add(this.position, x, y, z);
        return this.position;
    }

    public Vector3f scale(PoseStack poseStack, float x, float y, float z) {
        poseStack.scale(x, y, z);
        add(this.scale, x, y, z);
        return this.scale;
    }

    public Quaternionf rotate(PoseStack poseStack, Axis axis, float angle, boolean radians) {
        Quaternionf rotation = radians ? axis.rotation(angle) : axis.rotationDegrees(angle);
        poseStack.mulPose(rotation);
        this.rotation.mul(rotation);
        return this.rotation;
    }

    public Quaternionf rotate(PoseStack poseStack, Axis axis, float angle) {
        return rotate(poseStack, axis, angle, false);
    }

    public void copyFrom(ModelPartHolder other, PoseStack poseStack) {
        poseStack.translate(-this.position.x, -this.position.y, -this.position.z);
        poseStack.scale(1 / this.scale.x(), 1 / this.scale.y(), 1 / this.scale.z());
        poseStack.mulPose(inverseQuaternion(this.rotation));

        this.position.set(other.position);
        this.scale.set(other.scale.x(), other.scale.y(), other.scale.z());
        this.rotation.set(other.rotation.x(), other.rotation.y(), other.rotation.z(), other.rotation.w());

        poseStack.translate(this.position.x, this.position.y, this.position.z);
        poseStack.scale(this.scale.x(), this.scale.y(), this.scale.z());
        poseStack.mulPose(this.rotation);
    }

    private static Quaternionf inverseQuaternion(Quaternionf quaternion) {
        Quaternionf inverseRotation = new Quaternionf(quaternion);
        inverseRotation.conjugate();
        return inverseRotation;
    }

    private static Vector3d add(Vector3d vector, double x, double y, double z) {
        vector.set(vector.x + x, vector.y + y, vector.z + z);
        return vector;
    }

    private static Vector3f add(Vector3f vector, float x, float y, float z) {
        vector.set(vector.x() + x, vector.y() + y, vector.z() + z);
        return vector;
    }
}