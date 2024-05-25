package io.mindspice.mspice.engine.util.consumers;

import io.mindspice.mspice.engine.core.input.InputAction;


@FunctionalInterface
public interface KeyActionConsumer {
    void accept(InputAction input, int value);
}
