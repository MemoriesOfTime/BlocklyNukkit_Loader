package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.other.debug.Debuger;

public class DebugerCommand extends Command {
    public DebugerCommand() {
        super("bndebug","打开bn调试器");
        this.setPermission("blocklynukkit.opall");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        if(sender.isPlayer()){
            sender.sendMessage(TextFormat.RED+"This command can only be called from console!");
            return false;
        }
        new Debuger().display();
        return false;
    }
}
