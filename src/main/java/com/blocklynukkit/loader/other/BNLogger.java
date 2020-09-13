package com.blocklynukkit.loader.other;

import cn.nukkit.Server;

public class BNLogger {
    private String name;
    public BNLogger(String name){
        this.name=name.replaceFirst("\\.js","").replaceFirst("\\.py","").replaceFirst("\\.lua","");
    }
    public void info(String mes){
        Server.getInstance().getLogger().info("["+name+"] "+mes);
    }
    public void warning(String mes){
        Server.getInstance().getLogger().warning("["+name+"] "+mes);
    }
}
