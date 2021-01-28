package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.BlockIterator;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.BNLogger;
import com.blocklynukkit.loader.other.Entities.BNNPC;
import com.blocklynukkit.loader.other.Entities.FloatingText;
import com.blocklynukkit.loader.script.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SignatureCommand extends Command {
    public SignatureCommand() {
        super("signature","show the WebassemblyBN signature of a method.","<className> [methodName]",new String[]{"sig"});
        this.setPermission("blocklynukkit.opall");
    }
    public boolean execute(CommandSender sender, String s, String[] args){
        if(sender.isPlayer()){
            sender.sendMessage(TextFormat.RED+"This command can only be called from console!");
            return false;
        }else {
            try {
                Class clazz;
                if(args[0].equals("logger")){
                    clazz = BNLogger.class;
                }else if(args[0].equals("manager")){
                    clazz = FunctionManager.class;
                }else if(args[0].equals("algorithm")){
                    clazz = AlgorithmManager.class;
                }else if(args[0].equals("blockitem")){
                    clazz = BlockIterator.class;
                }else if(args[0].equals("database")){
                    clazz = DatabaseManager.class;
                }else if(args[0].equals("entity")){
                    clazz = EntityManager.class;
                }else if(args[0].equals("inventory")){
                    clazz = InventoryManager.class;
                }else if(args[0].equals("world")){
                    clazz = LevelManager.class;
                }else if(args[0].equals("notemusic")){
                    clazz = NotemusicManager.class;
                }else if(args[0].equals("particle")){
                    clazz = ParticleManager.class;
                }else if(args[0].equals("window")){
                    clazz = WindowManager.class;
                }else if(args[0].equals("BNNPC")){
                    clazz = BNNPC.class;
                }else if(args[0].equals("BNFloatingText")){
                    clazz = FloatingText.class;
                }else {
                    clazz = Class.forName(args[0]);
                }
                if(args.length<2){
                    for(Method each:clazz.getMethods()){
                        StringBuilder name = new StringBuilder((each.getName() + ": "));
                        for(Parameter p: each.getParameters()){
                            name.append(p.getType().getName()).append(";");
                        }
                        if(each.getParameterCount()==0){
                            name.append(";");
                        }
                        System.out.println(name.toString());
                    }
                }else {
                    for(Method each:clazz.getMethods()){
                        if(each.getName().equals(args[1])){
                            StringBuilder name = new StringBuilder((each.getName() + ": "));
                            for(Parameter p: each.getParameters()){
                                name.append(p.getType().getName()).append(";");
                            }
                            System.out.println(name.toString());
                            break;
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
