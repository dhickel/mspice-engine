package io.mindspice.mspice.engine.core.input;

import io.mindspice.mspice.engine.util.collections.CircularKeyQueue;
import io.mindspice.mspice.engine.util.consumers.KeyActionConsumer;
import org.lwjgl.glfw.GLFW;

import java.util.BitSet;
import java.util.List;


public class KeyCacheListener implements KeyListener {
    private boolean isListening = true;
    private final CircularKeyQueue inputQueue;
    private final ActionType listeningFor;
    private final BitSet listeningForInputs = new BitSet(GLFW.GLFW_KEY_LAST);

    public KeyCacheListener(ActionType listeningFor, int queueSize, List<InputAction> inputs) {
        this.listeningFor = listeningFor;
        inputQueue = new CircularKeyQueue(queueSize);
        inputs.forEach(i -> listeningForInputs.set(i.ordinal()));
    }

    @Override
    public boolean isListenerFor(ActionType actionType) {
        return listeningFor == actionType;
    }

    @Override
    public void offer(InputAction inputAction, int keyAction) {
        if (isListening && listeningForInputs.get(inputAction.ordinal())) {
            inputQueue.add(inputAction, keyAction);
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

    public void consume(KeyActionConsumer consumer) {
        inputQueue.consume(consumer);
    }

    public boolean hasInputs() {
        return !inputQueue.isEmpty();
    }

    public CircularKeyQueue getQueue() {
        return inputQueue;
    }
}
