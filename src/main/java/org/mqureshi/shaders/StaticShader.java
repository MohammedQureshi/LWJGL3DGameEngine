package org.mqureshi.shaders;

public class StaticShader extends ShaderProgram{

    private static final String vertexFile = "/shaders/vertexShader.glsl";
    private static final String fragmentFile = "/shaders/fragmentShader.glsl";

    public StaticShader() {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
