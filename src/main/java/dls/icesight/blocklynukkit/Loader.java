package dls.icesight.blocklynukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import dls.icesight.blocklynukkit.other.BNCrafting;
import dls.icesight.blocklynukkit.other.SocketServer;
import dls.icesight.blocklynukkit.other.card.CardMaker;
import dls.icesight.blocklynukkit.other.cmd.BuildJarCommand;
import dls.icesight.blocklynukkit.other.generator.SkyLand;
import dls.icesight.blocklynukkit.script.*;
import dls.icesight.blocklynukkit.script.event.EntityDamageByPlayerEvent;

import javax.script.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Loader extends PluginBase implements Listener {

    public static ScriptEngine engine;

    public static Loader plugin;

    public static Map<String,HashSet<String>> privatecalls = new HashMap<>();
    public static Set<String> bnpluginset = new HashSet<>();

    public static String positionstmp = "";

    public static Map<String, Skin> playerskinmap = new HashMap<>();
    public static Map<String, Skin> playerclothesmap = new HashMap<>();
    public static Map<String, BufferedImage> skinimagemap = new HashMap<>();
    public static Map<String, String> playergeonamemap = new HashMap<>();
    public static Map<String, String> playergeojsonmap = new HashMap<>();
    public static Map<Integer, String> functioncallback = new HashMap<>();
    public static Map<String, Object> easytmpmap = new HashMap<>();
    public static Map<String, String> htmlholdermap = new HashMap<>();
    public static BNCrafting bnCrafting = new BNCrafting();
    public static NoteBlockPlayerMain noteBlockPlayerMain = new NoteBlockPlayerMain();
    public static FunctionManager functionManager;
    public static WindowManager windowManager;
    public static AlgorithmManager algorithmManager;
    public static BlockItemManager blockItemManager;
    public static EntityManager entityManager;
    public static InventoryManager inventoryManager;
    public static LevelManager levelManager;
    public static DatabaseManager databaseManager;
    public static CardMaker cardMaker;
    public static NotemusicManager notemusicManager;


    @Override
    public void onEnable() {
        plugin=this;
        Map<String, Plugin> plugins = this.getServer().getPluginManager().getPlugins();
        if (!plugins.containsKey("EconomyAPI")){
            try {
                Utils.downloadPlugin("https://repo.nukkitx.com/main/me/onebone/economyapi/2.0.0-SNAPSHOT/economyapi-2.0.0-20190517.112309-17.jar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!plugins.containsKey("KotlinLib")){
            try {
                Utils.downloadPlugin("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/jar/KotlinLib.jar"); //KotlinLib url
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!plugins.containsKey("PlaceholderAPI")){
            try {
                if (Server.getInstance().getLanguage().getName().contains("中文"))
                Loader.getlogger().warning(TextFormat.RED+"您没有安装PlaceholderAPI,虽然不是必须安装，但PlaceHolderAPI是速建官网和计分板组件的必须前置，建议您安装，下载地址：https://repo.nukkitx.com/main/com/creeperface/nukkit/placeholderapi/PlaceholderAPI/1.4-SNAPSHOT/PlaceholderAPI-1.4-20200314.133954-18.jar");
                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                Loader.getlogger().warning(TextFormat.RED+"You haven't installed PlaceholderAPI,although it's not necessary,but PlaceHolderAPI is needed by the moudle inner_http_page_server and moudle scoreboard,we suggest you to install,download link: https://repo.nukkitx.com/main/com/creeperface/nukkit/placeholderapi/PlaceholderAPI/1.4-SNAPSHOT/PlaceholderAPI-1.4-20200314.133954-18.jar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!plugins.containsKey("FakeInventories")){
            try {
                Utils.downloadPlugin("https://ci.nukkitx.com/job/NukkitX/job/FakeInventories/job/master/lastSuccessfulBuild/artifact/target/fakeinventories-1.0.3-SNAPSHOT.jar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!plugins.containsKey("ScoreboardPlugin")){
            try {
                Utils.downloadPlugin("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/jar/ScoreboardAPI-1.3-SNAPSHOT.jar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        functionManager=new FunctionManager(plugin);windowManager=new WindowManager();blockItemManager=new BlockItemManager();
        algorithmManager=new AlgorithmManager();inventoryManager=new InventoryManager();levelManager=new LevelManager();entityManager=new EntityManager();
        databaseManager=new DatabaseManager();cardMaker=new CardMaker();notemusicManager=new NotemusicManager();
        noteBlockPlayerMain.onEnable();

        MetricsLite metricsLite=new MetricsLite(this,6769);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Utils.checkupdate();
            }
        },0,3600*4*1000);
        Config config = new Config(this.getDataFolder()+"/update.yml",Config.YAML);
        if(!config.exists("mods")){
            config.set("mods", Arrays.asList("first.js"));
            config.save();
        }
        List<String> list = (List<String>) config.get("mods");
        for(String a:list){
            Utils.download("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/"+a,new File(this.getDataFolder()+"/"+a));
        }
        this.getServer().getPluginManager().registerEvents(bnCrafting,this);
        final ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByMimeType("text/javascript");
        if (engine == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().error("JavaScript引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().error("JavaScript interpreter crashed!");
            return;
        }
        if (!(engine instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().error("JavaScript引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().error("JavaScript interpreter's version is too low!");
            engine = null;
            return;
        }

        if (Server.getInstance().getLanguage().getName().contains("中文"))
        getLogger().info(TextFormat.WHITE + "已经载入Javascript引擎: " + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());
        else
        getLogger().info(TextFormat.WHITE + "successfully loaded Javascript interpreter:" + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());
        engine.put("server", getServer());
        engine.put("plugin", this);
        engine.put("manager", Loader.functionManager);
        engine.put("logger", getLogger());
        engine.put("window", Loader.windowManager);
        engine.put("blockitem",Loader.blockItemManager);
        engine.put("algorithm",Loader.algorithmManager);
        engine.put("inventory",Loader.inventoryManager);
        engine.put("world",Loader.levelManager);
        engine.put("entity",Loader.entityManager);
        engine.put("database",Loader.databaseManager);
        engine.put("notemusic",Loader.notemusicManager);
        getDataFolder().mkdir();
        new File(getDataFolder()+"/skin").mkdir();
        new File(getDataFolder()+"/notemusic").mkdir();


        for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().endsWith(".js")){
                try (final Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                    engine.eval(reader);
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                    getLogger().warning("加载BN插件: " + file.getName());
                    else
                    getLogger().warning("loading BN plugin: " + file.getName());
                    bnpluginset.add(file.getName());
                } catch (final Exception e) {
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                    getLogger().error("无法加载： " + file.getName(), e);
                    else
                    getLogger().error("cannot load:" + file.getName(), e);
                }
            }
        }



        this.getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                engine.put("players", getServer().getOnlinePlayers().values());
            }
        }, 20, 20, true);

        this.getServer().getPluginManager().registerEvents(this, this);

        new EventLoader(this);
        plugin.getServer().getCommandMap().register("hotreloadjs",new ReloadJSCommand());
        plugin.getServer().getCommandMap().register("buildtojar",new BuildJarCommand());
        plugin.getServer().getCommandMap().register("bnplugins",new BNPluginsListCommand());
        plugin.getServer().getCommandMap().register("gentestworld",new GenTestWorldCommand());

        Config portconfig = new Config(this.getDataFolder()+"/port.yml",Config.YAML);
        int portto=8182;
        if(portconfig.exists("port")){
            portto=(int)portconfig.get("port");
        }else {
            portconfig.set("port",8182);
        }
        portconfig.save();
        Utils.makeHttpServer(portto);
    }

    @Override
    public void onDisable(){
        entityManager.recycleAllFloatingText();
    }

    public static synchronized void callEventHandler(final Event e, final String functionName) {
        try {
            if (engine.get(functionName) != null) {
                ((Invocable) engine).invokeFunction(functionName, e);
            }
            if(privatecalls.containsKey(functionName)){
                for(String a:privatecalls.get(functionName)){
                    if(engine.get(a) != null){
                        ((Invocable) engine).invokeFunction(a, e);
                    }
                }
            }
        } catch (final Exception se) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
            plugin.getLogger().error("在回调 " + functionName+" 时出错", se);
            else
            plugin.getLogger().error("errors when calling " + functionName, se);
            se.printStackTrace();
        }
    }

    public static synchronized void callEventHandler(final Event e, final String functionName,String type) {

        try {
            if(type.equals("StoneSpawnEvent")){
                StoneSpawnEvent event = ((StoneSpawnEvent)e);
                if (engine.get(functionName) != null){
                    ((Invocable) engine).invokeFunction(functionName, event);
                }
                if(privatecalls.containsKey(functionName)){
                    for(String a:privatecalls.get(functionName)){
                        if(engine.get(a) != null){
                            ((Invocable) engine).invokeFunction(a, e);
                        }
                    }
                }
            }else if(type.equals("EntityDamageByPlayerEvent")){
                EntityDamageByPlayerEvent event = ((EntityDamageByPlayerEvent)e);
                if (engine.get(functionName) != null){
                    ((Invocable) engine).invokeFunction(functionName, event);
                }
                if(privatecalls.containsKey(functionName)){
                    for(String a:privatecalls.get(functionName)){
                        if(engine.get(a) != null){
                            ((Invocable) engine).invokeFunction(a, e);
                        }
                    }
                }
            }
        } catch (final Exception se) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
            plugin.getLogger().error("在回调 " + functionName+" 时出错", se);
            else
            plugin.getLogger().error("errors when calling " + functionName, se);
            se.printStackTrace();
        }
    }

    public synchronized void callCommand(CommandSender sender, String[] args, String functionName){
        if(engine.get(functionName) == null){
            return;
        }
        try {
            ((Invocable) engine).invokeFunction(functionName, sender, args);
        } catch (final Exception se) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().error("在回调 " + functionName+" 时出错", se);
            else
            plugin.getLogger().error("errors when calling " + functionName, se);
            se.printStackTrace();
        }
    }

    public synchronized void call(String functionName, Object... args){
        if(engine.get(functionName) == null){
            return;
        }
        try {
            ((Invocable) engine).invokeFunction(functionName, args);
        } catch (final Exception se) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().error("在回调 " + functionName+"时出错", se);
            else
            plugin.getLogger().error("errors when calling " + functionName, se);
            se.printStackTrace();
        }
    }

    public synchronized String callbackString(String functionName, Object... args){
        if(engine.get(functionName) == null){
            return "NO FUNCTION";
        }
        try {
            return String.valueOf(((Invocable) engine).invokeFunction(functionName, args));
        } catch (final Exception se) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getLogger().error("在回调 " + functionName+"时出错", se);
            else
                plugin.getLogger().error("errors when calling " + functionName, se);
            se.printStackTrace();
            return "ERROR";
        }
    }

    private synchronized Object eval(final CommandSender sender, final String expression) throws ScriptException {
        if (sender != null && sender.isPlayer()) {
            final Player player = (Player) sender;
            engine.put("me", player);
            engine.put("level", player.getPosition().level);
            engine.put("pos", player.getPosition());
        } else {
            engine.put("me", null);
            engine.put("level", getServer().getDefaultLevel());
            engine.put("pos", null);
        }
        return engine.eval(expression);
    }

    public static PluginLogger getlogger(){
        return plugin.getLogger();
    }

    public class ReloadJSCommand extends Command {

        private String functionName;

        public ReloadJSCommand() {
            super("hotreloadjs","热重载js(仅控制台使用)");
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            if(sender.isPlayer()){
                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                sender.sendMessage("Only console can use this command!");
                else
                sender.sendMessage("只有控制台才能执行此命令");
                return false;
            }
            entityManager.recycleAllFloatingText();
            Loader.bnCrafting.craftEntryMap=new HashMap<>();
            Config config = new Config(Loader.plugin.getDataFolder()+"/update.yml",Config.YAML);
            if(!config.exists("mods")){
                config.set("mods", Arrays.asList("first.js"));
                config.save();
            }
            List<String> list = (List<String>) config.get("mods");
            for(String a:list){
                Utils.download("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/"+a,new File(Loader.plugin.getDataFolder()+"/"+a));
            }

            final ScriptEngineManager manager = new ScriptEngineManager();
            Loader.plugin.engine=null;
            Loader.plugin.engine = manager.getEngineByMimeType("text/javascript");

            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getLogger().info(TextFormat.WHITE + "已经载入Javascript引擎: " + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());
            else
                getLogger().info(TextFormat.WHITE + "successfully loaded Javascript interpreter:" + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());

            Loader.plugin.engine.put("server", getServer());
            Loader.plugin.engine.put("plugin", this);
            Loader.plugin.engine.put("manager", Loader.functionManager);
            Loader.plugin.engine.put("logger", getLogger());
            Loader.plugin.engine.put("window", Loader.windowManager);
            Loader.plugin.engine.put("blockitem",Loader.blockItemManager);
            Loader.plugin.engine.put("algorithm",Loader.algorithmManager);
            Loader.plugin.engine.put("inventory",Loader.inventoryManager);
            Loader.plugin.engine.put("world",Loader.levelManager);
            Loader.plugin.engine.put("entity",Loader.entityManager);
            Loader.plugin.engine.put("database",Loader.databaseManager);
            Loader.plugin.engine.put("notemusic",Loader.notemusicManager);

            getDataFolder().mkdir();
            new File(getDataFolder()+"/skin").mkdir();


            for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
                if(file.isDirectory()) continue;
                if(file.getName().contains(".js")){
                    try (final Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                        Loader.plugin.engine.eval(reader);
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getLogger().warning("加载BN插件: " + file.getName());
                        else
                            getLogger().warning("loading BN plugin: " + file.getName());
                    } catch (final Exception e) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getLogger().error("无法加载： " + file.getName(), e);
                        else
                            getLogger().error("cannot load:" + file.getName(), e);
                    }
                }
            }
            Loader.plugin.getServer().getScheduler().cancelAllTasks();

            return false;
        }
    }

    public class BNPluginsListCommand extends Command{
        public BNPluginsListCommand() {
            super("bnplugins","查看所有安装的blocklynukkit插件");
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            String out = TextFormat.GREEN+"BlocklyNukkit插件("+bnpluginset.size()+"): ";
            for(String a:bnpluginset){
                out+=a+", ";
            }
            sender.sendMessage(out);
            return false;
        }
    }

    public class GenTestWorldCommand extends Command{
        public GenTestWorldCommand() {
            super("gentestworld","生成测试世界");
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            if(args.length<2){
                return false;
            }
            levelManager.genLevel(args[0],999, args[1]);
            if(sender.isPlayer()){
                ((Player)sender).teleport(Server.getInstance().getLevelByName(args[0]).getSafeSpawn());
            }
            return false;
        }
    }
}
