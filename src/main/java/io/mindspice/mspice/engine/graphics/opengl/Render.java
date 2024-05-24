package io.mindspice.mspice.graphics.opengl;

import io.mindspice.mspice.graphics.components.SceneRender;
import io.mindspice.mspice.core.game.GameWindow;
import io.mindspice.mspice.graphics.components.Scene;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;


public class Render {
    private final GameWindow window;
    private final SceneRender sceneRender;

    public Render(GameWindow window) {
        //GL.createCapabilities();
        this.window = window;
        this.sceneRender = new SceneRender();
    }

    public void cleanup() {
        // Nothing to be done here yet
    }

    public void render(Scene scene) {
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        sceneRender.render(scene);
    }
}
