package io.mindspice.mspice.engine.util.collections;

import io.mindspice.mspice.engine.util.consumers.BiIntConsumer;

import java.util.Arrays;

// A simple non-robust map implementation to map methods to keypress
// uses no hashing and expects the indices to be glfw keycodes, or similar
// keys should all be within 0-fullCapacity


public class InputConsumerMap {
    private static final int EMPTY_KEY = Integer.MIN_VALUE;
    private BiIntConsumer[] values;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public InputConsumerMap(int fullCapacity) {
        values = new BiIntConsumer[fullCapacity];
        size = 0;
        Arrays.fill(values, null);
    }

    // Resizing is not allowed, if full return false;
    public boolean put(int key, BiIntConsumer consumer) {
        if (key < 0 || key >= values.length) { return false; }
        if (values[key] == null) { size++; }
        values[key] = consumer;
        return true;
    }

    public BiIntConsumer get(int key) {
        if (key < 0 || key >= values.length) { return null; }
        return values[key];
    }

    public BiIntConsumer remove(int key) {
        if (key < 0 || key >= values.length) { return null; }
        BiIntConsumer oldValue = values[key];
        if(oldValue != null) {
            values[key] = null;
            size--;
        }
        return oldValue;
    }

    public int size() {
        return size;
    }

    public boolean contains(int key) {
        if (key < 0 || key >= values.length) {
            return false;
        } else {
            return values[key] != null;
        }
    }
}