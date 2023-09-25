package io.mindspice.mspice.input;

import io.mindspice.mspice.util.consumers.BiDoubleConsumer;


public class MousePosListener {
    private double[] mousePos = new double[2];
    private boolean isListening = true;
    private boolean hasNext;

    public MousePosListener() {
    }

    public boolean isListening() {
        return isListening;
    }

    void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    boolean hasNext() {
        return hasNext;
    }

    public void consume(BiDoubleConsumer consumer) {
        consumer.accept(mousePos[0], mousePos[1]);
    }

    public void offerInput(double posX, double posY) {
        mousePos[0] = posX;
        mousePos[1] = posY;
    }

}
