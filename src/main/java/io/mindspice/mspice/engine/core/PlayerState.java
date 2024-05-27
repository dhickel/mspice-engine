package io.mindspice.mspice.engine.core;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import io.mindspice.mspice.engine.core.engine.CleanUp;
import io.mindspice.mspice.engine.core.engine.OnUpdate;
import io.mindspice.mspice.engine.core.input.*;
import io.mindspice.mspice.engine.core.renderer.components.GuiScene;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import io.mindspice.mspice.engine.core.renderer.components.SceneLights;
import io.mindspice.mspice.engine.core.renderer.lighting.AmbientLight;
import io.mindspice.mspice.engine.core.renderer.lighting.DirectionalLight;
import io.mindspice.mspice.engine.core.renderer.lighting.PointLight;
import io.mindspice.mspice.engine.core.renderer.lighting.SpotLight;
import io.mindspice.mspice.engine.core.renderer.opengl.GuiRenderer;
import io.mindspice.mspice.engine.core.renderer.opengl.Renderer;
import io.mindspice.mspice.engine.core.window.Window;
import io.mindspice.mspice.engine.core.window.FpViewPort;
import org.joml.Vector3f;

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

    }

    public void loadScene(Scene scene) {
        currScene = scene;
        guiRenderer.loadScene(new LightGui(scene));
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

    public class LightGui extends GuiScene {
        private float[] ambientColor;
        private float[] ambientFactor;
        private float[] dirConeX;
        private float[] dirConeY;
        private float[] dirConeZ;
        private float[] dirLightColor;
        private float[] dirLightIntensity;
        private float[] dirLightX;
        private float[] dirLightY;
        private float[] dirLightZ;
        private float[] pointLightColor;
        private float[] pointLightIntensity;
        private float[] pointLightX;
        private float[] pointLightY;
        private float[] pointLightZ;
        private float[] spotLightColor;
        private float[] spotLightCuttoff;
        private float[] spotLightIntensity;
        private float[] spotLightX;
        private float[] spotLightY;
        private float[] spotLightZ;

        ImGuiIO imGuiIO = ImGui.getIO();
        Scene scene;

        public LightGui(Scene scene) {
            this.scene = scene;
            SceneLights sceneLights = scene.getSceneLights();
            AmbientLight ambientLight = sceneLights.getAmbientLight();
            Vector3f color = ambientLight.getColor();

            ambientFactor = new float[]{ambientLight.getIntensity()};
            ambientColor = new float[]{color.x, color.y, color.z};

            PointLight pointLight = sceneLights.getPointLights().get(0);
            color = pointLight.getColor();
            Vector3f pos = pointLight.getPosition();
            pointLightColor = new float[]{color.x, color.y, color.z};
            pointLightX = new float[]{pos.x};
            pointLightY = new float[]{pos.y};
            pointLightZ = new float[]{pos.z};
            pointLightIntensity = new float[]{pointLight.getIntensity()};

            SpotLight spotLight = sceneLights.getSpotLights().get(0);
            pointLight = spotLight.getPointLight();
            color = pointLight.getColor();
            pos = pointLight.getPosition();
            spotLightColor = new float[]{color.x, color.y, color.z};
            spotLightX = new float[]{pos.x};
            spotLightY = new float[]{pos.y};
            spotLightZ = new float[]{pos.z};
            spotLightIntensity = new float[]{pointLight.getIntensity()};
            spotLightCuttoff = new float[]{spotLight.getCutOffAngle()};
            Vector3f coneDir = spotLight.getConeDirection();
            dirConeX = new float[]{coneDir.x};
            dirConeY = new float[]{coneDir.y};
            dirConeZ = new float[]{coneDir.z};

            DirectionalLight dirLight = sceneLights.getDirLight();
            color = dirLight.getColor();
            pos = dirLight.getDirection();
            dirLightColor = new float[]{color.x, color.y, color.z};
            dirLightX = new float[]{pos.x};
            dirLightY = new float[]{pos.y};
            dirLightZ = new float[]{pos.z};
            dirLightIntensity = new float[]{dirLight.getIntensity()};
        }

        @Override
        public void handleInput() {
            boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
            if (consumed) {
                SceneLights sceneLights = scene.getSceneLights();
                AmbientLight ambientLight = sceneLights.getAmbientLight();
                ambientLight.setIntensity(ambientFactor[0]);
                ambientLight.setColor(ambientColor[0], ambientColor[1], ambientColor[2]);

                PointLight pointLight = sceneLights.getPointLights().get(0);
                pointLight.setPosition(pointLightX[0], pointLightY[0], pointLightZ[0]);
                pointLight.setColor(pointLightColor[0], pointLightColor[1], pointLightColor[2]);
                pointLight.setIntensity(pointLightIntensity[0]);

                SpotLight spotLight = sceneLights.getSpotLights().get(0);
                pointLight = spotLight.getPointLight();
                pointLight.setPosition(spotLightX[0], spotLightY[0], spotLightZ[0]);
                pointLight.setColor(spotLightColor[0], spotLightColor[1], spotLightColor[2]);
                pointLight.setIntensity(spotLightIntensity[0]);
                spotLight.setCutOffAngle(spotLightColor[0]);
                spotLight.setConeDirection(dirConeX[0], dirConeY[0], dirConeZ[0]);

                DirectionalLight dirLight = sceneLights.getDirLight();
                dirLight.setPosition(dirLightX[0], dirLightY[0], dirLightZ[0]);
                dirLight.setColor(dirLightColor[0], dirLightColor[1], dirLightColor[2]);
                dirLight.setIntensity(dirLightIntensity[0]);

            }
        }

        @Override
        public void drawGui() {
            ImGui.newFrame();
            ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
            ImGui.setNextWindowSize(450, 400);

            ImGui.begin("Lights controls");
            if (ImGui.collapsingHeader("Ambient Light")) {
                ImGui.sliderFloat("Ambient factor", ambientFactor, 0.0f, 1.0f, "%.2f");
                ImGui.colorEdit3("Ambient color", ambientColor);
            }

            if (ImGui.collapsingHeader("Point Light")) {
                ImGui.sliderFloat("Point Light - x", pointLightX, -10.0f, 10.0f, "%.2f");
                ImGui.sliderFloat("Point Light - y", pointLightY, -10.0f, 10.0f, "%.2f");
                ImGui.sliderFloat("Point Light - z", pointLightZ, -10.0f, 10.0f, "%.2f");
                ImGui.colorEdit3("Point Light color", pointLightColor);
                ImGui.sliderFloat("Point Light Intensity", pointLightIntensity, 0.0f, 1.0f, "%.2f");
            }

            if (ImGui.collapsingHeader("Spot Light")) {
                ImGui.sliderFloat("Spot Light - x", spotLightX, -10.0f, 10.0f, "%.2f");
                ImGui.sliderFloat("Spot Light - y", spotLightY, -10.0f, 10.0f, "%.2f");
                ImGui.sliderFloat("Spot Light - z", spotLightZ, -10.0f, 10.0f, "%.2f");
                ImGui.colorEdit3("Spot Light color", spotLightColor);
                ImGui.sliderFloat("Spot Light Intensity", spotLightIntensity, 0.0f, 1.0f, "%.2f");
                ImGui.separator();
                ImGui.sliderFloat("Spot Light cutoff", spotLightCuttoff, 0.0f, 360.0f, "%2.f");
                ImGui.sliderFloat("Dir cone - x", dirConeX, -1.0f, 1.0f, "%.2f");
                ImGui.sliderFloat("Dir cone - y", dirConeY, -1.0f, 1.0f, "%.2f");
                ImGui.sliderFloat("Dir cone - z", dirConeZ, -1.0f, 1.0f, "%.2f");
            }

            if (ImGui.collapsingHeader("Dir Light")) {
                ImGui.sliderFloat("Dir Light - x", dirLightX, -1.0f, 1.0f, "%.2f");
                ImGui.sliderFloat("Dir Light - y", dirLightY, -1.0f, 1.0f, "%.2f");
                ImGui.sliderFloat("Dir Light - z", dirLightZ, -1.0f, 1.0f, "%.2f");
                ImGui.colorEdit3("Dir Light color", dirLightColor);
                ImGui.sliderFloat("Dir Light Intensity", dirLightIntensity, 0.0f, 1.0f, "%.2f");
            }

            ImGui.end();
            ImGui.endFrame();
            ImGui.render();
        }
    }

}
