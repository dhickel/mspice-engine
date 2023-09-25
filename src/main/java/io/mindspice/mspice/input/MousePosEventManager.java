package io.mindspice.mspice.input;

import java.util.Arrays;


public class MousePosEventManager {
    private MousePosListener[] mouseListeners;
    private int size = 0;

    public MousePosEventManager(int size) {
        mouseListeners = new MousePosListener[size];
        Arrays.fill(mouseListeners, null);
    }

    public void registerListener(MousePosListener listener) {
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

    public void unregisterListener(MousePosListener listener) {
        for (int i = 0; i < mouseListeners.length; ++i) {
            if (mouseListeners[i] == listener) {

                mouseListeners[i] = null;
                return;
            }
        }
        sort();
        size--;
    }

    private void sort() {
        // only needs to sort one null to the end, most efficient way to do it
        for (int i = 0; i < mouseListeners.length; i++) {
            if (mouseListeners[i] == null) {
                for (int j = mouseListeners.length - 1; j > i; j--) {
                    if (mouseListeners[j] != null) {
                        mouseListeners[i] = mouseListeners[j];
                        mouseListeners[j] = null;
                        break;
                    }
                }
            }
        }
    }

    // @Override
    public void broadcast(double posX, double posY) {
        for (int i = 0; i < size; i++) {
            var listener = mouseListeners[i];
            if (listener.isListening() ) {
                listener.offerInput(posX, posY);
            }
        }
    }
}
