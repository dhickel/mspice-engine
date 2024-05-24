package io.mindspice.mspice.engine.core;

import io.mindspice.mspice.engine.core.engine.OnCleanUp;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.InputManager;
import io.mindspice.mspice.engine.core.input.InputMap;
import io.mindspice.mspice.engine.core.renderer.opengl.Renderer;
import io.mindspice.mspice.engine.core.window.GameWindow;


public class PlayerState implements OnUpdate, OnCleanUp {
    private final InputManager inputManager;
    private final InputMap inputMap;
    private final GameWindow gameWindow;
    private final Renderer renderer;

    /*
    InputMap: Holds players key binds

    InputManager: Handles input Callbacks, broadcasting them to related mouse or keyboard input manager
        these place the related inputs in a queue for the input consumers to handle, and also
        dispatch the key actions to the event system; // TODO flag for if action should be dispatched?

    GameWindow: Handles the game windows *Note input manager needs bound to window for input callbacks to hook

    Render: Handles the rendering, currently overlaps with game window, these should possibly be nested? or
        at-least have the responsibilities more defined and ironed out

     */

    public PlayerState(int width, int height, float fov) {
        inputMap = new InputMap();
        inputManager = new InputManager(inputMap);

        gameWindow = new GameWindow("Test", new int[]{width, height}, false);
        inputManager.bindToWindow(gameWindow.getWindowHandle());

        renderer = new Renderer(gameWindow);
    }

    @Override
    public void onUpdate(long delta) {
        gameWindow.update();
    }

    @Override
    public void cleanup() {
        inputManager.cleanup();
        renderer.cleanup();
        gameWindow.cleanup();
    }

    public InputMap getInputMap() {
        return inputMap;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }



}