package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;

public class ResourceArmorManifest extends ResourceData {
    public ResourceArmorManifest(String itemName, String armorType){
        this.dataType = ResourceDataType.JSON;
        JsonObject root = new JsonObject();
        root.addProperty("format_version", "1.16.200");
        JsonObject attachAble = new JsonObject();
        JsonObject description = new JsonObject();
        description.addProperty("identifier", "blocklynukkit:"+itemName);
        JsonObject materials = new JsonObject();
        materials.addProperty("default", "armor");
        materials.addProperty("enchanted", "armor_enchanted");
        description.add("materials", materials);
        JsonObject textures = new JsonObject();
        textures.addProperty("enchanted", "textures/misc/enchanted_item_glint");
        textures.addProperty("default", "textures/models/armor/"+itemName);
        description.add("textures", textures);
        JsonObject geometry = new JsonObject();
        String geometryTmp = armorType;
        if("chest".equals(geometryTmp)){
            geometryTmp = "chestplate";
        }
        geometry.addProperty("default", "geometry.humanoid.armor."+geometryTmp);
        description.add("geometry", geometry);
        JsonObject scripts = new JsonObject();
        scripts.addProperty("parent_setup", "variable.chest_layer_visible = 0.0;");
        description.add("scripts", scripts);
        JsonArray render_controllers = new JsonArray(1);
        render_controllers.add("controller.render.armor");
        description.add("render_controllers", render_controllers);
        attachAble.add("description",description);
        root.add("minecraft:attachable", attachAble);
        this.content = new Gson().toJson(root).getBytes(StandardCharsets.UTF_8);
    }
}
