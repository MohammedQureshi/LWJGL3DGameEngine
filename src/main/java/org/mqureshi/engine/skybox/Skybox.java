package org.mqureshi.engine.skybox;

import org.mqureshi.engine.model.ModelLoader;
import org.mqureshi.engine.texture.TextureCache;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Model;

public class Skybox {

    private final Entity skyBoxEntity;
    private final Model skyBoxModel;

    public Skybox(String skyBoxModelPath, TextureCache textureCache) {
        skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache);
        skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel.getId());
    }

    public Entity getSkyBoxEntity() {
        return skyBoxEntity;
    }

    public Model getSkyBoxModel() {
        return skyBoxModel;
    }

}
