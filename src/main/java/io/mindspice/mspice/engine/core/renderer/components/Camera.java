package io.mindspice.mspice.engine.core.renderer.components;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Matrix4f viewMatrix;
    private final Quaternionf orientation;
    private final Vector3f tempVector;
    private Quaternionf deltaRotation;

    public Camera() {
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        orientation = new Quaternionf();
        tempVector = new Vector3f();
        deltaRotation = new Quaternionf();

        recalculate();
    }

    public void adjustRotation(float deltaX, float deltaY) {
        deltaRotation.identity().rotateY(-deltaY).rotateX(-deltaX);
        orientation.mul(deltaRotation);
        recalculate();
    }

    public void setRotation(float pitch, float yaw) {
        orientation.identity().rotateY(yaw).rotateX(pitch);
        recalculate();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    // General move method that reuses tempVector
    public void move(Vector3f mVec) {
        tempVector.set(mVec);
        orientation.transform(tempVector);
        position.add(tempVector);
        recalculate();
    }

//    public void moveForward(float distance) {
//        move(0, 0, -distance);
//    }
//
//    public void moveBackward(float distance) {
//        move(0, 0, distance);
//    }
//
//    public void moveRight(float distance) {
//        move(distance, 0, 0);
//    }
//
//    public void moveLeft(float distance) {
//        move(-distance, 0, 0);
//    }
//
//    public void moveUp(float distance) {
//        move(0, distance, 0);
//    }
//
//    public void moveDown(float distance) {
//        move(0, -distance, 0);
//    }

    private void recalculate() {
        viewMatrix.identity()
                .rotate(orientation.conjugate(new Quaternionf()))
                .translate(-position.x, -position.y, -position.z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }
}
