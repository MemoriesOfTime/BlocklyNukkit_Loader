package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import com.sun.istack.internal.NotNull;

import javax.script.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.function.Predicate;

import static com.blocklynukkit.loader.Loader.*;

public class GraalJSLoader {
    public Loader plugin;
    public GraalJSLoader(@NotNull Loader plugin){
        this.plugin=plugin;
    }
    public void loadplugins(){
        //加载js
        for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().endsWith(".js")&&!file.getName().contains("bak")){
                try (final Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getlogger().warning("加载BN插件: " + file.getName());
                    else
                        getlogger().warning("loading BN plugin: " + file.getName());
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
                    Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
                    bindings.put("polyglot.js.allowHostAccess", true);
                    bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
                    engineMap.put(file.getName(),engine);
                    if (engineMap.get(file.getName()) == null) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("GraalJS引擎加载出错！");
                        if (!Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("GraalJS interpreter crashed!");
                        return;
                    }
                    if (!(engineMap.get(file.getName()) instanceof Invocable)) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("GraalJS引擎版本过低！");
                        if (!Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().error("GraalJS interpreter's version is too low!");
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
    public void putGraalJSEngine(String name,String js){
        GraalJSScriptEngine engine = new GraalJSEngineFactory().getScriptEngine();
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowHostAccess", true);
        bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
        engineMap.put(name,engine);
        if (engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("GraalJS引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("GraalJS interpreter crashed!");
            return;
        }
        if (!(engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("GraalJS引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("GraalJS interpreter's version is too low!");
            return;
        }
        putBaseObject(name);
        try {
            engineMap.get(name).eval(js);
        } catch (ScriptException e) {
            previousException = e;
            if (Server.getInstance().getLanguage().getName().contains("中文")){
                Loader.getlogger().warning("在初始化\""+name+"\"时");
                Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                Loader.getlogger().warning(e.getMessage());
                Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
            }else {
                Loader.getlogger().warning("In initialization of \""+name+"\"");
                Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                Loader.getlogger().warning(e.getMessage());
                Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
            }
        }
        bnpluginset.add(name);
    }
}
