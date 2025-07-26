package org.mqureshi.scene;

import org.joml.Random;
import org.joml.Vector3f;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.models.RawModel;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.renderEngine.Loader;
import org.mqureshi.renderEngine.ObjLoader;
import org.mqureshi.terrains.Terrain;
import org.mqureshi.textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class DesertScene extends Scene {

    @Override
    public void init(Loader loader) {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();

        RawModel treeModel = ObjLoader.loadObjModel("assets/tree/tree.obj", loader);
        ModelTexture treeTexture = new ModelTexture(loader.loadTexture("assets/tree/tree.png"));
        TexturedModel tree = new TexturedModel(treeModel, treeTexture);
        tree.getModelTexture().setHasSway(true);
        tree.getModelTexture().setSwayProperties(7.4f, 0.1f, 0.05f);

        RawModel grassModel = ObjLoader.loadObjModel("assets/grass/grassModel.obj", loader);
        ModelTexture grassTexture = new ModelTexture(loader.loadTexture("assets/grass/grassTexture.png"));
        TexturedModel grass = new TexturedModel(grassModel, grassTexture);
        grass.getModelTexture().setHasTransparency(true);
        grass.getModelTexture().setUseFakeLighting(true);
        grass.getModelTexture().setHasSway(true);
        grass.getModelTexture().setSwayProperties(2.0f, 0.5f, 0.2f);

        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            entities.add(new Entity(tree, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 3));
        }
        for (int i = 0; i < 2000; i++) {
            entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 3));
        }

        terrains.add(new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("textures/sand.png"))));
        terrains.add(new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("textures/sand.png"))));

        light = new Light(new Vector3f(3000, 2000, 20), new Vector3f(1, 1, 1));
    }

    @Override
    public void update(float deltaTime) {
        // Scene-specific updates (animations, weather, spawning, etc.)
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public List<Terrain> getTerrains() {
        return terrains;
    }

    @Override
    public Light getLight() {
        return light;
    }

    @Override
    public void cleanup() {
        entities.clear();
        terrains.clear();
    }
}
