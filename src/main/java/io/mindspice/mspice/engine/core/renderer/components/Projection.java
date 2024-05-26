package io.mindspice.mspice.engine.core.renderer.components;

import org.joml.Matrix4f;


public class Projection {

    private final static float FOV = (float) Math.toRadians(70.0f);
    private final static float Z_FAR = 1000.f;
    private final static float Z_NEAR = 0.01f;

    private Matrix4f projMatrix;

    public Projection(int width, int height, float fov) { // FIXME why does FOV need static init
        projMatrix = new Matrix4f();
        updateProjMatrix(width, height);
       // FOV = ;
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    public void updateProjMatrix(int width, int height) {
        projMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
    }
}