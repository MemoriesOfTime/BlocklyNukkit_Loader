package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;

public class showStackTrace extends Command {
    public showStackTrace() {
        super("showstacktrace","display previous error stacktrace");
        this.setPermission("blocklynukkit.opall");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        if(sender.isPlayer()){
            sender.sendMessage(TextFormat.RED+"This command can only be called from console!");
            return false;
        }else {
            if(Loader.previousException!=null){
                Loader.previousException.printStackTrace();
            }
        }
        //com.blocklynukkit.loader.Loader.functionManager.callFunction("bn插件名::函数名");
        return false;
    }
}
