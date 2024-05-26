package io.mindspice.mspice.engine.core.input;

import io.mindspice.mspice.engine.util.consumers.BiDoubleConsumer;
import io.mindspice.mspice.engine.util.consumers.KeyActionConsumer;
import org.joml.Vector2f;

import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.IntConsumer;


public class MouseCallBackListener implements MouseListener {
    private final Vector2f pos = new Vector2f();
    private final ActionType actionType;
    private boolean isListening = true;
    private KeyActionConsumer buttonConsumer;
    private BiDoubleConsumer posConsumer;
    private IntConsumer scrollConsumer;

    public MouseCallBackListener(ActionType listeningFor) {
        this.actionType = listeningFor;
    }

    public boolean isListening() {
        return isListening;
    }

    public boolean isListenerFor(ActionType actionType) {
        return this.actionType == actionType;
    }

    public void setPosConsumer(BiDoubleConsumer posConsumer) {
        this.posConsumer = posConsumer;
    }

    public void setScrollConsumer(IntConsumer scrollConsumer) {
        this.scrollConsumer = scrollConsumer;
    }

    public void setButtonConsumer(KeyActionConsumer buttonConsumer) {
        this.buttonConsumer = buttonConsumer;
    }

    @Override
    public ActionType getListeningFor() {
        return actionType;
    }

    @Override
    public void offerPos(double x, double y) {
        pos.x = (float) x;
        pos.y = (float) y;
        if (posConsumer != null) {
            posConsumer.accept(x, y);
        }
    }

    @Override
    public void offerButton(InputAction inputAction, int keyAction) {
        if (buttonConsumer != null) {
            buttonConsumer.accept(inputAction, keyAction);
        }
    }

    @Override
    public void offerScroll(int val) {
        if (scrollConsumer != null) {
            scrollConsumer.accept(val);
        }
    }

    @Override
    public void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    public void consume(Consumer<Vector2f> consumer) {
        consumer.accept(pos);
    }

    public Vector2f getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MouseCallBackListener.class.getSimpleName() + "[", "]")
                .add("pos=" + pos)
                .add("actionType=" + actionType)
                .add("isListening=" + isListening)
                .add("buttonConsumer=" + buttonConsumer)
                .add("posConsumer=" + posConsumer)
                .add("scrollConsumer=" + scrollConsumer)
                .toString();
    }
}
