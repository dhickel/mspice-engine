package io.mindspice.mspice.util.collections;

import io.mindspice.mspice.util.Utils;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class IntSet {
    private static final int EMPTY = Integer.MIN_VALUE;
    private static final float LOAD_FACTOR = 0.75f;

    private int[] keys;
    private int size;

    public IntSet(int initialCapacity) {
        keys = new int[initialCapacity];
        Arrays.fill(keys, EMPTY);
    }

    public IntSet() {
        this(20);
    }

    public boolean add(int value) {
        if (size >= keys.length * LOAD_FACTOR) {
            resize(keys.length * 2);
        }

        int index = findIndex(value);
        if (keys[index] == EMPTY) {
            keys[index] = value;
            size++;
            return true;
        }
        return false;
    }

    public boolean contains(int value) {
        return keys[findIndex(value)] != EMPTY;
    }

    public boolean remove(int value) {
        int index = findIndex(value);
        if (keys[index] != EMPTY) {
            keys[index] = EMPTY;
            size--;
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        Arrays.fill(keys, EMPTY);
    }

    private void resize(int newCapacity) {
        int[] oldKeys = keys;
        keys = new int[newCapacity];
        Arrays.fill(keys, EMPTY);
        size = 0;

        for (int key : oldKeys) {
            if (key != EMPTY) {
                add(key);
            }
        }
    }

    private int findIndex(int value) {
        int index = indexFor(value);
        while (keys[index] != EMPTY && keys[index] != value) {
            index = (index + 1) % keys.length;
        }
        return index;
    }

    private int indexFor(int value) {
        return Utils.murmurHash3(value);
    }

    public void forEach(IntConsumer action) {
        for (int key : keys) {
            if (key != EMPTY) {
                action.accept(key);
            }
        }
    }
}

