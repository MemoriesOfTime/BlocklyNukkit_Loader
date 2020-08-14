package com.blocklynukkit.loader;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.other.BNLogger;
import com.blocklynukkit.loader.other.Entities.BNNPC;
import com.blocklynukkit.loader.other.Entities.FloatingItemManager;
import com.blocklynukkit.loader.other.Entities.FloatingText;
import com.blocklynukkit.loader.other.tips.TipsUtil;
import com.blocklynukkit.loader.script.*;
import com.blocklynukkit.loader.script.event.*;
import com.blocklynukkit.loader.scriptloader.JavaScriptLoader;
import com.blocklynukkit.loader.scriptloader.PythonLoader;
import com.sun.net.httpserver.HttpServer;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.blocklynukkit.loader.other.BNCrafting;
import com.blocklynukkit.loader.other.card.CardMaker;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.python.antlr.ast.Str;

import javax.script.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Loader extends PluginBase implements Listener {

    public static Loader plugin;

    public static Map<String, ScriptEngine> engineMap = new HashMap<>();
    public static Map<String,HashSet<String>> privatecalls = new HashMap<>();
    public static Set<String> bnpluginset = new HashSet<>();

    public static long previousTime = System.currentTimeMillis();
    public static ScriptException previousException = null;

    public static String positionstmp = "";
    public static int checkupdatetime = 0;

    public static Map<Item, Item> furnaceMap = new HashMap<>();
    public static Map<String, Skin> playerclothesmap = new HashMap<>();
    public static Map<String, BufferedImage> skinimagemap = new HashMap<>();
    public static Map<String, String> playergeonamemap = new HashMap<>();
    public static Map<String, String> playergeojsonmap = new HashMap<>();
    public static Map<String, String[]>mcfunctionmap = new HashMap<>();
    public static ConcurrentHashMap<Integer, String> functioncallback = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, ScriptObjectMirror> scriptObjectMirrorCallback = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> serverSettingCallback = new ConcurrentHashMap<>();
    public static Map<String, Object> easytmpmap = new HashMap<>();
    public static Map<String, String> htmlholdermap = new HashMap<>();
    public static BNCrafting bnCrafting = new BNCrafting();
    public static HttpServer httpServer = null;
    public static EventLoader eventLoader;
    public static FloatingItemManager floatingItemManager = new FloatingItemManager();
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
    public static ParticleManager particleManager;
    public static GameManager gameManager;
    public static Map<String, Plugin> plugins;


    @Override
    public void onEnable() {
        plugin=this;
        plugins=this.getServer().getPluginManager().getPlugins();
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
                Loader.getlogger().warning(TextFormat.RED+"您没有安装PlaceholderAPI,虽然不是必须安装，但PlaceHolderAPI前置有些作用，建议您安装，下载地址：https://repo.nukkitx.com/main/com/creeperface/nukkit/placeholderapi/PlaceholderAPI/1.4-SNAPSHOT/PlaceholderAPI-1.4-20200314.133954-18.jar");
                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                Loader.getlogger().warning(TextFormat.RED+"You haven't installed PlaceholderAPI,although it's not necessary,but PlaceHolderAPI is needed by the moudle inner_http_page_server and moudle scoreboard,we suggest you to install,download link: https://repo.nukkitx.com/main/com/creeperface/nukkit/placeholderapi/PlaceholderAPI/1.4-SNAPSHOT/PlaceholderAPI-1.4-20200314.133954-18.jar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!plugins.containsKey("FakeInventories")){
            try {
                Utils.downloadPlugin("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/jar/fakeinventories-1.0.3-SNAPSHOT.jar");
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
        if (!plugins.containsKey("GameAPI")){
            try {
                Utils.downloadPlugin("https://tools.blocklynukkit.com/BNGameAPI.jar");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //创建各种基对象
        //这里没有database因为后面要检查依赖库是否存在再创建
        functionManager=new FunctionManager(plugin);windowManager=new WindowManager();blockItemManager=new BlockItemManager();
        algorithmManager=new AlgorithmManager();inventoryManager=new InventoryManager();levelManager=new LevelManager();entityManager=new EntityManager();
        databaseManager=null;cardMaker=new CardMaker();notemusicManager=new NotemusicManager();particleManager=new ParticleManager();databaseManager=new DatabaseManager();
        noteBlockPlayerMain.onEnable();if(plugins.containsKey("GameAPI"))gameManager=new GameManager();
        //检测nk版本
        if (Server.getInstance().getLanguage().getName().contains("中文"))
            getLogger().warning("请注意：如果出现NoClassDefFoundError，说明您应该换新的NukkitX / PowerNukkit 版本了 ");
        else
            getLogger().warning("Please note: if NoClassDefFoundError appears, you should change to a new version of NukkitX or PowerNukkit ");
        //修改路径类加载器，使得脚本可以调用其他插件
        ClassLoader cl = plugin.getClass().getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        //为nashorn js引擎开启es6支持
        System.setProperty("nashorn.args", "--language=es6");
        //更新检测
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Utils.checkupdate();
            }
        },0,3600*2*1000);
        //加载统计器类
        MetricsLite metricsLite=new MetricsLite(this,6769);
        //世界生成器初始化
        levelManager.doreloadSkyLandGeneratorSettings();
        levelManager.doreloadOceanGeneratorSettings();
        //bn高级合成台模块监听
        this.getServer().getPluginManager().registerEvents(bnCrafting,this);
        //bn浮空物品模块监听
        this.getServer().getPluginManager().registerEvents(floatingItemManager,this);
        //获取云端同步列表并下载
        Config config = new Config(this.getDataFolder()+"/update.yml",Config.YAML);
        if(!config.exists("mods")){
            config.set("mods", Arrays.asList("first.js"));
            config.save();
        }
        List<String> list = (List<String>) config.get("mods");
        for(String a:list){
            Utils.download("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/"+a,new File(this.getDataFolder()+"/"+a));
        }
        //创建二级文件夹
        getDataFolder().mkdir();
        new File(getDataFolder()+"/skin").mkdir();
        new File(getDataFolder()+"/notemusic").mkdir();
        new File(getDataFolder()+"/lib").mkdir();

        //加载javascript
        new JavaScriptLoader(plugin).loadplugins();

        //加载python
        if(plugins.containsKey("PyBN")){
            new PythonLoader(plugin).loadplugins();
        }

        //注册事件监听器，驱动事件回调
        this.getServer().getPluginManager().registerEvents(this, this);
        eventLoader = new EventLoader(this);
        //检测nk版本，根据版本决定是否注册新增事件监听器
        boolean isNewNukkitVersion = false;
        try {
            isNewNukkitVersion = (null != Class.forName("cn.nukkit.event.player.PlayerJumpEvent"));
        } catch (Throwable t) {
            isNewNukkitVersion = false;
        }
        if(isNewNukkitVersion){
            new CompatibleEventLoader(this);
        }else {
            if (Server.getInstance().getLanguage().getName().contains("中文")){
                getlogger().warning(TextFormat.RED+"Nukkit版本太低！这可能导致一些问题。");
            }else {
            getlogger().warning(TextFormat.RED+"Nukkit version is too low! This may cause problems.");
            }
        }
        //注册bn的生物实体
        Entity.registerEntity("BNFloatingText", FloatingText.class);
        Entity.registerEntity("BNNPC", BNNPC.class);
        //注册bn命令
        functionManager.createPermission("blocklynukkit.opall","blocklynukkit插件op权限","OP");
        plugin.getServer().getCommandMap().register("hotreloadjs",new ReloadJSCommand());
        plugin.getServer().getCommandMap().register("bnplugins",new BNPluginsListCommand());
        plugin.getServer().getCommandMap().register("bninstall",new InstallCommand());
        plugin.getServer().getCommandMap().register("showstacktrace",new showStackTrace());
        plugin.getServer().getCommandMap().register("gentestworld",new GenTestWorldCommand());

        //开启速建官网服务器
        Config portconfig = new Config(this.getDataFolder()+"/port.yml",Config.YAML);
        int portto;
        if(portconfig.exists("port")){
            portto=(int)portconfig.get("port");
        }else {
            portconfig.set("port",8182);
            portto=8182;
        }
        portconfig.save();
        Utils.makeHttpServer(portto);

        //检测并注册Tips插件变量
        if(plugins.containsKey("Tips")){
            boolean isTipsVersion = false;
            try {
                isTipsVersion = (null != Class.forName("tip.utils.variables.BaseVariable"));
            } catch (Throwable t) {
                isTipsVersion = false;
            }
            if(isTipsVersion){
                TipsUtil.registerTips();
            }else {
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning(TextFormat.RED+"Tips版本太低！");
                }else {
                    getlogger().warning(TextFormat.RED+"Tips plugin's version is too low");
                }
            }
        }
    }


    @Override
    public void onDisable(){
        levelManager.dosaveSkyLandGeneratorSettings();
        levelManager.dosaveOceanGeneratorSettings();
        entityManager.recycleAllFloatingText();
        entityManager.recycleAllBNNPC();
        if(httpServer!=null){
            httpServer.stop(0);
        }
    }

    public static void putEngine(String name,String js){
        if(js.contains("//pragma JavaScript")||js.contains("//pragma javascript")||js.contains("//pragma js")||js.contains("//pragma JS")
        ||js.contains("// pragma JavaScript")||js.contains("// pragma javascript")||js.contains("// pragma js")||js.contains("// pragma JS")){
            new JavaScriptLoader(plugin).putJavaScriptEngine(name, js);
        }else if(js.contains("#pragma Python")||js.contains("#pragma python")||js.contains("#pragma PY")||js.contains("#pragma py")||
                js.contains("# pragma Python")||js.contains("# pragma python")||js.contains("# pragma PY")||js.contains("# pragma py")||
                js.contains("'''pragma Python")||js.contains("'''pragma python")||js.contains("'''pragma PY")||js.contains("'''pragma py")||
                js.contains("''' pragma Python")||js.contains("''' pragma python")||js.contains("''' pragma PY")||js.contains("''' pragma py")){
            if(plugins.containsKey("PyBN")){
                new PythonLoader(plugin).putPythonEngine(name, js);
            }else {
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning("无法加载:" + name+"! 缺少python依赖库");
                    getlogger().warning("请到https://tools.blocklynukkit.com/PyBN.jar下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + name+" python libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PyBN.jar");
                }
            }
        }else {
            new JavaScriptLoader(plugin).putJavaScriptEngine(name, js);
        }
    }


    public static void putBaseObject(String name){
        engineMap.get(name).put("server", plugin.getServer());
        engineMap.get(name).put("plugin", plugin);
        engineMap.get(name).put("manager", Loader.functionManager);
        engineMap.get(name).put("logger", new BNLogger(name));
        engineMap.get(name).put("window", Loader.windowManager);
        engineMap.get(name).put("blockitem",Loader.blockItemManager);
        engineMap.get(name).put("algorithm",Loader.algorithmManager);
        engineMap.get(name).put("inventory",Loader.inventoryManager);
        engineMap.get(name).put("world",Loader.levelManager);
        engineMap.get(name).put("entity",Loader.entityManager);
        engineMap.get(name).put("database",Loader.databaseManager);
        engineMap.get(name).put("notemusic",Loader.notemusicManager);
        engineMap.get(name).put("particle",Loader.particleManager);
        engineMap.get(name).put("gameapi",Loader.gameManager);
        engineMap.get(name).put("__NAME__",name);
    }

    public static synchronized void callEventHandler(final Event e, final String functionName) {
        if(e instanceof EntityDamageByEntityEvent||e.getEventName().equals("EntityDamageByEntityEvent")){
            boolean sametime = System.currentTimeMillis()-previousTime<8;
            previousTime=System.currentTimeMillis();
            if(sametime){
                return;
            }
        }
        for(Map.Entry<String,ScriptEngine> entry:engineMap.entrySet()){
            try {
                if (entry.getValue().get(functionName) != null) {
                    ((Invocable) entry.getValue()).invokeFunction(functionName, e);
                }
                if(privatecalls.containsKey(functionName)){
                    for(String a:privatecalls.get(functionName)){
                        if(entry.getValue().get(a) != null){
                            ((Invocable) entry.getValue()).invokeFunction(a, e);
                        }
                    }
                }
            }catch (final Exception se) {
                if (se instanceof ScriptException) {
                    ScriptException ee = (ScriptException) se;
                    previousException = ee;
                    if (Server.getInstance().getLanguage().getName().contains("中文")) {
                        Loader.getlogger().warning("在调用\"" + ee.getFileName() + "\"中的函数" + functionName + "时");
                        Loader.getlogger().warning("在第" + ee.getLineNumber() + "行第" + ee.getColumnNumber() + "列发生错误:");
                        Loader.getlogger().warning(ee.getMessage());
                        Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                    } else {
                        Loader.getlogger().warning("when calling function " + functionName + " in \"" + ee.getFileName() + "\"");
                        Loader.getlogger().warning("at line " + ee.getLineNumber() + " column " + ee.getColumnNumber() + " occurred an error:");
                        Loader.getlogger().warning(ee.getMessage());
                        Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                    }
                } else {
                    se.printStackTrace();
                }
            }
        }
    }

    public static synchronized void callEventHandler(final Event e, final String functionName,String type) {
        for(Map.Entry<String,ScriptEngine> entry:engineMap.entrySet()){
            try {if(type.equals("StoneSpawnEvent")){
                StoneSpawnEvent event = ((StoneSpawnEvent)e);
                if (entry.getValue().get(functionName) != null){
                    ((Invocable) entry.getValue()).invokeFunction(functionName, event);
                }
                if(privatecalls.containsKey(functionName)){
                    for(String a:privatecalls.get(functionName)){
                        if(entry.getValue().get(a) != null){
                            ((Invocable) entry.getValue()).invokeFunction(a, e);
                        }
                    }
                }
            }} catch (final Exception se) {
                if(se instanceof ScriptException){
                    ScriptException ee = (ScriptException)se;
                    previousException = ee;
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Loader.getlogger().warning("在调用\""+entry.getKey()+"\"中的函数"+functionName+"时");
                        Loader.getlogger().warning("在第"+ee.getLineNumber()+"行第"+ee.getColumnNumber()+"列发生错误:");
                        Loader.getlogger().warning(ee.getMessage());
                        Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                    }else {
                        Loader.getlogger().warning("when calling function "+functionName+" in \""+entry.getKey()+"\"");
                        Loader.getlogger().warning("at line "+ee.getLineNumber()+" column "+ee.getColumnNumber()+" occurred an error:");
                        Loader.getlogger().warning(ee.getMessage());
                        Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                    }
                }else {
                    se.printStackTrace();
                }
            }
        }
    }

    public synchronized void callCommand(CommandSender sender, String[] args, String functionName){
        for(Map.Entry<String,ScriptEngine> entry:engineMap.entrySet()){
            if(entry.getValue().get(functionName) == null){
                continue;
            }
            try {
                ((Invocable) entry.getValue()).invokeFunction(functionName, sender, args);
            } catch (final Exception se) {
                if(se instanceof ScriptException){
                    ScriptException e = (ScriptException)se;
                    previousException = e;
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Loader.getlogger().warning("在调用\""+entry.getKey()+"\"中的函数"+functionName+"时");
                        Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                        Loader.getlogger().warning(e.getMessage());
                        Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                    }else {
                        Loader.getlogger().warning("when calling function "+functionName+" in \""+entry.getKey()+"\"");
                        Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                        Loader.getlogger().warning(e.getMessage());
                        Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                    }
                }else {
                    se.printStackTrace();
                }
            }
        }
    }

    public synchronized Object call(String functionName, Object... args){
        if(functionName.contains("::")) {
            String[] sp = functionName.split("::");
            if(engineMap.containsKey(sp[0])) {
                ScriptEngine engine = engineMap.get(sp[0]);
                if(engine.get(sp[1]) == null){
                    return null;
                }
                try {
                    return ((Invocable) engine).invokeFunction(sp[1], args);
                } catch (final Exception se) {
                    if(se instanceof ScriptException){
                        ScriptException e = (ScriptException)se;
                        previousException = e;
                        if (Server.getInstance().getLanguage().getName().contains("中文")){
                            Loader.getlogger().warning("在调用\""+sp[0]+"\"中的函数"+functionName+"时");
                            Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                            Loader.getlogger().warning(e.getMessage());
                            Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                        }else {
                            Loader.getlogger().warning("when calling function "+functionName+" in \""+e.getFileName()+"\"");
                            Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                            Loader.getlogger().warning(e.getMessage());
                            Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                        }
                    }else {
                        se.printStackTrace();
                    }
                }
            }
        }else {
            for(Map.Entry<String,ScriptEngine> entry:engineMap.entrySet()){
                if(entry.getValue().get(functionName) == null){
                    continue;
                }
                try {
                    return ((Invocable) entry.getValue()).invokeFunction(functionName, args);
                } catch (final ScriptException e) {
                    previousException = e;
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Loader.getlogger().warning("在调用\""+entry.getKey()+"\"中的函数"+functionName+"时");
                        Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                        Loader.getlogger().warning(e.getMessage());
                        Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                    }else {
                        Loader.getlogger().warning("when calling function "+functionName+" in \""+entry.getKey()+"\"");
                        Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                        Loader.getlogger().warning(e.getMessage());
                        Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public synchronized String callbackString(String functionName, Object... args){
        if(functionName.contains("::")){
            String[] sp = functionName.split("::");
            if(engineMap.containsKey(sp[0])){
                ScriptEngine engine = engineMap.get(sp[0]);
                if(engine.get(sp[1]) == null){
                    return "NO FUNCTION";
                }
                try {

                    return String.valueOf(((Invocable) engine).invokeFunction(sp[1], args));
                } catch (final Exception se) {
                    if(se instanceof ScriptException){
                        ScriptException e = (ScriptException)se;
                        previousException = e;
                        if (Server.getInstance().getLanguage().getName().contains("中文")){
                            Loader.getlogger().warning("在调用\""+sp[0]+"\"中的函数"+functionName+"时");
                            Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                            Loader.getlogger().warning(e.getMessage());
                            Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                        }else {
                            Loader.getlogger().warning("when calling function "+functionName+" in \""+e.getFileName()+"\"");
                            Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                            Loader.getlogger().warning(e.getMessage());
                            Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                        }
                    }else {
                        se.printStackTrace();
                    }
                    return "ERROR";
                }
            }else {
                return "NO FUNCTION";
            }
        }else {
            for(Map.Entry<String,ScriptEngine> entry:engineMap.entrySet()){
                if(entry.getValue().get(functionName) == null){
                    continue;
                }
                try {
                    return String.valueOf(((Invocable) entry.getValue()).invokeFunction(functionName, args));
                } catch (final Exception se) {
                    if(se instanceof ScriptException){
                        ScriptException e = (ScriptException)se;
                        previousException = e;
                        if (Server.getInstance().getLanguage().getName().contains("中文")){
                            Loader.getlogger().warning("在调用\""+entry.getKey()+"\"中的函数"+functionName+"时");
                            Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                            Loader.getlogger().warning(e.getMessage());
                            Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                        }else {
                            Loader.getlogger().warning("when calling function "+functionName+" in \""+entry.getKey()+"\"");
                            Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                            Loader.getlogger().warning(e.getMessage());
                            Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
                        }
                    }else {
                        se.printStackTrace();
                    }
                    return "ERROR";
                }
            }
            return "NO FUNCTION";
        }

    }

    public static PluginLogger getlogger(){
        return plugin.getLogger();
    }

    /*
    * 下面是注册命令
    * 并没有大用处
    */

    public class ReloadJSCommand extends Command {

        private String functionName;

        public ReloadJSCommand() {
            super("hotreloadjs", "热重载js(仅控制台使用)");
            this.setPermission("blocklynukkit.opall");
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            if (sender.isPlayer()) {
                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                    sender.sendMessage("Only console can use this command!");
                else
                    sender.sendMessage("只有控制台才能执行此命令");
                return false;
            }
            entityManager.recycleAllFloatingText();
            Loader.plugin.getServer().getScheduler().cancelAllTasks();
            Loader.bnCrafting.craftEntryMap = new HashMap<>();
            Config config = new Config(Loader.plugin.getDataFolder() + "/update.yml", Config.YAML);
            if (!config.exists("mods")) {
                config.set("mods", Arrays.asList("first.js"));
                config.save();
            }
            List<String> list = (List<String>) config.get("mods");
            for (String a : list) {
                Utils.download("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/" + a, new File(Loader.plugin.getDataFolder() + "/" + a));
            }

            getDataFolder().mkdir();
            new File(getDataFolder() + "/skin").mkdir();


            for (File file : Objects.requireNonNull(Loader.plugin.getDataFolder().listFiles())) {
                if (file.isDirectory()) continue;
                if (file.getName().endsWith(".js") && !file.getName().contains("bak")) {
                    try (final Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
                        engineMap.put(file.getName(), new ScriptEngineManager().getEngineByName("nashorn"));
                        if (engineMap.get(file.getName()) == null) {
                            if (Server.getInstance().getLanguage().getName().contains("中文"))
                                getLogger().error("JavaScript引擎加载出错！");
                            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                                getLogger().error("JavaScript interpreter crashed!");
                            return false;
                        }
                        if (!(engineMap.get(file.getName()) instanceof Invocable)) {
                            if (Server.getInstance().getLanguage().getName().contains("中文"))
                                getLogger().error("JavaScript引擎版本过低！");
                            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                                getLogger().error("JavaScript interpreter's version is too low!");
                            return false;
                        }
                        putBaseObject(file.getName());
                        engineMap.get(file.getName()).eval(reader);
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
            return false;
        }
    }

    public class BNPluginsListCommand extends Command{
        public BNPluginsListCommand() {
            super("bnplugins","查看所有安装的blocklynukkit插件");
            this.setPermission("blocklynukkit.opall");
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            if(sender.isPlayer()){
                if(!sender.isOp())return false;
            }
            String out = TextFormat.GREEN+"BlocklyNukkit插件("+bnpluginset.size()+"): ";
            for(String a:bnpluginset){
                out+=a+", ";
            }
            sender.sendMessage(out);
            return false;
        }
    }

    public class InstallCommand extends Command{
        public InstallCommand() {
            super("bninstall","安装一个新的bn依赖模块");
            this.setAliases(new String[]{"installbn","bital"});
            this.setPermission("blocklynukkit.opall");
            Map<String, CommandParameter[]> cmdParameter = new HashMap();
            cmdParameter.put("default",new CommandParameter[]{new CommandParameter("libs",true,new String[]{"python","database"})});
            this.setCommandParameters(cmdParameter);
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            if (Server.getInstance().getLanguage().getName().contains("中文")){
                sender.sendMessage(TextFormat.YELLOW+""+TextFormat.BOLD+"模块"+args[0]+"已经预装");
            }else {
                sender.sendMessage(TextFormat.YELLOW+""+TextFormat.BOLD+"Module "+args[0]+" has been installed");
            }
            return false;
        }
    }

    public class GenTestWorldCommand extends Command{
        public GenTestWorldCommand() {
            super("gentestworld","生成测试世界");
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            if(!sender.isOp()){
                sender.sendMessage(TextFormat.RED+"你无权使用这个命令");
                return false;
            }
            Map<String,Map<String,String>> map= new HashMap<>();
            Map<String,String> eventname = new HashMap<>();
            Map<String,List<String>> eventmethodpars= new HashMap<>();
            for(Method method:eventLoader.getClass().getMethods()){
                if(method.getName().startsWith("on")){
                    Loader.getlogger().info("catch "+method.getName());
                    Class tmp = method.getParameterTypes()[0];
                    Map<String,String> m = new HashMap<>();
                    for(Method eventmethod:tmp.getMethods()){
                        if((eventmethod.getName().startsWith("get")||eventmethod.getName().startsWith("set")||eventmethod.getName().startsWith("is"))&&(!eventmethod.getName().contains("getClass")&&(!eventmethod.getName().contains("getHandler")))){
                            m.put(eventmethod.getName(),eventmethod.getReturnType().getSimpleName().replaceAll("\\[L","Array_"));
                            List<String> types = new LinkedList<>();
                            for(Class clazz:eventmethod.getParameterTypes()){
                                types.add(clazz.getSimpleName());
                            }
                            if(types.size()!=0){
                                eventmethodpars.put(eventmethod.getName(),types);
                            }
                        }
                    }
                    map.put(tmp.getSimpleName(),m);
                }
            }
            String eventNames = "&&玩家聊天&&PlayerChatEvent&&\n" +
                    "&&玩家进入&&PlayerJoinEvent&&\n" +
                    "&&方块破坏&&BlockBreakEvent&&\n" +
                    "&&方块放置&&BlockPlaceEvent&&\n" +
                    "&&玩家FormUI操作&&PlayerFormRespondedEvent&&\n" +
                    "&&玩家使用指令&&PlayerCommandPreprocessEvent&&\n" +
                    "&&物品转移(漏斗)&&InventoryTransactionEvent&&\n" +
                    "&&玩家交互(右键、点地)&&PlayerInteractEvent&&\n" +
                    "&&玩家移动&&PlayerMoveEvent&&\n" +
                    "&&生物生成&&EntitySpawnEvent&&\n" +
                    "&&生物被清除&&EntityDespawnEvent&&\n" +
                    "&&生物受到伤害&&EntityDamageEvent&&\n" +
                    "&&玩家死亡&&PlayerDeathEvent&&\n" +
                    "&&生物死亡(包括玩家)&&EntityDeathEvent&&\n" +
                    "&&生物瞬移&&EntityTeleportEvent&&\n" +
                    "&&掉落物消失&&ItemDespawnEvent&&\n" +
                    "&&掉落物生成&&ItemSpawnEvent&&\n" +
                    "&&投掷物击中&&ProjectileHitEvent&&\n" +
                    "&&投掷物发出&&ProjectileLaunchEvent&&\n" +
                    "&&生物切换世界&&EntityLevelChangeEvent&&\n" +
                    "&&生物切换物品栏&&EntityInventoryChangeEvent&&\n" +
                    "&&实体爆炸&&EntityExplosionPrimeEvent&&\n" +
                    "&&方块起火&&BlockBurnEvent&&\n" +
                    "&&方块生长&&BlockGrowEvent&&\n" +
                    "&&告示牌文字更改&&SignChangeEvent&&\n" +
                    "&&篝火抛出物品&&ItemFrameDropItemEvent&&\n" +
                    "&&树叶自然消失&&LeavesDecayEvent&&\n" +
                    "&&红石更新&&RedstoneUpdateEvent&&\n" +
                    "&&合成物品&&CraftItemEvent&&\n" +
                    "&&物品栏打开&&InventoryOpenEvent&&\n" +
                    "&&熔炉燃烧&&FurnaceBurnEvent&&\n" +
                    "&&smelt&&FurnaceSmeltEvent&&\n" +
                    "&&捡起箭&&InventoryPickupArrowEvent&&\n" +
                    "&&捡起物品&&InventoryPickupItemEvent&&\n" +
                    "&&添加药水效果&&PotionApplyEvent&&\n" +
                    "&&玩家进行传送&&PlayerTeleportEvent&&\n" +
                    "&&实体被实体攻击事件&&EntityDamageByEntityEvent&&\n" +
                    "&&实体被玩家攻击事件&&EntityDamageByPlayerEvent&&\n" +
                    "&&玩家下床&&PlayerBedEnterEvent&&\n" +
                    "&&玩家上床&&PlayerBedLeaveEvent&&\n" +
                    "&&右键点方块&&RightClickBlockEvent&&\n" +
                    "&&左键点方块&&LeftClickBlockEvent&&\n" +
                    "&&右键点空气&&ClickOnAirEvent&&\n" +
                    "&&物理触碰(压力板、按钮)&&PhysicalTouchEvent&&\n" +
                    "&&方块自然生成(下雪等)&&BlockFormEvent&&\n" +
                    "&&液体流动&&LiquidFlowEvent&&\n" +
                    "&&岩浆遇水生成圆石、石头&&StoneSpawnEvent&&\n" +
                    "&&虚拟物品栏操作事件&&FakeSlotChangeEvent&&\n" +
                    "&&红石音乐电台被摧毁&&SongDestroyingEvent&&\n" +
                    "&&红石音乐电台歌曲播放结束&&SongEndEvent&&\n" +
                    "&&红石音乐电台暂停&&SongStoppedEvent&&\n" +
                    "&&玩家退出事件&&PlayerQuitEvent&&\n" +
                    "&&物品合成事件&&CraftItemEvent&&\n" +
                    "&&玩家跳跃事件&&PlayerJumpEvent&&\n" +
                    "&&玩家开始滑翔(鞘翅)事件&&PlayerToggleGlideEvent&&\n" +
                    "&&玩家开始游泳事件&&PlayerToggleSwimEvent&&\n" +
                    "&&玩家开始潜行事件&&PlayerToggleSneakEvent&&\n" +
                    "&&玩家开始疾跑事件&&PlayerToggleSprintEvent&&\n" +
                    "&&玩家开始飞行事件&&PlayerToggleFlightEvent&&\n" +
                    "&&玩家与实体交互事件&&PlayerInteractEntityEvent&&\n" +
                    "&&玩家被玩家伤害事件&&PlayerDamageByPlayerEvent&&\n" +
                    "&&玩家被实体伤害事件&&PlayerDamageByEntityEvent&&\n" +
                    "&&实体被实体杀死事件&&EntityKilledByEntityEvent&&\n" +
                    "&&实体被玩家杀死事件&&EntityKilledByPlayerEvent&&\n" +
                    "&&玩家重生事件&&PlayerRespawnEvent&&\n" +
                    "&&玩家手持物品变化事件&&PlayerHeldEvent&&\n" +
                    "&&物品栏点击事件&&InventoryClickEvent&&\n" +
                    "&&区块卸载事件&&ChunkUnloadEvent&&\n" +
                    "&&方块因自然原因消失或衰落事件&&BlockFadeEvent&&\n" +
                    "&&方块因重力掉落事件&&BlockFallEvent&&\n" +
                    "&&液体流动/龙蛋自己传送的事件&&BlockFromToEvent&&\n" +
                    "&&方块生长事件&&BlockGrowEvent&&\n" +
                    "&&方块点燃事件&&BlockIgniteEvent&&\n" +
                    "&&活塞臂状态变化事件&&BlockPistonChangeEvent&&\n" +
                    "&&方块接受到的红石信号变化事件&&BlockRedstoneEvent&&\n" +
                    "&&开关门事件&&DoorToggleEvent&&\n" +
                    "&&使用生成蛋事件&&CreatureSpawnEvent&&\n" +
                    "&&苦力怕被雷劈中事件&&CreeperPowerEvent&&\n" +
                    "&&实体护甲变化事件&&EntityArmorChangeEvent&&\n" +
                    "&&实体改变方块事件&&EntityBlockChangeEvent&&\n" +
                    "&&实体因方块而燃烧事件&&EntityCombustByBlockEvent&&\n" +
                    "&&实体因其他实体而燃烧事件&&EntityCombustByEntityEvent&&\n" +
                    "&&实体燃烧事件&&EntityCombustEvent&&\n" +
                    "&&实体被方块伤害事件&&EntityDamageByBlockEvent&&\n" +
                    "&&实体被幼年实体伤害事件&&EntityDamageByChildEntityEvent&&\n" +
                    "&&实体爆炸事件&&EntityExplodeEvent&&\n" +
                    "&&实体运动事件&&EntityMotionEvent&&\n" +
                    "&&实体进入传送门事件&&EntityPortalEnterEvent&&\n" +
                    "&&实体回血事件&&EntityRegainHealthEvent&&\n" +
                    "&&实体射箭事件&&EntityShootBowEvent&&\n" +
                    "&&实体坐上载具事件&&EntityVehicleEnterEvent&&\n" +
                    "&&实体离开载具事件&&EntityVehicleExitEvent&&\n" +
                    "&&实体开始爆炸倒计时事件&&ExplosionPrimeEvent&&\n" +
                    "&&酿造完成事件&&BrewEvent&&\n" +
                    "&&给物品附魔事件&&EnchantItemEvent&&\n" +
                    "&&非玩家触发格子物品传输事件&&InventoryMoveItemEvent&&\n" +
                    "&&开始酿造事件&&StartBrewEvent&&\n" +
                    "&&区块加载事件&&ChunkLoadEvent&&\n" +
                    "&&新区块生成事件&&ChunkPopulateEvent&&\n" +
                    "&&世界初始化事件&&LevelInitEvent&&\n" +
                    "&&世界加载事件&&LevelLoadEvent&&\n" +
                    "&&世界保存事件&&LevelSaveEvent&&\n" +
                    "&&世界卸载事件&&LevelUnloadEvent&&\n" +
                    "&&世界重生点更改事件&&SpawnChangeEvent&&\n" +
                    "&&雷暴天气更改事件&&ThunderChangeEvent&&\n" +
                    "&&天气更改事件&&WeatherChangeEvent&&\n" +
                    "&&玩家达成成就事件&&PlayerAchievementAwardedEvent&&\n" +
                    "&&玩家动画事件&&PlayerAnimationEvent&&\n" +
                    "&&玩家尝试登录服务器事件&&PlayerAsyncPreLoginEvent&&\n" +
                    "&&玩家捡起方块事件&&PlayerBlockPickEvent&&\n" +
                    "&&玩家空桶事件&&PlayerBucketEmptyEvent&&\n" +
                    "&&玩家填桶事件&&PlayerBucketFillEvent&&\n" +
                    "&&玩家切换皮肤事件&&PlayerChangeSkinEvent&&\n" +
                    "&&玩家请求区块事件&&PlayerChunkRequestEvent&&\n" +
                    "&&玩家创建事件&&PlayerCreationEvent&&\n" +
                    "&&玩家丢物品事件&&PlayerDropItemEvent&&\n" +
                    "&&玩家吃东西事件&&PlayerEatFoodEvent&&\n" +
                    "&&玩家编辑书本事件&&PlayerEditBookEvent&&\n" +
                    "&&玩家饥饿值改变事件&&PlayerFoodLevelChangeEvent&&\n" +
                    "&&玩家游戏模式改变事件&&PlayerGameModeChangeEvent&&\n" +
                    "&&玩家填充玻璃瓶事件&&PlayerGlassBottleFillEvent&&\n" +
                    "&&玩家错误地移动世界&&PlayerInvalidMoveEvent&&\n" +
                    "&&玩家使用了一个一次性消耗品事件&&PlayerItemConsumeEvent&&\n" +
                    "&&玩家客户端加载完成事件&&PlayerLocallyInitializedEvent&&\n" +
                    "&&玩家请求地图数据事件&&PlayerMapInfoRequestEvent&&\n" +
                    "&&玩家鼠标在指向实体事件&&PlayerMouseOverEntityEvent&&\n" +
                    "&&玩家请求服务器设置事件&&PlayerServerSettingsRequestEvent&&\n" +
                    "&&玩家设置服务器设置事件&&PlayerSettingsRespondedEvent&&\n" +
                    "&&插件被关闭事件&&PluginDisableEvent&&\n" +
                    "&&插件被启用事件&&PluginEnableEvent&&\n" +
                    "&&药水效果添加事件&&PotionApplyEvent&&\n" +
                    "&&药水效果冲突事件&&PotionCollideEvent&&\n" +
                    "&&玩家数据序列化事件&&PlayerDataSerializeEvent&&\n" +
                    "&&远程rcon控制执行命令事件&&RemoteServerCommandEvent&&\n" +
                    "&&实体进入载具事件&&EntityEnterVehicleEvent&&\n" +
                    "&&实体离开载具事件&&EntityExitVehicleEvent&&\n" +
                    "&&载具创建事件&&VehicleCreateEvent&&\n" +
                    "&&载具受伤事件&&VehicleDamageEvent&&\n" +
                    "&&载具破坏事件&&VehicleDestroyEvent&&\n" +
                    "&&载具移动事件&&VehicleMoveEvent&&\n" +
                    "&&载具更新事件&&VehicleUpdateEvent&&\n" +
                    "&&打雷事件&&LightningStrikeEvent&&\n";
            for(String line:eventNames.split("\n")){
                line = line.replaceFirst("&&","");
                getlogger().info("翻译 "+line.split("&&")[1]+" 到 "+line.split("&&")[0]);
                eventname.put(line.split("&&")[1],line.split("&&")[0]);
            }
            String out = "";
            for(Map.Entry<String,Map<String,String>> entry:map.entrySet()){
                out+=("### "+entry.getKey()+"\n");
                out+=("*"+eventname.get(entry.getKey())+"*\n");
                for(Map.Entry<String,String> each:entry.getValue().entrySet()){
                    String kuohao = "";
                    if(eventmethodpars.keySet().contains(each.getKey()))
                    for(String p:eventmethodpars.get(each.getKey())){
                        kuohao+=(","+p);
                    }
                    kuohao.replaceFirst(",","");
                    out+=("- "+each.getValue()+" "+each.getKey()+"("+kuohao+")\n");
                }
                out+="\n";
            }
            functionManager.writeFile("./plugins/BlocklyNukkit/tmp.md",out);
            return false;
        }
    }

    public class showStackTrace extends Command{
        public showStackTrace() {
            super("showstacktrace","display previous error stacktrace");
            this.setPermission("blocklynukkit.opall");
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            if(sender.isPlayer()){
                sender.sendMessage(TextFormat.RED+"This command can only be called from console!");
                return false;
            }else {
                if(previousException!=null){
                    previousException.printStackTrace();
                }
            }
            return false;
        }
    }


}
