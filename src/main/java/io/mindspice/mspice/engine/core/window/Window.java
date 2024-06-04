package io.mindspice.mspice.engine.core.window;

import io.mindspice.mspice.engine.core.engine.InputListener;
import io.mindspice.mspice.engine.core.engine.CleanUp;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.InputManager;
import io.mindspice.mspice.engine.core.input.ActionType;
import io.mindspice.mspice.engine.core.input.InputAction;
import io.mindspice.mspice.engine.core.input.KeyCallBackListener;
import org.lwjgl.glfw.*;

import org.lwjgl.system.MemoryUtil;

import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;


public class Window implements CleanUp, OnUpdate, InputListener {

    private final long windowHandle;
    private int height;
    private boolean resized;
    private int width;
    private int[] winPosX = new int[1];
    private int[] winPosY = new int[1];
    private boolean vSyncEnabled = false;

    private KeyCallBackListener keyListener;

    public Window(String title, int[] size, boolean isCompatMode) {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        if (!glfwVulkanSupported()) {
            throw new IllegalStateException("Cannot find a compatible Vulkan installable client driver (ICD)");
        }

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode == null) {
            throw new IllegalStateException("Failed to get video mode for primary monitor");

        }
        width = vidMode.width();
        height = vidMode.height();
        winPosX[0] = (vidMode.width() - width) / 2;
        winPosY[0] = (vidMode.height() - height) / 2;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resize(w, h));
    }


    public void toggleCursor(boolean toggleOn) {
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR,
                toggleOn ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED
        );
    }

    public void setVSyncEnabled(boolean bool) {
        vSyncEnabled = bool;
        if (bool) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }



    @Override
    public void onUpdate(long delta) {
        glfwSwapBuffers(windowHandle);
    }

//    private void consumeKeyEvent() {
//        keyListener.getQueue().consumeAll(keyConsumers);
//    }

    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }

    @Override
    public void cleanup() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }


    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
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

    public void keyCheck(InputAction action, int state) {
        if (state != GLFW_PRESS) { return; }

        switch (action) {
            case RESIZE_WINDOW -> {
                glfwGetWindowPos(windowHandle, winPosX, winPosY);

                if (glfwGetWindowMonitor(windowHandle) == NULL) { // Dont Care arg is for refresh rate
                    glfwSetWindowMonitor(windowHandle, glfwGetPrimaryMonitor(), 0, 0, width, height, GLFW_DONT_CARE);
                } else {
                    glfwSetWindowMonitor(
                            windowHandle, NULL, winPosX[0], winPosY[0], width, height, GLFW_DONT_CARE
                    );
                }
            }
            case CLOSE_WINDOW -> glfwSetWindowShouldClose(windowHandle, true);
            default -> { }
        }
    }

    protected void resize(int width, int height) {
        resized = true;
        this.width = width;
        this.height = height;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void registerListener(InputManager inputManager) {
        keyListener = new KeyCallBackListener(
                ActionType.WINDOW,
                List.of(InputAction.CLOSE_WINDOW, InputAction.RESIZE_WINDOW),
                2,
                this::keyCheck
        );
        inputManager.regKeyListener(keyListener);
    }

    @Override
    public void setListening(boolean listening) {
        keyListener.setListening(listening);
    }

    @Override
    public boolean isListening() {
        return keyListener.isListening();
    }

}
