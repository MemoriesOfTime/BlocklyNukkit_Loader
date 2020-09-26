package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.scriptloader.scriptengines.BNLuaScriptEngine;
import com.sun.istack.internal.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuaScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.util.*;

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
        //asTable函数实现
        plugin.engineMap.get(name).put("asTable", new OneArgFunction() {
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
        plugin.engineMap.get(name).put("asMap", new OneArgFunction(){
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
        plugin.engineMap.get(name).put("asList", new OneArgFunction(){
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
        try {
            plugin.engineMap.get(name).eval(Lua);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        plugin.bnpluginset.add(name);
    }
}
