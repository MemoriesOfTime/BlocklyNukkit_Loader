package com.blocklynukkit.loader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.other.BNLogger;
import com.blocklynukkit.loader.other.Clothes;
import com.blocklynukkit.loader.other.Entities.BNNPC;
import com.blocklynukkit.loader.other.Entities.FloatingItemManager;
import com.blocklynukkit.loader.other.Entities.FloatingText;
import com.blocklynukkit.loader.script.*;
import com.blocklynukkit.loader.script.event.*;
import com.blocklynukkit.loader.scriptloader.GraalJSLoader;
import com.blocklynukkit.loader.scriptloader.JavaScriptLoader;
import com.blocklynukkit.loader.scriptloader.PythonLoader;
import com.sun.net.httpserver.HttpServer;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.blocklynukkit.loader.other.BNCrafting;
import com.blocklynukkit.loader.other.card.CardMaker;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Loader extends PluginBase implements Listener {

    public static Loader plugin;

    public static Map<String, ScriptEngine> engineMap = new HashMap<>();
    public static Map<String,HashSet<String>> privatecalls = new HashMap<>();
    public static Set<String> bnpluginset = new HashSet<>();

    public static String positionstmp = "";
    public static int checkupdatetime = 0;

    public static Map<String, Skin> playerskinmap = new HashMap<>();
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
        //创建各种基对象
        //这里没有database因为后面要检查依赖库是否存在再创建
        functionManager=new FunctionManager(plugin);windowManager=new WindowManager();blockItemManager=new BlockItemManager();
        algorithmManager=new AlgorithmManager();inventoryManager=new InventoryManager();levelManager=new LevelManager();entityManager=new EntityManager();
        databaseManager=null;cardMaker=new CardMaker();notemusicManager=new NotemusicManager();particleManager=new ParticleManager();databaseManager=new DatabaseManager();
        noteBlockPlayerMain.onEnable();
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
        },0,3600*4*1000);
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
        new PythonLoader(plugin).loadplugins();

        //注册事件监听器，驱动事件回调
        this.getServer().getPluginManager().registerEvents(this, this);
        new EventLoader(this);
        //检测nk版本，根据版本决定是否注册新增事件监听器
        boolean isNewNukkitVersion = false;
        try {
            isNewNukkitVersion = (null != Class.forName("cn.nukkit.event.player.PlayerJumpEvent"));
        } catch (Throwable t) {
            isNewNukkitVersion = false;
        }
        if(isNewNukkitVersion){
            new CompatibleEventLoader(this);
        }
        //注册bn的生物实体
        Entity.registerEntity("BNFloatingText", FloatingText.class);
        Entity.registerEntity("BNNPC", BNNPC.class);
        //注册bn命令
        functionManager.createPermission("blocklynukkit.opall","blocklynukkit插件op权限","OP");
        plugin.getServer().getCommandMap().register("hotreloadjs",new ReloadJSCommand());
        plugin.getServer().getCommandMap().register("bnplugins",new BNPluginsListCommand());
        plugin.getServer().getCommandMap().register("bninstall",new InstallCommand());
        //plugin.getServer().getCommandMap().register("testNPC",new testNPC());
        //plugin.getServer().getCommandMap().register("gentestworld",new GenTestWorldCommand());

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
            new PythonLoader(plugin).putPythonEngine(name, js);
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
        engineMap.get(name).put("__NAME__",name);
    }

    public static synchronized void callEventHandler(final Event e, final String functionName) {
        try {
            for(ScriptEngine engine:engineMap.values()){
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
            for(ScriptEngine engine:engineMap.values()){
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
                }else if(type.equals("PlayerDamageByPlayerEvent")){
                    PlayerDamageByPlayerEvent event = ((PlayerDamageByPlayerEvent)e);
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
                else if(type.equals("EntityKilledByEntityEvent")){
                    EntityKilledByEntityEvent event = ((EntityKilledByEntityEvent)e);
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
                }else if(type.equals("EntityKilledByPlayerEvent")){
                    EntityKilledByPlayerEvent event = ((EntityKilledByPlayerEvent)e);
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
                }else if(type.equals("PlayerDamageByEntityEvent")){
                    PlayerDamageByEntityEvent event = ((PlayerDamageByEntityEvent)e);
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
        for(ScriptEngine engine:engineMap.values()){
            if(engine.get(functionName) == null){
                continue;
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
    }

    public synchronized void call(String functionName, Object... args){
        if(functionName.contains("::")) {
            String[] sp = functionName.split("::");
            if(engineMap.containsKey(sp[0])) {
                ScriptEngine engine = engineMap.get(sp[0]);
                if(engine.get(sp[1]) == null){
                    return ;
                }
                try {
                    ((Invocable) engine).invokeFunction(sp[1], args);
                } catch (final Exception se) {
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getLogger().error("在回调 " + functionName+"时出错", se);
                    else
                        plugin.getLogger().error("errors when calling " + functionName, se);
                    se.printStackTrace();
                }
            }
        }else {
            for(ScriptEngine engine:engineMap.values()){
                if(engine.get(functionName) == null){
                    continue;
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
        }
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
                    if (Server.getInstance().getLanguage().getName().contains("中文"))
                        getLogger().error("在回调 " + functionName+"时出错", se);
                    else
                        plugin.getLogger().error("errors when calling " + functionName, se);
                    se.printStackTrace();
                    return "ERROR";
                }
            }else {
                return "NO FUNCTION";
            }
        }else {
            for(ScriptEngine engine:engineMap.values()){
                if(engine.get(functionName) == null){
                    continue;
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
            return "NO FUNCTION";
        }

    }

    private synchronized Object eval(final CommandSender sender, final String expression) throws ScriptException {
        for(ScriptEngine engine:engineMap.values()){
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
        return null;
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
            if(args.length<1){
                return false;
            }

//            levelManager.setSkyLandGenerator(64,0,true,
//                    20,17,0,128,20,9,0,64,
//                    8,8,0,16,1,7,0,10,
//                    2,9,0,32,1,8,0,16,
//                    10,33,0,128,8,33,0,128,
//                    10,33,0,80,10,33,0,80,
//                    10,33,0,80,true,true,true);
            //levelManager.genLevel(args[0],999,"FLAT");
            if(sender.isPlayer()){
                Server.getInstance().loadLevel(args[0]);
                levelManager.loadScreenTP(((Player)sender),Server.getInstance().getLevelByName(args[0]).getSafeSpawn(),60);
            }
            return false;
        }
    }

    public class testNPC extends Command{
        public testNPC() {
            super("testNPC","测试BNNPC");
            this.setPermission("blocklynukkit.opall");
        }
        @Override
        public boolean execute(CommandSender sender, String s, String[] args){
            if(sender.isPlayer()){
                if(!sender.isOp())return false;
            }else {
                return false;
            }
            Player player = Server.getInstance().getPlayer(sender.getName());
            double x=player.x,y=player.y,z=player.z;
            BNNPC tmp = new BNNPC(player.level.getChunk(((int)x)>>4,((int)z)>>4),Entity.getDefaultNBT(new Vector3(x,y,z)),"test",new Clothes(args[0]));
            tmp.spawnToAll();
            return false;
        }
    }


}
