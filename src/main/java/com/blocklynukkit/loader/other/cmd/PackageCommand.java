package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Babel;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.BNPackageLoader;
import com.google.gson.*;

import java.io.*;
import java.util.LinkedHashMap;

public class PackageCommand extends Command {
    public PackageCommand() {
        super("bnpackage","display previous error stacktrace","bnpackage <build/load> <args>",new String[]{"bnp","bnpack","bnpm"});
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
                if(json.getAsJsonArray("plugins")==null){
                    sender.sendMessage(TextFormat.RED+"Cannot find field \"plugins\" in build config!");
                    return false;
                }
                String name = json.getAsJsonPrimitive("name").getAsString();
                JsonArray array = json.getAsJsonArray("plugins");
                boolean compressed = json.has("compress");
                if(compressed){
                    if(!json.getAsJsonPrimitive("compress").isBoolean()){
                        sender.sendMessage(TextFormat.RED+"Field \"compress\" must ba a boolean!");
                        return false;
                    }
                    compressed = json.getAsJsonPrimitive("compress").getAsBoolean();
                }
                LinkedHashMap<String,String> bnpackage = new LinkedHashMap<>();
                for(JsonElement path:array){
                    File each = new File(path.getAsString());
                    if(!each.exists()){
                        sender.sendMessage(TextFormat.RED+"File: "+path.getAsString()+" not found!");
                        sender.sendMessage(TextFormat.RED+path.getAsString()+"will not be packed into "+name+".bnp!");
                        continue;
                    }
                    if(each.getName().endsWith(".js")||each.getName().endsWith(".py")||each.getName().endsWith(".lua")||each.getName().endsWith(".php")){
                        bnpackage.put(each.getName(), Utils.readToString(each.getPath()));
                    }else {
                        bnpackage.put(each.getPath(), Utils.readToString(each.getPath()));
                    }
                }
                File bnpf = new File(Loader.plugin.getDataFolder().getPath()+"/"+(compressed?(name.endsWith(".bnpx")?name:(name+".bnpx")):(name.endsWith(".bnp")?name:(name+".bnp"))));
                if(bnpf.exists())bnpf.delete();
                if(!compressed){
                    Utils.writeWithString(bnpf,new BNPackageLoader(Loader.plugin).pack2String(bnpackage));
                }else {
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(bnpf);
                        fileOutputStream.write(new BNPackageLoader(Loader.plugin).pack2Byte(bnpackage));
                        fileOutputStream.flush();fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sender.sendMessage(TextFormat.YELLOW+"Build completed in "+(System.currentTimeMillis()-start)+"ms");
                sender.sendMessage(TextFormat.YELLOW+"Output path: "+Loader.plugin.getDataFolder().getAbsolutePath()+"/"+name+(compressed?".bnpx":".bnp"));
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
            }else if(args[0].equals("transformjs")||args[0].equals("transformJS")||args[0].equals("transjs")||args[0].equals("transJS")||args[0].equals("tjs")){
                File input = new File(args[1]);
                if(!input.exists()){
                    sender.sendMessage(TextFormat.RED+"Input file not found!");
                    return false;
                }
                if(Loader.babel==null){
                    sender.sendMessage(TextFormat.WHITE+"JavaScript translator initializing...");
                    Loader.babel=new Babel();
                }
                String out = Loader.babel.transform(Utils.readToString(input));
                File output = new File(Utils.replaceLast(args[1],".js",".es5.js"));
                try {
                    if(output.exists())output.delete();
                    output.createNewFile();
                    Utils.writeWithString(output,"//  ____  _            _    _       _   _       _    _    _ _   \n" +
                            "// |  _ \\| |          | |  | |     | \\ | |     | |  | |  (_) |  \n" +
                            "// | |_) | | ___   ___| | _| |_   _|  \\| |_   _| | _| | ___| |_ \n" +
                            "// |  _ <| |/ _ \\ / __| |/ / | | | | . ` | | | | |/ / |/ / | __|\n" +
                            "// | |_) | | (_) | (__|   <| | |_| | |\\  | |_| |   <|   <| | |_ \n" +
                            "// |____/|_|\\___/ \\___|_|\\_\\_|\\__, |_| \\_|\\__,_|_|\\_\\_|\\_\\_|\\__|\n" +
                            "//                             __/ |                            \n" +
                            "//                            |___/                             \n\n" + out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(TextFormat.YELLOW+"Transform completed in "+(System.currentTimeMillis()-start)+"ms");
                sender.sendMessage(TextFormat.YELLOW+"Output path: "+output.getAbsolutePath());
            }
        }
        return false;
    }
}
