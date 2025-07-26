package org.mqureshi.textures;

public class ModelTexture {

    private final int textureId;

    private float shineDamper = 1;
    private float reflectivity = 0;
    private float swaySpeed;
    private float swayFrequency;
    private float swayStrength;

    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;
    private boolean hasSway = false;

    public ModelTexture(int id) {
        this.textureId = id;
    }

    public int getId() {
        return this.textureId;
    }

    public float getSwaySpeed() {
        return swaySpeed;
    }

    public float getSwayFrequency() {
        return swayFrequency;
    }

    public float getSwayStrength() {
        return swayStrength;
    }

    public void setSwayProperties(float swaySpeed, float swayFrequency, float swayStrength) {
        this.swaySpeed = swaySpeed;
        this.swayFrequency = swayFrequency;
        this.swayStrength = swayStrength;
    }

    public boolean isHasSway() {
        return hasSway;
    }

    public void setHasSway(boolean hasSway) {
        this.hasSway = hasSway;
    }

    public boolean isUseFakeLighting() {
        return useFakeLighting;
    }

    public void setUseFakeLighting(boolean useFakeLighting) {
        this.useFakeLighting = useFakeLighting;
    }

    public boolean isHasTransparency() {
        return hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
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
