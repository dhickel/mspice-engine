package io.mindspice.mspice.engine.core.graphics.primatives;

import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class Material {
    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    private List<Mesh> meshList;
    private String texturePath;
    private String normalMapPath;
    private Vector4f diffuseColor;
    private Vector4f ambientColor;
    private Vector4f specularColor;
    private float reflectance;

    public Material() {
        meshList = new ArrayList<>();
        diffuseColor = DEFAULT_COLOR;
        ambientColor = DEFAULT_COLOR;
        specularColor = DEFAULT_COLOR;
    }

    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public void setMeshList(List<Mesh> meshList) {
        this.meshList = meshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }

    public void setNormalMapPath(String normalMapPath) {
        this.normalMapPath = normalMapPath;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Material.class.getSimpleName() + "[", "]")
                .add("meshList=" + meshList)
                .add("texturePath='" + texturePath + "'")
                .add("diffuseColor=" + diffuseColor)
                .add("ambientColor=" + ambientColor)
                .add("specularColor=" + specularColor)
                .add("reflectance=" + reflectance)
                .toString();
    }
}