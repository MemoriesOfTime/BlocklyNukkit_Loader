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
        ResourcePack resourcePack = new ResourcePack("D:\\nukkit\\BlocklyNukkit\\nukkit\\resource_packs\\test.mcpack");
        resourcePack.addNode(new ResourceNode()
                .putData("manifest.json", new ResourceManifest("BN测试材质包","能不能好用呢？"))
                .putData("pack_icon.png", new ResourceLogo())
        );
        resourcePack.saveToDisk();
    }
}
