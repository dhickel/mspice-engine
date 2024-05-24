package io.mindspice.mspice.game;

import io.mindspice.mspice.graphics.components.Entity;
import io.mindspice.mspice.graphics.primatives.Material;
import io.mindspice.mspice.graphics.primatives.Mesh;
import io.mindspice.mspice.graphics.primatives.Model;
import io.mindspice.mspice.graphics.components.Scene;
import io.mindspice.mspice.core.game.GameEngine;
import io.mindspice.mspice.core.game.GameInput;
import io.mindspice.mspice.core.game.GameWindow;

import io.mindspice.mspice.core.interfaces.IGameLogic;
import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.graphics.opengl.Render;
import io.mindspice.mspice.graphics.primatives.Texture;
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
                    // V0
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

                    // For text coords in top face
                    // V8: V4 repeated
                    -0.5f, 0.5f, -0.5f,
                    // V9: V5 repeated
                    0.5f, 0.5f, -0.5f,
                    // V10: V0 repeated
                    -0.5f, 0.5f, 0.5f,
                    // V11: V3 repeated
                    0.5f, 0.5f, 0.5f,

                    // For text coords in right face
                    // V12: V3 repeated
                    0.5f, 0.5f, 0.5f,
                    // V13: V2 repeated
                    0.5f, -0.5f, 0.5f,

                    // For text coords in left face
                    // V14: V0 repeated
                    -0.5f, 0.5f, 0.5f,
                    // V15: V1 repeated
                    -0.5f, -0.5f, 0.5f,

                    // For text coords in bottom face
                    // V16: V6 repeated
                    -0.5f, -0.5f, -0.5f,
                    // V17: V7 repeated
                    0.5f, -0.5f, -0.5f,
                    // V18: V1 repeated
                    -0.5f, -0.5f, 0.5f,
                    // V19: V2 repeated
                    0.5f, -0.5f, 0.5f,
            };
            float[] textCoords = new float[]{
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,

                    0.0f, 0.0f,
                    0.5f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,

                    // For text coords in top face
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.0f, 1.0f,
                    0.5f, 1.0f,

                    // For text coords in right face
                    0.0f, 0.0f,
                    0.0f, 0.5f,

                    // For text coords in left face
                    0.5f, 0.0f,
                    0.5f, 0.5f,

                    // For text coords in bottom face
                    0.5f, 0.0f,
                    1.0f, 0.0f,
                    0.5f, 0.5f,
                    1.0f, 0.5f,
            };
            int[] indices = new int[]{
                    // Front face
                    0, 1, 3, 3, 1, 2,
                    // Top Face
                    8, 10, 11, 9, 8, 11,
                    // Right face
                    12, 13, 7, 5, 12, 7,
                    // Left face
                    14, 15, 6, 4, 14, 6,
                    // Bottom face
                    16, 18, 19, 17, 16, 19,
                    // Back face
                    4, 6, 7, 5, 4, 7,};
            Texture texture = scene.getTextureCache().createTexture("src/main/resources/cube.png");
            Material material = new Material();
            material.setTexturePath(texture.getTexturePath());
            List<Material> materialList = new ArrayList<>();
            materialList.add(material);

            Mesh mesh = new Mesh(positions, textCoords, indices);
            material.getMeshList().add(mesh);
            Model cubeModel = new Model("cube-model", materialList);
            scene.addModel(cubeModel);

            cubeEntity = new Entity("cube-entity", cubeModel.getId());
            cubeEntity.setPosition(0, 0, -2);
            scene.addEntity(cubeEntity);

        }

        @Override
        public void input(GameWindow window, Scene scene, long diffTimeMilli) {

        }

        @Override
        public void update(GameWindow window, Scene scene, long diffTimeMilli) {
            rotation += 0.25;
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
