package io.mindspice.mspice.graphics.components;

import io.mindspice.mspice.graphics.primatives.Texture;

import java.util.HashMap;
import java.util.Map;


public class TextureCache {

    public static final String DEFAULT_TEXTURE = "/home/mindspice/code/Java/game/mspice-engine/src/main/resources/default_texture.png";

    private Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
        textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
    }

    public void cleanup() {
        textureMap.values().forEach(Texture::cleanup);
    }

    public Texture createTexture(String texturePath) {
        return textureMap.computeIfAbsent(texturePath, Texture::new);
    }

    public Texture getTexture(String texturePath) {
        Texture texture = null;
        if (texturePath != null) {
            texture = textureMap.get(texturePath);
        }

        if (texture == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }

}
