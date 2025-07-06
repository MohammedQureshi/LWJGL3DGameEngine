package org.mqureshi.toolbox;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.mqureshi.entities.Camera;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        return new Matrix4f()
                .translate(translation)
                .rotateX((float) Math.toRadians(rx))
                .rotateY((float) Math.toRadians(ry))
                .rotateZ((float) Math.toRadians(rz))
                .scale(scale);
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f().identity()
                .rotateX((float) Math.toRadians(camera.getPitch()))
                .rotateY((float) Math.toRadians(camera.getYaw()))
                .rotateZ((float) Math.toRadians(camera.getRoll()));

        Vector3f pos = camera.getPosition();
        viewMatrix.translate(-pos.x, -pos.y, -pos.z);

        return viewMatrix;
    }

}
