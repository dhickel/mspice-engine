package io.mindspice.mspice.game;

import io.mindspice.mspice.graphics.components.Entity;
import io.mindspice.mspice.graphics.primatives.Mesh;
import io.mindspice.mspice.graphics.primatives.Model;
import io.mindspice.mspice.graphics.components.Scene;
import io.mindspice.mspice.core.game.GameEngine;
import io.mindspice.mspice.core.game.GameInput;
import io.mindspice.mspice.core.game.GameWindow;

import io.mindspice.mspice.core.interfaces.IGameLogic;
import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.graphics.opengl.Render;
import io.mindspice.mspice.singletons.InputMap;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        int width = 1920;
        int height = 1080;
        float fov = 60.0f;

        GameEngine engine = GameEngine.getInstance();
        InputMap.getInstance().set(GLFW.GLFW_KEY_R, InputAction.RESIZE_SCREEN);
        InputMap.getInstance().set(GLFW.GLFW_KEY_W, InputAction.MOVE_UP);
        InputMap.getInstance().set(GLFW.GLFW_KEY_S, InputAction.MOVE_DOWN);
        InputMap.getInstance().set(GLFW.GLFW_KEY_A, InputAction.MOVE_LEFT);
        InputMap.getInstance().set(GLFW.GLFW_KEY_D, InputAction.MOVE_RIGHT);
        InputMap.getInstance().set(0, InputAction.SCROLL_UP);
        InputMap.getInstance().set(0, InputAction.SCROLL_DOWN);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                engine.cleanup();
            }
        });
        GameInput input = new GameInput();
        GameWindow window = new GameWindow("test engine", new int[]{width, height}, false);
        Logic logic = new Logic();
        Render render = new Render(window);
        Scene scene = new Scene(width, height, fov);
        logic.init(window, scene, render);
        engine.init(window, input, new Render(window), logic);
        engine.setCurrScene(scene);

        engine.setFrameUPS(144);
        window.regListeners();

        //window.setVSyncEnabled(true);
        engine.run();

    }
    private static Entity cubeEntity;
    private static Vector4f displInc = new Vector4f();
    private static float rotation;

    private static class Logic implements IGameLogic {

        @Override
        public void init(GameWindow window, Scene scene, Render render) {

            float[] positions = new float[]{
                    // VO
                    -0.5f, 0.5f, 0.5f,
                    // V1
                    -0.5f, -0.5f, 0.5f,
                    // V2
                    0.5f, -0.5f, 0.5f,
                    // V3
                    0.5f, 0.5f, 0.5f,
                    // V4
                    -0.5f, 0.5f, -0.5f,
                    // V5
                    0.5f, 0.5f, -0.5f,
                    // V6
                    -0.5f, -0.5f, -0.5f,
                    // V7
                    0.5f, -0.5f, -0.5f,
            };
            float[] colors = new float[]{
                    0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.0f, 0.5f,
                    0.0f, 0.5f, 0.5f,
                    0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.0f, 0.5f,
                    0.0f, 0.5f, 0.5f,
            };
            int[] indices = new int[]{
                    // Front face
                    0, 1, 3, 3, 1, 2,
                    // Top Face
                    4, 0, 3, 5, 4, 3,
                    // Right face
                    3, 2, 7, 5, 3, 7,
                    // Left face
                    6, 1, 0, 6, 0, 4,
                    // Bottom face
                    2, 1, 6, 2, 6, 7,
                    // Back face
                    7, 6, 4, 7, 4, 5,
            };
            List<Mesh> meshList = new ArrayList<>();
            Mesh mesh = new Mesh(positions, colors, indices);
            meshList.add(mesh);
            String cubeModelId = "cube-model";
            Model model = new Model(cubeModelId, meshList);
            scene.addModel(model);

            cubeEntity = new Entity("cube-entity", cubeModelId);
            cubeEntity.setPosition(0, 0, -2);
            cubeEntity.updateModelMatrix();
            scene.addEntity(cubeEntity);

        }

        @Override
        public void input(GameWindow window, Scene scene, long diffTimeMilli) {

        }

        @Override
        public void update(GameWindow window, Scene scene, long diffTimeMilli) {
            rotation += 1.5;
            if (rotation > 360) {
                rotation = 0;
            }
            cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
            cubeEntity.updateModelMatrix();
        }

        @Override
        public void cleanup() {

        }
    }


}
