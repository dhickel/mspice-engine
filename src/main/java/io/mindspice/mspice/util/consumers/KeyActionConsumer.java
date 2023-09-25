package io.mindspice.mspice.util.consumers;

import io.mindspice.mspice.enums.InputAction;


@FunctionalInterface
public interface KeyActionConsumer {
    void accept(InputAction a, int b);
}
