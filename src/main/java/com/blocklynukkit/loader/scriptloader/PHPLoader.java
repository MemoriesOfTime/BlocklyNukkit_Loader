package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.BNLogger;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.scriptloader.bases.Interpreter;
import com.blocklynukkit.loader.scriptloader.scriptengines.BNPHPScriptEngine;
import com.google.gson.GsonBuilder;
import javassist.CtClass;

import static com.blocklynukkit.loader.Loader.bnClasses;
import static com.blocklynukkit.loader.Loader.engineMap;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PHPLoader extends ExtendScriptLoader implements Interpreter {
    public PHPLoader(Loader plugin){
        super(plugin);
    }
    public void loadplugins(){
        if(Loader.plugins.containsKey("PHPBN")){
            try{
                for (File file : Objects.requireNonNull(new File("./plugins/BlocklyNukkit").listFiles())) {
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
    @Override
    public void putEngine(String name,String code){
        this.putPHPEngine(name, code);
    }
    public void putPHPEngine(String name,String PHP){
        PHP = formatExportPHP(name, PHP);
        engineMap.put(name,new BNPHPScriptEngine(new BNLogger(name)));
        if (plugin.engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP interpreter crashed!");
            return;
        }
        if (!(plugin.engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("PHP引擎不支持过程调用！");
            else
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
    public String formatExportPHP(String name,String code){
        String[] lines = code.split("\n");
        String output = "";String tmp;
        Map<String,String[]> exportFunctions = new HashMap<>();
        for(String line:lines){
            tmp = line.trim();
            if(tmp.startsWith("static function")){
                output+=(line.replaceFirst("static ","")+"\n");
                tmp = tmp.replaceFirst("static function ","");
                String funName = tmp.split("\\(")[0].trim();
                String[] funArgs = tmp.split("\\(")[1].split("\\)")[0]
                        .trim().split(",");
                for(int i=0;i<funArgs.length;i++){
                    funArgs[i]=funArgs[i].replaceFirst("\\$","").trim();
                }
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
        output = output.replaceAll("(?<!\\/)(?<!['\"])echo (.*?);(?![ ]*\")","\\$logger->info($1);");
        output = output.replaceAll("(?<!\\/)(?<!['\"])print (.*?);(?![ ]*\")","\\$logger->info($1);");
        return output;
    }
    @Override
    public String toString(Object var){
        if(var.toString().equals(getClass().getName() + "@" + Integer.toHexString(hashCode()))){
            return new GsonBuilder().setPrettyPrinting().create().toJson(var);
        }else {
            return var.toString();
        }
    }
    @Override
    public boolean isThisLanguage(Object var){
        return var instanceof com.caucho.quercus.env.Value;
    }
}
