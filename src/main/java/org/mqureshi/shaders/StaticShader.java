package org.mqureshi.shaders;

import org.joml.Matrix4f;
import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Light;
import org.mqureshi.toolbox.Maths;

public class StaticShader extends ShaderProgram{

    private static final String vertexFile = "/shaders/vertexShader.glsl";
    private static final String fragmentFile = "/shaders/fragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColour;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_time;
    private int location_speed;
    private int location_frequency;
    private int location_swayStrength;
    private int location_swayOffset;
    private int location_useFakeWind;

    public StaticShader() {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocation() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_useFakeWind = super.getUniformLocation("useFakeWind");
        location_time = super.getUniformLocation("time");
        location_speed = super.getUniformLocation("speed");
        location_frequency = super.getUniformLocation("frequency");
        location_swayStrength = super.getUniformLocation("swayStrength");
        location_swayOffset = super.getUniformLocation("swayOffset");
    }

    public void loadTime(float time) {
        super.loadFloat(location_time, time);
    }

    public void loadSwayParameters(float speed, float frequency, float swayStrength) {
        super.loadFloat(location_speed, speed);
        super.loadFloat(location_frequency, frequency);
        super.loadFloat(location_swayStrength, swayStrength);
    }

    public void loadSwayOffset(float offset) {
        super.loadFloat(location_swayOffset, offset);
    }

    public void loadFakeSway(boolean useSway){
        super.loadBoolean(location_useFakeWind, useSway);
    }

    public void loadFakeLightingVariable(boolean useFakeLighting) {
        super.loadBoolean(location_useFakeLighting, useFakeLighting);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColour, light.getColour());
    }
}
