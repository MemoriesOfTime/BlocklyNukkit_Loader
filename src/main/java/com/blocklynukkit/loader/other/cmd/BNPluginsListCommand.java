package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;

public class BNPluginsListCommand extends Command {
    public BNPluginsListCommand() {
        super("bnplugins","查看所有安装的blocklynukkit插件");
        this.setPermission("blocklynukkit.opall");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        if(sender.isPlayer()){
            if(!sender.isOp())return false;
        }
        String out = TextFormat.GREEN+"BlocklyNukkit插件("+ Loader.bnpluginset.size()+"): ";
        for(String a:Loader.bnpluginset){
            out+=a+", ";
        }
        sender.sendMessage(out);
        return false;
    }
}
