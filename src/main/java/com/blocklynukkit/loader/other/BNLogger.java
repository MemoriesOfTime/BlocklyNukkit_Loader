package com.blocklynukkit.loader.other;

import cn.nukkit.Server;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;

public class BNLogger {
    private String name;
    public BNLogger(String name){
        this.name=name.replaceFirst("\\.js","").replaceFirst("\\.py","").replaceFirst("\\.lua","").replaceFirst("\\.php","");
    }
    public void info(String mes){
        Server.getInstance().getLogger().info("["+name+"] "+mes);
    }
    public void info(ScriptObjectMirror scriptObjectMirror){
        info(ScriptUtils.unwrap(scriptObjectMirror).toString());
    }
    public void warning(String mes){
        Server.getInstance().getLogger().warning("["+name+"] "+mes);
    }
}
