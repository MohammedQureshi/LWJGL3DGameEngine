package org.mqureshi.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {

    private final Vector3f position = new Vector3f(0, 5, 0);
    private float pitch = 10;
    private float yaw;
    private float roll;
    private final long windowHandle;

    private final float moveSpeed = 0.1f;

    public Camera(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    public void move() {

        Vector3f forward = new Vector3f(
                (float) -Math.sin(Math.toRadians(yaw)),
                0,
                (float) -Math.cos(Math.toRadians(yaw))
        );
        Vector3f right = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)),
                0,
                (float) -Math.sin(Math.toRadians(yaw))
        );

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.add(forward.mul(moveSpeed, new Vector3f()));
        }

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.sub(forward.mul(moveSpeed, new Vector3f()));
        }

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.add(right.mul(moveSpeed, new Vector3f()));
        }

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.sub(right.mul(moveSpeed, new Vector3f()));
        }

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            position.y += moveSpeed;
        }

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) {
            position.y -= moveSpeed;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
}
