package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.scriptloader.bases.Interpreter;
import com.blocklynukkit.loader.scriptloader.scriptengines.WasmScriptEngine;
import com.blocklynukkit.loader.utils.Utils;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.blocklynukkit.loader.Loader.*;

public class WasmLoader extends ExtendScriptLoader implements Interpreter {
    public WasmLoader(Loader loader) {
        super(loader);
    }

    public void loadplugins(){
        //加载js
        for (File file : Objects.requireNonNull(new File("./plugins/BlocklyNukkit").listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().endsWith(".wasm")&&!file.getName().contains("bak")){
                if (Server.getInstance().getLanguage().getName().contains("中文"))
                    getlogger().warning("加载BN插件: " + file.getName());
                else
                    getlogger().warning("loading BN plugin: " + file.getName());
                try {
                    putWasmEngine(file.getName(),Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void putEngine(String name, String code) {
        WasmScriptEngine engine = new WasmScriptEngine();
        engineMap.put(name,engine);
        if (engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly interpreter crashed!");
            return;
        }
        if (!(engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly interpreter's version is too low!");
            return;
        }
        putBaseObject(name);
        engine.put("javax.script.filename",name);
        try {
            engine.eval(code);
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
    }

    public void putEngine(String name,byte[] wasm){
        this.putEngine(name, wasm);
    }

    public void putWasmEngine(String name,byte[] wasm){
        WasmScriptEngine engine = new WasmScriptEngine();
        engineMap.put(name,engine);
        if (engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly interpreter crashed!");
            return;
        }
        if (!(engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("Webassembly interpreter's version is too low!");
            return;
        }
        putBaseObject(name);
        engine.put("javax.script.filename",name);
        try {
            engine.eval(wasm);
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
    }

    @Override
    public String toString(Object var) {
        return var.toString();
    }

    @Override
    public boolean isThisLanguage(Object var) {
        return false;
    }

    @Override
    public List<String> getPragma(String code) {
        return null;
    }
}
