package com.blocklynukkit.loader.other;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.scriptloader.JavaScriptLoader;
import com.blocklynukkit.loader.scriptloader.LuaLoader;
import com.blocklynukkit.loader.scriptloader.PHPLoader;
import com.blocklynukkit.loader.scriptloader.PythonLoader;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

public class BNLogger {
    private String name;
    public BNLogger(String name){
        this.name=name.replaceFirst("\\.js","").replaceFirst("\\.py","").replaceFirst("\\.lua","").replaceFirst("\\.php","");
    }
    public void info(Object mes){
        if(mes==null){
            Server.getInstance().getLogger().info("["+name+"] null");
        }
        String output = mes.toString();
        JavaScriptLoader js = new JavaScriptLoader(Loader.plugin);
        if(js.isThisLanguage(mes)){
            output = js.toString(mes);
            Server.getInstance().getLogger().info("["+name+"] "+output);
            return;
        }
        if(Loader.plugins.containsKey("PyBN")){
            PythonLoader py = new PythonLoader(Loader.plugin);
            if(py.isThisLanguage(mes)){
                output = py.toString(mes);
                Server.getInstance().getLogger().info("["+name+"] "+output);
                return;
            }
        }
        if(Loader.plugins.containsKey("PHPBN")){
            PHPLoader php = new PHPLoader(Loader.plugin);
            if(php.isThisLanguage(mes)){
                output = php.toString(mes);
                Server. getInstance().getLogger().info("["+name+"] "+output);
                return;
            }
        }
        LuaLoader lua = new LuaLoader(Loader.plugin);
        if(lua.isThisLanguage(mes)){
            output = lua.toString(mes);
            Server.getInstance().getLogger().info("["+name+"] "+output);
            return;
        }
        if(mes!=null&&mes.getClass().isArray()){
            output = Arrays.toString((Object[]) mes);
            Server.getInstance().getLogger().info("["+name+"] "+output);
        }
        Server.getInstance().getLogger().info("["+name+"] "+output);
    }

    public void info(Object mes1,Object mes2){
        info(mes1);info(mes2);
    }

    public void info(Object mes1,Object mes2,Object mes3){
        info(mes1, mes2);info(mes3);
    }

    public void info(Object mes1,Object mes2,Object mes3,Object mes4){
        info(mes1, mes2, mes3);info(mes4);
    }

    public void info(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5){
        info(mes1, mes2, mes3, mes4);info(mes5);
    }

    public void info(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6){
        info(mes1, mes2, mes3, mes4, mes5);info(mes6);
    }

    public void info(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7){
        info(mes1, mes2, mes3, mes4, mes5, mes6);info(mes7);
    }

    public void info(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8){
        info(mes1, mes2, mes3, mes4, mes5, mes6, mes7);info(mes8);
    }

    public void info(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8,Object... meses){
        info(mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8);
        for(Object mes:meses)info(mes);
    }

    public void log(Object mes){
        info(mes);
    }

    public void log(Object mes1,Object mes2){
        log(mes1);log(mes2);
    }

    public void log(Object mes1,Object mes2,Object mes3){
        log(mes1, mes2);log(mes3);
    }

    public void log(Object mes1,Object mes2,Object mes3,Object mes4){
        log(mes1, mes2, mes3);log(mes4);
    }

    public void log(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5){
        log(mes1, mes2, mes3, mes4);log(mes5);
    }

    public void log(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6){
        log(mes1, mes2, mes3, mes4, mes5);log(mes6);
    }

    public void log(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7){
        log(mes1, mes2, mes3, mes4, mes5, mes6);log(mes7);
    }

    public void log(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8){
        log(mes1, mes2, mes3, mes4, mes5, mes6, mes7);log(mes8);
    }

    public void log(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8,Object... meses){
        log(mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8);
        for(Object mes:meses)log(mes);
    }

    //发送警告

    public void warning(Object mes){
        if(mes==null){
            Server.getInstance().getLogger().info("["+name+"] null");
        }
        String output = mes.toString();
        JavaScriptLoader js = new JavaScriptLoader(Loader.plugin);
        if(js.isThisLanguage(mes)){
            output = js.toString(mes);
            Server.getInstance().getLogger().warning("["+name+"] "+output);
            return;
        }
        if(Loader.plugins.containsKey("PyBN")){
            PythonLoader py = new PythonLoader(Loader.plugin);
            if(py.isThisLanguage(mes)){
                output = py.toString(mes);
                Server.getInstance().getLogger().warning("["+name+"] "+output);
                return;
            }
        }
        if(Loader.plugins.containsKey("PHPBN")){
            PHPLoader php = new PHPLoader(Loader.plugin);
            if(php.isThisLanguage(mes)){
                output = php.toString(mes);
                Server. getInstance().getLogger().warning("["+name+"] "+output);
                return;
            }
        }
        LuaLoader lua = new LuaLoader(Loader.plugin);
        if(lua.isThisLanguage(mes)){
            output = lua.toString(mes);
            Server.getInstance().getLogger().warning("["+name+"] "+output);
            return;
        }
        if(mes!=null&&mes.getClass().isArray()){
            output = Arrays.toString((Object[]) mes);
            Server.getInstance().getLogger().info("["+name+"] "+output);
        }
        if(output.startsWith(mes.getClass().getName()+"@")){
            try{
                output = new GsonBuilder().setPrettyPrinting().create().toJson(mes);
            }catch (Throwable e){
                //ignore
            }
        }
        Server.getInstance().getLogger().warning("["+name+"] "+output);
    }

    public void warning(Object mes1,Object mes2){
        warning(mes1);warning(mes2);
    }

    public void warning(Object mes1,Object mes2,Object mes3){
        warning(mes1, mes2);warning(mes3);
    }

    public void warning(Object mes1,Object mes2,Object mes3,Object mes4){
        warning(mes1, mes2, mes3);warning(mes4);
    }

    public void warning(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5){
        warning(mes1, mes2, mes3, mes4);warning(mes5);
    }

    public void warning(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6){
        warning(mes1, mes2, mes3, mes4, mes5);warning(mes6);
    }

    public void warning(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7){
        warning(mes1, mes2, mes3, mes4, mes5, mes6);warning(mes7);
    }

    public void warning(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8){
        warning(mes1, mes2, mes3, mes4, mes5, mes6, mes7);warning(mes8);
    }

    public void warning(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8,Object... meses){
        warning(mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8);
        for(Object mes:meses)warning(mes);
    }

    public void warn(Object mes){
        warning(mes);
    }

    public void warn(Object mes1,Object mes2){
        warn(mes1);warn(mes2);
    }

    public void warn(Object mes1,Object mes2,Object mes3){
        warn(mes1, mes2);warn(mes3);
    }

    public void warn(Object mes1,Object mes2,Object mes3,Object mes4){
        warn(mes1, mes2, mes3);warn(mes4);
    }

    public void warn(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5){
        warn(mes1, mes2, mes3, mes4);warn(mes5);
    }

    public void warn(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6){
        warn(mes1, mes2, mes3, mes4, mes5);warn(mes6);
    }

    public void warn(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7){
        warn(mes1, mes2, mes3, mes4, mes5, mes6);warn(mes7);
    }

    public void warn(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8){
        warn(mes1, mes2, mes3, mes4, mes5, mes6, mes7);warn(mes8);
    }

    public void warn(Object mes1,Object mes2,Object mes3,Object mes4,Object mes5,Object mes6,Object mes7,Object mes8,Object... meses){
        warn(mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8);
        for(Object mes:meses)warn(mes);
    }

    public String getName(){
        return this.name;
    }
}
