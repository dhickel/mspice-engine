package io.mindspice.mspice.engine.game;

import io.mindspice.mspice.engine.core.PlayerState;
import io.mindspice.mspice.engine.core.engine.EngineProperties;

import io.mindspice.mspice.engine.core.renderer.Scene;
import io.mindspice.mspice.engine.core.window.Window;
import io.mindspice.mspice.engine.core.engine.IGameLogic;

import io.mindspice.mspice.engine.core.engine.GameEngine;
import io.mindspice.mspice.engine.core.input.InputAction;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        int width = 1920;
        int height = 1080;
        float fov = 60.0f;

        GameEngine engine = GameEngine.getInstance();
        PlayerState ps = new PlayerState(width, height, fov);
        Logic logic = new Logic();
        engine.init(ps, logic);
        engine.run();

        EngineProperties props = EngineProperties.getInstance();

        ps.getInputMap().set(GLFW.GLFW_KEY_R, InputAction.RESIZE_WINDOW);
        ps.getInputMap().set(GLFW.GLFW_KEY_ESCAPE, InputAction.CLOSE_WINDOW);
        ps.getInputMap().set(GLFW.GLFW_KEY_W, InputAction.MOVE_UP);
        ps.getInputMap().set(GLFW.GLFW_KEY_S, InputAction.MOVE_DOWN);
        ps.getInputMap().set(GLFW.GLFW_KEY_A, InputAction.MOVE_LEFT);
        ps.getInputMap().set(GLFW.GLFW_KEY_D, InputAction.MOVE_RIGHT);
        ps.getInputMap().set(GLFW.GLFW_KEY_TAB, InputAction.GUI_TAB);
        ps.getInputMap().set(0, InputAction.SCROLL_UP);
        ps.getInputMap().set(0, InputAction.SCROLL_DOWN);
        ps.getInputMap().set(GLFW.GLFW_MOUSE_BUTTON_LEFT, InputAction.GUI_LEFT_CLICK);
        ps.getInputMap().set(GLFW.GLFW_MOUSE_BUTTON_RIGHT, InputAction.GUI_RIGHT_CLICK);

        while (true) {

        }

    }



    private static class Logic implements IGameLogic {

        @Override
        public void init(Scene scene) {
        }

        @Override
        public void input(Window window, Scene scene, long diffTimeMilli) {

        }

        @Override
        public void update(long delta) {
        }

        @Override
        public void cleanup() {

        }
    }

}
