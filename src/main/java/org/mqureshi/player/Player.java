package org.mqureshi.player;

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
        Vector3f position = getPosition();

        if (window.isKeyPressed(GLFW_KEY_W)) {
            position.z -= moveAmount;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            position.z += moveAmount;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            position.x -= moveAmount;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            position.x += moveAmount;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            position.y += moveAmount;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            position.y -= moveAmount;
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
