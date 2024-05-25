package io.mindspice.mspice.engine.core.renderer.opengl;

import io.mindspice.mspice.engine.core.engine.OnCleanUp;
import io.mindspice.mspice.engine.core.window.GameWindow;
import io.mindspice.mspice.engine.core.renderer.components.SceneRender;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;


public class Renderer implements OnCleanUp {
    private final GameWindow window;
    private final SceneRender sceneRender;

    public Renderer(GameWindow window) {
        this.window = window;
        this.sceneRender = new SceneRender();
        initDefaults();
    }

    public void initDefaults() {
        GL.createCapabilities();
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_STENCIL_TEST);
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
