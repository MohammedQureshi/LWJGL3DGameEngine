package org.mqureshi.main;

import org.joml.Vector3f;
import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.renderEngine.DisplayManager;
import org.mqureshi.renderEngine.Loader;
import org.mqureshi.models.RawModel;
import org.mqureshi.renderEngine.ObjLoader;
import org.mqureshi.renderEngine.Renderer;
import org.mqureshi.shaders.StaticShader;
import org.mqureshi.textures.ModelTexture;

public class GameLoop {

    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager(1280, 720, "Mohammed's Awesome Game");
        displayManager.createDisplay();

        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(displayManager, shader);

        RawModel model = ObjLoader.loadObjModel("/assets/dragon/dragon.obj", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("textures/image.png"));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -50), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
        Camera camera = new Camera(displayManager.getWindowHandle());

        while (!displayManager.isCloseRequested()) {
            entity.increaseRotation(0, 0.1f, 0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity, shader);
            shader.stop();
            displayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        displayManager.cleanup();
    }

}
