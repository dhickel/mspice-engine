package io.mindspice.mspice.engine.marchingcubes;

public interface ScalarField {
    float getDensity(float x, float y, float z);
}