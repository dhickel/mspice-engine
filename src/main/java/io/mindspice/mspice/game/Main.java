package io.mindspice.mspice.game;

import io.mindspice.mspice.core.GameEngine;
import io.mindspice.mspice.core.GameInput;
import io.mindspice.mspice.core.GameWindow;

import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.singletons.InputMap;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {



        GameEngine engine = GameEngine.getInstance();
        InputMap.getInstance().set(GLFW.GLFW_KEY_R, InputAction.RESIZE_SCREEN );
        InputMap.getInstance().set(GLFW.GLFW_KEY_W, InputAction.MOVE_UP);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                engine.cleanup();
            }
        });
        GameInput input = new GameInput();
        GameWindow window = new GameWindow("test engine", new int[]{1920, 1080}, false);
        engine.init(window, input, null);
        engine.setFrameUPS(144);
        window.regListeners();

        //window.setVSyncEnabled(true);
        engine.run();



    }


}
