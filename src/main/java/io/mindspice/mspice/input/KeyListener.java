package io.mindspice.mspice.input;

import io.mindspice.mspice.enums.ActionType;
import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.util.collections.CircularKeyQueue;
import io.mindspice.mspice.util.consumers.KeyActionConsumer;


public class KeyListener {
    private boolean isListening = true;
    private CircularKeyQueue inputQueue;
    private ActionType[] listeningFor;

    public KeyListener(ActionType[] listeningFor, int queueSize) {
        this.listeningFor = listeningFor;
        inputQueue = new CircularKeyQueue(queueSize);
    }


    public boolean isListenerFor(ActionType actionType) {
        for (int i = 0; i < listeningFor.length; i++) {
            if (listeningFor[i] == actionType) {
                return true;
            }
        }
        return false;
    }


    public void offerInput(InputAction inputAction, int keyAction) {
        if (!isListening) { return; }
        inputQueue.add(inputAction, keyAction);
    }


    public void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    public boolean isListening() {
        return isListening;
    }


    public void consume(KeyActionConsumer consumer) {
        inputQueue.poll(consumer);
    }

    public CircularKeyQueue getQueue() {
        return inputQueue;
    }

    public ActionType[] getListeningFor() {
        return listeningFor;
    }
}
