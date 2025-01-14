package org.mqureshi.gui;

import org.mqureshi.engine.util.Window;
import org.mqureshi.scenes.Scene;

public interface IGuiInstance {

    void drawGui();

    boolean handleGuiInput(Scene scene, Window window);
}
