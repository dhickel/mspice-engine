package io.mindspice.mspice.opengl;

import io.mindspice.mspice.core.GameWindow;
import io.mindspice.mspice.game.Scene;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
public class Render {

    public Render() {
        GL.createCapabilities();
    }

    public void cleanup() {
        // Nothing to be done here yet
    }

    public void render(GameWindow window, Scene scene) {
        GL11.glClear(GL_COLOR_BUFFER_BIT |GL_DEPTH_BUFFER_BIT);
    }
}
