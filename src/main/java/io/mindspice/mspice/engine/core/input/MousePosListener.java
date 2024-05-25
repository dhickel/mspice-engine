package io.mindspice.mspice.engine.core.input;

import org.joml.Vector2f;

import java.util.function.Consumer;


public class MousePosListener {
    private final Vector2f pos = new Vector2f();
    private boolean isListening = true;
    private boolean hasNext;

    public MousePosListener() {
    }

    public boolean isListening() {
        return isListening;
    }

    void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    boolean hasNext() {
        return hasNext;
    }

    public void consume(Consumer<Vector2f> consumer) {
        consumer.accept(pos);
    }

    public Vector2f getPos() {
        return pos;
    }

    public void offerInput(double posX, double posY) {
        pos.set(posX, posY);
    }

}
