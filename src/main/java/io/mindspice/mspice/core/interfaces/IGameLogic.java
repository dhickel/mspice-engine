package io.mindspice.mspice.core.interfaces;

import io.mindspice.mspice.core.game.GameWindow;
import io.mindspice.mspice.graphics.components.Scene;
import io.mindspice.mspice.graphics.opengl.Render;


public interface IGameLogic {

    void init(GameWindow window, Scene scene, Render render);

    void input(GameWindow window, Scene scene, long diffTimeMilli);

    void update(GameWindow window, Scene scene, long diffTimeMilli);

    void cleanup();

}
