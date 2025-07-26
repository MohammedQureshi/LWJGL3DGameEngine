package org.mqureshi.scene;

import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.renderEngine.Loader;
import org.mqureshi.terrains.Terrain;

import java.util.List;

public abstract class Scene {

    protected List<Entity> entities;
    protected List<Terrain> terrains;
    protected Light light;

    public abstract void init(Loader loader);
    public abstract void update(float deltaTime);
    public abstract List<Entity> getEntities();
    public abstract List<Terrain> getTerrains();
    public abstract Light getLight();
    public abstract void cleanup();

}
