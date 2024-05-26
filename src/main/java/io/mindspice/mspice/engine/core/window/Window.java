package io.mindspice.mspice.engine.core.window;

import io.mindspice.mspice.engine.core.engine.InputListener;
import io.mindspice.mspice.engine.core.engine.CleanUp;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.InputManager;
import io.mindspice.mspice.engine.core.input.ActionType;
import io.mindspice.mspice.engine.core.input.InputAction;
import io.mindspice.mspice.engine.core.input.KeyCallBackListener;
import org.joml.Matrix4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Window implements CleanUp, OnUpdate, InputListener {

    private final long windowHandle;
    private int height;
    private int width;
    private boolean vSyncEnabled = false;
    private int[] winPosX = new int[1];
    private int[] winPosY = new int[1];

    float fov = (float) Math.toRadians(90);
    float zNear = 1f;
    float zFar = 1000f;
    float aspectRatio;
    Matrix4f projectionMatrix;

    private KeyCallBackListener keyListener;

    public Window(String title, int[] size, boolean isCompatMode) {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        setHints(isCompatMode);

        this.width = size[0];
        this.height = size[1];
        aspectRatio = (float) width / height;
        windowHandle = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        projectionMatrix = new Matrix4f();

        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");

        }
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (vidMode == null) {
            throw new RuntimeException("Failed to get Video Mode");
        }

        winPosX[0] = (vidMode.width() - width) / 2;
        winPosY[0] = (vidMode.height() - height) / 2;
        glfwSetWindowPos(windowHandle, winPosX[0], winPosY[0]);

        setCallBacks();

        GLFW.glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        GL11.glViewport(0, 0, width, height);
        int[] fbWidth = new int[1];
        int[] fbeHeight = new int[1];

        GLFW.glfwGetFramebufferSize(windowHandle, fbWidth, fbeHeight);
        glfwSwapInterval(0);
        glfwShowWindow(windowHandle);
        toggleCursor(false);
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

    public void setFOV(double fov) {
        this.fov = (float) Math.toRadians(fov);
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

        GLFWErrorCallback errorCB = glfwSetErrorCallback(null);
        if (errorCB != null) {
            errorCB.free();
        }
        GLFWWindowSizeCallback windowCB = glfwSetWindowSizeCallback(windowHandle, null);
        if (windowCB != null) {
            windowCB.free();
        }
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        return projectionMatrix.setPerspective(fov, aspectRatio, zNear, zFar);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspect = (float) width / height;
        return projectionMatrix.setPerspective(fov, aspect, zNear, zFar);
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

    protected void resized(int width, int height) {
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
