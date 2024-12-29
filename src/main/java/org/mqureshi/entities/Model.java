package org.mqureshi.entities;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private final List<Entity> entityList;
    private final List<Material> materialList;

    public Model(String id, List<Material> materialList) {
        this.id = id;
        entityList = new ArrayList<>();
        this.materialList = materialList;
    }

    public void cleanup() {
        materialList.forEach(Material::cleanup);
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public String getId() {
        return id;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

}
