package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.blocklynukkit.loader.utils.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Vector;

public class BNMiscBuild extends Command {
    public BNMiscBuild() {
        super("bnmiscbuild","其他构建","bnmiscbuild listClass");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        try{
            if(!sender.isPlayer()) {
                if (args.length == 1) {
                    if (args[0].equals("listClass")) {
                        Field f = ClassLoader.class.getDeclaredField("classes");
                        f.setAccessible(true);
                        Vector<Class<?>> classesThread = (Vector<Class<?>>) f.get(this.getClass().getClassLoader());
                        Vector<Class<?>> classesSystem = (Vector<Class<?>>) f.get(ClassLoader.getSystemClassLoader());
                        StringBuilder output = new StringBuilder();
                        classesThread.forEach(c -> output.append(c.toGenericString()).append('\n'));
                        output.append("------------------------\nSystemClassLoader\n------------------------\n");
                        classesSystem.forEach(c -> output.append(c.toGenericString()).append('\n'));
                        Utils.writeWithString(new File("./classes.txt"), output.toString());
                        
                    }
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
