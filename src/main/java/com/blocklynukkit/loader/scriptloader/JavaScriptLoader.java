package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.sun.istack.internal.NotNull;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

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
            }else if(file.getName().endsWith(".py")&&!file.getName().contains("bak") && !plugins.containsKey("PyBN")){
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning("无法加载:" + file.getName()+"! 缺少python依赖库");
                    getlogger().warning("请到https://tools.blocklynukkit.com/PyBN.jar下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + file.getName()+" python libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PyBN.jar");
                }
            }
        }
    }
    public void putJavaScriptEngine(String name,String js){
        engineMap.put(name,new NashornScriptEngineFactory().getScriptEngine());
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
            previousException = e;
            if (Server.getInstance().getLanguage().getName().contains("中文")){
                Loader.getlogger().warning("在初始化\""+e.getFileName()+"\"时");
                Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                Loader.getlogger().warning(e.getMessage());
                Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
            }else {
                Loader.getlogger().warning("In initialization of\""+e.getFileName()+"\"");
                Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                Loader.getlogger().warning(e.getMessage());
                Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
            }
        }
        bnpluginset.add(name);
    }
}
