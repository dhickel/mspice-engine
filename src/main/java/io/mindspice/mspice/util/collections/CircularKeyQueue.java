package io.mindspice.mspice.util.collections;

// Circular BiInt Queue, used to queue primitives for a high performance queue with no GC impact

import io.mindspice.mspice.enums.InputAction;
import io.mindspice.mspice.util.consumers.KeyActionConsumer;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class CircularKeyQueue {
    private final InputAction[] inputActions;
    private final int[] keyStates;
    private final int capacity;
    private int head;
    private int tail;
    private volatile int size = 0;
    private  boolean rejectIfFull;

    public CircularKeyQueue(int capacity) {
        this.capacity = capacity;
        this.inputActions = new InputAction[capacity];
        this.keyStates = new int[capacity];
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

    public void setRejectIfFull(boolean doReject) {
        rejectIfFull = doReject;
    }

    // Adds a new element to the queue if there is space available. Modulo operation is used to wrap around the tail
    // index when it reaches the end of the capacity. This allows it to wrap around for consumed values.



    public void add(InputAction inputAction, int keyState) {
        if (rejectIfFull && isFull()) { return; }
        inputActions[tail] = inputAction;
        keyStates[tail] = keyState;
        tail = (tail + 1) % capacity;
        if (size < capacity) {
            size++;
        }
        // Removed debug print
    }

    public void poll(KeyActionConsumer consumer) {
        if (!isEmpty()) {
            consumer.accept(inputActions[head], keyStates[head]); // Consume before moving head
            head = (head + 1) % capacity;
            size--;
        }
    }



    public void consumeAll(KeyActionConsumer[] consumers) {
        while (!isEmpty()) {
            if (consumers[inputActions[head].ordinal()] != null) {
                consumers[inputActions[head].ordinal()].accept(inputActions[head], keyStates[head]);
            }
            head = (head + 1) % capacity;
            size--;
        }
    }


    public void consumeAll(IntConsumer[] consumers) {
        while (!isEmpty()) {
            if (consumers[inputActions[head].ordinal()] != null) {
                consumers[inputActions[head].ordinal()].accept(keyStates[head]);
            }
            head = (head + 1) % capacity;
            size--;
        }
    }

    public void peek(KeyActionConsumer consumer) {
        if (!isEmpty()) {
            consumer.accept(inputActions[head], keyStates[head]);
        }
    }

    public String toString() {
        return Arrays.toString(inputActions);
    }
}
