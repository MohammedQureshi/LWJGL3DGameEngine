package org.mqureshi;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.mqureshi.client.HandleClient;
import org.mqureshi.engine.*;
import org.mqureshi.entities.Entity;
import org.mqureshi.entities.Material;
import org.mqureshi.entities.Mesh;
import org.mqureshi.entities.Model;
import org.mqureshi.scenes.Scene;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements GameLogicInterface {

    private Entity cubeEntity;
    private final Vector4f displayInc = new Vector4f();
    private float rotation;

    private Channel udpChannel;
    private InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
    private HandleClient handleClient;

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
        float[] positions = new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};

        Texture texture = scene.getTextureCache().createTexture("/models/cube/cube.png");
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = new Mesh(positions, textCoords, indices);
        material.getMeshList().add(mesh);
        Model cubeModel = new Model("cube-model", materialList);
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0,0,-2);
        scene.addEntity(cubeEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        displayInc.zero();
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displayInc.y = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displayInc.y = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displayInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displayInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            displayInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            displayInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            displayInc.w = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            displayInc.w = 1;
        }

        displayInc.mul(diffTimeMillis / 1000.0f);

        Vector3f entityPos = cubeEntity.getPosition();
        cubeEntity.setPosition(displayInc.x + entityPos.x, displayInc.y + entityPos.y, displayInc.z + entityPos.z);
        cubeEntity.setScale(cubeEntity.getScale() + displayInc.w);
        cubeEntity.updateModelMatrix();

        if (udpChannel != null) {
            String message = String.format("Position: %.2f, %.2f, %.2f", entityPos.x, entityPos.y, entityPos.z);
            udpChannel.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(message, CharsetUtil.UTF_8),
                    serverAddress
            ));
        }
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
