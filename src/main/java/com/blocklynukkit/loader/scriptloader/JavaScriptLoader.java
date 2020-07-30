package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.sun.istack.internal.NotNull;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

import static com.blocklynukkit.loader.Loader.*;

public class JavaScriptLoader {
    public Loader plugin;
    public JavaScriptLoader(@NotNull Loader plugin){
        this.plugin=plugin;
    }
    public void loadplugins(){
        //加载js
        for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().endsWith(".js")&&!file.getName().contains("bak")){
                if (Server.getInstance().getLanguage().getName().contains("中文"))
                    getlogger().warning("加载BN插件: " + file.getName());
                else
                    getlogger().warning("loading BN plugin: " + file.getName());
                String js = Utils.readToString(file);
                if(js.contains("//pragma es9")||js.contains("//pragma es6")||js.contains("//pragma es2019")||js.contains("//pragma es2016")||js.contains("//pragma graal")||js.contains("//pragma Graal")||js.contains("//pragma graalvm")||js.contains("//pragma Graalvm")||js.contains("//pragma graalVM")||js.contains("//pragma GraalVM")||js.contains("//pragma graaljs")||js.contains("//pragma graalJS")||js.contains("//pragma Graaljs")||js.contains("//pragma GraalJS")||
                        js.contains("// pragma es9")||js.contains("// pragma es6")||js.contains("// pragma es2019")||js.contains("// pragma es2016")||js.contains("// pragma graal")||js.contains("// pragma Graal")||js.contains("// pragma graalvm")||js.contains("// pragma Graalvm")||js.contains("// pragma graalVM")||js.contains("// pragma GraalVM")||js.contains("// pragma graaljs")||js.contains("// pragma graalJS")||js.contains("// pragma Graaljs")||js.contains("// pragma GraalJS")){
                    new GraalJSLoader(plugin).putGraalJSEngine(file.getName(),js);
                }else {
                    putJavaScriptEngine(file.getName(),js);
                }
            }


        }
    }
    public void putJavaScriptEngine(String name,String js){
        engineMap.put(name,new ScriptEngineManager().getEngineByName("nashorn"));
        if (engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript interpreter crashed!");
            return;
        }
        if (!(engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript interpreter's version is too low!");
            return;
        }
        putBaseObject(name);
        try {
            engineMap.get(name).eval(js);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        bnpluginset.add(name);
    }
}
