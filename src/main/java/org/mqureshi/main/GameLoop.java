package org.mqureshi.main;

import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.renderEngine.*;
import org.mqureshi.scene.DesertScene;
import org.mqureshi.scene.ForestScene;
import org.mqureshi.scene.Scene;
import org.mqureshi.scene.SceneManager;
import org.mqureshi.terrains.Terrain;

import static org.lwjgl.glfw.GLFW.*;

public class GameLoop {

    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager(1280, 720, "Mohammed's Awesome Game");
        displayManager.createDisplay();
        Loader loader = new Loader();

        Scene forestScene = new ForestScene();
        Scene desertScene = new DesertScene();
        SceneManager sceneManager = new SceneManager();
        sceneManager.setScene(forestScene, loader);

        Camera camera = new Camera(displayManager.getWindowHandle());
        MasterRenderer renderer = new MasterRenderer(displayManager);

        //TODO: Remove these when FPS counter not needed
        long lastTime = System.currentTimeMillis();
        int frames = 0;

        while (!displayManager.isCloseRequested()) {

            if (glfwGetKey(displayManager.getWindowHandle(), GLFW_KEY_1) == GLFW_PRESS) {
                sceneManager.setScene(desertScene, loader);
            }
//              DEBUGGING TO SWITCH BETWEEN SCENES
            if (glfwGetKey(displayManager.getWindowHandle(), GLFW_KEY_2) == GLFW_PRESS) {
                sceneManager.setScene(forestScene, loader);
            }

            camera.move();

            Scene currentScene = sceneManager.getCurrentScene();

            for (Terrain terrain : currentScene.getTerrains()) {
                renderer.processTerrain(terrain);
            }

            for (Entity entity : currentScene.getEntities()) {
                renderer.processEntity(entity);
            }

            renderer.render(currentScene.getLight(), camera);

            displayManager.updateDisplay();

            //TODO: FPS Counter needs to be removed
            frames++;
            if (System.currentTimeMillis() - lastTime >= 1000) {
                glfwSetWindowTitle(displayManager.getWindowHandle(), "Mohammed's Awesome Game | FPS: " + frames);
                frames = 0;
                lastTime += 1000;
            }

        }
        renderer.cleanUp();
        loader.cleanUp();
        displayManager.cleanup();
    }

}
