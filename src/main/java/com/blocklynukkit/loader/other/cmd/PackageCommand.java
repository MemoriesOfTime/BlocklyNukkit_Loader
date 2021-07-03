package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Babel;
import com.blocklynukkit.loader.utils.StringUtils;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.BNPackageLoader;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class PackageCommand extends Command {
    private static final String tsHead = "type double = number;\n" +
            "type float = number;\n" +
            "type byte = 0xff;\n" +
            "type short = number;\n" +
            "type int = number;\n" +
            "type long = number;\n" +
            "type char = string;\n" +
            "declare var server : cn.nukkit.Server;\n" +
            "declare var plugin : cn.nukkit.plugin.Plugin;\n" +
            "declare var logger : com.blocklynukkit.loader.other.BNLogger;\n" +
            "type FunV = string | Function;\n" +
            "type Fun<T> = string | ((a: T) => void);\n" +
            "type Fun2<A, B> = string | ((a: A, b: B) => void);\n" +
            "type Fun3<A, B, C> = string | ((a: A, b: B, c: C) => void);\n" +
            "type Fun4<A, B, C, D> = string | ((a: A, b: B, c: C, d: D) => void);\n" +
            "type Fun5<A, B, C, D, E> = string | ((a: A, b: B, c: C, d: D, e: E) => void);\n" +
            "function F<A, B, C, D, E>(input: Fun5<A, B, C, D, E>) : Fun5<A, B, C, D, E>;\n" +
            "function F<A, B, C, D>(input: Fun4<A, B, C, D>) : Fun3<A, B, C, D>;\n" +
            "function F<A, B, C>(input: Fun3<A, B, C>) : Fun3<A, B, C>;\n" +
            "function F<A, B>(input: Fun2<A, B>) : Fun2<A, B>;\n" +
            "function F<A>(input: Fun<A>) : Fun<A>;\n" +
            "function F(input: FunV) : FunV;\n" +
            "declare namespace java.lang{\n" +
            "    class Object{}\n" +
            "    type String = string;\n" +
            "    type Integer = number;\n" +
            "    type Long = number;\n" +
            "    type Float = number;\n" +
            "    type Double = number;\n" +
            "    type Short = number;\n" +
            "    type Byte = 0xff;\n" +
            "    type Character = string;\n" +
            "}\n" +
            "declare namespace java.math{\n" +
            "    type BigDecimal = number;\n" +
            "    type BigInteger = number;\n" +
            "}\n" +
            "/** 一个来自Java的类 */\n" +
            "/**\n" +
            " * @version 1.2.9.4\n" +
            " */\n" +
            "interface Class{}\n" +
            "/**\n" +
            " * @version 1.2.9.4\n" +
            " * @param {java.lang.String} 要导入的模块或者java类的名称\n" +
            " */\n" +
            "declare function require(className:java.lang.String): Class;\n" +
            "/**\n" +
            " * @version 1.2.9.4\n" +
            " */\n" +
            "declare namespace Java{\n" +
            "    /** \n" +
            "     * 将一个java类导入到js中\n" +
            "     * @param {string} className java类的全类名\n" +
            "     */\n" +
            "    function type(className: java.lang.String): Class;\n" +
            "    /**\n" +
            "     * 用js继承并拓展一个java类\n" +
            "     * @param {Class} clazz 被继承拓展的java类对象\n" +
            "     * @param {any} methods 包含拓展函数的js对象\n" +
            "     */\n" +
            "    function extend(clazz: Class, methods: any): Class;\n" +
            "    /**\n" +
            "     * 将java对象转为合适的js数组或对象\n" +
            "     * @param {Class} input 要被转换的java对象\n" +
            "     * @example Java.from(java数组) = 对应的js数组\n" +
            "     * @example Java.from(javaMap) = 对应js对象\n" +
            "     */\n" +
            "    function from(input: any): any;\n" +
            "    /**\n" +
            "     * 将java对象转为合适的js数组或对象\n" +
            "     * @param {Class} input 要被转换的java对象\n" +
            "     * @example Java.from(java数组) = 对应的js数组\n" +
            "     * @example Java.from(javaMap) = 对应js对象\n" +
            "     */\n" +
            "    function from(input: java.util.List): Array<any>;\n" +
            "    /**\n" +
            "     * 将java对象转为合适的js数组或对象\n" +
            "     * @param {Class} input 要被转换的java对象\n" +
            "     * @example Java.from(java数组) = 对应的js数组\n" +
            "     * @example Java.from(javaMap) = 对应js对象\n" +
            "     */\n" +
            "    function from(input: java.util.Map): JSON;\n" +
            "    /**\n" +
            "     * 将js数组或对象转为合适的java对象\n" +
            "     * @param {any} input 要被转换的js对象\n" +
            "     * @param {(Class|string)} 要转换到的java类\n" +
            "     * @example Java.to(new Array()) = 对应的java数组\n" +
            "     * @example Java.to(new Object()) = 对应的javaMap\n" +
            "     */\n" +
            "    function to(input: any,toClass: Class|string): any;\n" +
            "    /**\n" +
            "     * 将js数组或对象转为合适的java对象\n" +
            "     * @param {any} input 要被转换的js对象\n" +
            "     * @param {(Class|string)} 要转换到的java类\n" +
            "     * @example Java.to(new Array()) = 对应的java数组\n" +
            "     * @example Java.to(new Object()) = 对应的javaMap\n" +
            "     */\n" +
            "    function to(input: Array<any>,toClass: Class|string): java.util.List;\n" +
            "}";
    public PackageCommand() {
        super("bnpackage","display previous error stacktrace","bnpm <install/update/build/load> <args>",new String[]{"bnp","bnpack","bnpm"});
        this.setPermission("blocklynukkit.opall");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        long start = System.currentTimeMillis();
        if(sender.isPlayer()){
            sender.sendMessage(TextFormat.RED+Utils.translate("该命令仅能从服务器控制台使用！","This command can only be called from console!"));
            return false;
        }else {
            if(args.length<2){
                sender.sendMessage(TextFormat.RED+Utils.translate("命令参数错误！","wrong arguments!"));
                sender.sendMessage(this.getUsage());
                return false;
            }else if(args[0].equalsIgnoreCase("build")||args[0].equalsIgnoreCase("-b")){
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
            }else if(args[0].equalsIgnoreCase("-t")||args[0].equals("transformjs")||args[0].equals("transformJS")||args[0].equals("transjs")||args[0].equals("transJS")||args[0].equals("tjs")){
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
            }else if(args[0].equalsIgnoreCase("install") || args[0].equalsIgnoreCase("-i")){
                try {
                    sender.sendMessage(TextFormat.YELLOW+Utils.translate("正在检索BNPM插件中心...","Checking BNPM plugin center..."));
                    String infoTmp = Utils.sendGet("https://blocklunukkit-1488c9-1259395953.ap-shanghai.app.tcloudbase.com/bnpm/pluginVersion","name="+args[1]);
                    if(infoTmp.contains("ERR:INTERNAL")){
                        sender.sendMessage(TextFormat.RED+Utils.translate("错误：BNPM中央服务器错误","ERROR: BNPM central server encountered an error!"));
                        return false;
                    }else if(infoTmp.contains("ERR:NOT_FOUND")){
                        sender.sendMessage(TextFormat.RED+Utils.translate("错误：找不到"+args[1]+"插件","ERROR: Plugin "+args[1]+" not found!"));
                        return false;
                    }
                    JsonObject infoJson = new JsonParser().parse(infoTmp).getAsJsonObject();
                    String version = infoJson.get("version").getAsString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date lastUpdate = dateFormat.parse(infoJson.get("time").getAsString());
                    Config bnpmConfig = new Config(new File("./plugins/BlocklyNukkit/bnpm/"+args[1]+".yml"),Config.YAML);
                    String currentUpdateStr = bnpmConfig.getString("time");
                    if(currentUpdateStr != null && !currentUpdateStr.equals("") && lastUpdate.before(dateFormat.parse(currentUpdateStr))){
                        sender.sendMessage(Utils.translate("已经安装了"+args[1]+"最新版"+version,"latest "+version+" of "+args[1]+" has already installed!"));
                        return false;
                    }
                    JsonArray array = new JsonParser().parse(Utils.sendGet("https://blocklunukkit-1488c9-1259395953.ap-shanghai.app.tcloudbase.com/bnpm/pluginAssets","name="+args[1])).getAsJsonArray();
                    String[] assets = new String[array.size()];
                    List<String> localPaths = new ArrayList<>(assets.length);
                    for(int i=0;i<array.size();i++){
                        assets[i] = array.get(i).getAsString();
                    }
                    bnpmConfig.set("version",version);
                    bnpmConfig.set("time",dateFormat.format(Utils.localDateTime2Date(LocalDateTime.now())));
                    for (String asset : assets) {
                        String fileName = asset.substring(asset.lastIndexOf('/') + 1);
                        if(fileName.endsWith(".jar")){
                            localPaths.add("./plugins/"+fileName);
                            Utils.downLoadFromUrl(asset, fileName, "./plugins");
                        }else {
                            localPaths.add("./plugins/BlocklyNukkit/"+fileName);
                            Utils.downLoadFromUrl(asset, fileName, "./plugins/BlocklyNukkit");
                        }
                        sender.sendMessage(Utils.translate("正在下载 " + fileName + " ...", "Downloading " + fileName + " ..."));
                    }
                    bnpmConfig.set("pluginPaths",localPaths);
                    bnpmConfig.save();
                    sender.sendMessage(Utils.translate(args[1]+"安装完成","Successfully installed "+args[1]));
                } catch (ParseException | IOException e) {
                    sender.sendMessage(TextFormat.RED+Utils.translate("错误：网络连接出错","ERROR: Network error"));
                    e.printStackTrace();
                }
            }else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("-d")){
                try {
                    File bnpmConfigFile = new File("./plugins/BlocklyNukkit/bnpm/"+args[1]+".yml");
                    if(bnpmConfigFile.exists()){
                        Config bnpmConfig = new Config(bnpmConfigFile,Config.YAML);
                        String version = bnpmConfig.getString("version");
                        List<String> pluginPaths = bnpmConfig.getStringList("pluginPaths");
                        for(String each:pluginPaths){
                            File tmp = new File(each);
                            if (tmp.exists() && !tmp.delete())
                                sender.sendMessage(TextFormat.RED + Utils.translate("文件" + each + "删除失败", "Failed to delete file " + each));
                        }
                        bnpmConfigFile.delete();
                        sender.sendMessage(TextFormat.YELLOW+Utils.translate(args[1]+version+"已经删除",args[1]+" "+version+" has been deleted"));
                    }else {
                        sender.sendMessage(TextFormat.RED+Utils.translate("插件"+args[1]+"尚未安装！","Plugin "+args[1]+" hasn't been installed!"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(args[0].equalsIgnoreCase("ts")){
                Map<String, JsonObject> defs = new HashMap<>();
                String output = ""+tsHead;
                String manifest = Utils.readToString(args[1]);
                for(String each:manifest.split(";")){
                    defs.put(getBaseObjName(each),generateDefinition(each));
                }
                for(Map.Entry<String, JsonObject> entry:defs.entrySet()){
                    System.out.println("正在处理"+entry.getKey());
                    String eachOutput = "";
                    String version = entry.getValue().get("version").getAsString();
                    if(!entry.getKey().contains(".")){
                        eachOutput = "/**\n" +
                                " * @version " + version + "\n" +
                                " */\ndeclare namespace "+entry.getKey()+"{" +
                                "\n";
                        JsonObject fields = entry.getValue().get("definitions").getAsJsonObject().get("fields").getAsJsonObject();
                        for(Map.Entry<String,JsonElement> field:fields.entrySet()){
                            eachOutput += ("    /** "+field.getValue().getAsJsonObject().get("comment").getAsString()+" */\n    var "+field.getKey()+": "+field.getValue().getAsJsonObject().get("type").getAsString()+";\n");
                        }
                        JsonObject methods = entry.getValue().get("definitions").getAsJsonObject().get("methods").getAsJsonObject();
                        for(Map.Entry<String,JsonElement> method:methods.entrySet()){
                            JsonArray list = method.getValue().getAsJsonArray();
                            for(JsonElement tmpInfo:list){
                                JsonObject info = (JsonObject) tmpInfo;
                                //加函数注释
                                eachOutput += ("    /**\n     * "+info.get("comment").getAsString()+"\n");
                                for(JsonElement param:info.get("parameters").getAsJsonArray()){
                                    JsonObject detailParam = param.getAsJsonObject();
                                    eachOutput+=("     * @param {"+detailParam.get("type").getAsString()+"} "+paramConvert(detailParam.get("name").getAsString())+" "+detailParam.get("comment").getAsString()+"\n");
                                }
                                eachOutput += ("     */\n");
                                //加函数声明
                                eachOutput += ("    function "+method.getKey()+"(");
                                for(JsonElement param:info.get("parameters").getAsJsonArray()){
                                    JsonObject detailParam = param.getAsJsonObject();
                                    if(detailParam.get("callbackInfo").isJsonArray()){
                                        JsonArray array = detailParam.getAsJsonArray("callbackInfo");
                                        String fx = "Fun"+array.size()+"<";
                                        if(fx.equals("Fun0<")){
                                            fx = "FunV";
                                        }else{
                                            for(JsonElement eachType:array){
                                                fx += (((JsonObject)eachType).get("type").getAsString()+",");
                                            }
                                            fx = Utils.replaceLast(fx, ",", ">");
                                        }
                                        eachOutput+=((detailParam.get("varargs").getAsBoolean()?"...":"")+paramConvert(detailParam.get("name").getAsString())+": "+fx+",");
                                    }else
                                        eachOutput+=((detailParam.get("varargs").getAsBoolean()?"...":"")+paramConvert(detailParam.get("name").getAsString())+": "+detailParam.get("type").getAsString()+",");
                                }
                                eachOutput += ("): "+info.get("returnType").getAsString()+";\n").replaceAll("final","");
                                if(info.get("parameterCount").getAsInt() != 0)
                                    eachOutput = Utils.replaceLast(eachOutput,",","");
                            }
                        }
                        eachOutput+=("}\n");
                        output+=eachOutput;
                    }else{
                        String namespace = entry.getKey().substring(0,entry.getKey().lastIndexOf("."));
                        String className = entry.getKey().substring(entry.getKey().lastIndexOf(".")+1);
                        eachOutput = "declare namespace "+namespace+"{\n" +
                                "    "+(entry.getValue().get("abstract").getAsBoolean()?"abstract":"")+" class "+className+" extends "+entry.getValue().get("extends").getAsString()+"{\n";
                        JsonObject fields = entry.getValue().get("definitions").getAsJsonObject().get("fields").getAsJsonObject();
                        for(Map.Entry<String,JsonElement> field:fields.entrySet()){
                            eachOutput += ("        /** "+field.getValue().getAsJsonObject().get("comment").getAsString()+" */\n        "+field.getKey()+": "+field.getValue().getAsJsonObject().get("type").getAsString()+";\n");
                        }
                        JsonObject methods = entry.getValue().get("definitions").getAsJsonObject().get("methods").getAsJsonObject();
                        for(Map.Entry<String,JsonElement> method:methods.entrySet()){
                            JsonArray list = method.getValue().getAsJsonArray();
                            for(JsonElement tmpInfo:list){
                                JsonObject info = (JsonObject) tmpInfo;
                                //加函数注释
                                eachOutput += ("        /**\n         * "+info.get("comment").getAsString()+"\n");
                                for(JsonElement param:info.get("parameters").getAsJsonArray()){
                                    JsonObject detailParam = param.getAsJsonObject();
                                    eachOutput+=("         * @param {"+detailParam.get("type").getAsString()+"} "+paramConvert(detailParam.get("name").getAsString())+" "+detailParam.get("comment").getAsString()+"\n");
                                }
                                eachOutput += ("         */\n");
                                //加函数声明
                                eachOutput += ("        "+info.get("modifier").getAsString()
                                        .replaceAll("final","")
                                        .replaceAll("synchronized","")
                                        .replaceAll("transient","")
                                        .replaceAll("native","")
                                        .replaceAll("volatile","")+" "+method.getKey()+"(");
                                for(JsonElement param:info.get("parameters").getAsJsonArray()){
                                    JsonObject detailParam = param.getAsJsonObject();
                                    eachOutput+=((detailParam.get("varargs").getAsBoolean()?"...":"")+paramConvert(detailParam.get("name").getAsString())+": "+detailParam.get("type").getAsString()+",");
                                }
                                eachOutput += ("): "+info.get("returnType").getAsString()+";\n");
                                if(info.get("parameterCount").getAsInt() != 0)
                                    eachOutput = Utils.replaceLast(eachOutput,",","");
                            }
                        }
                        eachOutput+=("    }\n}\n");
                        output+=eachOutput;
                    }
                }
                File dtsFile = new File("./plugins/dts/bn.d.ts");
                File dtsFolder = new File("./plugins/dts");
                if(!dtsFolder.exists())dtsFolder.mkdirs();
                if(!dtsFile.exists()) {
                    try {
                        dtsFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Utils.writeWithString(dtsFile,output);
            }
        }
        return false;
    }
    public JsonObject generateDefinition(String className){
        class MethodDef implements Serializable{
            String modifier;
            String returnType;
            int parameterCount;
            Map<String,Object>[] parameters;
            String comment;
            public Map<String, Object> toMap(){
                Map<String, Object> map = new HashMap<>();
                map.put("modifier",modifier);
                map.put("returnType",returnType);
                map.put("parameterCount",parameterCount);
                map.put("parameters",parameters);
                map.put("comment",comment);
                return map;
            }
        }
        String originClassName = StringUtils.replace(className,"$","",1);
        String superClassName = "java.lang.Object";
        boolean isAbstract = false;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, List<Map<String, Object>>> info = new HashMap<>();
        Map<String, Map<String, String>> fields = new HashMap<>();
        List<Map<String,Object>> constructors = new ArrayList<>();
        try {
            Class clazz = Class.forName(className);
            if(clazz.getSuperclass() != null)
                superClassName = clazz.getSuperclass().getCanonicalName();
            isAbstract = Modifier.isAbstract(clazz.getModifiers());
            for(Constructor constructor:clazz.getDeclaredConstructors()){
                Map<String, Object> con = new HashMap<>();
                con.put("modifier",Modifier.toString(constructor.getModifiers()));
                con.put("parameterCount",constructor.getParameterCount());
                Map<String,Object>[] parameters = new HashMap[constructor.getParameterCount()];
                for (int i = 0; i < parameters.length; i++) {
                    Map<String,Object> tmp = new HashMap<>();
                    for(Parameter parameter:constructor.getParameters()){
                        tmp.put("name",parameter.getName());
                        tmp.put("type",parameter.getType().getCanonicalName());
                        tmp.put("varargs",parameter.isVarArgs());
                        Comment comment = parameter.getAnnotation(Comment.class);
                        if(comment!=null){
                            tmp.put("comment",comment.value());
                        }else{
                            tmp.put("comment","");
                        }
                    }
                    parameters[i] = tmp;
                }
                con.put("parameters",parameters);
                Comment constructorComment = (Comment) constructor.getAnnotation(Comment.class);
                if(constructorComment!=null){
                    con.put("comment",constructorComment.value());
                }else{
                    con.put("comment","");
                }
                constructors.add(con);
            }
            for(Method method: clazz.getDeclaredMethods()) {
                if (info.containsKey(method.getName())) {
                    MethodDef methodDef = new MethodDef();
                    methodDef.modifier = Modifier.toString(method.getModifiers());
                    methodDef.returnType = method.getReturnType().getCanonicalName();
                    methodDef.parameterCount = method.getParameterCount();
                    Comment methodComment = method.getAnnotation(Comment.class);
                    if(methodComment!=null)
                        methodDef.comment = methodComment.value();
                    else
                        methodDef.comment = "";
                    Map<String,Object>[] tmp = new HashMap[method.getParameterCount()];
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        Parameter parameter = method.getParameters()[i];
                        HashMap<String,Object> arg = new HashMap<>();
                        arg.put("name",parameter.getName());
                        arg.put("type",parameter.getType().getCanonicalName());
                        arg.put("varargs",parameter.isVarArgs());
                        Comment comment = parameter.getAnnotation(Comment.class);
                        if(comment!=null)
                            arg.put("comment",comment.value());
                        else
                            arg.put("comment","");
                        CallbackFunction cb = parameter.getAnnotation(CallbackFunction.class);
                        if(cb!=null){
                            System.out.println(method.getName()+":"+parameter.getName()+" ENC!");
                            String[] CB_classes = cb.classes();
                            String[] CB_parameters = cb.parameters();
                            String[] CB_comments = cb.comments();
                            Map<String, String>[] array = new Map[CB_classes.length];
                            for(int j=0;j<CB_classes.length;j++){
                                Map<String, String> entry = new HashMap<>();
                                entry.put("type",CB_classes[j]);
                                entry.put("name", CB_parameters[j]);
                                entry.put("comment", CB_comments[j]);
                                array[j] = entry;
                            }
                            arg.put("callbackInfo",array);
                        }else{
                            arg.put("callbackInfo","");
                        }
                        tmp[i] = arg;
                    }
                    methodDef.parameters = tmp;
                    List<Map<String, Object>> newArr = info.get(method.getName());
                    newArr.add(methodDef.toMap());
                    info.put(method.getName(), newArr);
                } else {
                    MethodDef methodDef = new MethodDef();
                    methodDef.modifier = Modifier.toString(method.getModifiers());
                    methodDef.returnType = method.getReturnType().getCanonicalName();
                    methodDef.parameterCount = method.getParameterCount();
                    Comment methodComment = method.getAnnotation(Comment.class);
                    if(methodComment!=null)
                        methodDef.comment = methodComment.value();
                    else
                        methodDef.comment = "";
                    Map<String,Object>[] tmp = new HashMap[method.getParameterCount()];
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        Parameter parameter = method.getParameters()[i];
                        HashMap<String,Object> arg = new HashMap<>();
                        arg.put("name",parameter.getName());
                        arg.put("type",parameter.getType().getCanonicalName());
                        arg.put("varargs",parameter.isVarArgs());
                        Comment comment = parameter.getAnnotation(Comment.class);
                        if(comment!=null){
                            arg.put("comment",comment.value());
                        }
                        else{
                            arg.put("comment","");
                        }
                        CallbackFunction cb = parameter.getAnnotation(CallbackFunction.class);
                        if(cb!=null){
                            System.out.println(method.getName()+":"+parameter.getName()+" ENC!");
                            String[] CB_classes = cb.classes();
                            String[] CB_parameters = cb.parameters();
                            String[] CB_comments = cb.comments();
                            Map<String, String>[] array = new Map[CB_classes.length];
                            for(int j=0;j<CB_classes.length;j++){
                                Map<String, String> entry = new HashMap<>();
                                entry.put("type",CB_classes[j]);
                                entry.put("name", CB_parameters[j]);
                                entry.put("comment", CB_comments[j]);
                                array[j] = entry;
                            }
                            arg.put("callbackInfo",array);
                        }else{
                            arg.put("callbackInfo","");
                        }
                        tmp[i] = arg;
                    }
                    methodDef.parameters = tmp;
                    ArrayList<Map<String, Object>> newArr = new ArrayList<>();
                    newArr.add(methodDef.toMap());
                    info.put(method.getName(), newArr);
                }
            }
            for(Field field:clazz.getDeclaredFields()){
                Map<String, String> entry = new HashMap<>();
                entry.put("modifier",Modifier.toString(field.getModifiers()));
                entry.put("type",field.getType().getCanonicalName());
                Comment comment = field.getAnnotation(Comment.class);
                if(comment!=null){
                    entry.put("comment",comment.value());
                }else {
                    entry.put("comment","");
                }
                fields.put(field.getName(),entry);
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
        Map<String,Object> out = new HashMap<>();
        out.put("type",getBaseObjName(className).equals(className)?"class":"baseobj");
        out.put("version",Loader.plugin.getDescription().getVersion());
        out.put("extends",superClassName);
        out.put("abstract",isAbstract);
        Map<String,Object> defs = new HashMap<>();
        defs.put("name",getBaseObjName(className));
        defs.put("constructors",constructors);
        defs.put("methods",info);
        defs.put("fields",fields);
        out.put("definitions",defs);
        File f = new File("./definitions/"+getBaseObjName(className)+".json");
        File folders = new File("./definitions");
        if(!folders.exists()){
            folders.exists();
        }
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeWithString(f,gson.toJson(out));
        return (JsonObject) gson.toJsonTree(out);
    }
    private String getBaseObjName(String className){
        if(!(className.startsWith("com.blocklynukkit.loader.script") && className.contains("Manager"))){
            return className;
        }else {
            className = StringUtils.replace(className,"com.blocklynukkit.loader.script.","",1);
        }
        switch (className){
            case "AlgorithmManager":return "algorithm";
            case "BlockItemManager":return "blockitem";
            case "DatabaseManager":return "database";
            case "EntityManager":return "entity";
            case "FunctionManager":return "manager";
            case "GameManager":return "gameapi";
            case "InventoryManager":return "inventory";
            case "LevelManager":return "world";
            case "NotemusicManager":return "notemusic";
            case "ParticleManager":return "particle";
            case "WindowManager":return "window";
            default:return className;
        }
    }
    private String paramConvert(String param){
        switch (param){
            case "var": return "_var";
            case "function": return "_function";
            default: return param;
        }
    }
}
