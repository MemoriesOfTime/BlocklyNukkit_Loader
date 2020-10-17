package com.blocklynukkit.loader.other;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.scriptloader.JavaScriptLoader;
import com.blocklynukkit.loader.scriptloader.LuaLoader;
import com.blocklynukkit.loader.scriptloader.PHPLoader;
import com.blocklynukkit.loader.scriptloader.PythonLoader;

public class BNLogger {
    private String name;
    public BNLogger(String name){
        this.name=name.replaceFirst("\\.js","").replaceFirst("\\.py","").replaceFirst("\\.lua","").replaceFirst("\\.php","");
    }
    public void info(Object mes){
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


    public void warning(String mes){
        Server.getInstance().getLogger().warning("["+name+"] "+mes);
    }
}
