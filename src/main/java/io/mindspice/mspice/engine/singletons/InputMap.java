package io.mindspice.mspice.singletons;

import io.mindspice.mspice.enums.InputAction;
import org.lwjgl.glfw.GLFW;


public class InputMap {
    private static final InputMap INSTANCE = new InputMap();
    private final InputAction[] keyMap = new InputAction[GLFW.GLFW_KEY_LAST];

    private InputMap() {}

    public void set(int keyCode, InputAction inputAction) {
        keyMap[keyCode] = inputAction;
    }

    public static InputMap getInstance(){
        return INSTANCE;
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
