package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.sun.istack.internal.NotNull;
import javassist.*;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeFunction;

import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                putJavaScriptEngine(file.getName(),js);
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
        js = formatExportJavaSript(name,js);
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
            ((Compilable)engineMap.get(name)).compile(js).eval();
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
    public String formatExportJavaSript(String name,String code){
        String[] lines = code.split("\n");
        String output = "";String tmp;
        Map<String,String[]> exportFunctions = new HashMap<>();
        for(String line:lines){
            tmp = line.trim();
            if(tmp.startsWith("export function")){
                output+=(line.replaceFirst("export ","")+"\n");
                tmp = tmp.replaceFirst("export function ","");
                String funName = tmp.split("\\(")[0].trim();
                String[] funArgs = tmp.split("\\(")[1].split("\\)")[0]
                        .trim().split(",");
                exportFunctions.put(funName,funArgs);
            }else {
                output+=(line+"\n");
            }
        }
        Pattern pattern = Pattern.compile("^[A-Za-z_$]+[A-Za-z_$.\\d]+$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.matches()){
            CtClass bn = JavaExporter.makeExportJava(name.endsWith(".js")?name:(name+".js"),exportFunctions);
            if(bn!=null) bnClasses.put(name,bn);
        }
        return output;
    }
}
