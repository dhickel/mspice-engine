package io.mindspice.mspice.engine.game;

import io.mindspice.mspice.engine.core.PlayerState;
import io.mindspice.mspice.engine.core.engine.Engine;
import io.mindspice.mspice.engine.core.renderer.components.*;
import io.mindspice.mspice.engine.core.renderer.lighting.AmbientLight;
import io.mindspice.mspice.engine.core.renderer.lighting.DirectionalLight;
import io.mindspice.mspice.engine.core.renderer.lighting.PointLight;
import io.mindspice.mspice.engine.core.renderer.lighting.SpotLight;
import io.mindspice.mspice.engine.core.window.Window;
import io.mindspice.mspice.engine.core.engine.IGameLogic;
import io.mindspice.mspice.engine.core.graphics.primatives.Model;
import io.mindspice.mspice.engine.core.engine.GameEngine;

import io.mindspice.mspice.engine.core.input.InputAction;
import io.mindspice.mspice.engine.util.ModelLoader;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        int width = 1920;
        int height = 1080;
        float fov = 60.0f;

        GameEngine engine = GameEngine.getInstance();
        PlayerState ps = new PlayerState(width, height, fov);

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

        Scene scene = new Scene(width, height, fov);
        Logic logic = new Logic();



        ps.loadScene(scene);
        logic.setCamera(ps.getViewport().getCamera());
        logic.init(scene);
        engine.init(ps, logic);
        engine.setFrameUPS(144);
        Engine.GET().init(engine);
        Engine.GET().addPlayerState(ps);
        //window.setVSyncEnabled(true);

    }

    private static class Logic implements IGameLogic {
        private static Entity cubeEntity;
        private static float rotation;
        private static final int NUM_CHUNKS = 40;

        private Entity[][] terrainEntities;

        private Camera camera;
        private Scene scene;

        public void setCamera(Camera camera) {
            this.camera = camera;
        }

        @Override
        public void init(Scene scene) {
            String quadModelId = "quad-model";
            Model quadModel = ModelLoader.loadModel("quad-model", "/home/mindspice/code/Java/game/mspice-engine/src/main/resources/terrain.obj",
                    scene.getTextureCache());
            scene.addModel(quadModel);

            int numRows = NUM_CHUNKS * 2 + 1;
            int numCols = numRows;
            terrainEntities = new Entity[numRows][numCols];
            for (int j = 0; j < numRows; j++) {
                for (int i = 0; i < numCols; i++) {
                    Entity entity = new Entity("TERRAIN_" + j + "_" + i, quadModelId);
                    terrainEntities[j][i] = entity;
                    scene.addEntity(entity);
                }
            }

            SceneLights sceneLights = new SceneLights();
            AmbientLight ambientLight = sceneLights.getAmbientLight();
            ambientLight.setIntensity(0.5f);
            ambientLight.setColor(0.3f, 0.3f, 0.3f);

            DirectionalLight dirLight = sceneLights.getDirLight();
            dirLight.setPosition(0, 1, 0);
            dirLight.setIntensity(1.0f);
            scene.setSceneLights(sceneLights);


            SkyBox skyBox = new SkyBox("/home/mindspice/code/Java/game/mspice-engine/src/main/resources/skybox.obj", scene.getTextureCache());
            skyBox.getSkyBoxEntity().setScale(50);
            scene.setSkyBox(skyBox);

            camera.move(new Vector3f(0f, 0.1f, 0f));
            scene.setFog(new Fog(true, new Vector3f(0.6f, 0.4f, 0.5f), 0.95f));
            updateTerrain();

        }

        public void updateTerrain() {
            int cellSize = 10;
            Vector3f cameraPos = camera.getPosition();
            int cellCol = (int) (cameraPos.x / cellSize);
            int cellRow = (int) (cameraPos.z / cellSize);

            int numRows = NUM_CHUNKS * 2 + 1;
            int numCols = numRows;
            int zOffset = -NUM_CHUNKS;
            float scale = cellSize / 2.0f;
            for (int j = 0; j < numRows; j++) {
                int xOffset = -NUM_CHUNKS;
                for (int i = 0; i < numCols; i++) {
                    Entity entity = terrainEntities[j][i];
                    entity.setScale(scale);
                    entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
                    entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
                    xOffset++;
                }
                zOffset++;
            }
        }

        @Override
        public void input(Window window, Scene scene, long diffTimeMilli) {

        }

        @Override
        public void update(long delta) {
            updateTerrain();
        }

        @Override
        public void cleanup() {

        }
    }

}
