package org.mqureshi.player;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.mqureshi.control.MouseInput;
import org.mqureshi.engine.util.Window;
import static org.lwjgl.glfw.GLFW.*;
import io.netty.channel.Channel;
import org.mqureshi.entities.Camera;
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

    public void handleInput(Window window, MouseInput mouseInput, long deltaTime, Camera camera) {
        float moveAmount = deltaTime * SPEED;
        float strafeAmount = moveAmount;
        float turnSpeed = 0.002f; // Mouse sensitivity

        Vector3f position = getPosition();
        Quaternionf rotation = getRotation(); // Player's rotation
        Vector2f displVec = mouseInput.getDisplayVector(); // Get mouse movement

        // Rotate player left/right with mouse X movement (Yaw)
        float yawAmount = -displVec.x * turnSpeed; // Invert for natural feel
        rotation.rotateY(yawAmount);

        // Move forward/backward
        if (window.isKeyPressed(GLFW_KEY_W)) {
            position.add(new Vector3f(0, 0, moveAmount).rotate(rotation));
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            position.add(new Vector3f(0, 0, -moveAmount).rotate(rotation));
        }

        // Strafing left/right
        if (window.isKeyPressed(GLFW_KEY_A)) {
            position.add(new Vector3f(-strafeAmount, 0, 0).rotate(rotation));
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            position.add(new Vector3f(strafeAmount, 0, 0).rotate(rotation));
        }

        // Vertical movement (Jump & Crouch)
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            position.y += moveAmount;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            if (position.y - 0.02 <= 0) { // Clamp to prevent sinking below ground
                return;
            }
            position.y -= moveAmount;
        }

        // Correcting camera rotation (pitch)
        float pitchAmount = displVec.y * turnSpeed;
        float newPitch = camera.getRotation().x + pitchAmount;

        // Clamp pitch to prevent flipping
        newPitch = Math.max((float) Math.toRadians(-89), Math.min((float) Math.toRadians(89), newPitch));

        // Update camera position to follow player
        float playerYaw = (float) Math.atan2(2.0 * (rotation.y * rotation.w + rotation.x * rotation.z),
                1.0 - 2.0 * (rotation.y * rotation.y + rotation.z * rotation.z));

        camera.followPlayer(position, 1.0f, newPitch, playerYaw);


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
