package org.mqureshi;

import io.netty.channel.Channel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.mqureshi.client.HandleClient;
import org.mqureshi.control.MouseInput;
import org.mqureshi.engine.light.LightControls;
import org.mqureshi.engine.light.point.PointLight;
import org.mqureshi.engine.light.spot.SpotLight;
import org.mqureshi.engine.model.ModelLoader;
import org.mqureshi.engine.interfaces.GameLogicInterface;
import org.mqureshi.engine.skybox.Skybox;
import org.mqureshi.engine.util.Engine;
import org.mqureshi.engine.util.Render;
import org.mqureshi.engine.util.Window;
import org.mqureshi.entities.*;
import org.mqureshi.scenes.Scene;
import org.mqureshi.scenes.SceneLights;

import java.net.InetSocketAddress;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements GameLogicInterface {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cubeEntity;
    private float rotation;
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;

    private Channel udpChannel;
    private InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
    private HandleClient handleClient;
    private boolean isMouseCaptured = false;

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEngine = new Engine("LWJGL Game", new Window.WindowOptions(), main);
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

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);

        Skybox skyBox = new Skybox("assets/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(50);
        scene.setSkybox(skyBox);

        scene.getCamera().moveUp(0.1f);

        updateTerrain(scene);
//        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
//                new Vector3f(0, 0, -1.4f), 1.0f));
//
//        Vector3f coneDir = new Vector3f(0, 0, -1);
//        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
//                new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));
//
//        LightControls lightControls = new LightControls(scene);
//        scene.setGuiInstance(lightControls);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) return;

        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isLeftButtonPressedOnce()) {
            toggleMouseCapture(window);
        }

        if (isMouseCaptured) {
            Vector2f displVec = mouseInput.getDisplayVector();
            float rotationX = (float) Math.toRadians(displVec.y * MOUSE_SENSITIVITY);
            float rotationY = (float) Math.toRadians(displVec.x * MOUSE_SENSITIVITY);

            // Optional: Clamp vertical rotation to avoid camera flipping
            camera.addRotation(rotationY, rotationX);
        }

//        if (udpChannel != null) {
//            String message = String.format("Position: %.2f, %.2f, %.2f", entityPos.x, entityPos.y, entityPos.z);
//            udpChannel.writeAndFlush(new DatagramPacket(
//                    Unpooled.copiedBuffer(message, CharsetUtil.UTF_8),
//                    serverAddress
//            ));
//        }
    }

    private void toggleMouseCapture(Window window) {
        isMouseCaptured = !isMouseCaptured;
        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, isMouseCaptured ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        updateTerrain(scene);
//        rotation += 0F;
//        if (rotation > 360) {
//            rotation = 0;
//        }
//
//        float position = cubeEntity.getPosition().z + 0.01F;
//        cubeEntity.setPosition(0,0, position);
//        cubeEntity.setRotation(0, 1, 0, (float) Math.toRadians(rotation));
//        cubeEntity.updateModelMatrix();
    }

    public void updateTerrain(Scene scene) {
        int cellSize = 10;
        Camera camera = scene.getCamera();
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
