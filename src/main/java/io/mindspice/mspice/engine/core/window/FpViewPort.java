package io.mindspice.mspice.engine.core.window;

import io.mindspice.mspice.engine.core.engine.InputListener;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.InputAction;
import io.mindspice.mspice.engine.core.input.InputManager;
import io.mindspice.mspice.engine.core.input.KeyCallBackListener;
import io.mindspice.mspice.engine.core.input.MouseCallBackListener;
import io.mindspice.mspice.engine.core.renderer.components.Camera;
import io.mindspice.mspice.engine.core.input.ActionType;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Arrays;


public class FpViewPort implements OnUpdate, InputListener {
    private final Camera camera;
    private KeyCallBackListener keyListener;
    private final MouseCallBackListener mouseCallBackListener;
    private boolean inWindow = true;
    private long delta;

    // MouseView
    private final Vector2f currMPos = new Vector2f();
    private final Vector2f prevMPos = new Vector2f(-1, -1);
    private final Vector2f viewVec = new Vector2f();
    private final float mSensitivity = 0.1f;

    // Movement
    private int[] keyInputs = new int[4];
    private Vector3f moveVec = new Vector3f();

    private final float moveSpeed = 0.005f;

    public FpViewPort() {
        camera = new Camera();
        mouseCallBackListener = new MouseCallBackListener(ActionType.GAME_INPUT);
    }

    @Override
    public void registerListener(InputManager inputManager) {
        keyListener = new KeyCallBackListener(
                ActionType.GAME_INPUT,
                Arrays.stream(InputAction.values()).filter(e -> e.actionType == ActionType.GAME_INPUT).toList(),
                20,
                this::keyInputConsumer

        );
        inputManager.regKeyListener(keyListener);
        inputManager.regMousePosListener(mouseCallBackListener);
    }

    @Override
    public void setListening(boolean listening) {
        keyListener.setListening(listening);
    }

    @Override
    public boolean isListening() {
        return keyListener.isListening();
    }

    private void procMouseInput() {
        prevMPos.set(currMPos);
        currMPos.set(mouseCallBackListener.getPos());

        viewVec.x = 0;
        viewVec.y = 0;

        if (prevMPos.x > 0 && prevMPos.y > 0 && inWindow) {
            double dx = currMPos.x - prevMPos.x;
            double dy = currMPos.y - prevMPos.y;
            boolean rotateX = dx != 0;
            boolean rotateY = dy != 0;

            if (rotateX) {
                viewVec.y = (float) dx;
            }
            if (rotateY) {
                viewVec.x = (float) dy;
            }
        }

        float rotX = Math.toRadians(viewVec.x * mSensitivity);
        float rotY = Math.toRadians(viewVec.y * mSensitivity);
        camera.adjustRotation(rotX, rotY);
    }

    private void procKeyInput(long delta) {
        float amount = delta * moveSpeed;
        moveVec.zero();

        if (keyInputs[0] == 1) { moveVec.z -= amount; }
        if (keyInputs[1] == 1) { moveVec.z += amount; }
        if (keyInputs[2] == 1) { moveVec.x -= amount; }
        if (keyInputs[3] == 1) { moveVec.x += amount; }

        if (moveVec.length() > 0) {
            moveVec.normalize(amount);
        }
        camera.move(moveVec);
    }

    private void keyInputConsumer(InputAction input, int value) {
        if (value == 2) { return; } // Ignore held input signal
        switch (input) {
            case InputAction.MOVE_UP -> keyInputs[0] = value;
            case InputAction.MOVE_DOWN -> keyInputs[1] = value;
            case InputAction.MOVE_LEFT -> keyInputs[2] = value;
            case InputAction.MOVE_RIGHT -> keyInputs[3] = value;
            default -> { }
        }
    }

    @Override
    public void onUpdate(long delta) {
        procMouseInput();
        procKeyInput(delta);

    }

    public Matrix4f getViewMatrix() {
        return camera.getViewMatrix();
    }

}
