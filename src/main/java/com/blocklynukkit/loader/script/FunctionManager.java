package com.blocklynukkit.loader.script;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.network.protocol.VideoStreamConnectPacket;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.*;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.MetricsLite;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.other.BstatsBN;
import com.blocklynukkit.loader.other.Clothes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.ir.Block;
import me.onebone.economyapi.EconomyAPI;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class FunctionManager {

    private Loader plugin;

    public FunctionManager(Loader plugin){
        this.plugin = plugin;
    }

    //here 8/8
    public List<String> getEventFunctions(Event event){
        List<String> list = new ArrayList<>();
        for(Method method:event.getClass().getMethods()){
            if(fiterMethod(method.getName())){
                list.add(method.getName());
            }
        }
        return list;
    }
    //here 8/5
    public double getCPULoad(){
        OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osMxBean.getSystemLoadAverage();
    }
    public int getCPUCores(){
        OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osMxBean.getAvailableProcessors();
    }
    public double getMemoryTotalSizeMB(){
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return mem.getTotalPhysicalMemorySize()/(1024d*1024d);
    }
    public double getMemoryUsedSizeMB(){
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return (mem.getTotalPhysicalMemorySize()-mem.getFreePhysicalMemorySize())/(1024d*1024d);
    }
    public void forceDisconnect(Player player){
        VideoStreamConnectPacket packet = new  VideoStreamConnectPacket();
        packet.address = "8.8.8.8";
        packet.action = VideoStreamConnectPacket.ACTION_OPEN;
        packet.screenshotFrequency =1.0f;
        player.dataPacket(packet);
    }

    //here 8/4
    public Object getVariableFrom(String scriptName,String varName){
        ScriptEngine engine = Loader.engineMap.get(scriptName);
        return engine.get(varName);
    }
    public void putVariableTo(String scriptName,String varName,Object var){
        ScriptEngine engine = Loader.engineMap.get(scriptName);
        engine.put(varName,var);
    }
    //here 6/28
    @Deprecated
    public void loadJar(String path){
        try{
            URL urls[] = new URL[ ]{ new File(path).toURL() };
            URLClassLoader urlLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class sysclass = URLClassLoader.class;
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(urlLoader, urls);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void bStats(String pluginName,String pluginVer,String authorName,int pluginid){
        new BstatsBN(new Plugin() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onEnable() {

            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void onDisable() {

            }

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public File getDataFolder() {
                return null;
            }

            @Override
            public PluginDescription getDescription() {
                return new PluginDescription("name: "+pluginName+"\n" +
                        "main: com.blocklynukkit.loader.Loader\n" +
                        "version: \""+pluginVer+"\"\n" +
                        "author: "+authorName+"\n" +
                        "api: [\"1.0.8\"]\n" +
                        "description: a blocklynukkit based plugin\n" +
                        "load: POSTWORLD\n");
            }

            @Override
            public InputStream getResource(String s) {
                return null;
            }

            @Override
            public boolean saveResource(String s) {
                return false;
            }

            @Override
            public boolean saveResource(String s, boolean b) {
                return false;
            }

            @Override
            public boolean saveResource(String s, String s1, boolean b) {
                return false;
            }

            @Override
            public Config getConfig() {
                return null;
            }

            @Override
            public void saveConfig() {

            }

            @Override
            public void saveDefaultConfig() {

            }

            @Override
            public void reloadConfig() {

            }

            @Override
            public Server getServer() {
                return Server.getInstance();
            }

            @Override
            public String getName() {
                return pluginName;
            }

            @Override
            public PluginLogger getLogger() {
                return Loader.plugin.getLogger();
            }

            @Override
            public PluginLoader getPluginLoader() {
                return null;
            }

            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                return false;
            }
        },pluginid);
    }
    //here 5/8
    //用于测试
    public void callObject(ScriptObjectMirror scriptObjectMirror){
        scriptObjectMirror.call(Loader.functionManager);
    }

    public Vector3 buildvec3(double x,double y,double z){
        return new Vector3(x,y,z);
    }
    //是不是神奇的Windows？
    public boolean isWindows(){
        return Utils.isWindows();
    }
    //json与yaml互转
    public String JSONtoYAML(String json){
        json = formatJSON(json);
        writeFile("./transferTMP.json",json);
        Config jsonConfig = new Config(new File("./transferTMP.json"),Config.JSON);
        ConfigSection section = jsonConfig.getRootSection();
        Config yamlConfig = new Config(new File("./transferTMP.yml"),Config.YAML);
        yamlConfig.setAll(section);
        yamlConfig.save();
        String out = readFile("./transferTMP.yml");
        new File("./transferTMP.json").delete();
        new File("./transferTMP.yml").delete();
        return out;
    }
    public String YAMLtoJSON(String yaml){
        writeFile("./transferTMP.yml",yaml);
        Config yamlConfig = new Config(new File("./transferTMP.yml"),Config.YAML);
        ConfigSection section = yamlConfig.getRootSection();
        Config jsonConfig = new Config(new File("./transferTMP.json"),Config.JSON);
        jsonConfig.setAll(section);
        jsonConfig.save();
        String out = readFile("./transferTMP.json");
        new File("./transferTMP.json").delete();
        new File("./transferTMP.yml").delete();
        out = formatJSON(out);
        return out;
    }
    public String formatJSON(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
    //文件读写
    public String readFile(String path){
        File file = new File(path);
        if(file.exists()){
            return Utils.readToString(file);
        }else {
            return "FILE NOT FOUND";
        }
    }
    public void writeFile(String path,String text){
        File file = new File(path);
        if(!file.exists()) {
            try {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeWithString(file,text);
    }
    public boolean isFileSame(String path1,String path2){
        return Utils.check(new File(path1),new File(path2));
    }
    //end here
    //跨命名空间调用
    public Object callFunction(String functionname,Object... args){
        return Loader.plugin.call(functionname, args);
    }
    //http
    public String httpRequest(String method,String url,String data){
        method=method.toUpperCase();
        if(method.equals("GET")){
            return Utils.sendGet(url,data);
        }else if(method.equals("POST")){
            return Utils.sendPost(url,data);
        }else {
            return "NO SUCH METHOD";
        }
    }
    //私有回调
    @Deprecated
    public void setPrivateCall(String event,String callname){
        if(Loader.privatecalls.containsKey(event)){
            Loader.privatecalls.get(event).add(callname);
        }else {
            HashSet<String> set = new HashSet<>();
            set.add(callname);
            Loader.privatecalls.put(event,set);
        }
    }

    //云黑检测
    public String checkIsBear(Player player){
        String response = Utils.sendGetBlackBE("http://47.103.201.235/api/check.php?","id="+player.getName());
        return response;
    }
    public String checkIsBearName(String player){
        String response = Utils.sendGet("http://47.103.201.235/api/check.php","id="+player);
        return response;
    }

    //获取玩家地理位置
    public String getPlayerArea(Player player){
        String response = Utils.sendGet("http://whois.pconline.com.cn/ip.jsp","ip="+player.getAddress().substring(0, player.getAddress().indexOf(":")));
        return response;
    }

    //html占位符
    public void setHTMLPlaceholder(String key,String value){
        Loader.htmlholdermap.put(key, value);
    }

    //踢了玩家
    public void kickPlayer(Player player,String reason){
        player.kick(PlayerKickEvent.Reason.UNKNOWN,reason,false);
    }

    //获取玩家是否op
    public boolean PlayerIsOP(Player player){
        return player.isOp();
    }

    //获取玩家游戏模式
    public int getPlayerGameMode(Player player){
        return player.getGamemode();
    }
    //简易存储API
    public void putEasy(String string,Object object){
        Loader.easytmpmap.put(string, object);
    }
    public String getEasyString(String string){
        return (String)Loader.easytmpmap.get(string);
    }
    public double getEasyNumber(String string){
        return (Double)Loader.easytmpmap.get(string);
    }
    public boolean getEasyBoolean(String string){
        return (Boolean)Loader.easytmpmap.get(string);
    }
    public Position getEasyPosition(String string){
        return (Position)Loader.easytmpmap.get(string);
    }
    public Player getEasyPlayer(String string){
        return (Player)Loader.easytmpmap.get(string);
    }
    public Item getEasyItem(String string){
        return (Item)Loader.easytmpmap.get(string);
    }
    public Block getEasyBlock(String string){
        return (Block)Loader.easytmpmap.get(string);
    }
    //configAPI
    public List getAllKeyInConfig(Config config){
        return new ArrayList<>(config.getKeys());
    }
    //金钱API
    public double getMoney(Player player){
        return EconomyAPI.getInstance().myMoney(player);
    }
    public void reduceMoney(Player player,double money){
        EconomyAPI.getInstance().reduceMoney(player, money);
    }
    public void addMoney(Player player,double money){
        EconomyAPI.getInstance().addMoney(player, money);
    }
    public void setMoney(Player player,double money){
        EconomyAPI.getInstance().setMoney(player, money);
    }

    //4D皮肤API
    public void buildskin(Player player,String skinname){
        if(Loader.playerclothesmap.get(player.getName()+skinname)==null){
            Skin tmp = new Clothes(skinname, player.getName()).build();
            PlayerSkinPacket packet = new PlayerSkinPacket();
            packet.oldSkinName=player.getSkin().getGeometryData();
            packet.newSkinName=tmp.getGeometryData();
            packet.skin=tmp;
            packet.uuid=player.getUniqueId();
            player.setSkin(tmp);
            Loader.playerclothesmap.put(player.getName()+skinname,tmp);
            Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(),packet);
        }else {
            Skin tmp = Loader.playerclothesmap.get(player.getName()+skinname);
            PlayerSkinPacket packet = new PlayerSkinPacket();
            packet.oldSkinName=player.getSkin().getGeometryData();
            packet.newSkinName=tmp.getGeometryData();
            packet.skin=tmp;
            packet.uuid=player.getUniqueId();
            player.setSkin(Loader.playerclothesmap.get(player.getName()+skinname));
            Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(),packet);
        }
    }

    public void buildskinfor(Player player,String skinname,Player to){
        if(Loader.playerclothesmap.get(player.getName()+skinname)==null){
            Skin tmp = new Clothes(skinname, player.getName()).build();
            PlayerSkinPacket packet = new PlayerSkinPacket();
            packet.oldSkinName=player.getSkin().getGeometryData();
            packet.newSkinName=tmp.getGeometryData();
            packet.skin=tmp;
            packet.uuid=player.getUniqueId();
            player.setSkin(tmp);
            Loader.playerclothesmap.put(player.getName()+skinname,tmp);
            to.dataPacket(packet);
        }else {
            Skin tmp = Loader.playerclothesmap.get(player.getName()+skinname);
            PlayerSkinPacket packet = new PlayerSkinPacket();
            packet.oldSkinName=player.getSkin().getGeometryData();
            packet.newSkinName=tmp.getGeometryData();
            packet.skin=tmp;
            packet.uuid=player.getUniqueId();
            player.setSkin(Loader.playerclothesmap.get(player.getName()+skinname));
            to.dataPacket(packet);
        }
    }

    public File getFile(String folder, String archive){
        File file = new File(plugin.getDataFolder() + "/" + folder + "/");
        file.mkdir();
        return new File(plugin.getDataFolder() + "/" + folder + "/" + archive);
    }

    public Config createConfig(File file, int type){
        return new Config(file, type);
    }
    //here 6/11
    public void createPermission(String per,String description,String defaultper){
        if(defaultper.equals("OP")){
            defaultper=Permission.DEFAULT_OP;
        }else if(defaultper.equals("ALL")||defaultper.equals("EVERY")||defaultper.equals("all")||defaultper.equals("every")){
            defaultper=Permission.DEFAULT_TRUE;
        }else if(defaultper.equals("NONE")||defaultper.equals("NO")||defaultper.equals("none")||defaultper.equals("no")){
            defaultper=Permission.DEFAULT_FALSE;
        }
        plugin.getServer().getPluginManager().addPermission(new Permission(per,description,defaultper));
    }

    public void removePermission(String per){
        Server.getInstance().getPluginManager().removePermission(per);
    }

    public boolean checkPlayerPermission(String per,Player player){
        return player.hasPermission(per);
    }

    public void createCommand(String name, String description, String functionName){
        plugin.getServer().getCommandMap().register(functionName, new EntryCommand(name, description, functionName));
    }
    public void createCommand(String name, String description, String functionName, String per){
        plugin.getServer().getCommandMap().register(functionName, new EntryCommand(name, description, functionName, per));
    }
    //here 5/8
    public void newCommand(String name, String description, ScriptObjectMirror scriptObjectMirror){
        plugin.getServer().getCommandMap().register(name,new LambdaCommand(name,description,scriptObjectMirror));
    }
    public void newCommand(String name, String description, ScriptObjectMirror scriptObjectMirror,String per){
        plugin.getServer().getCommandMap().register(name,new LambdaCommand(name,description,scriptObjectMirror,per));
    }
    //end here

    public TaskHandler createTask(String functionName, int delay){
        return plugin.getServer().getScheduler().scheduleDelayedTask(new ModTask(functionName), delay);
    }
    //here 5/9
    public int setTimeout(ScriptObjectMirror scriptObjectMirror,int delay,Object... args){
        return plugin.getServer().getScheduler().scheduleDelayedTask(new LambdaTask(scriptObjectMirror,args),delay).getTaskId();
    }
    public void clearTimeout(int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }
    //end here
    public TaskHandler createLoopTask(String functionName, int delay){
        return plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(new ModTask(functionName), 20, delay);
    }
    //here 5/9
    public int setInterval(ScriptObjectMirror scriptObjectMirror,int delay,Object... args){
        return plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(new LambdaTask(scriptObjectMirror,args),delay,delay).getTaskId();
    }
    public void clearInterval(int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }
    //end here
    public int getTaskId(TaskHandler handler){
        return handler.getTaskId();
    }

    public void cancelTask(int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }

    public Plugin getPlugin(String name){
        return plugin.getServer().getPluginManager().getPlugin(name);
    }

    public String MD5Encryption(String str){
        return Utils.StringEncryptor(str,"MD5");
    }

    public String SHA1Encryption(String str){
        return Utils.StringEncryptor(str,"SHA1");
    }

    public String time(int seconds){
        int ss = seconds % 60;
        seconds /= 60;
        int min = seconds % 60;
        seconds /= 60;
        int hours = seconds % 24;
        return strzero(hours) + ":" + strzero(min) + ":" + strzero(ss);
    }

    public String format(String msg, Object... args){
        return String.format(msg, args);
    }

    private String strzero(int time){
        if(time < 10)
            return "0" + time;
        return String.valueOf(time);
    }

    public Loader plugin(){
        return plugin;
    }

    public class EntryCommand extends Command{

        private String functionName;

        public EntryCommand(String name, String description, String functionName) {
            super(name, description);
            this.functionName = functionName;
        }

        public EntryCommand(String name, String description, String functionName, String per) {
            super(name, description);
            this.functionName = functionName;
            this.setPermission(per);
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            plugin.callCommand(sender, args, functionName);
            return false;
        }
    }

    public class LambdaCommand extends Command{

        private ScriptObjectMirror callback;

        public LambdaCommand(String name, String description, ScriptObjectMirror objectMirror) {
            super(name, description);
            this.callback=objectMirror;
        }

        public LambdaCommand(String name, String description, ScriptObjectMirror objectMirror, String per) {
            super(name, description);
            this.callback=objectMirror;
            this.setPermission(per);
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            callback.call(Loader.functionManager,sender,args);
            return false;
        }


    }

    public class ModTask extends Task{

        private String functionName;

        public ModTask(String functionName){
            this.functionName = functionName;
        }

        @Override
        public void onRun(int i) {
            plugin.call(functionName, i);
        }
    }

    public class LambdaTask extends Task{

        private ScriptObjectMirror callback;
        private Object Args[];

        public LambdaTask(ScriptObjectMirror scriptObjectMirror,Object[] args){
            this.callback = scriptObjectMirror;
            this.Args = args;
        }

        @Override
        public void onRun(int i) {
            callback.call(Loader.functionManager,Args);
        }
    }

    private boolean fiterMethod(String method){
        if(method.endsWith("equals")||method.endsWith("clone")||method.endsWith("wait")||method.endsWith("getClass")||
        method.endsWith("finalize")||method.endsWith("notify")||method.endsWith("notifyAll")||method.endsWith("toString")){
            return false;
        }else {
            if(!(method.startsWith("get")||method.startsWith("is")||method.startsWith("set"))){
                return false;
            }else
            return true;
        }
    }
}