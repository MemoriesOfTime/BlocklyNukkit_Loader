package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.scriptloader.scriptengines.BNPHPScriptEngine;
import com.sun.istack.internal.NotNull;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.util.Objects;

public class PHPLoader {
    public Loader plugin;
    public PHPLoader(@NotNull Loader plugin){
        this.plugin=plugin;
    }
    public void loadplugins(){
        File php = new File(plugin.getDataFolder()+"/lib/pythonForBN.jar");
        if(php.exists()){
            try{
                for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
                    if(file.isDirectory()) continue;
                    if(file.getName().endsWith(".php")&&!file.getName().contains("bak")){
                        try {
                            if (Server.getInstance().getLanguage().getName().contains("中文"))
                                plugin.getLogger().warning("加载BN插件: " + file.getName());
                            else
                                plugin.getLogger().warning("loading BN plugin: " + file.getName());
                            putPHPEngine(file.getName(), Utils.readToString(file.getPath()));
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
    }
    public void putPHPEngine(String name,String PHP){
        plugin.engineMap.put(name,new BNPHPScriptEngine());
        if (plugin.engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP interpreter crashed!");
            return;
        }
        if (!(plugin.engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP interpreter's version is too low!");
            return;
        }
        plugin.putBaseObject(name);
        try {
            plugin.engineMap.get(name).eval(PHP);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        plugin.bnpluginset.add(name);
    }
}
