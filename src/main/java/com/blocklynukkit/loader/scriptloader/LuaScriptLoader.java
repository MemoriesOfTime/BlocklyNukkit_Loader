package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.scriptloader.scriptengines.BNLuaScriptEngine;
import com.sun.istack.internal.NotNull;
import org.luaj.vm2.script.LuaScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.util.Objects;

public class LuaScriptLoader {
    public Loader plugin;
    public LuaScriptLoader(@NotNull Loader plugin){
        this.plugin=plugin;
    }
    public void loadplugins(){
        try{
            for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
                if(file.isDirectory()) continue;
                if(file.getName().endsWith(".lua")&&!file.getName().contains("bak")){
                    try {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            plugin.getLogger().warning("加载BN插件: " + file.getName());
                        else
                            plugin.getLogger().warning("loading BN plugin: " + file.getName());
                        putLuaEngine(file.getName(), Utils.readToString(file.getPath()));
                    } catch (final Exception e) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            plugin.getLogger().error("无法加载： " + file.getName(), e);
                        else
                            plugin.getLogger().error("cannot load:" + file.getName(), e);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void putLuaEngine(String name,String Lua){
        plugin.engineMap.put(name, new BNLuaScriptEngine());
        if (plugin.engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Lua引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Lua interpreter crashed!");
            return;
        }
        if (!(plugin.engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Lua引擎不支持过程调用！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Lua interpreter's version is too low!");
            return;
        }
        plugin.putBaseObject(name);
        try {
            plugin.engineMap.get(name).eval(Lua);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        plugin.bnpluginset.add(name);
    }
}
