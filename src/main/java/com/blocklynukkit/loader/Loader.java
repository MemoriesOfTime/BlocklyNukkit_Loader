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
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import com.blocklynukkit.loader.other.BNLogger;
import com.blocklynukkit.loader.other.Babel;
import com.blocklynukkit.loader.other.cmd.*;
import com.blocklynukkit.loader.other.Entities.BNNPC;
import com.blocklynukkit.loader.other.Entities.FloatingItemManager;
import com.blocklynukkit.loader.other.Entities.FloatingText;
import com.blocklynukkit.loader.other.debug.data.CommandInfo;
import com.blocklynukkit.loader.other.generator.render.BaseRender;
import com.blocklynukkit.loader.other.lizi.bnqqbot;
import com.blocklynukkit.loader.other.tips.TipsUtil;
import com.blocklynukkit.loader.script.*;
import com.blocklynukkit.loader.script.event.*;
import com.blocklynukkit.loader.script.window.windowCallbacks.WindowCallback;
import com.blocklynukkit.loader.scriptloader.*;
import com.blocklynukkit.loader.other.BNCrafting;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.utils.MetricsLite;
import com.blocklynukkit.loader.utils.Utils;

import com.sun.net.httpserver.HttpServer;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import de.theamychan.scoreboard.network.Scoreboard;
import javassist.CtClass;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Loader extends PluginBase implements Listener {

    public static Loader plugin;
    public static File pluginFile;

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
    public static Map<String, Object> easytmpmap = new HashMap<>();
    public static Map<String, String> htmlholdermap = new HashMap<>();
    public static BNCrafting bnCrafting = new BNCrafting();
    public static HttpServer httpServer = null;
    public static EventLoader eventLoader;
    public static FloatingItemManager floatingItemManager = new FloatingItemManager();
    public static NoteBlockPlayerMain noteBlockPlayerMain = new NoteBlockPlayerMain();
    public static Map<String, Plugin> plugins;
    public static Map<String, CommandInfo> plugincmdsmap = new HashMap<>();
    public static Map<String,CtClass> bnClasses = new HashMap<>();
    public static boolean enablePython = false;
    public static boolean enablePHP = false;
    public static Map<String,List<Integer>> pluginTasksMap = new HashMap<>();
    public static Random mainRandom = new Random(System.currentTimeMillis());
    //es2020->es5翻译器
    public static Babel babel = null;
    //windowManager变量
    public static ConcurrentHashMap<Integer, String> functioncallback = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, WindowCallback> windowCallbackMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, ScriptObjectMirror> scriptObjectMirrorCallback = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> serverSettingCallback = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Boolean> acceptCloseCallback = new ConcurrentHashMap<>();
    public static Map<String, Scoreboard> boards = new HashMap<>();
    public static Map<String,String> tipsVar = new HashMap<>();
    //levelManager变量
    public static Map<String,Object> skylandoptions = new HashMap<>();
    public static int OceanSeaLevel = 64;
    public static List<BaseRender> levelRenderList = new ArrayList<>();
    //functionManager变量
    public static bnqqbot qq = new bnqqbot();
    public static String fakeNukkitCodeVersion = "";
    public static ExtendScriptLoader nodejs = null;

    @Override
    public void onEnable() {
        plugin=this;
        pluginFile=this.getFile();
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
                if (Server.getInstance().getLanguage().getName().contains("中文"))
                    Loader.getlogger().warning(TextFormat.RED+"您没有安装BNGameAPI,虽然不是必须安装，但它是部分bn插件的必要依赖，建议您安装，下载地址：https://tools.blocklynukkit.com/BNGameAPI.jar");
                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                    Loader.getlogger().warning(TextFormat.RED+"You haven't installed bngameapi,although it's not necessary,but PlaceHolderAPI is needed by the gameapi module,we suggest you to install,download link: https://tools.blocklynukkit.com/BNGameAPI.jar");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //创建各种基对象
        //这里没有database因为后面要检查依赖库是否存在再创建
        //10/25add 现在创建多个基对象
        noteBlockPlayerMain.onEnable();//if(plugins.containsKey("GameAPI"))gameManager=new GameManager();
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
        LevelManager.doreloadSkyLandGeneratorSettings();
        LevelManager.doreloadOceanGeneratorSettings();
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
        if(list!=null)
        for(String a:list){
            Utils.download("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/"+a,new File(this.getDataFolder()+"/"+a));
        }
        //创建二级文件夹
        getDataFolder().isDirectory();
        getDataFolder().mkdir();
        new File(getDataFolder()+"/skin").mkdir();
        new File(getDataFolder()+"/notemusic").mkdir();
        new File(getDataFolder()+"/lib").mkdir();

        //加载bn插件包
        new BNPackageLoader(this).loadplugins();
        //加载javascript
        try{
            new JavaScriptLoader(plugin).loadplugins();
        }catch (NoClassDefFoundError e){
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getLogger().warning("java运行时环境不完整，无法加载.js插件，请安装java8SE-java14SE的完整版本！");
            else
                getLogger().warning("java runtime is incomplete!");
        }
        //加载python
        if(plugins.containsKey("PyBN")){
            new PythonLoader(plugin).loadplugins();
            enablePython=true;
        }

        //加载PHP
        if(plugins.containsKey("PHPBN")){
            new PHPLoader(plugin).loadplugins();
            enablePHP=false;
        }

        //加载Lua
        new LuaLoader(plugin).loadplugins();

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
        plugin.getServer().getPluginManager().addPermission(new Permission("blocklynukkit.opall","blocklynukkit插件op权限","op"));
        //hotreloadjs命令被bnreload替代
        //plugin.getServer().getCommandMap().register("hotreloadjs",new ReloadJSCommand());
        plugin.getServer().getCommandMap().register("bnplugins",new BNPluginsListCommand());
        plugin.getServer().getCommandMap().register("bninstall",new InstallCommand());
        plugin.getServer().getCommandMap().register("showstacktrace",new showStackTrace());
        plugin.getServer().getCommandMap().register("gentestworld",new GenTestWorldCommand());
        plugin.getServer().getCommandMap().register("bndebug",new DebugerCommand());
        plugin.getServer().getCommandMap().register("exportdevjar",new ExportDevJarCommand());
        plugin.getServer().getCommandMap().register("bnpackage",new PackageCommand());
        plugin.getServer().getCommandMap().register("bnreload",new BNReloadCommand());

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
    //监听bn被卸载事件
    public void onDisable(){
        LevelManager.dosaveSkyLandGeneratorSettings();
        LevelManager.dosaveOceanGeneratorSettings();
        EntityManager.recycleAllFloatingText();
        EntityManager.recycleAllBNNPC();
        if(httpServer!=null){
            httpServer.stop(0);
        }
        engineMap.clear();
        Server.getInstance().getScheduler().cancelTask(this);
        System.gc();
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
                    getlogger().warning("请到 https://tools.blocklynukkit.com/PyBN.jar 下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + name+" python libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PyBN.jar");
                }
            }
        }else if(js.contains("--pragma Lua")||js.contains("--pragma lua")||js.contains("--pragma LUA")
                ||js.contains("-- pragma Lua")||js.contains("-- pragma lua")||js.contains("-- pragma LUA")){
            new LuaLoader(plugin).putLuaEngine(name, js);
        }else if(js.contains("//pragma php")||js.contains("//pragma PHP")||js.contains("// pragma php")||js.contains("// pragma PHP")||
                js.contains("/*pragma php")||js.contains("/*pragma PHP")||js.contains("/* pragma php")||js.contains("/* pragma PHP")||
                js.contains("/*\npragma php")||js.contains("/*\npragma PHP")||js.contains("/*\n pragma php")||js.contains("/*\n pragma PHP")||
                js.contains("/*\n  pragma php")||js.contains("/*\n  pragma PHP")||js.contains("/*\n    pragma php")||js.contains("/*\n    pragma PHP")){
            if(plugins.containsKey("PHPBN")){
                new PHPLoader(plugin).putPHPEngine(name, js);
            }else {
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning("无法加载:" + name+"! 缺少php依赖库");
                    getlogger().warning("请到 https://tools.blocklynukkit.com/PHPBN.jar 下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + name+" PHP libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PHPBN.jar");
                }
            }
        }else if(js.startsWith("bnp")){
            BNPackageLoader bnPackageLoader = new BNPackageLoader(plugin);
            bnPackageLoader.runPlugins(bnPackageLoader.unpack(js));
        }
        else {
            new JavaScriptLoader(plugin).putJavaScriptEngine(name, js);
        }
    }


    public static void putBaseObject(String name){
        ScriptEngine engine = engineMap.get(name);
        engine.put("server", plugin.getServer());
        engine.put("plugin", plugin);
        engine.put("manager", new FunctionManager(engine));
        engine.put("logger", new BNLogger(name));
        engine.put("window", new WindowManager(engine));
        engine.put("blockitem",new BlockItemManager(engine));
        engine.put("algorithm",new AlgorithmManager(engine));
        engine.put("inventory",new InventoryManager(engine));
        engine.put("world",new LevelManager(engine));
        engine.put("entity",new EntityManager(engine));
        engine.put("database",new DatabaseManager(engine));
        engine.put("notemusic",new NotemusicManager(engine));
        engine.put("particle",new ParticleManager(engine));
        engine.put("gameapi",plugins.containsKey("GameAPI")?new GameManager(engine):null);
        engine.put("__NAME__",name);
    }

    public static synchronized void callEventHandler(final Event e, final String functionName) {
        if(e instanceof EntityDamageByEntityEvent||e.getEventName().equals("EntityDamageByEntityEvent")){
            boolean sametime = System.currentTimeMillis()-previousTime<10;
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
                        Loader.getlogger().warning("在调用\"" + entry.getKey() + "\"中的函数" + functionName + "时");
                        Loader.getlogger().warning("在第" + ee.getLineNumber() + "行第" + ee.getColumnNumber() + "列发生错误:");
                        Loader.getlogger().warning(ee.getMessage());
                        Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
                    } else {
                        Loader.getlogger().warning("when calling function " + functionName + " in \"" + entry.getKey() + "\"");
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
            try {
                if(type.equals("StoneSpawnEvent")){
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
                }else if(type.equals("QQFriendMessageEvent")){
                    QQFriendMessageEvent event = (QQFriendMessageEvent)e;
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
                }else if(type.equals("QQGroupMessageEvent")){
                    QQGroupMessageEvent event = (QQGroupMessageEvent)e;
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
                }else if(type.equals("QQOtherEvent")){
                    QQOtherEvent event = (QQOtherEvent)e;
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
                }else if(type.equals("FakeSlotChangeEvent")){
                    FakeSlotChangeEvent event = (FakeSlotChangeEvent)e;
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
                }
            } catch (final Exception se) {
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

    public synchronized void callCommand(String commandName,CommandSender sender, String[] args, String functionName){
        long start = System.currentTimeMillis();
        for(Map.Entry<String,ScriptEngine> entry:engineMap.entrySet()){
            if(entry.getValue().get(functionName) == null){
                continue;
            }
            try {
                Loader.plugincmdsmap.get(commandName).newCall(System.currentTimeMillis()-start,
                    LocalDateTime.now().toString(),entry.getKey(),functionName,sender.getName(),args);
                ((Invocable) entry.getValue()).invokeFunction(functionName, sender, args);
            } catch (final Exception se) {
                if(se instanceof ScriptException){
                    ScriptException e = (ScriptException)se;
                    previousException = e;
                    Loader.plugincmdsmap.get(commandName).setLastCallError("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误",se.getStackTrace());
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

    public static FunctionManager getFunctionManager(){
        return new FunctionManager(null);
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
            Server.getInstance().getPluginManager().disablePlugin(plugin);
            String path = new String(plugin.getFile().getAbsolutePath().toCharArray());
            Server.getInstance().reload();
            Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    Server.getInstance().getPluginManager().loadPlugin(path);
                }
            },60);
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
        public boolean execute(CommandSender sender, String s, String[] args) {
            return false;
        }
    }
}
