package io.mindspice.mspice.engine.util.collections;

// Circular BiInt Queue, used to queue primitives for a high performance queue with no GC impact

import io.mindspice.mspice.engine.util.consumers.IntDoubleConsumer;


public class CircularIntDblQueue {
    private final int[] keyCodes;
    private final double[] keyStates;
    private final int capacity;
    private int head;
    private int tail;
    private int size;

    public CircularIntDblQueue(int capacity) {
        this.capacity = capacity;
        this.keyCodes = new int[capacity];
        this.keyStates = new double[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    // Adds a new element to the queue if there is space available. Modulo operation is used to wrap around the tail
    // index when it reaches the end of the capacity. This allows it to wrap around for consumed values.
    // isFull make sure it doesn't overwrite non consumed values, opts to ignore values if full as to not crash game
    // in the edge case of someone somehow inputting 100 inputs between frames

    public void add(int keyCode, double keyState) {
        if (isFull()) { return; }
        keyCodes[tail] = keyCode;
        keyStates[tail] = keyState;
        tail = (tail + 1) % capacity;
        size++;
    }

    // Consumer to allow for lambda consumption of both events in one operation


    public void poll(IntDoubleConsumer consumer) {
        if (!isEmpty()) {
            int keyCode = keyCodes[head];
            double keyState = keyStates[head];
            head = (head + 1) % capacity;
            size--;

            consumer.accept(keyCode, keyState);
        }
    }

    public void peek(IntDoubleConsumer consumer) {
        if (!isEmpty()) {
            int keyCode = keyCodes[head];
            double keyState = keyStates[head];
            consumer.accept(keyCode, keyState);
        }
    }

}
