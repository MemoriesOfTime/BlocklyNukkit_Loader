package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResourceSoundManifest extends ResourceData {
    private static Map<String, String> sounds = new HashMap<>();
    public static void addSound(String soundName,String soundFilePath){
        sounds.put(soundName, soundFilePath);
    }
    public ResourceSoundManifest(){
        JsonObject root = new JsonObject();
        root.addProperty("format_version", "1.16.0");
        JsonObject defs = new JsonObject();
        for(Map.Entry<String, String> entry:sounds.entrySet()){
            JsonObject eachDef = new JsonObject();
            eachDef.addProperty("category", "neutral");
            eachDef.addProperty("__use_legacy_max_distance", "true");
            eachDef.addProperty("sounds", entry.getValue());
            defs.add(entry.getKey(), eachDef);
        }
        root.add("sound_definitions", defs);
        this.content = new Gson().toJson(root).getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public int hashCode() {
        int hash = Integer.MIN_VALUE;
        for(Map.Entry<String, String> entry:sounds.entrySet()){
            hash += entry.getKey().hashCode();
            hash += (entry.getValue().hashCode() * 16);
        }
        return hash;
    }
}
