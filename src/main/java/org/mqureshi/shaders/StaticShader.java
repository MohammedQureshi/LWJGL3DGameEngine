package org.mqureshi.shaders;

import org.joml.Matrix4f;

public class StaticShader extends ShaderProgram{

    private static final String vertexFile = "/shaders/vertexShader.glsl";
    private static final String fragmentFile = "/shaders/fragmentShader.glsl";

    private int location_transformationMatrix;

    public StaticShader() {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocation() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
}
