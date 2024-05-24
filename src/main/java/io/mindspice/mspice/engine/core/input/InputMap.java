package io.mindspice.mspice.engine.core.input;

import org.lwjgl.glfw.GLFW;


public class InputMap {
    private final InputAction[] keyMap = new InputAction[GLFW.GLFW_KEY_LAST];

    public void set(int keyCode, InputAction inputAction) {
        keyMap[keyCode] = inputAction;
    }

    public InputAction get(int keyCode) {
        return keyMap[keyCode];
    }

    public void remove(int keyCode, InputAction actionInput) {
        keyMap[keyCode] = null;
    }

    public boolean contains(int keyCode) {
        return keyMap[keyCode] != null;
    }
}
