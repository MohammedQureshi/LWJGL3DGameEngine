package org.mqureshi.toolbox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        return new Matrix4f()
                .translate(translation)
                .rotateX((float) Math.toRadians(rx))
                .rotateY((float) Math.toRadians(ry))
                .rotateZ((float) Math.toRadians(rz))
                .scale(scale);
    }
}
