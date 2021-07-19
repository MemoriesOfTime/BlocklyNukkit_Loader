package com.blocklynukkit.loader.script;
import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
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
import cn.nukkit.utils.EventException;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Timing;
import com.blocklynukkit.loader.other.control.JVM;
import com.blocklynukkit.loader.other.net.http.CustomHttpHandler;
import com.blocklynukkit.loader.other.net.http.HttpRequestEntry;
import com.blocklynukkit.loader.other.net.websocket.WsClient;
import com.blocklynukkit.loader.other.net.websocket.WsServer;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.other.BstatsBN;
import com.blocklynukkit.loader.other.Clothes;
import com.blocklynukkit.loader.other.debug.data.CommandInfo;
import com.blocklynukkit.loader.other.lizi.bnqqbot;
import com.blocklynukkit.loader.scriptloader.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mchange.net.ProtocolException;
import com.mchange.net.SmtpMailSender;
import com.sun.management.OperatingSystemMXBean;
import com.sun.net.httpserver.HttpServer;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.ir.Block;
import me.onebone.economyapi.EconomyAPI;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.blocklynukkit.loader.Loader.*;

public class FunctionManager extends BaseManager {
    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }

    private Loader plugin;

    public bnqqbot qq = Loader.qq;

    public JVM jvm = new JVM();

    public Timing timing = new Timing();

    public FunctionManager(ScriptEngine engine){
        super(engine);
        this.plugin = Loader.plugin;
        if(Loader.plugins.keySet().contains("NodeBN_Windows_64")||Loader.plugins.keySet().contains("NodeBN_Linux_64")){
            nodejs = new NodeJSLoader();
        }else {
            nodejs = new NodeJSNotFoundLoader();
        }
    }

    @Comment(value = "创建WebSocket服务器")
    final public WsServer createWsServer(@Comment(value = "端口") int port
            ,@Comment(value = "有新的ws客户端连接回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsServer", "org.java_websocket.WebSocket"}, parameters = {"server", "ws"}, comments = {"ws服务器对象", "ws连接对象"}) String newWsConnectCallback
            ,@Comment(value = "ws客户端断开连接回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsServer", "org.java_websocket.WebSocket", "int", "java.lang.String", "boolean"}, parameters = {"server", "ws", "existCode", "reason", "remoteClose"}, comments = {"ws服务器对象", "ws连接对象", "退出值", "断开原因", "是否客户端发起断开"}) String closeWsConnectCallback
            ,@Comment(value = "接收到字符串数据回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsServer", "org.java_websocket.WebSocket", "java.lang.String"}, parameters = {"server", "ws", "data"}, comments = {"ws服务器对象", "ws连接对象", "发送的字符串数据"}) String receiveStringCallback
            ,@Comment(value = "接收到非字符串数据回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsServer", "org.java_websocket.WebSocket", "java.nio.ByteBuffer"}, parameters = {"server", "ws", "data"}, comments = {"ws服务器对象", "ws连接对象", "发送的数据缓冲区"}) String receiveDataCallback){
        return new WsServer(port, newWsConnectCallback, closeWsConnectCallback, receiveStringCallback, receiveDataCallback);
    }

    @Comment(value = "创建WebSocket客户端")
    final public WsClient createWsClient(@Comment(value = "远程ws服务器链接") String serverUrl
            ,@Comment(value = "ws成功连接回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsClient", "org.java_websocket.WebSocket"}, parameters = {"client", "ws"}, comments = {"ws客户端对象", "ws连接对象"}) String newWsConnectCallback
            ,@Comment(value = "ws客户端断开连接回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsClient", "org.java_websocket.WebSocket", "int", "java.lang.String", "boolean"}, parameters = {"client", "ws", "existCode", "reason", "remoteClose"}, comments = {"ws客户端对象", "ws连接对象", "退出值", "断开原因", "是否客户端发起断开"}) String closeWsConnectCallback
            ,@Comment(value = "接收到字符串数据回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsClient", "org.java_websocket.WebSocket", "java.lang.String"}, parameters = {"client", "ws", "data"}, comments = {"ws客户端对象", "ws连接对象", "发送的字符串数据"}) String receiveStringCallback
            ,@Comment(value = "接收到非字符串数据回调")
             @CallbackFunction(classes = {"com.blocklynukkit.loader.other.net.websocket.WsClient", "org.java_websocket.WebSocket", "java.nio.ByteBuffer"}, parameters = {"client", "ws", "data"}, comments = {"ws客户端对象", "ws连接对象", "发送的数据缓冲区"}) String receiveDataCallback){
        return new WsClient(serverUrl, newWsConnectCallback, closeWsConnectCallback, receiveStringCallback, receiveDataCallback);
    }

    @Comment(value = "动态监听事件")
    final public void customEventListener(@Comment(value = "要监听的事件的java类名") String fullEventName
            ,@Comment(value = "事件回调函数") @CallbackFunction(classes = {"cn.nukkit.event.Event"}, parameters = {"event"}, comments = {"事件对象"}) String callbackFunction
            ,@Comment(value = "事件优先级，可选NORMAL MONITOR LOWEST LOW HIGH HIGHEST") String priority){
        try {
            Server.getInstance().getPluginManager().registerEvent((Class<? extends Event>) Class.forName(fullEventName)
                    , new Listener() {}, EventPriority.valueOf(priority)
                    , (listener, event) -> Loader.plugin.call(callbackFunction, event), Loader.plugin);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Comment(value = "创建一个新的HTTP服务器，返回是否创建成功，如创建成功则可以启动")
    final public boolean createHttpServer(@Comment(value = "服务器端口") int port){
        if(Loader.httpServers.containsKey(port)){
            return false;
        }else {
            try {
                HttpServer httpServer = HttpServer.create(new InetSocketAddress(port),0);
                httpServer.setExecutor(mainExecutor);
                httpServers.put(port,httpServer);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    @Comment(value = "启动指定端口的HTTP服务器")
    final public void startHttpServer(@Comment(value = "要启动的http服务器端口") int port) throws IOException {
        if(httpServers.containsKey(port)){
            httpServers.get(port).start();
        }else {
            throw new IOException("No httpServer instance found on port "+port);
        }
    }
    @Comment(value = "将处理器函数绑定到指定端口的http服务器上")
    final public boolean attachHandlerToHttpServer(@Comment(value = "要绑定处理器函数的http服务器的端口") int port
            ,@Comment(value = "要绑定处理器处理的访问路径") String path
            ,@Comment(value = "要绑定的处理器函数")
             @CallbackFunction(classes = "com.blocklynukkit.loader.other.net.http.HttpRequestEntry", parameters = "request", comments = "请求内容对象") String function){
        if(httpServers.containsKey(port)){
            httpServers.get(port).createContext(path,new CustomHttpHandler(function));
            return true;
        }
        return false;
    }
    @Comment(value = "进行垃圾回收，释放无用内存，该函数会暂时阻塞线程")
    final public void jvmGC(){
        Runtime.getRuntime().gc();
    }
    @Comment(value = "强制与NK主线程同步调用指定函数")
    final public Object syncCallFunction(@Comment(value = "要调用的函数") @CallbackFunction String functionName
            ,@Comment(value = "调用函数的参数") Object... args){
        synchronized (Server.getInstance()){
            return Loader.plugin.call(functionName, args);
        }
    }
    @Comment(value = "检验bn解释器版本，如果过低则停止插件加载并发出错误信息")
    final public void requireMinVersion(@Comment(value = "插件要求的最低版本的版本号") String minVersion
            ,@Comment(value = "版本太低时发出的错误提示信息") String failMessage) throws ScriptException {
        int check = Integer.parseInt(minVersion.replaceAll("\\.",""));
        int nowVersion = Integer.parseInt(Server.getInstance().getPluginManager().getPlugin("BlocklyNukkit").getDescription().getVersion().replaceAll("\\.",""));
        if(nowVersion<check){
            throw new ScriptException(failMessage);
        }
    }
    @Comment(value = "将指定函数立即在新线程上调用，并立即返回该函数运行的线程对象")
    final public Thread runThread(@Comment(value = "要在新线程运行的函数名称") @CallbackFunction String functionName
            ,@Comment(value = "调用函数的参数") Object... args){
        Thread thread = new Thread(() -> Loader.plugin.call(getScriptName()+"::"+functionName,args));
        thread.start();
        return thread;
    }
    @Comment(value = "从指定URL下载文件并保存在本地")
    final public void downloadFromURL(@Comment(value = "要从其下载文件的url") String url
            ,@Comment(value = "保存此文件的目录路径") String saveDir
            ,@Comment(value = "下载后保存的文件名") String saveName){
        try {
            Utils.downLoadFromUrl(url,saveName,saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Comment(value = "强制中断当前函数运行")
    final public void interrupt(@Comment(value = "中断后发出的警告信息") String info) throws Throwable {
        throw new ScriptException(info);
    }
    @Comment(value = "获取当前服务器在线的所有玩家")
    final public Player[] getOnlinePlayers(){
        return Server.getInstance().getOnlinePlayers().values().toArray(new Player[]{});
    }
    //here 11/6
    @Comment(value = "指定文件路径是否存在")
    final public boolean isPathExists(@Comment(value = "绝对路径或相对路径") String path){
        return new File(path).exists();
    }
    @Comment(value = "获取指定路径下的所有文件和文件夹名称")
    final public String[] getFolderFiles(@Comment(value = "绝对路径或相对路径") String path){
        File file = new File(path);
        return (file.exists()?(file.isDirectory()?(file.list()):null):null);
    }
    @Comment(value = "获取指定路径的文件大小，如果是文件夹返回-1")
    final public long getFileSize(@Comment(value = "绝对路径或相对路径，非文件夹") String path){
        File file = new File(path);
        return file.exists()?(file.isFile()?(file.length()):-1):-1;
    }
    @Comment(value = "删除指定路径的文件或文件夹")
    final public void deleteFile(@Comment(value = "绝对路径或相对路径") String path){
        File file = new File(path);
        if(file.exists())file.delete();
    }
    @Comment(value = "如路径不存在，创建指定路径的文件或文件夹及其所有未创建的父文件夹")
    final public void doPathCreate(@Comment(value = "绝对路径或相对路径") String path){
        File file = new File(path);
        if(file.exists())file.delete();
        if(file.getName().contains(".")){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            file.mkdirs();
        }
    }
    @Comment(value = "路径上的文件是否可读")
    final public boolean isPathReadable(@Comment(value = "绝对路径或相对路径，非文件夹") String path){
        return new File(path).canRead();
    }
    @Comment(value = "路径上的文件是否可写")
    final public boolean isPathWritable(@Comment(value = "绝对路径或相对路径，非文件夹") String path){
        return new File(path).canWrite();
    }
    @Comment(value = "将一个路径上的文件复制到另一个路径")
    final public void copyFile(@Comment(value = "绝对路径或相对路径，非文件夹") String fromPath
            ,@Comment(value = "绝对路径或相对路径，非文件夹") String toPath){
        File from = new File(fromPath);File to = new File(toPath);
        if(to.exists())to.delete();
        if(from.exists()&&from.canRead()&&to.canWrite()){
            try {
                Files.copy(from.toPath(),to.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //here 10/15
    @Comment(value = "执行系统控制台命令")
    final public void runCMD(@Comment(value = "命令具体内容") String cmd){
        String cmdStr = cmd;
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmdStr);
            process.waitFor(6, TimeUnit.SECONDS);
            InputStream in = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(reader);
            StringBuffer sb = new StringBuffer();
            String message;
            while((message = br.readLine()) != null) {
                sb.append(message);
            }
            System.out.println(sb);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //here 9/17
    @Comment(value = "将指定路径的文件当作bn插件加载")
    final public void newPlugin(@Comment(value = "绝对路径或相对路径") String path){
        File file = new File(path);
        Loader.putEngine(file.getName(),Utils.readToString(file));
    }
    @Comment(value = "加载一个新的js插件")
    final public void newJSPlugin(@Comment(value = "插件名") String name,@Comment(value = "插件代码") String code){
        if(!name.endsWith(".js")){
            name+=".js";
        }
        new JavaScriptLoader(Loader.plugin).putJavaScriptEngine(name,code);
    }
    @Comment(value = "加载一个新的python插件")
    final public void newPYPlugin(@Comment(value = "插件名") String name,@Comment(value = "插件代码") String code){
        if(!name.endsWith(".py")){
            name+=".py";
        }
        new PythonLoader(Loader.plugin).putPythonEngine(name,code);
    }
    @Comment(value = "加载一个新的lua插件")
    final public void newLUAPlugin(@Comment(value = "插件名") String name,@Comment(value = "插件代码") String code){
        if(!name.endsWith(".lua")){
            name+=".lua";
        }
        new LuaLoader(Loader.plugin).putLuaEngine(name,code);
    }
    @Comment(value = "加载一个新的php插件")
    final public void newPHPPlugin(@Comment(value = "插件名") String name,@Comment(value = "插件代码") String code){
        if(!name.endsWith(".php")){
            name+=".php";
        }
        new PHPLoader(Loader.plugin).putPHPEngine(name,code);
    }
    //here 10/13
    @Comment(value = "获取网络或本地的资源内容")
    final public String getResource(@Comment(value = "URL或文件路径") String name){
        if(name.startsWith("http")){
            return Utils.sendGet(name,null);
        }
        name = name.replaceFirst("\\./","");
        File tmp = new File("./plugins/BlocklyNukkit/"+name);
        if(tmp.exists()){
            return Utils.readToString("./plugins/BlocklyNukkit/"+name);
        }
        return null;
    }
    //here 8/21
    @Comment(value = "设置NK分支号（用于装13）划掉")
    final public void setNukkitCodeVersion(@Comment(value = "新的NK分支版本号名称") String string){
        fakeNukkitCodeVersion = string;
    }
    //here 8/18
    @Comment(value = "获取玩家游戏设备种类")
    final public String getPlayerDeviceModal(@Comment(value = "玩家对象") Player player){
        return player.getLoginChainData().getDeviceModel();
    }
    @Comment(value = "获取玩家设备唯一识别码")
    final public String getPlayerDeviceID(@Comment(value = "玩家对象") Player player){
        return player.getLoginChainData().getDeviceId();
    }
    @Comment(value = "获取玩家设备的操作系统名称")
    final public String getPlayerDeviceOS(@Comment(value = "玩家对象") Player player){
        int os = player.getLoginChainData().getDeviceOS();
        switch (os) {
            case 1: return "Android";
            case 2: return "iOS";
            case 3: return "macOS";
            case 4: return "FireOS";
            case 5: return "Gear VR";
            case 6: return "Hololens";
            case 7: return "Windows 10";
            case 8: return "Windows";
            case 9: return "Dedicated";
            case 10: return "PS4";
            case 11: case 12: return "Switch";
            case 13: return "Xbox One";
            case 14: return "Windows Phone";
        }
        return "Unknown";
    }
    @Comment(value = "获取一个事件对象可用的所有成员函数的函数名")
    final public String[] getEventFunctions(@Comment(value = "事件对象") Event event){
        List<String> list = new ArrayList<>();
        for(Method method:event.getClass().getMethods()){
            if(fiterMethod(method.getName())){
                list.add(method.getName());
            }
        }
        return list.toArray(new String[list.size()]);
    }
    //here 8/5
    @Comment(value = "获取CPU负载")
    final public double getCPULoad(){
        double load = -1;
        java.lang.management.OperatingSystemMXBean osMxBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        load = osMxBean.getSystemLoadAverage();
        if(load == -1){
            OperatingSystemMXBean osMxBean2 = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            load = osMxBean2.getSystemCpuLoad();
        }
        if(load == -1){
            OperatingSystemMXBean osMxBean3 = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            load = osMxBean3.getProcessCpuLoad();
        }
        if(load == -1){
            OperatingSystemMXBean osMxBean4 = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            load = osMxBean4.getSystemLoadAverage();
        }
        return load;
    }
    @Comment(value = "获取CPU核心数量")
    final public int getCPUCores(){
        OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osMxBean.getAvailableProcessors();
    }
    @Comment(value = "获取总物理内存大小")
    final public double getMemoryTotalSizeMB(){
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return mem.getTotalPhysicalMemorySize()/(1024d*1024d);
    }
    @Comment(value = "获取正在使用的内存大小")
    final public double getMemoryUsedSizeMB(){
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return (mem.getTotalPhysicalMemorySize()-mem.getFreePhysicalMemorySize())/(1024d*1024d);
    }
    @Comment(value = "强制让玩家与服务器断开链接，就像玩家断网了一样")
    final public void forceDisconnect(@Comment(value = "玩家对象") Player player){
        VideoStreamConnectPacket packet = new  VideoStreamConnectPacket();
        packet.address = "8.8.8.8";
        packet.action = VideoStreamConnectPacket.ACTION_OPEN;
        packet.screenshotFrequency =1.0f;
        player.dataPacket(packet);
    }

    //here 8/4
    @Comment(value = "从指定文件名的bn插件中获取指定变量名的变量")
    final public Object getVariableFrom(@Comment(value = "插件文件名") String scriptName
            ,@Comment(value = "变量名") String varName){
        ScriptEngine engine = Loader.engineMap.get(scriptName);
        return engine.get(varName);
    }
    @Comment(value = "将指定文件名的bn插件中的指定变量设为指定值")
    final public void putVariableTo(@Comment(value = "插件文件名") String scriptName
            ,@Comment(value = "变量名") String varName
            ,@Comment(value = "变量对应的值") Object var){
        ScriptEngine engine = Loader.engineMap.get(scriptName);
        engine.put(varName,var);
    }
    //here 6/28
    @Comment(value = "加载指定路径上的jar包作为第三方依赖库")
    final public void loadJar(@Comment(value = "jar包的路径") String path){
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
    @Comment(value = "调用bStats统计服务")
    final public void bStats(@Comment(value = "插件名称") String pluginName
            ,@Comment(value = "版本") String pluginVer
            ,@Comment(value = "作者名") String authorName
            ,@Comment(value = "唯一识别符ID") int pluginid){
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
    //here 10/11

    @Comment(value = "从xyz构建三维向量")
    final public Vector3 buildvec3(@Comment(value = "向量x分量") double x
            ,@Comment(value = "向量y分量") double y
            ,@Comment(value = "向量z分量") double z){
        return new Vector3(x,y,z);
    }

    @Comment(value = "此函数已经废弃")
    @Deprecated
    final public void getServerMotd(@Comment(value = "此函数已经放弃") String host
            ,@Comment(value = "此函数已经放弃") int port
            ,@Comment(value = "此函数已经放弃") String callback)
    {
		new MotdThread(host, port, callback).start();
    }
    
    //是不是神奇的Windows？
    @Comment(value = "是否运行在Windows系统上")
    final public boolean isWindows(){
        return Utils.isWindows();
    }
    //json与yaml互转
    @Comment(value = "JSON字符串转YAML字符串")
    final public String JSONtoYAML(@Comment(value = "要转换的json字符串") String json){
        json = formatJSON(json);
        Config config = new Config(Config.YAML);
        config.setAll((LinkedHashMap)new Gson().fromJson(json, (new TypeToken<LinkedHashMap<String, Object>>() {}).getType()));
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        return yaml.dump(config.getRootSection());
    }
    @Comment(value = "YAML字符串转JSON字符串")
    final public String YAMLtoJSON(@Comment(value = "要转换的YAML字符串") String yaml){
        Config config = new Config(Config.JSON);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yamlObj = new Yaml(dumperOptions);
        config.setAll(yamlObj.loadAs(yaml, LinkedHashMap.class));
        return new GsonBuilder().setPrettyPrinting().create().toJson(config.getRootSection());
    }
    @Comment(value = "格式化JSON字符串（重排版）")
    final public String formatJSON(@Comment(value = "要格式化的json字符串") String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
    //文件读写
    @Comment(value = "以自适应编码读取本地文本文件")
    final public String readFile(@Comment(value = "文本文件的路径") String path){
        File file = new File(path);
        if(file.exists()){
            return Utils.readToString(file);
        }else {
            return "FILE NOT FOUND";
        }
    }
    @Comment(value = "以UTF-8编码写入本地文本文件")
    final public void writeFile(@Comment(value = "文本文件路径") String path
            ,@Comment(value = "要写入的内容") String text){
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
    @Comment(value = "以UTF-8编码追加写入本地文本文件")
    final public void appendFile(@Comment(value = "文本文件路径") String path
            ,@Comment(value = "要追加写入的文本") String text){
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
        Utils.appendWithString(file,text);
    }
    @Comment(value = "比较两个文件是否内容相同")
    final public boolean isFileSame(@Comment(value = "一个文件") String path1
            ,@Comment(value = "另一个文件") String path2){
        return Utils.check(new File(path1),new File(path2));
    }
    //end here
    //跨命名空间调用
    @Comment(value = "调用其他插件或本插件的的函数")
    final public Object callFunction(@Comment(value = "要调用的函数名，格式为 插件名::函数名") String functionname
            ,@Comment(value = "调用参数") Object... args){
        return Loader.plugin.call(functionname, args);
    }
    //http
    @Comment(value = "发起HTTP请求")
    final public String httpRequest(@Comment(value = "方法(GET/POST)") String method
            ,@Comment(value = "请求地址") String url
            ,@Comment(value = "请求数据") String data){
        method=method.toUpperCase();
        if(method.equals("GET")){
            return Utils.sendGet(url,data);
        }else if(method.equals("POST")){
            return Utils.sendPost(url,data);
        }else {
            return "NO SUCH METHOD";
        }
    }
    //email
    @Comment(value = "通过smtp服务器发送电子邮件")
    final public void sendMail(@Comment(value = "smtp服务器地址") String smtpMailServer
            ,@Comment(value = "发件人") String from
            ,@Comment(value = "收件人") String to
            ,@Comment(value = "抄送") String cc
            ,@Comment(value = "隐式抄送") String bcc
            ,@Comment(value = "主题") String subject
            ,@Comment(value = "内容") String content){
        try {
            SmtpMailSender smtpMailSender = new SmtpMailSender(smtpMailServer);
            smtpMailSender.sendMail(from,to.split("[;, ]+"),cc.split("[;, ]+"),bcc.split("[;, ]+"),subject,content,"utf-8");
        } catch (IOException | ProtocolException e) {
            e.printStackTrace();
        }
    }
    //私有回调
    @Deprecated
    @Comment(value = "此函数已经废弃")
    final public void setPrivateCall(@Comment(value = "此函数已经废弃") String event
            ,@Comment(value = "此函数已经废弃") String callname){
        try {
            Class e = Class.forName(event);
            Server.getInstance().getPluginManager().registerEvent(e, Loader.eventLoader, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    plugin.callEventHandler(event,callname);
                }
            },plugin);
        } catch (ClassNotFoundException e) {
            if(Loader.privatecalls.containsKey(event)){
                Loader.privatecalls.get(event).add(callname);
            }else {
                HashSet<String> set = new HashSet<>();
                set.add(callname);
                Loader.privatecalls.put(event,set);
            }
        }
    }

    //云黑检测
    @Comment(value = "通过BlackBE检测玩家是否为熊孩子")
    final public String checkIsBear(@Comment(value = "玩家对象") Player player){
        String response = Utils.sendGetBlackBE("http://blackbe.xyz/api/check","id="+player.getName());
        return response;
    }
    @Comment(value = "通过BlackBE根据玩家名检测玩家是否为熊孩子")
    final public String checkIsBearName(@Comment(value = "玩家名") String player){
        String response = Utils.sendGet("http://47.103.201.235/api/check.php","id="+player);
        return response;
    }

    //获取玩家地理位置
    @Comment(value = "获取玩家的具体地理位置")
    final public String getPlayerArea(@Comment(value = "玩家对象") Player player){
        String response = Utils.sendGet("http://whois.pconline.com.cn/ip.jsp","ip="+player.getAddress().substring(0, player.getAddress().indexOf(":")));
        return response;
    }

    //html占位符
    @Comment(value = "设置快速建站模块的html占位符")
    final public void setHTMLPlaceholder(@Comment(value = "占位符") String key
            ,@Comment(value = "替换为的值") String value){
        Loader.htmlholdermap.put(key, value);
    }

    //踢了玩家
    @Comment(value = "踢出指定玩家")
    final public void kickPlayer(@Comment(value = "玩家对象") Player player
            ,@Comment(value = "踢出原因") String reason){
        player.kick(PlayerKickEvent.Reason.UNKNOWN,reason,false);
    }

    //获取玩家是否op
    @Comment(value = "获取玩家是否为OP")
    final public boolean PlayerIsOP(@Comment(value = "玩家对象") Player player){
        return player.isOp();
    }

    //获取玩家游戏模式
    @Comment(value = "获取玩家的游戏模式")
    final public int getPlayerGameMode(@Comment(value = "玩家对象") Player player){
        return player.getGamemode();
    }
    //简易存储API
    @Comment(value = "向简易存储放入内容")
    final public void putEasy(@Comment(value = "键") String string
            ,@Comment(value = "值") Object object){
        Loader.easytmpmap.put(string, object);
    }
    @Comment(value = "取出简易存储字符串")
    final public String getEasyString(@Comment(value = "键") String string){
        return (String)Loader.easytmpmap.get(string);
    }
    @Comment(value = "取出简易存储数字")
    final public double getEasyNumber(@Comment(value = "键") String string){
        return (Double)Loader.easytmpmap.get(string);
    }
    @Comment(value = "取出简易存储布尔值")
    final public boolean getEasyBoolean(@Comment(value = "键") String string){
        return (Boolean)Loader.easytmpmap.get(string);
    }
    @Comment(value = "取出简易存储坐标")
    final public Position getEasyPosition(@Comment(value = "键") String string){
        return (Position)Loader.easytmpmap.get(string);
    }
    @Comment(value = "取出简易存储玩家")
    final public Player getEasyPlayer(@Comment(value = "键") String string){
        return (Player)Loader.easytmpmap.get(string);
    }
    @Comment(value = "取出简易存储物品")
    final public Item getEasyItem(@Comment(value = "键") String string){
        return (Item)Loader.easytmpmap.get(string);
    }
    @Comment(value = "取出简易存储方块")
    final public Block getEasyBlock(@Comment(value = "键") String string){
        return (Block)Loader.easytmpmap.get(string);
    }
    //configAPI
    @Comment(value = "获取配置文件对象中的所有键")
    final public String[] getAllKeyInConfig(@Comment(value = "配置文件对象") Config config){
        return config.getKeys().toArray(new String[config.getKeys().size()]);
    }
    //金钱API
    @Comment(value = "获取玩家金钱")
    final public double getMoney(@Comment(value = "玩家对象") Player player){
        return EconomyAPI.getInstance().myMoney(player);
    }
    @Comment(value = "减少玩家金钱")
    final public void reduceMoney(@Comment(value = "玩家对象") Player player,@Comment(value = "金钱") double money){
        EconomyAPI.getInstance().reduceMoney(player, money);
    }
    @Comment(value = "增加玩家金钱")
    final public void addMoney(@Comment(value = "玩家对象") Player player,@Comment(value = "金钱") double money){
        EconomyAPI.getInstance().addMoney(player, money);
    }
    @Comment(value = "设置玩家金钱")
    final public void setMoney(@Comment(value = "玩家对象") Player player,@Comment(value = "金钱") double money){
        EconomyAPI.getInstance().setMoney(player, money);
    }
    @Comment(value = "获取玩家金钱")
    final public double getMoney(@Comment(value = "玩家名") String player){
        return EconomyAPI.getInstance().myMoney(player);
    }
    @Comment(value = "减少玩家金钱")
    final public void reduceMoney(@Comment(value = "玩家名") String player,@Comment(value = "金钱") double money){
        EconomyAPI.getInstance().reduceMoney(player, money);
    }
    @Comment(value = "增加玩家金钱")
    final public void addMoney(@Comment(value = "玩家名") String player,@Comment(value = "金钱") double money){
        EconomyAPI.getInstance().addMoney(player, money);
    }
    @Comment(value = "设置玩家金钱")
    final public void setMoney(@Comment(value = "玩家名") String player,@Comment(value = "金钱") double money){
        EconomyAPI.getInstance().setMoney(player, money);
    }

    //4D皮肤API
    @Comment(value = "为指定玩家构建4D皮肤")
    final public void buildskin(@Comment(value = "玩家对象") Player player
            ,@Comment(value = "皮肤名") String skinname){
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
    @Comment(value = "为指定玩家构建4D皮肤并且只能让特定的其他玩家看见")
    final public void buildskinfor(@Comment(value = "玩家对象") Player player
            ,@Comment(value = "皮肤名") String skinname
            ,@Comment(value = "能看见的另一个玩家") Player to){
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

    @Comment(value = "获取本地文件对象")
    final public File getFile(@Comment(value = "文件夹名") String folder
            ,@Comment(value = "文件名") String archive){
        File file = new File(plugin.getDataFolder() + "/" + folder + "/");
        file.mkdir();
        return new File(plugin.getDataFolder() + "/" + folder + "/" + archive);
    }

    @Comment(value = "创建配置文件对象")
    final public Config createConfig(@Comment(value = "配置文件对象对应的文件对象") File file
            ,@Comment(value = "配置文件种类") int type){
        return new Config(file, type);
    }
    //here 2/1
    @Comment(value = "移除指定的命令")
    final public void removeCommand(@Comment(value = "命令主名") String name){
        this.plugin.getServer().getCommandMap().getCommand(name).unregister(this.plugin.getServer().getCommandMap());
    }
    //here 6/11
    @Comment(value = "创建新的权限节点")
    final public void createPermission(@Comment(value = "权限节点名称") String per
            ,@Comment(value = "权限节点描述") String description
            ,@Comment(value = "默认授权，可以为OP/ALL或者NONE") String defaultper){
        if(defaultper.equals("OP")){
            defaultper=Permission.DEFAULT_OP;
        }else if(defaultper.equals("ALL")||defaultper.equals("EVERY")||defaultper.equals("all")||defaultper.equals("every")){
            defaultper=Permission.DEFAULT_TRUE;
        }else if(defaultper.equals("NONE")||defaultper.equals("NO")||defaultper.equals("none")||defaultper.equals("no")){
            defaultper=Permission.DEFAULT_FALSE;
        }
        plugin.getServer().getPluginManager().addPermission(new Permission(per,description,defaultper));
    }

    @Comment(value = "从权限系统中移除指定权限节点")
    final public void removePermission(@Comment(value = "权限节点名称") String per){
        Server.getInstance().getPluginManager().removePermission(per);
    }

    @Comment(value = "检查玩家是否永远指定权限节点")
    final public boolean checkPlayerPermission(String per,Player player){
        return player.hasPermission(per);
    }
    //here 9/19
    @SuppressWarnings("unchecked")
    @Comment(value = "为指定命令添加或覆写命令补全器")
    final public void addCommandCompleter(@Comment(value = "命令主名") String cmd
            ,@Comment(value = "补全器ID，nk默认补全器id为default") String id
            ,@Comment(value = "补全器补全的规则，详见bnwiki#专题#命令补全器") String completer){
        completer += " ";
        List<CommandParameter> commandParameters = new LinkedList<>();
        Pattern pattern = Pattern.compile("[<\\[][a-zA-Z0-9_\\u4e00-\\u9fa5,()\\[\\].:!$@=;]+[>\\]]");
        Matcher matcher = pattern.matcher(completer);
        String[] all = completer.split(" ");
        if(matcher.find()){
            for(int i=0;i<all.length;i++){
                if(all[i].replaceAll(" ","").length()<3)continue;
                if(!pattern.matcher(all[i]).matches())continue;
                boolean optional = all[i].startsWith("[");
                String each = Utils.replaceLast(Utils.replaceLast(all[i].replaceFirst("[<\\[]",""),">",""),"]","");
                String[] token = each.split(":");
                String name = token[0];
                String context,enums;
                if(token[1].contains("=")){
                    context = token[1].split("=")[0];
                    enums = token[1].split("=")[1];
                }else {
                    context = token[1];
                    enums = null;
                }
                CommandParameter current = new CommandParameter(name,optional);
                switch (context) {
                    case "@target":
                        current.type = CommandParamType.TARGET;
                        break;
                    case "@blockpos":
                        current.type = CommandParamType.BLOCK_POSITION;
                        break;
                    case "@pos":
                        current.type = CommandParamType.POSITION;
                        break;
                    case "@int":
                        current.type = CommandParamType.INT;
                        break;
                    case "@float":
                        current.type = CommandParamType.FLOAT;
                        break;
                    case "@string":
                        current.type = CommandParamType.STRING;
                        break;
                    case "@rawtext":
                    case "@text":
                        current.type = CommandParamType.RAWTEXT;
                        break;
                    case "@message":
                        current.type = CommandParamType.MESSAGE;
                        break;
                    case "@command":
                    case "@cmd":
                        current.type = CommandParamType.COMMAND;
                        break;
                    case "@json":
                    case "@JSON":
                        current.type = CommandParamType.JSON;
                        break;
                    case "@filepath":
                    case "@path":
                        current.type = CommandParamType.FILE_PATH;
                        break;
                    case "@operator":
                        current.type = CommandParamType.OPERATOR;
                        break;
                    case "@sub":
                        break;
                }
                if(enums!=null)current.enumData = new CommandEnum(name,Arrays.asList(enums.split(";")));
                commandParameters.add(current);
            }
        }
        Server.getInstance().getCommandMap().getCommand(cmd).addCommandParameters(id,commandParameters.toArray(new CommandParameter[commandParameters.size()]));
    }
    @Comment(value = "创建新的命令")
    final public void createCommand(@Comment(value = "命令主名") String name
            ,@Comment(value = "命令描述") String description
            ,@Comment(value = "命令回调函数")
             @CallbackFunction(classes = {"cn.nukkit.command.CommandSender", "java.lang.String[]"}, parameters = {"sender", "args"}, comments = {"命令发送者", "命令回调参数数组"}) String functionName){
        plugin.getServer().getCommandMap().register(functionName, new EntryCommand(name, description, functionName));
        Loader.plugincmdsmap.put(name,new CommandInfo(name,description,getScriptName()));//debug记录器
    }
    @Comment(value = "创建新的命令")
    final public void createCommand(@Comment(value = "命令主名") String name
            ,@Comment(value = "命令描述") String description
            ,@Comment(value = "命令回调函数")
             @CallbackFunction(classes = {"cn.nukkit.command.CommandSender", "java.lang.String[]"}, parameters = {"sender", "args"}, comments = {"命令发送者", "命令回调参数数组"}) String functionName
            ,@Comment(value = "命令对应的权限节点") String per){
        plugin.getServer().getCommandMap().register(functionName, new EntryCommand(name, description, functionName, per));
        Loader.plugincmdsmap.put(name,new CommandInfo(name,description,getScriptName()));//debug记录器
    }
    //here 5/8
    @Comment(value = "此函数已废弃")
    final public void newCommand(@Comment(value = "此函数已废弃")String name,@Comment(value = "此函数已废弃") String description,@Comment(value = "此函数已废弃") ScriptObjectMirror scriptObjectMirror){
        plugin.getServer().getCommandMap().register(name,new LambdaCommand(name,description,scriptObjectMirror));
        Loader.plugincmdsmap.put(name,new CommandInfo(name,description,getScriptName()));//debug记录器
    }
    @Comment(value = "此函数已废弃")
    final public void newCommand(@Comment(value = "此函数已废弃")String name,@Comment(value = "此函数已废弃") String description,@Comment(value = "此函数已废弃") ScriptObjectMirror scriptObjectMirror,@Comment(value = "此函数已废弃") String per){
        plugin.getServer().getCommandMap().register(name,new LambdaCommand(name,description,scriptObjectMirror,per));
        Loader.plugincmdsmap.put(name,new CommandInfo(name,description,getScriptName()));//debug记录器
    }
    //end here
    @Comment(value = "创建新的延迟任务，返回任务对象")
    final public TaskHandler createTask(@Comment(value = "任务回调函数名") @CallbackFunction String functionName
            ,@Comment(value = "任务延迟(刻)") int delay
            ,@Comment(value = "回调函数调用参数") Object... args){
        TaskHandler handler = plugin.getServer().getScheduler().scheduleDelayedTask(Loader.plugin,new ModTask(functionName,args), delay);
        List<Integer> tmp = plugin.pluginTasksMap.get(getScriptName());
        if(tmp==null){
            tmp = new ArrayList<>();tmp.add(handler.getTaskId());
            plugin.pluginTasksMap.put(getScriptName(),tmp);
        }else {
            tmp.add(handler.getTaskId());
        }
        return handler;
    }
    //here 5/9
    @Comment(value = "此函数已废弃")
    final public int setTimeout(@Comment(value = "此函数已废弃") ScriptObjectMirror scriptObjectMirror,@Comment(value = "此函数已废弃") int delay,@Comment(value = "此函数已废弃") Object... args){
        if(scriptObjectMirror.isFunction()||scriptObjectMirror.isStrictFunction()){
            int id = plugin.getServer().getScheduler().scheduleDelayedTask(Loader.plugin,new LambdaTask(scriptObjectMirror,args),delay).getTaskId();
            List<Integer> tmp = plugin.pluginTasksMap.get(getScriptName());
            if(tmp==null){
                tmp = new ArrayList<>();tmp.add(id);
                plugin.pluginTasksMap.put(getScriptName(),tmp);
            }else {
                tmp.add(id);
            }
            return id;
        }else {
            return __setTimeoutInner(scriptObjectMirror.toString(),delay, args);
        }
    }
    @Comment(value = "此函数不对外暴露")
    final public int __setTimeoutInner(String callback,int delay,Object... args){
        return createTask(callback, delay, args).getTaskId();
    }
    @Comment(value = "此函数已废弃")
    final public void clearTimeout(@Comment(value = "此函数已废弃") int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }
    //end here
    @Comment(value = "创建新的定时循环任务，返回任务对象")
    final public TaskHandler createLoopTask(@Comment(value = "定时循环任务名") @CallbackFunction String functionName
            ,@Comment(value = "间隔(刻)") int delay
            ,@Comment(value = "回调函数调用参数") Object... args){
        TaskHandler handler = plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(Loader.plugin,new ModTask(functionName,args), 20, delay);
        List<Integer> tmp = plugin.pluginTasksMap.get(getScriptName());
        if(tmp==null){
            tmp = new ArrayList<>();tmp.add(handler.getTaskId());
            plugin.pluginTasksMap.put(getScriptName(),tmp);
        }else {
            tmp.add(handler.getTaskId());
        }
        return handler;
    }
    //here 5/9
    @Comment(value = "此函数已废弃")
    final public int setInterval(@Comment(value = "此函数已废弃") ScriptObjectMirror scriptObjectMirror,@Comment(value = "此函数已废弃") int delay,@Comment(value = "此函数已废弃") Object... args){
        int id = plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(Loader.plugin,new LambdaTask(scriptObjectMirror,args),delay,delay).getTaskId();
        List<Integer> tmp = plugin.pluginTasksMap.get(getScriptName());
        if(tmp==null){
            tmp = new ArrayList<>();tmp.add(id);
            plugin.pluginTasksMap.put(getScriptName(),tmp);
        }else {
            tmp.add(id);
        }
        return id;
    }
    @Comment(value = "此函数不对外暴露")
    final public int __setIntervalInner(String callback,int delay,Object... args){
        return createLoopTask(callback, delay, args).getTaskId();
    }
    @Comment(value = "此函数已废弃")
    final public void clearInterval(@Comment(value = "此函数已废弃") int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }
    //end here
    @Comment(value = "获取任务对象的任务id")
    final public int getTaskId(@Comment(value = "任务对象") TaskHandler handler){
        return handler.getTaskId();
    }
    @Comment(value = "根据任务id取消运行或等待中的任务")
    final public void cancelTask(@Comment(value = "任务对象的id") int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }
    @Comment(value = "根据插件名获取其他的java插件对象")
    final public Plugin getPlugin(@Comment(value = "其他java插件的注册名") String name){
        return plugin.getServer().getPluginManager().getPlugin(name);
    }
    @Comment(value = "字符串MD5加密，返回密文")
    final public String MD5Encryption(@Comment(value = "要加密的字符串") String str){
        return Utils.StringEncryptor(str,"MD5");
    }
    @Comment(value = "字符串SHA1加密，返回密文")
    final public String SHA1Encryption(@Comment(value = "要加密的字符串") String str){
        return Utils.StringEncryptor(str,"SHA1");
    }
    @Comment(value = "格式化时间，返回格式化后的时间字符串")
    final public String time(@Comment(value = "秒数") int seconds){
        int ss = seconds % 60;
        seconds /= 60;
        int min = seconds % 60;
        seconds /= 60;
        int hours = seconds % 24;
        return strzero(hours) + ":" + strzero(min) + ":" + strzero(ss);
    }
    @Comment(value = "字符串格式化")
    final public String format(@Comment(value = "格式化模板") String msg
            ,@Comment(value = "格式化参数run") Object... args){
        return String.format(msg, args);
    }

    @Comment(value = "是否运行在PowerNukkit上")
    final public boolean isPowerNukkit(){
        try {
            Field codename = Nukkit.class.getDeclaredField("CODENAME");
            return codename.get(Nukkit.class).equals("PowerNukkit");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Comment(value = "并行运行函数")
    final public void concurrentRun(@Comment(value = "函数名") @CallbackFunction String functionName,@Comment(value = "参数") Object... args){
        CompletableFuture.runAsync(() -> Loader.plugin.call(getScriptName()+"::"+functionName,args), mainExecutor);
    }

    private String strzero(int time){
        if(time < 10)
            return "0" + time;
        return String.valueOf(time);
    }

    public Loader plugin(){
        return plugin;
    }

    public interface stopAbleCommand{
        void stop();
    }

    public class EntryCommand extends Command implements stopAbleCommand{

        private String functionName;
        private boolean stop = false;

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
            if(!stop)
            plugin.callCommand(this.getName(),sender, args, functionName);
            return false;
        }

        @Override
        public void stop() {
            this.stop=true;
        }
    }

    public class LambdaCommand extends Command implements stopAbleCommand{

        private ScriptObjectMirror callback;
        private boolean stop = false;

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
            callback.call(callback,sender,args);
            return false;
        }

        @Override
        public void stop() {
            this.stop=true;
        }
    }
    
    public class MotdThread extends Thread
    {
        private String callback;
        private String host;
        private int port;
        
        final private  byte[] motdData = new byte[]{1, 0, 0, 0, 0, 0, 3, 106, 7, 0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120, -100, 116, 22, -68};
        
        public MotdThread(String host, int port, String callback)
        {
             this.callback = callback;
             this.host = host;
             this.port = port;
        }
        
        public void run()
        {
		     DatagramSocket socket = null;
	         try
		     {
			       socket = new DatagramSocket();
			       socket.setSoTimeout(5000);
			       DatagramPacket packet = new DatagramPacket(Arrays.copyOf(this.motdData, 1024), 1024, InetAddress.getByName(this.host), this.port);
			       socket.send(packet);
			       socket.receive(packet);

		           plugin.call(this.callback, new String(packet.getData(), 35, packet.getLength()).split(";"));
		     }
	    	 catch (Throwable e)
	         {
	    		   plugin.call(this.callback, false, e);
	    	 }
	    	 finally
	    	 {
	     		   if (socket != null)
	    		   {
	    			    socket.close();
	    		   }
             }
        }
    }

    public class ModTask extends Task{

        private String functionName;
        public Object Args[];

        public ModTask(String functionName,Object[] args){
            this.functionName = functionName;
            this.Args = args;
        }

        @Override
        public void onRun(int i) {
            if(Args.length==0||Args==null){
                plugin.call(functionName,i);
            }else {
                plugin.call(functionName,Args);
            }
        }
    }

    public class LambdaTask extends Task{

        private ScriptObjectMirror callback;
        public Object Args[];

        public LambdaTask(ScriptObjectMirror scriptObjectMirror,Object[] args){
            this.callback = scriptObjectMirror;
            this.Args = args;
        }

        @Override
        public void onRun(int i) {
            callback.call(callback,Args);
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