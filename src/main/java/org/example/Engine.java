package org.example;

public class Engine {

    public static final int TARGET_UPS = 30;
    private final GameLogicInterface gameLogic;
    private final Window window;
    private final Render render;
    private boolean running;
    private final Scene scene;
    private final int targetFps;
    private final int targetUps;

    public Engine(String windowTitle, Window.WindowOptions options, GameLogicInterface gameLogic) {
        window = new Window(windowTitle, options, () -> {
            resize();
            return null;
        });
        targetFps = options.fps;
        targetUps = options.ups;
        this.gameLogic = gameLogic;
        render = new Render();
        scene = new Scene();
        gameLogic.init(window, scene, render);
        running = true;
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        //Time it took to start
        float timeU = 1000f / targetUps;
        //Time it took to render
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0f;
        float deltaUpdate = 0;
        float deltaFps = 0;

        long updateTime = initialTime;
        while(running && !window.windowShouldClose()) {
            window.pollEvents();

            long now = System.currentTimeMillis();
            deltaUpdate += (now - initialTime) / timeU;
            deltaFps += (now - initialTime) / timeR;

            if(targetFps <= 0 || deltaFps >= 1) {
                gameLogic.input(window, scene, now - initialTime);
            }

            if (deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                gameLogic.update(window, scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            if (targetFps <=0 || deltaFps >= 1) {
                render.render(window, scene);
                deltaFps--;
                window.update();
            }
            initialTime = now;
        }
        cleanup();
    }

    public void start() {
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }

    private void cleanup() {
        gameLogic.cleanup();
        render.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    private void resize() {

    }

}
