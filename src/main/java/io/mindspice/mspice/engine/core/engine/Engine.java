package io.mindspice.mspice.engine.core.engine;

import io.mindspice.mspice.engine.core.PlayerState;

import java.util.Arrays;


public class Engine {
    private static final Engine instance = new Engine();
    private GameEngine gameEngine;
    private PlayerState[] playerStates = new PlayerState[0];
    private int pCount = 0;

    private Engine() { }

    public static Engine GET() {
        return instance;
    }

    public void init(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        gameEngine.run();
    }

    public int addPlayerState(PlayerState playerState) {
        pCount++;
        playerStates = Arrays.copyOf(playerStates, pCount);
        playerStates[pCount - 1] = playerState;
        return pCount - 1;
    }

    public PlayerState getPlayerState(int id) {
        return playerStates[id];
    }
}
