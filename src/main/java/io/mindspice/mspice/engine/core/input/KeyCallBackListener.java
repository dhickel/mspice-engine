package io.mindspice.mspice.engine.core.input;

import io.mindspice.mspice.engine.util.collections.CircularKeyQueue;
import io.mindspice.mspice.engine.util.consumers.KeyActionConsumer;
import org.lwjgl.glfw.GLFW;

import java.util.BitSet;
import java.util.List;


public class KeyCallBackListener implements KeyListener {
    private boolean isListening = true;
    private final CircularKeyQueue inputQueue;
    private final ActionType listeningFor;
    private final KeyActionConsumer consumer;
    private final BitSet listeningForInputs = new BitSet(GLFW.GLFW_KEY_LAST);

    public KeyCallBackListener(ActionType listeningFor, List<InputAction> inputs, int queueSize, KeyActionConsumer consumer) {
        this.listeningFor = listeningFor;
        inputs.forEach(i -> listeningForInputs.set(i.ordinal()));
        inputQueue = new CircularKeyQueue(queueSize);
        this.consumer = consumer;

    }

    @Override
    public boolean isListenerFor(ActionType actionType) {
        return listeningFor == actionType;
    }

    @Override
    public void offer(InputAction inputAction, int keyAction) {
        if (isListening && listeningForInputs.get(inputAction.ordinal())) {
            consumer.accept(inputAction, keyAction);
        }
    }

    @Override
    public void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    @Override
    public boolean isListening() {
        return isListening;
    }

    @Override
    public ActionType getListeningFor() {
        return listeningFor;
    }

    public CircularKeyQueue getQueue() {
        return inputQueue;
    }
}
