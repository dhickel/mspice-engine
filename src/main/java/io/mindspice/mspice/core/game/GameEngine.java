package io.mindspice.mspice.core;

import io.mindspice.mspice.enums.ActionType;
import io.mindspice.mspice.game.Scene;
import io.mindspice.mspice.input.KeyEventManager;
import io.mindspice.mspice.opengl.Render;
import io.mindspice.mspice.input.KeyListener;
import io.mindspice.mspice.input.MousePosListener;
import io.mindspice.mspice.util.consumers.BiDoubleConsumer;
import io.mindspice.mspice.util.consumers.IntDoubleConsumer;
import io.mindspice.mspice.util.consumers.KeyActionConsumer;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;


public class GameEngine implements Runnable {

    private static final GameEngine INSTANCE = new GameEngine();
    private GameWindow gameWindow;
    private GameInput gameInput;
    private Render render;
    private Scene currScene;
    private boolean running;
    private int frameUPS = 60;
    private int logicUPS = 60;

    private GameEngine() { }

    public void init(GameWindow window, GameInput input, Render render) {
        this.gameWindow = window;
        this.gameInput = input;
        gameInput.bindCallBacks(window.getWindowHandle());
        this.render = render;
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

    public void cleanup() {
        gameWindow.cleanup();
        gameInput.cleanup();
        GLFW.glfwDestroyWindow(gameWindow.getWindowHandle());
        GLFW.glfwTerminate();
    }

    private void resize() {
        // Nothing to be done yet
    }

    @Override
    public void run() {
        running = true;
        long initialTime = System.nanoTime();
        double timeU = GameConst.NANO_SEC / logicUPS;
        double timeR = frameUPS > 0 ? GameConst.NANO_SEC / frameUPS : 0;
        double deltaUpdate = 0;
        double deltaFps = 0;
        long lastTime = initialTime;
        int frames = 0;
        long fpsTimer = System.currentTimeMillis();

        KeyListener screenListener = new KeyListener(new ActionType[]{ActionType.SCREEN}, 10);
        KeyListener gameListener = new KeyListener(new ActionType[]{ActionType.GAME_INPUT},10);
        MousePosListener mPosListener = new MousePosListener();
        gameInput.regMousePosListener(mPosListener);
        gameInput.regKeyboardListener(screenListener);
        gameInput.regKeyboardListener(gameListener);

        KeyActionConsumer keyInputConsumer = (keyCode, keyState) -> {
            System.out.println(keyCode + ":" + keyState);
        };

        BiDoubleConsumer mouseInputConsumer = (posX, posY) -> {
            System.out.println(posX + ":" + posY);
        };

        IntDoubleConsumer mouseScrollConsumer = (pos, offset) -> {
            System.out.println(pos + ":" + offset);
        };



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
                // add render
               // mPosListener.consume(mouseInputConsumer);


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
            LockSupport.parkNanos(1000);
        }
        cleanup();
    }

    public GameInput getGameInput() {
        return gameInput;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public void stop() {
        running = false;
    }

    public void start() {
        running = true;
    }

}
