package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.debug.data.CommandInfo;
import com.blocklynukkit.loader.script.FunctionManager;
import com.blocklynukkit.loader.utils.Utils;

import java.io.File;

import static com.blocklynukkit.loader.Loader.getlogger;

public class BNReloadCommand extends Command {
    public BNReloadCommand() {
        super("bnreload","重载指定的blocklynukkit插件");
        this.setPermission("blocklynukkit.opall");
        this.setAliases(new String[]{"bnr","reloadbn"});
    }
    @Override
    public boolean execute(CommandSender commandSender, String cmd, String[] args) {
        if(!commandSender.isOp()){
            commandSender.sendMessage(TextFormat.RED+"You cannot use this command!");
            return false;
        }
        if(args.length>0){
            for(String each:args){
                if(each.endsWith(".jar")||each.endsWith(".bnpx")||each.endsWith(".bnp")||!each.contains(".")
                        ||!Loader.engineMap.containsKey(each)||new File(Server.getInstance().getPluginPath()+"/BlocklyNukkit/"+each).exists()){
                    if(Loader.pluginTasksMap.containsKey(each))
                    for(int i:Loader.pluginTasksMap.get(each)){
                        Server.getInstance().getScheduler().cancelTask(i);
                    }
                    if(Loader.plugincmdsmap.containsKey(each))
                    for(CommandInfo info:Loader.plugincmdsmap.values()){
                        if(info.canIdentityPlugin()){
                            if(info.getPlugin().equals(each)){
                                Command c = Server.getInstance().getCommandMap().getCommand(info.commandName);
                                if(c instanceof FunctionManager.stopAbleCommand){
                                    ((FunctionManager.stopAbleCommand)c).stop();
                                }
                            }
                        }
                    }
                    Loader.engineMap.remove(each);
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getlogger().warning("加载BN插件: " + each);
                    else
                        getlogger().warning("loading BN plugin: " + each);
                    Loader.putEngine(each, Utils.readToString(Server.getInstance().getPluginPath()+"/BlocklyNukkit/"+each));
                }else {
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getlogger().warning("找不到加载BN插件: " + each);
                    else
                        getlogger().warning("Cannot find BN plugin: " + each);
                }
            }
        }else {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().warning("命令格式错误！/bnreload <bn插件文件名>");
            else
                getlogger().warning("Wrong arguments! /bnreload <bnPluginFile>");
            return false;
        }
        return false;
    }
}
