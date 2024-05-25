package io.mindspice.mspice.engine.core.window.viewport;

import io.mindspice.mspice.engine.core.engine.InputListener;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.InputAction;
import io.mindspice.mspice.engine.core.input.InputManager;
import io.mindspice.mspice.engine.core.input.KeyListener;
import io.mindspice.mspice.engine.core.input.MousePosListener;
import io.mindspice.mspice.engine.core.renderer.components.Camera;
import io.mindspice.mspice.engine.enums.ActionType;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;


public class ViewPort implements OnUpdate, InputListener {
    private final Camera camera;
    private final KeyListener keyListener;
    private final MousePosListener mousePosListener;

    private final Vector2f currPos = new Vector2f();
    private final Vector2f prevPos = new Vector2f(-1, -1);
    private final Vector2f displayVec = new Vector2f();
    private boolean inWindow = true;

    private final float mSensitivity = 0.1f;
    private final float moveSpeed = 0.005f;
    private long delta;

    public ViewPort() {
        camera = new Camera();
        keyListener = new KeyListener(new ActionType[]{ActionType.GAME_INPUT}, 20);
        mousePosListener = new MousePosListener();

    }

    @Override
    public void registerListener(InputManager inputManager) {
        inputManager.regKeyListener(keyListener);
        inputManager.regMousePosListener(mousePosListener);
    }

    private void procMouseInput() {
        prevPos.set(currPos);
        currPos.set(mousePosListener.getPos());

        displayVec.x = 0;
        displayVec.y = 0;

        if (prevPos.x > 0 && prevPos.y > 0 && inWindow) {
            double dx = currPos.x - prevPos.x;
            double dy = currPos.y - prevPos.y;
            boolean rotateX = dx != 0;
            boolean rotateY = dy != 0;

            if (rotateX) {
                displayVec.y = (float) dx;
            }
            if (rotateY) {
                displayVec.x = (float) dy;
            }
        }
    }

    public Matrix4f getViewMatrix() {
        return camera.getViewMatrix();
    }

    @Override
    public void onUpdate(long delta) {
        this.delta = delta;
        // Handle Mouse Input
        procMouseInput();
        float rotX = Math.toRadians(displayVec.x * mSensitivity);
        float rotY = Math.toRadians(displayVec.y * mSensitivity);
        camera.addRotation(rotX, rotY);

        // Handle Keyboard Input
        keyListener.consume(this::processKeyInput);
    }

    private void processKeyInput(InputAction input, int value) {
        if (value == 0) {
            return;
        }

        final float amount = delta * moveSpeed;

        System.out.println("Found Input: " + input);
        switch (input) {
            case InputAction.MOVE_UP -> camera.moveForward(amount);
            case InputAction.MOVE_DOWN -> camera.moveBackwards(amount);
            case InputAction.MOVE_LEFT -> camera.moveLeft(amount);
            case InputAction.MOVE_RIGHT -> camera.moveRight(amount);
            default -> { }
        }
    }

}
