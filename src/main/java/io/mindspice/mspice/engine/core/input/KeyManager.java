package io.mindspice.mspice.engine.core.input;

import java.util.Arrays;


public class KeyManager {
    private KeyListener[] keyListeners;
    private final InputMap inputMap;
    private ActionType listenFilter = null;
    int size = 0;

    public KeyManager(int size, InputMap inputMap) {
        keyListeners = new KeyCallBackListener[size];
        this.inputMap = inputMap;
        Arrays.fill(keyListeners, null);
    }

    public void registerListener(KeyListener listener) {
        for (int i = 0; i < keyListeners.length; i++) {
            if (keyListeners[i] == null) {
                keyListeners[i] = listener;
                size++;
                return;
            }
        }
        keyListeners = Arrays.copyOf(keyListeners, keyListeners.length * 2);
        registerListener(listener);
    }

    public void unregisterListener(KeyListener listener) {
        for (int i = 0; i < keyListeners.length; ++i) {
            if (keyListeners[i] == listener) {
                keyListeners[i] = null;
                return;
            }
        }
        sort();
        size--;
    }

    private void sort() { // TODO maybe just swap last and null?
        for (int i = 0; i < keyListeners.length; i++) {
            if (keyListeners[i] == null) {
                keyListeners[i] = keyListeners[size - 1];
                keyListeners[size - 1] = null;
            }
        }
    }

    public void setListenFilter(ActionType actionType) {
        listenFilter = actionType;
    }

    public void disableListenFilter() {
        listenFilter = null;
    }

    public void broadcast(int keyCode, int keyAction) {
        InputAction inputAction = inputMap.get(keyCode);

        if (inputAction == null) { return; }
        if (listenFilter != null && inputAction.actionType != listenFilter) { return; }

        for (int i = 0; i < size; i++) {
            KeyListener listener = keyListeners[i];
            if (listener.isListening() && listener.isListenerFor(inputAction.actionType)) {
                listener.offer(inputAction, keyAction);
            }
        }
    }
}
