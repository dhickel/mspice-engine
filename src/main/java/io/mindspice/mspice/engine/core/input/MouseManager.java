package io.mindspice.mspice.engine.core.input;

import java.util.Arrays;
import java.util.Objects;


public class MouseManager {
    private MouseListener[] mouseListeners;
    private ActionType listenFilter = null;
    private int size = 0;

    public MouseManager(int size) {
        mouseListeners = new MouseCallBackListener[size];
        Arrays.fill(mouseListeners, null);
    }

    public void registerListener(MouseCallBackListener listener) {
        for (int i = 0; i < mouseListeners.length; i++) {
            if (mouseListeners[i] == null) {
                mouseListeners[i] = listener;
                size++;
                return;
            }
        }
        mouseListeners = Arrays.copyOf(mouseListeners, mouseListeners.length * 2);
        registerListener(listener);
    }

    public void unregisterListener(MouseCallBackListener listener) {
        for (int i = 0; i < mouseListeners.length; ++i) {
            if (mouseListeners[i] == listener) {
                mouseListeners[i] = null;
                return;
            }
        }
        sort();
        size--;
    }

    public void setListenFilter(ActionType actionType) {
        listenFilter = actionType;
    }

    public void disableListenFilter() {
        listenFilter = null;
    }

    private void sort() { // TODO maybe just swap last and null?

        for (int i = 0; i < mouseListeners.length; i++) {
            if (mouseListeners[i] == null) {
                mouseListeners[i] = mouseListeners[size - 1];
                mouseListeners[size - 1] = null;
            }
        }
    }

     void broadcastPos(double posX, double posY) {
        for (int i = 0; i < size; i++) {
            var listener = mouseListeners[i];

            if (listenFilter != null && !listener.isListenerFor(listenFilter)) { ;
                continue;
            }

            if (listener.isListening()) {
                listener.offerPos(posX, posY);
            }
        }
    }

     void broadcastScroll(int val) {
        for (int i = 0; i < size; i++) {
            var listener = mouseListeners[i];

            if (listenFilter != null && !listener.isListenerFor(listenFilter)) {
                continue;
            }

            if (listener.isListening()) {
                listener.offerScroll(val);
            }
        }
    }

     void broadcastButtons(InputAction action, int val) {
        for (int i = 0; i < size; i++) {
            var listener = mouseListeners[i];

            if (listenFilter != null && !listener.isListenerFor(listenFilter)) {
                continue;
            }

            if (listener.isListening()) {
                listener.offerButton(action, val);
            }
        }
    }
}
