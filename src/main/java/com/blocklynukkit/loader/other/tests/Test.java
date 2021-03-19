package com.blocklynukkit.loader.other.tests;

import cn.nukkit.utils.Config;
import com.blocklynukkit.loader.script.FunctionManager;
import com.blocklynukkit.loader.scriptloader.transformer.JsArrowFunctionTransformer;
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
        String js = "//pragma js\n" +
                "\n" +
                "//常量\n" +
                "const configPath = \"./plugins/BlocklyNukkit/NKHI/config.yml\";\n" +
                "//生成并读取配置文件\n" +
                "var configText = manager.readFile(configPath);\n" +
                "if(configText == \"FILE NOT FOUND\"){\n" +
                "    configText = \"port: 80\";\n" +
                "    manager.writeFile(configPath,configText);\n" +
                "}\n" +
                "var fun = ((toReplace, filter, handler) => {});\n" +
                "var config = JSON.parse(manager.YAMLtoJSON(configText));\n" +
                "//准备启动http服务器\n" +
                "if(manager.createHttpServer(config['port'])){\n" +
                "    console.log(\"成功开始http服务器\");\n" +
                "    if(manager.attachHandlerToHttpServer(config['port'],\"/NKHI\",F(function(request){\n" +
                "        request.addDefaultResponseHeader();\n" +
                "        if(request.response(200,JSON.stringify({\n" +
                "            base: server.getName(),\n" +
                "            playerNumber: manager.getOnlinePlayers().length,\n" +
                "            version: server.getVersion(),\n" +
                "            motd: server.getMotd(),\n" +
                "            submotd: server.getSubMotd()\n" +
                "        }))){\n" +
                "            /* 这里\n" +
                "              为了\"\"'\n" +
                "            测试\n" +
                "            */\n" +
                "            console.log(\"成'\\\"功响应\");\n" +
                "        }else{//console.log('一个注释')\n" +
                "            console.warn('响应\\'失败');\n" +
                "        }\n" +
                "    }))){\n" +
                "        console.log(\"成功监听端口\");\n" +
                "    }else{\n" +
                "        console.warn(\"无法监听端口\");\n" +
                "    }\n" +
                "    manager.startHttpServer(config['port']);\n" +
                "}else{\n" +
                "    console.warn(\"无法开启http服务器\");\n" +
                "}";
        long start = System.currentTimeMillis();
        System.out.println(new JsArrowFunctionTransformer(js).transform());
        System.out.println(System.currentTimeMillis()-start);
    }
}
