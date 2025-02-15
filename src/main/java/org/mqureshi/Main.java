package org.mqureshi;

import io.netty.channel.Channel;
import org.joml.Vector3f;
import org.mqureshi.client.HandleClient;
import org.mqureshi.control.MouseInput;
import org.mqureshi.engine.model.ModelLoader;
import org.mqureshi.engine.interfaces.GameLogicInterface;
import org.mqureshi.engine.skybox.Skybox;
import org.mqureshi.engine.util.Engine;
import org.mqureshi.engine.util.Render;
import org.mqureshi.engine.util.Window;
import org.mqureshi.entities.*;
import org.mqureshi.engine.fog.Fog;
import org.mqureshi.engine.fog.FogControls;
import org.mqureshi.player.Player;
import org.mqureshi.scenes.Scene;
import org.mqureshi.scenes.SceneLights;

import java.net.InetSocketAddress;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements GameLogicInterface {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.001f;
    private static final int NUM_CHUNKS = 1;
    private Entity[][] terrainEntities;

    private Channel udpChannel;
    private InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
    private HandleClient handleClient;
    private boolean isMouseCaptured = false;
    private Player player;

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEngine = new Engine("Lunar Realms", new Window.WindowOptions(), main);
        gameEngine.start();
    }

    @Override
    public void cleanup() {
        if (handleClient != null) {
            handleClient.stopClient();  // Cleanup both UDP channel and EventLoopGroup
        }
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        // Initialize the game scene
        initializeGameScene(scene);

        // Start the UDP client in a separate thread
        handleClient = new HandleClient();
        udpChannel = handleClient.startClient(serverAddress);
    }

    public void initializeGameScene( Scene scene) {
        String quadModelId = "quad-model";
        Model quadModel = ModelLoader.loadModel("quad-model", "assets/quad/quad.obj",
                scene.getTextureCache());
        scene.addModel(quadModel);

        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        terrainEntities = new Entity[numRows][numCols];
        for (int j = 0; j < numRows; j++) {
            for (int i = 0; i < numCols; i++) {
                Entity entity = new Entity("TERRAIN_" + j + "_" + i, quadModelId);
                terrainEntities[j][i] = entity;
                scene.addEntity(entity);
            }
        }

        Model mageModel = ModelLoader.loadModel("mage-model", "assets/mage/mage.obj",
                scene.getTextureCache());
        scene.addModel(mageModel);

        player = new Player("player1", "mage-model", udpChannel, serverAddress);
        player.setScale(0.1f);
        player.updateModelMatrix();
        scene.addEntity(player);

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);

        Skybox skyBox = new Skybox("assets/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(10);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkybox(skyBox);

        scene.getCamera().moveUp(0.1f);

        scene.setFog(new Fog(true, new Vector3f(0.352f, 0.401f, 0.466f), 0.25f));
        scene.setGuiInstance(new FogControls(scene));

        updateTerrain(scene);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) return;

        MouseInput mouseInput = window.getMouseInput();
        Camera camera = scene.getCamera();

        if (player != null) {
            player.handleInput(window, mouseInput, diffTimeMillis, camera);
        }

        if (mouseInput.isLeftButtonPressedOnce()) {
            toggleMouseCapture(window);
        }
    }

    private void toggleMouseCapture(Window window) {
        isMouseCaptured = !isMouseCaptured;
        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, isMouseCaptured ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        updateTerrain(scene);
    }

    public void updateTerrain(Scene scene) {

        Camera camera = scene.getCamera();

        Entity mageEntity = scene.getModelMap().get("mage-model").getEntityList().getFirst();
        mageEntity.updateModelMatrix();

        int cellSize = 10;
        Vector3f cameraPos = camera.getPosition();
        int cellCol = (int) (cameraPos.x / cellSize);
        int cellRow = (int) (cameraPos.z / cellSize);

        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        int zOffset = -NUM_CHUNKS;
        float scale = cellSize / 2.0f;
        for (int j = 0; j < numRows; j++) {
            int xOffset = -NUM_CHUNKS;
            for (int i = 0; i < numCols; i++) {
                Entity entity = terrainEntities[j][i];
                entity.setScale(scale);
                entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
                entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
                xOffset++;
            }
            zOffset++;
        }
    }
}
