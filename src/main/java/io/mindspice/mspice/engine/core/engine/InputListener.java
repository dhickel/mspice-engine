package io.mindspice.mspice.engine.core.engine;

import io.mindspice.mspice.engine.core.input.InputManager;


public interface InputListener {
    void registerListener(InputManager inputManager);
    void setListening(boolean listening);
    boolean isListening();
}
