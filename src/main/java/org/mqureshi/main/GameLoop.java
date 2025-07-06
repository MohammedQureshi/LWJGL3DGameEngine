package org.mqureshi.main;

import org.joml.Random;
import org.joml.Vector3f;
import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.renderEngine.*;
import org.mqureshi.models.RawModel;
import org.mqureshi.terrains.Terrain;
import org.mqureshi.textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class GameLoop {

    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager(1280, 720, "Mohammed's Awesome Game");
        displayManager.createDisplay();
        Loader loader = new Loader();

        RawModel model = ObjLoader.loadObjModel("assets/tree/tree.obj", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("assets/tree/tree.png"));
        TexturedModel staticModel  = new TexturedModel(model, texture);

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<500;i++){
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
        }


        Light light = new Light(new Vector3f(3000, 2000, 20), new Vector3f(1, 1, 1));

        Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("textures/grass.png")));
        Terrain terrain2 = new Terrain(-1, 0, loader, new ModelTexture(loader.loadTexture("textures/grass.png")));

        Camera camera = new Camera(displayManager.getWindowHandle());
        MasterRenderer renderer = new MasterRenderer(displayManager);

        while (!displayManager.isCloseRequested()) {
            camera.move();

            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);

            for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            displayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        displayManager.cleanup();
    }

}
