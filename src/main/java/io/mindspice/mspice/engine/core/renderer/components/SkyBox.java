package io.mindspice.mspice.engine.core.renderer.components;

import io.mindspice.mspice.engine.core.graphics.primatives.Model;
import io.mindspice.mspice.engine.util.ModelLoader;

import java.util.StringJoiner;


public class SkyBox {

    private Entity skyBoxEntity;
    private Model skyBoxModel;

    public SkyBox(String skyBoxModelPath, TextureCache textureCache) {
        skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache);
        skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel.getId());
    }

    public Entity getSkyBoxEntity() {
        return skyBoxEntity;
    }

    public Model getSkyBoxModel() {
        return skyBoxModel;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SkyBox.class.getSimpleName() + "[", "]")
                .add("skyBoxEntity=" + skyBoxEntity)
                .add("skyBoxModel=" + skyBoxModel)
                .toString();
    }
}
