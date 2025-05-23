package com.bawnorton.runtimetrims.client.model.item;

import com.bawnorton.runtimetrims.client.model.item.json.BlockAtlas;
import com.bawnorton.runtimetrims.client.model.item.json.TextureLayers;
import com.bawnorton.runtimetrims.client.model.item.json.serialisation.TextureLayersSerializer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public final class JsonParser {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(TextureLayers.class, new TextureLayersSerializer())
            .setPrettyPrinting()
            .create();

    public <T> T fromResource(Resource resource, Class<T> clazz) {
        try(BufferedReader reader = resource.getReader()) {
            return fromReader(reader, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public <T> T fromString(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public <T> T fromReader(Reader original, Class<T> clazz) {
        return GSON.fromJson(original, clazz);
    }

    public <T> T fromJson(JsonObject json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public Resource toResource(ResourcePack resourcePack, Object object) {
        return new Resource(resourcePack, () -> IOUtils.toInputStream(GSON.toJson(object), StandardCharsets.UTF_8));
    }

    public String toJson(Object object) {
        return GSON.toJson(object);
    }

    public JsonObject toJsonObject(Object object) {
        return GSON.toJsonTree(object).getAsJsonObject();
    }

    public BufferedReader toReader(Object object) {
        return new BufferedReader(new StringReader(toJson(object)));
    }
}
