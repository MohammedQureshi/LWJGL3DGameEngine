package org.mqureshi.entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f direction;
    private final Vector3f position;
    private final Vector3f right;
    private final Vector3f up;
    private final Vector2f rotation;
    private final Matrix4f viewMatrix;
    private static final float HEIGHT_OFFSET = 0.75f;

    public Camera() {
        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        rotation = new Vector2f();
    }

    public void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void moveUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }

    public void moveDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    public void moveLeft(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.sub(right);
        recalculate();
    }

    public void moveRight(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.add(right);
        recalculate();
    }

    public void moveForward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.add(direction);
        recalculate();
    }

    public void moveBackwards(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.sub(direction);
        recalculate();
    }

    private void recalculate() {
        // Update direction vector based on rotation
        direction.set(
                (float) (Math.cos(rotation.y) * Math.cos(rotation.x)),
                (float) Math.sin(rotation.x),
                (float) (Math.sin(rotation.y) * Math.cos(rotation.x))
        ).normalize();

        // Use LookAt to ensure the camera faces the correct direction
        viewMatrix.identity().lookAt(
                position,
                new Vector3f(position).add(direction),
                new Vector3f(0, 1, 0) // Up direction
        );
    }


    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }

    public void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }

    public Vector2f getRotation() {
        return rotation;
    }

    /**
     * Make the camera orbit around the player.
     *
     * @param playerPosition The position of the player.
     * @param distance       The distance of the camera from the player.
     * @param angleX         The rotation angle around the X-axis (vertical).
     * @param angleY         The rotation angle around the Y-axis (horizontal).
     */
    public void followPlayer(Vector3f playerPosition, float distance, float angleX, float angleY) {
        // Prevent flipping by clamping vertical rotation
        angleX = Math.max((float) Math.toRadians(-89), Math.min((float) Math.toRadians(89), angleX));

        // Convert spherical coordinates to Cartesian coordinates
        float horizontalDistance = (float) (distance * Math.cos(angleX));
        float offsetX = (float) (horizontalDistance * Math.sin(angleY));
        float offsetZ = (float) (horizontalDistance * Math.cos(angleY));
        float offsetY = (float) (distance * Math.sin(angleX)) + HEIGHT_OFFSET; // Apply height offset correctly

        // Set the new camera position relative to the player
        position.set(playerPosition.x - offsetX, playerPosition.y + offsetY, playerPosition.z - offsetZ);

        // Update rotation values
        rotation.set(angleX, angleY);

        // Ensure the camera looks at the player
        viewMatrix.identity().lookAt(position, playerPosition.add(0, HEIGHT_OFFSET, 0, new Vector3f()), new Vector3f(0, 1, 0));
    }



}
