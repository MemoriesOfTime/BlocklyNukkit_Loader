package dls.icesight.blocklynukkit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import dls.icesight.blocklynukkit.script.FunctionManager;
import dls.icesight.blocklynukkit.script.WindowManager;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Loader extends PluginBase implements Listener {

    private ScriptEngine engine;

    public static Loader plugin;

    public static Map<String, Skin> playerskinmap = new HashMap<>();
    public static Map<String, Skin> playerclothesmap = new HashMap<>();
    public static Map<String, BufferedImage> skinimagemap = new HashMap<>();
    public static Map<String, String> playergeonamemap = new HashMap<>();
    public static Map<String, String> playergeojsonmap = new HashMap<>();
    public static Map<Integer, String> functioncallback = new HashMap<>();
    public static Map<String, String> easytmpmap = new HashMap<>();

    @Override
    public void onEnable() {
        plugin=this;
        MetricsLite metricsLite=new MetricsLite(this,6769);

        Config config = new Config(this.getDataFolder()+"/update.yml",Config.YAML);
        if(!config.exists("mods")){
            config.set("mods", Arrays.asList("first.js"));
            config.save();
        }
        List<String> list = (List<String>) config.get("mods");
        for(String a:list){
            download("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/"+a,new File(this.getDataFolder()+"/"+a));
        }

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

        new EventLoader(this);
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
    private static void download(String downloadUrl, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }
            fileOutputStream.close();
            inputStream.close();
            if(isWindows()){
                getlogger().info(TextFormat.YELLOW+"正在为windows转码... "+TextFormat.GREEN+"作者对微软的嘲讽：(sb Windows,都老老实实用utf编码会死吗？)");
            }
        } catch (IOException e) {
            getlogger().error("download error ! url :{"+downloadUrl+"}, exception:{"+e+"}");
        }
        getlogger().info(TextFormat.GREEN+"成功同步："+file.getName());
    }
    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }
    public static String readToString(File file) {
        String encoding = "UTF-8";

        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("操作系统不支持 " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
