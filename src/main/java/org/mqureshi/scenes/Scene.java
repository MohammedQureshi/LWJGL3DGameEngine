package org.mqureshi.scenes;

import org.mqureshi.engine.skybox.Skybox;
import org.mqureshi.engine.util.Projection;
import org.mqureshi.engine.texture.TextureCache;
import org.mqureshi.entities.Camera;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Model;
import org.mqureshi.engine.fog.Fog;
import org.mqureshi.engine.gui.IGuiInstance;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private final Map<String, Model> modelMap;
    private final Projection projection;
    private final TextureCache textureCache;
    private final Camera camera;
    private IGuiInstance guiInstance;
    private SceneLights sceneLights;
    private Skybox skybox;
    private Fog fog;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
        fog = new Fog();
    }

    public Camera getCamera() {
        return camera;
    }

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntityList().add(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Projection getProjection() {
        return projection;
    }

    public void resize(int width, int height) {
        projection.updateProjectionMatrix(width, height);
    }

    public SceneLights getSceneLights() {
        return sceneLights;
    }

    public void setSceneLights(SceneLights sceneLights) {
        this.sceneLights = sceneLights;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

}
