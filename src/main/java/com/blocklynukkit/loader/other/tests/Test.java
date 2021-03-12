package com.blocklynukkit.loader.other.tests;

import cn.nukkit.utils.Config;
import com.blocklynukkit.loader.script.FunctionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;

public class Test {
    public static void main(String[] args){
        String json = "{\n" +
                "    \"a\": 1,\n" +
                "    \"b\": \"hello world\",\n" +
                "    \"me\": {\n" +
                "        \"name\": \"Superice666\"\n" +
                "    }\n" +
                "}";
        String yaml = "a: 1.0\n" +
                "b: hello world\n" +
                "me:\n" +
                "  name: Superice666";
        System.out.println(YAMLtoJSON(yaml));
    }
    public static String JSONtoYAML(String json){
        json = formatJSON(json);
        Config config = new Config(Config.YAML);
        config.setAll((LinkedHashMap)new Gson().fromJson(json, (new TypeToken<LinkedHashMap<String, Object>>() {}).getType()));
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        return yaml.dump(config.getRootSection());
    }
    public static String formatJSON(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
    public static String YAMLtoJSON(String yaml){
        Config config = new Config(Config.JSON);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yamlObj = new Yaml(dumperOptions);
        config.setAll(yamlObj.loadAs(yaml, LinkedHashMap.class));
        return new GsonBuilder().setPrettyPrinting().create().toJson(config.getRootSection());
    }
}
