package io.mindspice.mspice.engine.core.engine;

import io.mindspice.mspice.engine.core.PlayerState;
import io.mindspice.mspice.engine.core.window.GameWindow;
import io.mindspice.mspice.engine.enums.ActionType;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import io.mindspice.mspice.engine.core.renderer.opengl.Renderer;
import io.mindspice.mspice.engine.core.input.KeyListener;
import io.mindspice.mspice.engine.core.input.MousePosListener;
import io.mindspice.mspice.engine.util.consumers.BiDoubleConsumer;
import io.mindspice.mspice.engine.util.consumers.KeyActionConsumer;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.locks.LockSupport;


public class GameEngine implements Runnable, OnCleanUp {

    private static final GameEngine INSTANCE = new GameEngine();
    private PlayerState playerState;
    private Scene currScene;
    private IGameLogic gameLogic;
    private boolean running;
    private int frameUPS = 60;
    private int logicUPS = 60;

    private GameEngine() { }

    public void init(PlayerState playerState, IGameLogic gameLogic) {
        this.playerState = playerState;
        this.gameLogic = gameLogic;

        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    public void setCurrScene(Scene scene) {
        currScene = scene;
    }

    public void setFrameUPS(int updateRate) {
        frameUPS = updateRate;
    }

    public void setLogicUPS(int updateRate) {
        logicUPS = updateRate;
    }

    public static GameEngine getInstance() {
        return INSTANCE;
    }

    @Override
    public void cleanup() {
        currScene.cleanup();
        gameLogic.cleanup();
        playerState.cleanup();

        GLFW.glfwDestroyWindow(playerState.getGameWindow().getWindowHandle());
        GLFW.glfwTerminate();
    }

    private void resize() {
        // Nothing to be done yet
    }

    @Override
    public void run() {
        running = true;
        long initTime = System.nanoTime();
        double timeU = GameConst.NANO_SEC / logicUPS;
        double timeR = frameUPS > 0 ? GameConst.NANO_SEC / frameUPS : 0;
        double deltaUpdate = 0;
        double deltaFps = 0;
        long lastTime = initTime;
        int frames = 0;
        long fpsTimer = System.currentTimeMillis();

        final KeyListener screenListener = new KeyListener(new ActionType[]{ActionType.SCREEN}, 10);
        final KeyListener gameListener = new KeyListener(new ActionType[]{ActionType.GAME_INPUT}, 10);
        final MousePosListener mPosListener = new MousePosListener();
        final KeyListener scrollListener = new KeyListener(new ActionType[]{ActionType.GAME_INPUT}, 10);
        playerState.getInputManager().regKeyListener(screenListener);
        playerState.getInputManager().regKeyListener(gameListener);
        playerState.getInputManager().regMousePosListener(mPosListener);

        final KeyActionConsumer keyInputConsumer = (keyCode, keyState) -> {
            System.out.println(keyCode + ":" + keyState);
        };

        final BiDoubleConsumer mouseInputConsumer = (posX, posY) -> {
            //  System.out.println(posX + ":" + posY);
        };

        final GameWindow gameWindow = playerState.getGameWindow();
        final Renderer renderer = playerState.getRenderer();

        while (running && !gameWindow.windowShouldClose()) {
            gameWindow.pollEvents();

            long now = System.nanoTime();
            long elapsed = now - lastTime;
            deltaUpdate += elapsed / timeU;
            deltaFps += elapsed / timeR;

            while (deltaUpdate >= 1) {
                deltaUpdate--;
                //update set update logic here
            }

            if (deltaFps >= 1) {
                mPosListener.consume(mouseInputConsumer);
                scrollListener.consume(keyInputConsumer);
                gameListener.consume(keyInputConsumer);

                // gameLogic.update(gameWindow, currScene, (long) (elapsed / GameConst.NANO_SEC));
                gameLogic.update(elapsed);
                gameLogic.input(gameWindow, currScene, elapsed);

                renderer.render(currScene);
                gameWindow.update();

                deltaFps--;
                frames++; // Increment the frame counter
            }

            if (System.currentTimeMillis() - fpsTimer > 1000) {
                gameWindow.setWindowTitle("FPS: " + frames);
                fpsTimer = System.currentTimeMillis();
                frames = 0;
            }

            lastTime = now;
            LockSupport.parkNanos(500);
        }
        cleanup();
    }

    // TODO vsync

    public void stop() {
        running = false;
    }

    public void start() {
        running = true;
    }

}
