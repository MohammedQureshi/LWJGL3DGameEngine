package org.mqureshi.engine.fog;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.mqureshi.control.MouseInput;
import org.mqureshi.engine.util.Window;
import org.mqureshi.engine.gui.IGuiInstance;
import org.mqureshi.scenes.Scene;

public class FogControls implements IGuiInstance {
    private final Scene scene;
    private final float[] fogColor = new float[3]; // Array for ImGui

    public FogControls(Scene scene) {
        this.scene = scene;
        Vector3f initialColor = scene.getFog().getColor();
        fogColor[0] = initialColor.x;
        fogColor[1] = initialColor.y;
        fogColor[2] = initialColor.z;
    }

    @Override
    public void drawGui() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(450, 400);
        ImGui.begin("Fog colour controls");
        ImGui.begin("Fog Controls", ImGuiCond.Always);
        if (ImGui.colorEdit3("Fog Color", fogColor)) {
            float r = Math.max(0.0f, Math.min(1.0f, fogColor[0]));
            float g = Math.max(0.0f, Math.min(1.0f, fogColor[1]));
            float b = Math.max(0.0f, Math.min(1.0f, fogColor[2]));
            System.out.printf("scene.setFog(new Fog(true, new Vector3f(%.3ff, %.3ff, %.3ff), 0.50f));%n", r, g, b);
            scene.setFog(new Fog(true, new Vector3f(r, g, b), 0.50f));
        }
        ImGui.end();

        ImGui.end();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleGuiInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPosition();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        imGuiIO.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());
        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        return consumed;
    }
}
