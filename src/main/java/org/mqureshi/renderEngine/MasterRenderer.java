package org.mqureshi.renderEngine;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.shaders.StaticShader;
import org.mqureshi.shaders.TerrainShader;
import org.mqureshi.terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private final StaticShader shader = new StaticShader();
    private final EntityRenderer entityRenderer;
    private final TerrainRenderer terrainRenderer;
    private final TerrainShader terrainShader = new TerrainShader();

    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private final List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer(DisplayManager displayManager) {
        enableCulling();
        createProjectionMatrix(displayManager);
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(Light sun, Camera camera) {
        prepare();
        float time = (float) GLFW.glfwGetTime();
        shader.start();
        shader.loadTime(time);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        entities.computeIfAbsent(entityModel, k -> new ArrayList<>()).add(entity);
    }

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0, 0.6f, 1, 1);
    }

    private void createProjectionMatrix(DisplayManager displayManager) {
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(displayManager.getWindowHandle(), width, height);

        float aspectRatio = (float) width[0] / (float) height[0];

        projectionMatrix = new Matrix4f().setPerspective(
                (float) Math.toRadians(FOV),
                aspectRatio,
                NEAR_PLANE,
                FAR_PLANE
        );
    }
}
