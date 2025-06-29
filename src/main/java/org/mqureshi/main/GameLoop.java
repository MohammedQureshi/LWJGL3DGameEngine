package org.mqureshi.main;

import org.mqureshi.renderEngine.DisplayManager;
import org.mqureshi.renderEngine.Loader;
import org.mqureshi.renderEngine.RawModel;
import org.mqureshi.renderEngine.Renderer;

public class GameLoop {

    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager(1280, 720, "Mohammed's Awesome Game");
        displayManager.createDisplay();

        Loader loader = new Loader();
        Renderer renderer = new Renderer();

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

        RawModel model = loader.loadToVao(vertices, indices);

        while (!displayManager.isCloseRequested()) {
            renderer.prepare();
            renderer.render(model);
            displayManager.updateDisplay();
        }

        loader.cleanUp();
        displayManager.cleanup();
    }

}
