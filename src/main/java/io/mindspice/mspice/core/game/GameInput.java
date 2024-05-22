package io.mindspice.mspice.core;

import io.mindspice.mspice.input.KeyEventManager;
import io.mindspice.mspice.input.KeyListener;
import io.mindspice.mspice.input.MousePosEventManager;
import io.mindspice.mspice.input.MousePosListener;
import org.lwjgl.glfw.*;


public class GameInput {
    //    private final CircularBiIntQueue keyInputQueue = new CircularBiIntQueue(100);
//    private final CircularBiIntQueue mouseInputQueue = new CircularBiIntQueue(100);
//    private final CircularIntDblQueue scrollInputQueue = new CircularIntDblQueue(100);
    private final double[] mousePos = new double[2];
    private final KeyEventManager keyboardEvents = new KeyEventManager(10);
    private final KeyEventManager mouseEvents = new KeyEventManager(10);
    private final KeyEventManager scrollEvents = new KeyEventManager(10);
    private final MousePosEventManager mousePosEvents = new MousePosEventManager(10);

    private final GLFWKeyCallback keyInputCallBack;
    private final GLFWMouseButtonCallback mouseInputCallBack;
    private final GLFWCursorPosCallback mousePosCallBack;
    private final GLFWScrollCallback mouseScrollCallBack;

    public GameInput() {
        keyInputCallBack = new GLFWKeyCallback() {
            @Override // Release = 0, Press = 1, Repeat =2
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                keyboardEvents.broadcast(key, action);
            }
        };
        mouseInputCallBack = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseEvents.broadcast(button, action);
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
                if (offsetX > 0) {
                    scrollEvents.broadcast(0,1);
                } else if(offsetX < 0) {
                    scrollEvents.broadcast(0,0);
                }

            }
        };
    }

    public void enableRawInput(long window, boolean bool) {
        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, bool ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        }
    }

    public void regKeyboardListener(KeyListener listener) {
        keyboardEvents.registerListener(listener);
    }

    public void regMouseButtonListener(KeyListener listener) {
        mouseEvents.registerListener(listener);
    }

    public void regScrollListener(KeyListener listener) {
        scrollEvents.registerListener(listener);
    }

    public void regMousePosListener(MousePosListener listener) {
        mousePosEvents.registerListener(listener);
    }

    public void unRegKeyboardListener(KeyListener listener) {
        keyboardEvents.unregisterListener(listener);
    }

    public void unRegMouseButtonListener(KeyListener listener) {
        mouseEvents.unregisterListener(listener);
    }

    public void unRegScrollListener(KeyListener listener) {
        scrollEvents.unregisterListener(listener);
    }

    public void unRegMousePosListener(MousePosListener listener) {
        mousePosEvents.unregisterListener(listener);
    }

    public void cleanup() {
        keyInputCallBack.free();
        mouseInputCallBack.free();
        mousePosCallBack.free();
        mouseScrollCallBack.free();
    }

    public double[] getMousePos() {
        return mousePos;
    }

    public void bindCallBacks(long window) {
        GLFW.glfwSetKeyCallback(window, keyInputCallBack);
        GLFW.glfwSetCursorPosCallback(window, mousePosCallBack);
        GLFW.glfwSetMouseButtonCallback(window, mouseInputCallBack);
        GLFW.glfwSetScrollCallback(window, mouseScrollCallBack);
    }


}
