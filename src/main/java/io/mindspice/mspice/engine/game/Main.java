package io.mindspice.mspice.engine.game;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import io.mindspice.mspice.engine.core.PlayerState;
import io.mindspice.mspice.engine.core.engine.Engine;
import io.mindspice.mspice.engine.core.graphics.primatives.Material;
import io.mindspice.mspice.engine.core.graphics.primatives.Mesh;
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
import io.mindspice.mspice.engine.marchingcubes.FastNoiseLite;
import io.mindspice.mspice.engine.marchingcubes.MCFactory;
import io.mindspice.mspice.engine.marchingcubes.ScalarField;
import io.mindspice.mspice.engine.util.ModelLoader;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.List;


public class Main {




    public static class JNoiseScalarField implements ScalarField {
        private JNoise noiseSource;

        public JNoiseScalarField(JNoise noiseSource) {
            this.noiseSource = noiseSource;
        }

        @Override
        public float getDensity(float x, float y, float z) {
            // Convert JNoise 3D noise output to density required by Marching Cubes
            return (float) noiseSource.evaluateNoise(x, y, z);
        }
    }

    public static void main(String[] args) throws IOException {
        int width = 1920;
        int height = 1080;
        float fov = 60.0f;

        GameEngine engine = GameEngine.getInstance();
        PlayerState ps = new PlayerState(width, height, fov);

        System.out.println("generaing mesh");
        Mesh mesh = MCFactory.generateMesh(2,2,2, new FastNoiseLite());
        System.out.println("Finsihed mesh gen");


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
        logic.setMesh(mesh);



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
        private Mesh mesh;

        public void setCamera(Camera camera) {
            this.camera = camera;
        }

        public void setMesh(Mesh mesh) {
            this.mesh = mesh;
        }

        @Override
        public void init(Scene scene) {
            System.out.println("Start Gen");
//             JNoise noisePipeline=JNoise.newBuilder().perlin(3301, Interpolation.COSINE, FadeFunction.QUINTIC_POLY).octavate(4,1.0,1.0, FractalFunction.FBM,false).build();
//            JNoiseScalarField scalarField = new JNoiseScalarField(noisePipeline);
//
            Vector3f origin = new Vector3f(0, 0, 0);
            int size = 20;
            float cubeSize = 0.1f;

            // Generate the mesh using the Marching Cubes algorithm
            System.out.println();
            Material mat = new Material();
            mat.setMeshList(List.of(mesh));
            Model model = new Model("test", List.of(mat));
            scene.addModel(model);
            System.out.println("Here");

            SceneLights sceneLights = new SceneLights();
            sceneLights.getAmbientLight().setIntensity(0.2f);
            DirectionalLight dirLight = sceneLights.getDirLight();
            dirLight.setPosition(1, 1, 0);
            dirLight.setIntensity(1.0f);
            scene.setSceneLights(sceneLights);

            camera.moveUp(5.0f);
            camera.adjustRotation((float) Math.toRadians(90), 0);

            var lightAngle = -76;
            double angRad = Math.toRadians(lightAngle);
            dirLight.getDirection().x = (float) Math.sin(angRad);
            dirLight.getDirection().y = (float) Math.cos(angRad);
        }

        public void updateTerrain() {
//            int cellSize = 10;
//            Vector3f cameraPos = camera.getPosition();
//            int cellCol = (int) (cameraPos.x / cellSize);
//            int cellRow = (int) (cameraPos.z / cellSize);
//
//            int numRows = NUM_CHUNKS * 2 + 1;
//            int numCols = numRows;
//            int zOffset = -NUM_CHUNKS;
//            float scale = cellSize / 2.0f;
//            for (int j = 0; j < numRows; j++) {
//                int xOffset = -NUM_CHUNKS;
//                for (int i = 0; i < numCols; i++) {
//                    Entity entity = terrainEntities[j][i];
//                    entity.setScale(scale);
//                    entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
//                    entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
//                    xOffset++;
//                }
//                zOffset++;
//            }
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
