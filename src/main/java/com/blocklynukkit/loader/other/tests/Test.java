package com.blocklynukkit.loader.other.tests;

import cn.nukkit.utils.BinaryStream;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceNode;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourcePack;
import com.blocklynukkit.loader.other.AddonsAPI.resource.data.ResourceLogo;
import com.blocklynukkit.loader.other.AddonsAPI.resource.data.ResourceManifest;
import com.blocklynukkit.loader.script.FunctionManager;
import com.blocklynukkit.loader.scriptloader.transformer.JsES6Transformer;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class Test {
    public static void main(String[] args){
        JsES6Transformer transformer = new JsES6Transformer("function BNInitializedEvent(/**@type {com.blocklynukkit.loader.script.event.BNInitializedEvent}*/event){\n" +
                "    //引入翻译模块\n" +
                "    /** @description 翻译模块 @type {Object} */\n" +
                "    var Translate = require(\"TechDawnTranslate\");;\n" +
                "    /** @description 翻译函数 @type {(toTranslate: string) => string} */\n" +
                "    var T = Translate.translate;\n" +
                "    for(let each of textureList){\n" +
                "        logger.info(\"https://raw.fastgit.org/BlocklyNukkit/TectDawn/master/image/\"+(java.net.URLEncoder).encode(each, \"UTF-8\")+\".png\");\n" +
                "    }\n" +
                "}");
        System.out.println(transformer.transform());
    }
}
