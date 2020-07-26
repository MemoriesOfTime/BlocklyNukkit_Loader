package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.sun.istack.internal.NotNull;
import org.develnext.jphp.scripting.JPHPContext;
import php.runtime.env.Environment;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

import static com.blocklynukkit.loader.Loader.*;

public class PHPLoader {
    public Loader plugin;
    public PHPLoader(@NotNull Loader plugin){
        this.plugin=plugin;
    }
    public void loadplugins(){
        //加载js
        for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().endsWith(".php")&&!file.getName().contains("bak")){
                try (final Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getlogger().warning("加载BN插件: " + file.getName());
                    else
                        getlogger().warning("loading BN plugin: " + file.getName());
                    engineMap.put(file.getName(),new ScriptEngineManager().getEngineByName("jphp"));
                    if (engineMap.get(file.getName()) == null) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("PHP引擎加载出错！");
                        if (!Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("PHP interpreter crashed!");
                        return;
                    }
                    if (!(engineMap.get(file.getName()) instanceof Invocable)) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("PHP引擎版本过低！");
                        if (!Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("PHP interpreter's version is too low!");
                        return;
                    }
                    putBaseObject(file.getName());
                    engineMap.get(file.getName()).eval(reader);
                    bnpluginset.add(file.getName());
                } catch (final Exception e) {
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getlogger().error("无法加载： " + file.getName(), e);
                    else
                        getlogger().error("cannot load: " + file.getName(), e);
                }
            }


        }
    }
    public void putPHPEngine(String name,String php){
        engineMap.put(name,new ScriptEngineManager().getEngineByName("jphp"));
        if (engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("PHP引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("PHP interpreter crashed!");
            return;
        }
        if (!(engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("PHP引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("php interpreter's version is too low!");
            return;
        }
        putBaseObject(name);
        try {
            engineMap.get(name).eval(php);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        bnpluginset.add(name);
    }
}
