package io.mindspice.mspice.engine.core.renderer.opengl;

import io.mindspice.mspice.engine.core.engine.CleanUp;
import io.mindspice.mspice.engine.core.renderer.components.SceneRender;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import io.mindspice.mspice.engine.core.window.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;


public class Renderer implements CleanUp {
    private final Window window;
    private final SceneRender sceneRender;

    public Renderer(Window window) {
        this.window = window;
        this.sceneRender = new SceneRender();
        initDefaults();
    }

    public void initDefaults() {
        GL.createCapabilities();
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        GL11.glEnable(GL_BACK);
    }

    @Override
    public void cleanup() {
        // Nothing to be done here yet
    }

    public void render(Scene scene, Matrix4f viewMatrix) {
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        sceneRender.render(scene, viewMatrix);
    }
}
