package io.mindspice.mspice.engine.core.engine;

import io.mindspice.mspice.engine.core.window.GameWindow;
import io.mindspice.mspice.engine.core.renderer.components.Scene;
import io.mindspice.mspice.engine.core.renderer.opengl.Renderer;


public interface IGameLogic {

    void init(Scene scene);

    void input(GameWindow window, Scene scene, long diffTimeMilli);

    void update(long delta);

    void cleanup();

}
