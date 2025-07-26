package org.mqureshi.scene;

import org.mqureshi.renderEngine.Loader;

public class SceneManager {
    private Scene currentScene;

    public void setScene(Scene newScene, Loader loader) {
        if (currentScene != null) {
            currentScene.cleanup();
        }
        currentScene = newScene;
        currentScene.init(loader);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }
}

