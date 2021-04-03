package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.scriptloader.bases.Interpreter;
import com.blocklynukkit.loader.scriptloader.scriptengines.BNLuaScriptEngine;
import com.google.gson.GsonBuilder;
import javassist.CtClass;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.blocklynukkit.loader.Loader.bnClasses;

public class LuaLoader extends ExtendScriptLoader implements Interpreter {
    List<String> pragmas;
    public LuaLoader(Loader plugin){
        super(plugin);
    }
    public void loadplugins(){
        try{
            for (File file : Objects.requireNonNull(new File("./plugins/BlocklyNukkit").listFiles())) {
                if(file.isDirectory()) continue;
                if(file.getName().endsWith(".lua")&&!file.getName().contains("bak")){
                    try {
                        String lua = Utils.readToString(file.getPath());
                        pragmas= getPragma(lua);
                        if(pragmas.contains("pragma autoload false")){
                            return;
                        }
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            plugin.getLogger().warning("加载BN插件: " + file.getName());
                        else
                            plugin.getLogger().warning("loading BN plugin: " + file.getName());
                        putLuaEngine(file.getName(), lua);
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
    @Override
    public void putEngine(String name,String code){
        this.putLuaEngine(name, code);
    }
    public void putLuaEngine(String name,String Lua){
        Lua = formatExportLua(name, Lua);String engineName = name;
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
        BNLuaScriptEngine engine = (BNLuaScriptEngine) plugin.engineMap.get(name);
        engine.put("javax.script.filename",name);
        //asTable函数实现
        engine.put("asTable", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                Object value = BNLuaScriptEngine.toJava(luaValue);
                return to(value);
            }
            private LuaValue to(Object value){
                System.out.println(value.getClass());
                if(value instanceof List){
                    LuaTable result = new LuaTable();
                    List from = (List) value;
                    int position = 1;
                    for (Object each:from) {
                        // LuaTable默认索引从1开始，0也可以，但是后面获取长度等有问题
                        result.set(position,this.to(each));
                    }
                    return result;
                }else if(value instanceof Map){
                    LuaTable result = new LuaTable();
                    Map<Object,Object> from = (Map) value;
                    for(Map.Entry<Object,Object> entry:from.entrySet()){
                        result.set(BNLuaScriptEngine.toLua(entry.getKey().toString()),BNLuaScriptEngine.toLua(entry.getValue()));
                    }
                    return result;
                }else if(value instanceof Object[]){
                    LuaTable result = new LuaTable();
                    Object[] from = (Object[]) value;
                    int position = 1;
                    for (Object each:from) {
                        // LuaTable默认索引从1开始，0也可以，但是后面获取长度等有问题
                        result.set(position,this.to(each));
                    }
                }else if(value instanceof LuaValue){
                    LuaValue from = (LuaValue)value;
                    if(from.istable()){
                        return (LuaTable)from;
                    }
                }
                return BNLuaScriptEngine.toLua(value);
            }
        });
        engine.put("asMap", new OneArgFunction(){
            @Override
            public LuaValue call(LuaValue luaValue) {
                Object value = BNLuaScriptEngine.toJava(luaValue);
                return to(value);
            }
            public LuaValue to(Object object){
                if(object instanceof LuaValue){
                    if(((LuaValue)object).istable()){
                        LuaTable from = (LuaTable)((LuaValue)object);
                        Map<Object,Object> result = new HashMap<>();
                        for(LuaValue each:from.keys()){
                            result.put(BNLuaScriptEngine.toJava(each),this.to(from.get(each)));
                        }
                        return BNLuaScriptEngine.toLua(result);
                    }
                }
                return BNLuaScriptEngine.toLua(object);
            }
        });
        engine.put("asList", new OneArgFunction(){
            @Override
            public LuaValue call(LuaValue luaValue) {
                Object value = BNLuaScriptEngine.toJava(luaValue);
                return to(value);
            }
            public LuaValue to(Object object){
                if(object instanceof LuaValue){
                    if(((LuaValue)object).istable()){
                        LuaTable from = (LuaTable)((LuaValue)object);
                        List<Object> result = new ArrayList<>();
                        for(LuaValue each:from.keys()){
                            result.add(this.to(from.get(each)));
                        }
                        return BNLuaScriptEngine.toLua(result);
                    }
                }
                return BNLuaScriptEngine.toLua(object);
            }
        });
        engine.put("F", new OneArgFunction(){
            @Override
            public LuaValue call(LuaValue luaValue) {
                if(luaValue.isfunction()){
                    engine.lambdaCountPP();
                    engine.lambdaHashMap.put("Lambda_"+Utils.getMD5(engineName.getBytes())+"_"+engine.lambdaCount,(LuaFunction)luaValue);
                    return BNLuaScriptEngine.toLua("Lambda_"+Utils.getMD5(engineName.getBytes())+"_"+engine.lambdaCount);
                }
                return LuaValue.NIL;
            }
        });
        try {
            engine.eval(Lua);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        plugin.bnpluginset.add(name);
    }
    public String formatExportLua(String name,String code){
        String[] lines = code.split("\n");
        String output = "";String tmp;
        Map<String,String[]> exportFunctions = new HashMap<>();
        for(String line:lines){
            tmp = line.trim();
            if(tmp.endsWith("-->export")){
                output+=(line.replaceFirst("-->export ","")+"\n");
                tmp = tmp.replaceFirst("function ","");
                String funName = tmp.split("\\(")[0].trim();
                String[] funArgs = tmp.split("\\(")[1].split("\\)")[0]
                        .trim().split(",");
                for(int i=0;i<funArgs.length;i++){
                    funArgs[i]=funArgs[i].trim();
                }
                exportFunctions.put(funName,funArgs);
            }else {
                output+=(line+"\n");
            }
        }
        Pattern pattern = Pattern.compile("^[A-Za-z_$]+[A-Za-z_$.\\d]+$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.matches()){
            String moduleName = null;
            if(pragmas!=null)
                for(String each:pragmas){
                    if(each.startsWith("pragma module")){
                        moduleName = each.replaceFirst("pragma module","").trim().replaceAll(" ","_");
                    }
                }
            CtClass bn = JavaExporter.makeExportJava(name.endsWith(".lua")?name:(name+".lua"),exportFunctions,moduleName);
            if(bn!=null) bnClasses.put(name,bn);
        }
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
        return var instanceof LuaValue;
    }

    @Override
    public List<String> getPragma(String code) {
        List<String> pragma = new ArrayList<>();
        String[] lines = code.split("\n");
        for(String line:lines){
            if(line.trim().startsWith("--")){
                String toCheck = line.replaceFirst("--","").trim();
                if(toCheck.startsWith("pragma")){
                    toCheck = toCheck.replaceAll(" +"," ");
                    if(toCheck.startsWith("pragma end")){
                        break;
                    }
                    pragma.add(toCheck.toLowerCase());
                }
            }
        }
        return pragma;
    }
}
