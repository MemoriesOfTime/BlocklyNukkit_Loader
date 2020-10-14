package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.scriptloader.BNPackageLoader;
import com.blocklynukkit.loader.scriptloader.bases.SingleRunner;
import com.google.gson.*;

import java.io.*;
import java.util.LinkedHashMap;

public class PackageCommand extends Command {
    public PackageCommand() {
        super("bnpackage","display previous error stacktrace","bnpackage <build/load> <args>",new String[]{"bnp","bnpack"});
        this.setPermission("blocklynukkit.opall");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        long start = System.currentTimeMillis();
        if(sender.isPlayer()){
            sender.sendMessage(TextFormat.RED+"This command can only be called from console!");
            return false;
        }else {
            if(args.length<2){
                sender.sendMessage(TextFormat.RED+"wrong arguments!");
                sender.sendMessage(this.getUsage());
                return false;
            }else if(args[0].equals("build")){
                File config = new File(args[1]);
                JsonObject json = null;
                if(!config.exists()){
                    sender.sendMessage(TextFormat.RED+"Build config file not found!");
                    return false;
                }
                try {
                    json = new JsonParser().parse(new FileReader(config)).getAsJsonObject();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                if(json==null){
                    sender.sendMessage(TextFormat.RED+"Build config file cannot be parsed!");
                    return false;
                }
                if(json.getAsJsonPrimitive("name")==null){
                    sender.sendMessage(TextFormat.RED+"Cannot find field \"name\" in build config!");
                    return false;
                }
                if(!json.getAsJsonPrimitive("name").isString()){
                    sender.sendMessage(TextFormat.RED+"Field \"name\" must ba a string!");
                    return false;
                }
                if(json.getAsJsonPrimitive("plugins")==null){
                    sender.sendMessage(TextFormat.RED+"Cannot find field \"plugins\" in build config!");
                    return false;
                }
                if(!json.getAsJsonPrimitive("name").isJsonArray()){
                    sender.sendMessage(TextFormat.RED+"Field \"name\" must ba an array of paths!");
                    return false;
                }
                String name = json.getAsJsonPrimitive("name").getAsString();
                JsonArray array = json.getAsJsonPrimitive("plugins").getAsJsonArray();
                LinkedHashMap<String,String> bnpackage = new LinkedHashMap<>();
                for(JsonElement path:array){
                    File each = new File(path.getAsString());
                    if(!each.exists()){
                        sender.sendMessage(TextFormat.RED+"File: "+path.getAsString()+" not found!");
                        sender.sendMessage(TextFormat.RED+path.getAsString()+"will not be packed into "+name+".bnp!");
                        continue;
                    }
                    bnpackage.put(each.getName(), Utils.readToString(each.getPath()));
                }
                byte[] fileContent = new BNPackageLoader(Loader.plugin).pack(bnpackage);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(Loader.plugin.getDataFolder().getPath()+"/"+(name.endsWith(".bnp")?name:(name+".bnp"))));
                    fileOutputStream.write(fileContent);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(TextFormat.YELLOW+"Build completed in "+(System.currentTimeMillis()-start)+"ms");
                sender.sendMessage(TextFormat.YELLOW+"Output path: "+Loader.plugin.getDataFolder().getAbsolutePath()+"/"+name+".bnp");
            }else if(args[0].equals("load")){
                File bnp = new File(args[1]);
                if(!bnp.exists()){
                    sender.sendMessage(TextFormat.RED+"BNPackage file not found!");
                    return false;
                }
                try {
                    FileInputStream fileInputStream = new FileInputStream(bnp);
                    byte[] bytes = new byte[fileInputStream.available()];
                    fileInputStream.read(bytes);
                    BNPackageLoader bploader = new BNPackageLoader(Loader.plugin);
                    bploader.runPlugins(bploader.unpack(bytes));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
