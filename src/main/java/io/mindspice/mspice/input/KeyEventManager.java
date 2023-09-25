package io.mindspice.mspice.input;

import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.singletons.InputMap;

import java.util.Arrays;


public class KeyEventManager {
    private KeyListener[] keyListeners;
    private final InputMap inputMap = InputMap.getInstance();
    int size = 0;

    public KeyEventManager(int size) {
        keyListeners = new KeyListener[size];
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

    private void sort() {
        // only needs to sort one null to the end, most efficient way to do it
        for (int i = 0; i < keyListeners.length; i++) {
            if (keyListeners[i] == null) {
                for (int j = keyListeners.length - 1; j > i; j--) {
                    if (keyListeners[j] != null) {
                        keyListeners[i] = keyListeners[j];
                        keyListeners[j] = null;
                        break;
                    }
                }
            }
        }
    }

    // @Override
    public void broadcast(int keyCode, int keyAction) {
        InputAction inputAction = inputMap.get(keyCode);
        if (inputAction == null) { return; }
        for (int i = 0; i < size; i++) {
            var listener = keyListeners[i];
            if (listener.isListening() && listener.isListenerFor(inputAction.actionType)) {
                listener.offerInput(inputAction, keyAction);
            }
        }
    }
}
