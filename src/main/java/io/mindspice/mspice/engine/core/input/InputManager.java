package io.mindspice.mspice.engine.core.input;

import io.mindspice.mspice.engine.core.engine.OnCleanUp;
import org.lwjgl.glfw.*;


public class InputManager implements OnCleanUp {
    private final KeyManager keyEvents;
    private final MousePosManager mousePosEvents;

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
        keyEvents = new KeyManager(20, inputMap);
        mousePosEvents = new MousePosManager(20);

        keyInputCallBack = new GLFWKeyCallback() {
            @Override // Release = 0, Press = 1, Repeat =2
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                keyEvents.broadcast(key, action);
            }
        };

        mouseInputCallBack = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                keyEvents.broadcast(button, action);
            }
        };

        mousePosCallBack = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                mousePosEvents.broadcast(x, y);
            }
        };
        mouseScrollCallBack = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double offsetX, double offsetY) {
                if (offsetY > 0) {
                    keyEvents.broadcast(0, 1);
                } else if (offsetY < 0) {
                    keyEvents.broadcast(0, 0);
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

    public void regKeyListener(KeyListener listener) {
        keyEvents.registerListener(listener);
    }

    public void unRegKeyListener(KeyListener listener) {
        keyEvents.unregisterListener(listener);
    }

    public void regMousePosListener(MousePosListener listener) {
        mousePosEvents.registerListener(listener);
    }

    public void unRegMousePosListener(MousePosListener listener) {
        mousePosEvents.unregisterListener(listener);
    }

}
