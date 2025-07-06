package org.mqureshi.renderEngine;

import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Light;
import org.mqureshi.models.TexturedModel;
import org.mqureshi.shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private final StaticShader shader = new StaticShader();
    private final Renderer renderer;
    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public MasterRenderer(DisplayManager displayManager) {
        this.renderer = new Renderer(displayManager, shader);
    }

    public void render(Light sun, Camera camera) {
        renderer.prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        entities.computeIfAbsent(entityModel, _ -> new ArrayList<>()).add(entity);
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
