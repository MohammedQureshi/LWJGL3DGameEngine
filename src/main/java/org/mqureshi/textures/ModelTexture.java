package org.mqureshi.textures;

public class ModelTexture {

    private final int textureId;

    private float shineDamper = 1;
    private float reflectivity = 0;

    public ModelTexture(int id) {
        this.textureId = id;
    }

    public int getId() {
        return this.textureId;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }
}
