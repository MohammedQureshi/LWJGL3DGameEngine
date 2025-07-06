package org.mqureshi.main;

import org.joml.Random;
import org.joml.Vector3f;
import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.renderEngine.*;
import org.mqureshi.models.RawModel;
import org.mqureshi.textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class GameLoop {

    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager(1280, 720, "Mohammed's Awesome Game");
        displayManager.createDisplay();
        Loader loader = new Loader();

        RawModel model = ObjLoader.loadObjModel("assets/box/box.obj", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("textures/image.png"));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Light light = new Light(new Vector3f(3000, 2000, 20), new Vector3f(1, 1, 1));
        Camera camera = new Camera(displayManager.getWindowHandle());

        List<Entity> allCubes = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 200; i++) {
            float x = random.nextFloat() * 100 - 50;
            float y = random.nextFloat() * 100 - 50;
            float z = random.nextFloat() * -300;
            allCubes.add(new Entity(texturedModel, new Vector3f(x, y, z), random.nextFloat() * 180f, random.nextFloat() * 180f, 0f, 1f));
        }

        MasterRenderer renderer = new MasterRenderer(displayManager);

        while (!displayManager.isCloseRequested()) {
            camera.move();
            for (Entity cube : allCubes) {
                renderer.processEntity(cube);
            }
            renderer.render(light, camera);
            displayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        displayManager.cleanup();
    }

}
