package io.mindspice.mspice.engine.core;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import io.mindspice.mspice.engine.core.engine.CleanUp;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.*;
import io.mindspice.mspice.engine.core.renderer.components.GuiScene;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import io.mindspice.mspice.engine.core.renderer.opengl.GuiRenderer;
import io.mindspice.mspice.engine.core.renderer.opengl.Renderer;
import io.mindspice.mspice.engine.core.window.Window;
import io.mindspice.mspice.engine.core.window.FpViewPort;

import java.util.List;


public class PlayerState implements OnUpdate, CleanUp {
    private final InputManager inputManager;
    private final InputMap inputMap;
    private final Window window;
    private final Renderer renderer;
    private final GuiRenderer guiRenderer;
    private final FpViewPort viewport;

    private Scene currScene = null;

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

        window = new Window("Test", new int[]{width, height}, false);
        inputManager.bindToWindow(window.getWindowHandle());
        window.registerListener(inputManager);

        renderer = new Renderer(window);
        guiRenderer = new GuiRenderer(window);
        guiRenderer.registerListener(inputManager);

        viewport = new FpViewPort();
        viewport.registerListener(inputManager);
        registerListeners();

        guiRenderer.loadScene(guiDemo);
    }

    public void loadScene(Scene scene) {
        currScene = scene;
    }

    private void registerListeners() {
        KeyCallBackListener listener = new KeyCallBackListener(
                ActionType.GUI,
                List.of(InputAction.GUI_TAB),
                2,
                (InputAction action, int value) -> {
                    if (value != 1) { return; }
                    if (guiRenderer.isEnabled()) {
                        guiRenderer.setEnabled(false);
                        inputManager.disableFilter();
                        window.toggleCursor(false);
                    } else {
                        guiRenderer.setEnabled(true);
                        inputManager.setFilter(ActionType.GUI);
                        window.toggleCursor(true);
                    }
                }
        );
        inputManager.regKeyListener(listener);

        MouseCallBackListener mouseListener = new MouseCallBackListener(ActionType.GUI);
        mouseListener.setPosConsumer((x, y) ->
        {
            ImGui.getIO().setMousePos((float) x, (float) y);
        });

        mouseListener.setButtonConsumer((action, value) -> {
            if (action == InputAction.GUI_LEFT_CLICK) {
                ImGui.getIO().setMouseDown(0, value == 1);
            } else if (action == InputAction.GUI_RIGHT_CLICK) {
                ImGui.getIO().setMouseDown(1, value == 1);
            }
        });

        inputManager.regMousePosListener(mouseListener);
    }

    @Override
    public void onUpdate(long delta) {
        window.pollEvents();
        viewport.onUpdate(delta);
        renderer.render(currScene, viewport.getViewMatrix());
        guiRenderer.render();
        window.onUpdate(delta);
    }

    @Override
    public void cleanup() {
        inputManager.cleanup();
        renderer.cleanup();
        window.cleanup();
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

    public Window getGameWindow() {
        return window;
    }

    public GuiScene guiDemo = new GuiScene() {
        @Override
        public void render() {
            ImGui.newFrame();
            ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
            ImGui.showDemoWindow();
            ImGui.endFrame();
            ImGui.render();
        }
    };

}
