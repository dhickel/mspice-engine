package io.mindspice.mspice.engine.game;

import io.mindspice.mspice.engine.core.PlayerState;
import io.mindspice.mspice.engine.core.window.GameWindow;
import io.mindspice.mspice.engine.core.engine.IGameLogic;
import io.mindspice.mspice.engine.core.renderer.components.Entity;
import io.mindspice.mspice.engine.core.graphics.primatives.Model;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import io.mindspice.mspice.engine.core.engine.GameEngine;

import io.mindspice.mspice.engine.core.input.InputAction;
import io.mindspice.mspice.engine.util.ModelLoader;
import org.joml.Vector4f;
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
        ps.getInputMap().set(0, InputAction.SCROLL_UP);
        ps.getInputMap().set(0, InputAction.SCROLL_DOWN);


        Logic logic = new Logic();
        Scene scene = new Scene(width, height, fov);
        logic.init(scene);
        ps.loadScene(scene);
        engine.init(ps, logic);
        engine.setFrameUPS(144);

        //window.setVSyncEnabled(true);
        engine.run();

    }


    private static class Logic implements IGameLogic {
        private static Entity cubeEntity;
        private static Vector4f displInc = new Vector4f();
        private static float rotation;

        @Override
        public void init(Scene scene) {
            Model cubeModel = ModelLoader.loadModel("cube-model", "/home/mindspice/code/Java/game/mspice-engine/src/main/resources/cube.obj",
                    scene.getTextureCache());
            scene.addModel(cubeModel);

            cubeEntity = new Entity("cube-entity", cubeModel.getId());
            cubeEntity.setPosition(0, 0, -2);
            scene.addEntity(cubeEntity);

        }

        @Override
        public void input(GameWindow window, Scene scene, long diffTimeMilli) {

        }

        @Override
        public void update(long delta) {
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
