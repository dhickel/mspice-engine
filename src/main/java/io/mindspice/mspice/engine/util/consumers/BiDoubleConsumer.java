package io.mindspice.mspice.engine.util.consumers;

@FunctionalInterface
public interface BiDoubleConsumer {
    void accept(double a, double b);

}
