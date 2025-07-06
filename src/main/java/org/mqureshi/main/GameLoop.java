package org.mqureshi.main;

import org.joml.Vector3f;
import org.mqureshi.entities.Entity;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.renderEngine.DisplayManager;
import org.mqureshi.renderEngine.Loader;
import org.mqureshi.models.RawModel;
import org.mqureshi.renderEngine.Renderer;
import org.mqureshi.shaders.StaticShader;
import org.mqureshi.textures.ModelTexture;

public class GameLoop {

    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager(1280, 720, "Mohammed's Awesome Game");
        displayManager.createDisplay();

        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();

        // Triangle in vertices
        float[] vertices = new float[]{
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        float[] textureCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        RawModel model = loader.loadToVao(vertices, textureCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("textures/image.png"));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Entity entity = new Entity(texturedModel, new Vector3f(-1, 0, 0), 0, 0, 0, 1);

        while (!displayManager.isCloseRequested()) {
            renderer.prepare();
            shader.start();
            renderer.render(entity, shader);
            shader.stop();
            displayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        displayManager.cleanup();
    }

}
