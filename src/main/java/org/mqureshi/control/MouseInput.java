package org.mqureshi.control;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private Vector2f currentPosition;
    private Vector2f displayVector;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private Vector2f previousPosition;
    private boolean rightButtonPressed;
    private boolean wasLeftButtonPressed;
    private boolean wasRightButtonPressed;
    private float xScale;
    private float yScale;

    public MouseInput(long windowHandle) {
        previousPosition = new Vector2f(-1, -1);
        currentPosition = new Vector2f();
        displayVector = new Vector2f();
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindow = false;
        wasLeftButtonPressed = false;
        wasRightButtonPressed = false;

        float[] xScaleArray = new float[1];
        float[] yScaleArray = new float[1];
        glfwGetWindowContentScale(windowHandle, xScaleArray, yScaleArray);
        xScale = xScaleArray[0];
        yScale = yScaleArray[0];

        int[] framebufferWidth = new int[1];
        int[] framebufferHeight = new int[1];
        glfwGetFramebufferSize(windowHandle, framebufferWidth, framebufferHeight);

        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        glfwGetWindowSize(windowHandle, windowWidth, windowHeight);

        float widthRatio = framebufferWidth[0] / (float) windowWidth[0];
        float heightRatio = framebufferHeight[0] / (float) windowHeight[0];

        glfwSetCursorPosCallback(windowHandle, (handle, xPosition, yPosition) -> {
            currentPosition.x = (float) xPosition * widthRatio;
            currentPosition.y = (float) yPosition * heightRatio;
        });

        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> inWindow = entered);

        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }


    public Vector2f getCurrentPosition() {
        return currentPosition;
    }

    public Vector2f getDisplayVector() {
        return displayVector;
    }

    public void input() {
        displayVector.x = 0;
        displayVector.y = 0;
        if (previousPosition.x > 0 && previousPosition.y > 0 && inWindow) {
            double deltaX = currentPosition.x - previousPosition.x;
            double deltaY = currentPosition.y - previousPosition.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                displayVector.x = (float) deltaX; // Correct: X movement affects X
            }
            if (rotateY) {
                displayVector.y = (float) deltaY; // Correct: Y movement affects Y
            }
        }
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    public boolean isLeftButtonPressedOnce() {
        if (leftButtonPressed && !wasLeftButtonPressed) {
            wasLeftButtonPressed = true;
            return true;
        }
        if (!leftButtonPressed) {
            wasLeftButtonPressed = false;
        }
        return false;
    }

    public boolean isRightButtonPressedOnce() {
        if (rightButtonPressed && !wasRightButtonPressed) {
            wasRightButtonPressed = true;
            return true;
        }
        if (!rightButtonPressed) {
            wasRightButtonPressed = false;
        }
        return false;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
