package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import javassist.CannotCompileException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class ExportDevJarCommand extends Command {
    public ExportDevJarCommand() {
        super("exportdevjar","导出bn插件为开发用jar包","exportdevjar <BNPluginName>");
        this.setPermission("blocklynukkit.opall");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!sender.isOp()){
            sender.sendMessage(TextFormat.RED+"This command can only be called by ops!");
        }
        if(args.length!=1){
            return true;
        }
        if(Loader.bnClasses.get(args[0])==null){
            sender.sendMessage(TextFormat.RED+"Plugin Not Found");
            return true;
        }
        try{
            File jarfile = new File("./plugins/BlocklyNukkit/devJars/"+args[0]+".jar");
            jarfile.mkdirs();
            if(jarfile.exists())jarfile.delete();
            if(!jarfile.exists())jarfile.createNewFile();
            byte[] clazz = Loader.bnClasses.get(args[0]).toBytecode();
            JarOutputStream jops = new JarOutputStream(new FileOutputStream(jarfile));
            jops.putNextEntry(new JarEntry(args[0].split("\\.")[0]+"/"+args[0].split("\\.")[1]+".class"));
            jops.write(clazz);
            jops.flush();jops.close();
            sender.sendMessage(TextFormat.YELLOW+"Finish.");
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}