package io.mindspice.mspice.core;

import io.mindspice.mspice.enums.ActionType;
import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.input.KeyListener;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.function.IntConsumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class GameWindow {

    private final long windowHandle;
    private int height;
    private int width;
    private boolean vSyncEnabled = false;
    private GameInput input;
    private int[] winPosX = new int[1];
    private int[] winPosY = new int[1];

    private KeyListener keyListener = new KeyListener(new ActionType[]{ActionType.SCREEN}, 100);
    private IntConsumer[] keyConsumers = new IntConsumer[InputAction.values().length];

    public GameWindow(String title, int[] size, boolean isCompatMode) {
        if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW"); }
        setHints(isCompatMode);

        this.width = size[0];
        this.height = size[1];
        windowHandle = glfwCreateWindow(width, height, "", NULL, NULL);

        if (windowHandle == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (vidMode == null) { throw new RuntimeException("Failed to get Video Mode"); }
        winPosX[0] = (vidMode.width() - width) / 2;
        winPosY[0] = (vidMode.height() - height) / 2;
        glfwSetWindowPos(windowHandle, winPosX[0], winPosY[0]);

        setCallBacks();

        GLFW.glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glViewport(0, 0, width, height);
        int[] fbWidth = new int[1];
        int[] fbeHeight = new int[1];
        GLFW.glfwGetFramebufferSize(windowHandle, fbWidth, fbeHeight);
        glfwSwapInterval(0);
        glfwShowWindow(windowHandle);
    }

    public void regListeners() {
        GameEngine.getInstance().getGameInput().regKeyboardListener(keyListener);
        keyConsumers[InputAction.RESIZE_SCREEN.ordinal()] = this::setFullScreen;
    }

    private void setHints(boolean isCompatMode) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        if (isCompatMode) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }
    }

    public void setCallBacks() {
        glfwSetErrorCallback((int errorCode, long msgPtr) -> {
            System.out.println(errorCode + "  " + MemoryUtil.memUTF8(msgPtr));
        });
        glfwSetWindowSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            GL11.glViewport(0, 0, width, height);
            // TODO will want to update projection matrix as well
        });
    }

    public void setVSyncEnabled(boolean bool) {
        vSyncEnabled = bool;
        if (bool) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }

    public void update() {
        consumeKeyEvent();
        GL11.glClearColor(0.5f, 0.7f, 0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(windowHandle);
    }


    private void consumeKeyEvent(){
        keyListener.getQueue().consumeAll(keyConsumers);
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }

    public void cleanup() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();

        GLFWErrorCallback errorCB = glfwSetErrorCallback(null);
        if (errorCB != null) { errorCB.free(); }
        GLFWWindowSizeCallback windowCB = glfwSetWindowSizeCallback(windowHandle, null);
        if (windowCB != null) { windowCB.free(); }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public GameInput getInput() {
        return input;
    }

    public void setFullScreen(int action) {
        if (action != GLFW_RELEASE) { return; }
        glfwGetWindowPos(windowHandle, winPosX, winPosY);

        if (glfwGetWindowMonitor(windowHandle) == NULL) {
            glfwSetWindowMonitor( // Dont Care arg is for refresh rate
                                  windowHandle, glfwGetPrimaryMonitor(), 0, 0, width, height, GLFW_DONT_CARE
            );
        } else {
            glfwSetWindowMonitor(
                    windowHandle, NULL, winPosX[0], winPosY[0], width, height, GLFW_DONT_CARE
            );
        }
    }

    protected void resized(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public static class WindowOptions {
        public boolean compatibleProfile;
        public int[] windowSize;
    }
}
