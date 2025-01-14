package org.mqureshi.engine.interfaces;

import org.mqureshi.engine.util.Render;
import org.mqureshi.engine.util.Window;
import org.mqureshi.scenes.Scene;

public interface GameLogicInterface {

    void cleanup();

    void init(Window window, Scene scene, Render render);

    void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed);

    void update(Window window, Scene scene, long diffTimeMillis);

}
