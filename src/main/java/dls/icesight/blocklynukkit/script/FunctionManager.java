package dls.icesight.blocklynukkit.script;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import dls.icesight.blocklynukkit.Loader;
import dls.icesight.blocklynukkit.Utils;
import dls.icesight.blocklynukkit.other.Clothes;
import jdk.nashorn.internal.ir.Block;
import me.onebone.economyapi.EconomyAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FunctionManager {

    private Loader plugin;

    public FunctionManager(Loader plugin){
        this.plugin = plugin;
    }

    public Vector3 buildvec3(double x,double y,double z){
        return new Vector3(x,y,z);
    }

    //云黑检测
    public boolean checkIsBear(Player player){
        String response = Utils.sendGet("http://blackbe-api.bugmc.com:2222/BlackBE/check.php","id="+player.getName());
        return !response.equals("0");
    }

    //获取玩家地理位置
    public String getPlayerArea(Player player){
        String response = Utils.sendGet("http://whois.pconline.com.cn/ip.jsp","ip="+player.getAddress().substring(0, player.getAddress().indexOf(":")));
        return response.split(" ")[0];
    }

    //html占位符
    public void setHTMLPlaceholder(String key,String value){
        Loader.htmlholdermap.put(key, value);
    }

    //踢了玩家
    public void kickPlayer(Player player,String reason){
        player.kick(reason);
    }

    //获取玩家是否op
    public boolean PlayerIsOP(Player player){
        return player.isOp();
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

    public void createCommand(String name, String description, String functionName){
        plugin.getServer().getCommandMap().register(functionName, new EntryCommand(name, description, functionName));
    }

    public TaskHandler createTask(String functionName, int delay){
        return plugin.getServer().getScheduler().scheduleDelayedTask(new ModTask(functionName), delay);
    }

    public TaskHandler createLoopTask(String functionName, int delay){
        return plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(new ModTask(functionName), 20, delay);
    }

    public void cancelTask(int id){
        plugin.getServer().getScheduler().cancelTask(id);
    }

    public Plugin getPlugin(String name){
        return plugin.getServer().getPluginManager().getPlugin(name);
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

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            plugin.callCommand(sender, args, functionName);
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

}