package org.mqureshi.scenes;

import org.joml.Vector3f;
import org.mqureshi.engine.light.ambient.AmbientLight;
import org.mqureshi.engine.light.directional.DirectionalLight;
import org.mqureshi.engine.light.point.PointLight;
import org.mqureshi.engine.light.spot.SpotLight;

import java.util.ArrayList;
import java.util.List;

public class SceneLights {

    private final AmbientLight ambientLight;
    private final DirectionalLight dirLight;
    private final List<PointLight> pointLights;
    private List<SpotLight> spotLights;

    public SceneLights() {
        ambientLight = new AmbientLight();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        dirLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirectionalLight getDirLight() {
        return dirLight;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(List<SpotLight> spotLights) {
        this.spotLights = spotLights;
    }

}
