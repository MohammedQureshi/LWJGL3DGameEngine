package org.mqureshi.player;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mqureshi.engine.util.Window;
import static org.lwjgl.glfw.GLFW.*;
import io.netty.channel.Channel;
import org.mqureshi.entities.Entity;

import java.net.InetSocketAddress;

public class Player extends Entity {
    private static final float SPEED = 0.01f;
    private Channel udpChannel;
    private InetSocketAddress serverAddress;

    public Player(String id, String modelId, Channel udpChannel, InetSocketAddress serverAddress) {
        super(id, modelId);
        this.udpChannel = udpChannel;
        this.serverAddress = serverAddress;
    }

    public void handleInput(Window window, long deltaTime) {
        float moveAmount = deltaTime * SPEED;
        float turnSpeed = 0.005f * deltaTime;

        Vector3f position = getPosition();
        Quaternionf rotation = getRotation(); // Player's rotation

        if (window.isKeyPressed(GLFW_KEY_W)) {
            position.add(new Vector3f(0, 0, moveAmount).rotate(rotation));
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            position.add(new Vector3f(0, 0, -moveAmount).rotate(rotation));
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            position.y += moveAmount;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            position.y -= moveAmount;
        }

        // Rotation
        if (window.isKeyPressed(GLFW_KEY_A)) {
            rotation.rotateY(turnSpeed);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            rotation.rotateY(-turnSpeed);
        }

        setPosition(position.x, position.y, position.z);

        updateModelMatrix();
        sendPositionUpdate();
    }

    private void sendPositionUpdate() {
        if (udpChannel != null) {
            String message = String.format("%s:%.2f,%.2f,%.2f", getId(), getPosition().x, getPosition().y, getPosition().z);
            udpChannel.writeAndFlush(message);
        }
    }
}
