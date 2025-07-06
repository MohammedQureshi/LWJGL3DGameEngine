package org.mqureshi.renderEngine;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.mqureshi.entities.Entity;
import org.mqureshi.models.RawModel;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.shaders.StaticShader;
import org.mqureshi.textures.ModelTexture;
import org.mqureshi.toolbox.Maths;

public class Renderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    public Renderer(DisplayManager displayManager, StaticShader shader) {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix(displayManager);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0, 0.6f, 1, 1);
    }

    public void render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModel();
        RawModel rawModel = model.getRawModel();

        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        ModelTexture texture = model.getModelTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        shader.loadTransformationMatrix(transformationMatrix);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getModelTexture().getId());

        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
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
