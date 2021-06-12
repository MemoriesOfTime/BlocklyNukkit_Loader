package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class ResourceItemManifest extends ResourceData {
    private static List<String> items = new LinkedList<>();
    public ResourceItemManifest(){
        this.dataType = ResourceDataType.JSON;
        JsonObject root = new JsonObject();
        root.addProperty("resource_pack_name", "vanilla");
        root.addProperty("texture_name", "atlas.items");
        JsonObject textures = new JsonObject();
        JsonObject tmp;
        for(String item:items){
            tmp = new JsonObject();
            tmp.addProperty("textures", "textures/items/"+item);
            textures.add(item, tmp);
        }
        root.add("texture_data", textures);
        this.content = new Gson().toJson(root).getBytes(StandardCharsets.UTF_8);
    }

    public static void addItem(String itemName){
        items.add(itemName);
    }

    @Override
    public int hashCode() {
        int hash = Integer.MIN_VALUE;
        for(String each:items){
            hash += each.hashCode();
        }
        return hash;
    }
}
