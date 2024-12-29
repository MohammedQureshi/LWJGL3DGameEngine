package org.mqureshi;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.joml.Vector2f;
import org.mqureshi.client.HandleClient;
import org.mqureshi.control.MouseInput;
import org.mqureshi.engine.*;
import org.mqureshi.entities.*;
import org.mqureshi.scenes.Scene;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements GameLogicInterface {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cubeEntity;
    private final Vector4f displayInc = new Vector4f();
    private float rotation;

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
        initializeGameScene(window, scene, render);

        // Start the UDP client in a separate thread
        handleClient = new HandleClient();
        udpChannel = handleClient.startClient(serverAddress);
    }

    public void initializeGameScene(Window window, Scene scene, Render render) {
        Model cubeModel = ModelLoader.loadModel("cube-model", "/models/cube/cube.obj",
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0,0,-2);
        scene.addEntity(cubeEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
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
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
                    (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
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
        rotation += 1.5F;
        if (rotation > 360) {
            rotation = 0;
        }
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();
    }
}
