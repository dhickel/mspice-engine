package io.mindspice.mspice.engine.core.renderer.components;

public abstract class GuiScene {
    public void render() {
        drawGui();
        handleInput();
    };

    public abstract void handleInput();
    public abstract void drawGui();
}
