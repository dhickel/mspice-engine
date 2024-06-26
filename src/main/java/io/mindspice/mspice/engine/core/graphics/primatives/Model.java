package io.mindspice.mspice.engine.core.graphics.primatives;

import io.mindspice.mspice.engine.core.renderer.components.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class Model {
    private final String id;
    private List<Entity> entitiesList;
    private List<Material> materialList;

    public Model(String id, List<Material> materialList) {
        this.id = id;
        entitiesList = new ArrayList<>();
        this.materialList = materialList;
    }

    public void cleanup() {
        materialList.forEach(Material::cleanup);
    }

    public List<Entity> getEntitiesList() {
        return entitiesList;
    }

    public String getId() {
        return id;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Model.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("entitiesList=" + entitiesList)
                .add("materialList=" + materialList)
                .toString();
    }
}
