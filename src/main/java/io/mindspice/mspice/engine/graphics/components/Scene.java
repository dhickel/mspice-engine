package io.mindspice.mspice.graphics.components;

import io.mindspice.mspice.graphics.primatives.Model;

import java.util.*;


public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private Camera camera;

    public Scene(int width, int height, float fov) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height, fov);
        textureCache = new TextureCache();
        camera = new Camera();
    }

    public Camera getCamera() {
        return camera;
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Projection getProjection() {
        return projection;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }
}