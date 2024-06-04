package io.mindspice.mspice.engine.core.engine;

import io.mindspice.mspice.engine.core.renderer.Scene;
import io.mindspice.mspice.engine.core.window.Window;


public interface IGameLogic {

    void init(Scene scene);

    void input(Window window, Scene scene, long diffTimeMilli);

    void update(long delta);

    void cleanup();

}
