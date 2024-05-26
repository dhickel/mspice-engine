package io.mindspice.mspice.engine.core.input;

import io.mindspice.mspice.engine.core.engine.CleanUp;
import org.lwjgl.glfw.*;


public class InputManager implements CleanUp {
    private final KeyManager keyManager;
    private final MouseManager mouseManager;

    private final GLFWKeyCallback keyInputCallBack;
    private final GLFWMouseButtonCallback mouseInputCallBack;
    private final GLFWCursorPosCallback mousePosCallBack;
    private final GLFWScrollCallback mouseScrollCallBack;

    /*
    Polled inputs from the game window are processed here and dispatched to queues
    related to key/butt presses and mouse position updates. Listeners for these
    queues are registered and unregistered. Systems should register listeners for
    events when needing to listen, as the listeners will queue what they are interested
    in awaiting consume to be called to act on them; Direct Registering of listeners
    through here, must ensure they are for systems on the main engine thread. Other
    listeners from other threads should subscribe with the event system.
     */

    public InputManager(InputMap inputMap) {
        keyManager = new KeyManager(20, inputMap);
        mouseManager = new MouseManager(20);

        keyInputCallBack = new GLFWKeyCallback() {
            @Override // Release = 0, Press = 1, Repeat =2
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                keyManager.broadcast(key, action);
            }
        };

        mouseInputCallBack = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                InputAction inputAction = inputMap.get(button);
                if (inputAction != null) {
                    mouseManager.broadcastButtons(inputAction, action);
                }
            }
        };

        mousePosCallBack = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                mouseManager.broadcastPos(x, y);
            }
        };
        mouseScrollCallBack = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double offsetX, double offsetY) {
                if (offsetY > 0) {
                    mouseManager.broadcastScroll(1);
                } else if (offsetY < 0) {
                    mouseManager.broadcastScroll(0);
                }
            }
        };
    }

    public void bindToWindow(long window) {
        GLFW.glfwSetKeyCallback(window, keyInputCallBack);
        GLFW.glfwSetCursorPosCallback(window, mousePosCallBack);
        GLFW.glfwSetMouseButtonCallback(window, mouseInputCallBack);
        GLFW.glfwSetScrollCallback(window, mouseScrollCallBack);
    }

    @Override
    public void cleanup() {
        keyInputCallBack.free();
        mouseInputCallBack.free();
        mousePosCallBack.free();
        mouseScrollCallBack.free();
    }

    public void enableRawInput(long window, boolean bool) {
        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, bool ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        }
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public MouseManager getMouseManager() {
        return mouseManager;
    }

    public void setFilter(ActionType actionType) {
        mouseManager.setListenFilter(actionType);
        keyManager.setListenFilter(actionType);
    }

    public void disableFilter() {
        mouseManager.disableListenFilter();
        keyManager.disableListenFilter();
    }

    public void regKeyListener(KeyListener listener) {
        keyManager.registerListener(listener);
    }

    public void unRegKeyListener(KeyListener listener) {
        keyManager.unregisterListener(listener);
    }

    public void regMousePosListener(MouseCallBackListener listener) {
        mouseManager.registerListener(listener);
    }

    public void unRegMousePosListener(MouseCallBackListener listener) {
        mouseManager.unregisterListener(listener);
    }

}
