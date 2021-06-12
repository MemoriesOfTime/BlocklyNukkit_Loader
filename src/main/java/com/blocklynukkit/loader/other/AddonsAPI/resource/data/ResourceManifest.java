package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;
import com.google.gson.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ResourceManifest extends ResourceData {
    public ResourceManifest(String name, String description) {
        super();
        this.dataType = ResourceDataType.JSON;
        JsonObject root = new JsonObject();
        root.add("format_version", new JsonPrimitive(1));
        JsonObject header = new JsonObject();
        header.add("description", new JsonPrimitive(description));
        header.add("name", new JsonPrimitive(name));
        header.add("uuid", new JsonPrimitive(UUID.randomUUID().toString()));
        JsonArray version = new JsonArray(3);
        version.add(1);version.add(0);version.add(0);
        header.add("version", version);
        root.add("header", header);
        JsonObject module = new JsonObject();
        module.add("description", new JsonPrimitive(description));
        module.add("type", new JsonPrimitive("resources"));
        module.add("uuid", new JsonPrimitive(UUID.randomUUID().toString()));
        module.add("version", version.deepCopy());
        JsonArray modules = new JsonArray(1);
        modules.add(module);
        root.add("modules", modules);
        content = new Gson().toJson(root).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
