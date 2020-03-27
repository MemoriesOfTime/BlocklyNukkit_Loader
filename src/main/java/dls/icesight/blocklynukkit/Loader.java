package dls.icesight.blocklynukkit;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import dls.icesight.blocklynukkit.other.BNCrafting;
import dls.icesight.blocklynukkit.script.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Loader extends PluginBase implements Listener {

    public ScriptEngine engine;

    public static Loader plugin;

    public static String positionstmp = "";

    public static Map<String, Skin> playerskinmap = new HashMap<>();
    public static Map<String, Skin> playerclothesmap = new HashMap<>();
    public static Map<String, BufferedImage> skinimagemap = new HashMap<>();
    public static Map<String, String> playergeonamemap = new HashMap<>();
    public static Map<String, String> playergeojsonmap = new HashMap<>();
    public static Map<Integer, String> functioncallback = new HashMap<>();
    public static Map<String, Object> easytmpmap = new HashMap<>();
    public static BNCrafting bnCrafting = new BNCrafting();

    @Override
    public void onEnable() {
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
                Utils.downloadPlugin("https://repo.nukkitx.com/main/com/creeperface/nukkit/placeholderapi/PlaceholderAPI/1.4-SNAPSHOT/PlaceholderAPI-1.4-20200314.133954-18.jar");
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
                Utils.downloadPlugin("https://repo.nukkitx.com/snapshot/com/nukkitx/fakeinventories/1.0.3-SNAPSHOT/fakeinventories-1.0.3-20190326.084826-4.jar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin=this;
        MetricsLite metricsLite=new MetricsLite(this,6769);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Utils.checkupdate();
            }
        },0,3600*6*1000);
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
            getLogger().error("JavaScript引擎加载出错！");
            return;
        }
        if (!(engine instanceof Invocable)) {
            getLogger().error("JavaScript引擎版本过低！");
            engine = null;
            return;
        }

        getLogger().info(TextFormat.WHITE + "已经载入Javascript引擎: " + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());

        engine.put("server", getServer());
        engine.put("plugin", this);
        engine.put("manager", new FunctionManager(this));
        engine.put("logger", getLogger());
        engine.put("window", new WindowManager());
        engine.put("blockitem",new BlockItemManager());
        engine.put("algorithm",new AlgorithmManager());
        engine.put("entity",new EntityManager());
        engine.put("inventory",new InventoryManager());

        getDataFolder().mkdir();
        new File(getDataFolder()+"/skin").mkdir();


        for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().contains(".js")){
                try (final Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                    engine.eval(reader);
                    getLogger().warning("加载BN插件: " + file.getName());
                } catch (final Exception e) {
                    getLogger().error("无法加载： " + file.getName(), e);
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

        new EventLoader(this);//AlgorithmManager.test();
        plugin.getServer().getCommandMap().register("hotreloadjs",new ReloadJSCommand());
    }

    public synchronized void callEventHandler(final Event e, final String functionName) {
        if (engine.get(functionName) == null) {
            return;
        }
        try {
            ((Invocable) engine).invokeFunction(functionName, e);
        } catch (final Exception se) {
            getLogger().error("在回调 " + functionName+" 时出错", se);
            se.printStackTrace();
        }
    }

    public synchronized void callEventHandler(final Event e, final String functionName,String type) {
        if (engine.get(functionName) == null) {
            return;
        }
        try {
            if(type.equals("StoneSpawnEvent")){
                StoneSpawnEvent event = ((StoneSpawnEvent)e);
                ((Invocable) engine).invokeFunction(functionName, event);
            }
        } catch (final Exception se) {
            getLogger().error("在回调 " + functionName+" 时出错", se);
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
            getLogger().error("在回调 " + functionName+" 时出错", se);
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
            getLogger().error("在回调 " + functionName+"时出错", se);
            se.printStackTrace();
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
//    private static void downloadZip(String downloadUrl, File file) {
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            URL url = new URL(downloadUrl);
//            URLConnection connection = url.openConnection();
//            InputStream inputStream = connection.getInputStream();
//            int length = 0;
//            byte[] bytes = new byte[1024];
//            while ((length = inputStream.read(bytes)) != -1) {
//                fileOutputStream.write(bytes, 0, length);
//            }
//            fileOutputStream.close();
//            inputStream.close();
//        } catch (IOException e) {
//            log.error("download error ! url :{}, exception:{}", downloadUrl, e);
//        }
//        System.out.println("end");
//    }

    public class ReloadJSCommand extends Command {

        private String functionName;

        public ReloadJSCommand() {
            super("hotreloadjs","热重载js(仅控制台使用)");
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            if(sender.isPlayer()){
                sender.sendMessage("只有控制台才能执行此命令");
                return false;
            }
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

            getLogger().info(TextFormat.WHITE + "已经载入Javascript引擎: " + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());

            Loader.plugin.engine.put("server", getServer());
            Loader.plugin.engine.put("plugin", this);
            Loader.plugin.engine.put("manager", new FunctionManager(Loader.plugin));
            Loader.plugin.engine.put("logger", getLogger());
            Loader.plugin.engine.put("window", new WindowManager());
            Loader.plugin.engine.put("blockitem",new BlockItemManager());
            Loader.plugin.engine.put("algorithm",new AlgorithmManager());
            Loader.plugin.engine.put("inventory",new InventoryManager());


            getDataFolder().mkdir();
            new File(getDataFolder()+"/skin").mkdir();


            for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
                if(file.isDirectory()) continue;
                if(file.getName().contains(".js")){
                    try (final Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                        Loader.plugin.engine.eval(reader);
                        getLogger().warning("加载BN插件: " + file.getName());
                    } catch (final Exception e) {
                        getLogger().error("无法加载： " + file.getName(), e);
                    }
                }
            }
            Loader.plugin.getServer().getScheduler().cancelAllTasks();

            return false;
        }
    }
}
