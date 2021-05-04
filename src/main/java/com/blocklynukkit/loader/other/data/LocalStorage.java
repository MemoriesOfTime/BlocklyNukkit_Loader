package com.blocklynukkit.loader.other.data;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import com.blocklynukkit.loader.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.FunctionManager;
import com.blocklynukkit.loader.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.blocklynukkit.loader.Loader.plugin;

public class LocalStorage {
    File dir;
    String pluginName = null;
    Map<String,String> storage = new HashMap<>();
    Set<String> changes = new HashSet<>();
    public String savePath;
    public LocalStorage self = this;
    @Comment(value = "构造函数")
    public LocalStorage(File dir,String pluginName){
        this.dir = dir;
        this.savePath = dir.getPath();
        this.pluginName = pluginName;
        if(!dir.exists())dir.mkdirs();
        TaskHandler handler = Server.getInstance().getScheduler().scheduleDelayedTask(plugin, () -> self.save(),60);
        List<Integer> tmp = Loader.pluginTasksMap.get(pluginName);
        if(tmp==null){
            tmp = new ArrayList<>();tmp.add(handler.getTaskId());
            Loader.pluginTasksMap.put(pluginName,tmp);
        }else {
            tmp.add(handler.getTaskId());
        }
        initKeys();
    }
    @Comment(value = "初始化所有的键")
    private void initKeys(){
        for(String each: Objects.requireNonNull(dir.list())){
            storage.put(each,null);
        }
    }
    @Comment(value = "将所有的键缓存起来")
    public void cacheAll(){
        for(String each: Objects.requireNonNull(dir.list())){
            cache(each);
        }
    }
    @Comment(value = "缓存指定的键")
    public String cache(@Comment(value = "键") String key){
        String re = Utils.readToString(savePath+"/"+key);
        storage.put(key, re);
        return re;
    }
    @Comment(value = "设置键值对")
    public void setItem(@Comment(value = "键") String key,@Comment(value = "值") String item){
        storage.put(key, item);
        changes.add(key);
    }
    @Comment(value = "获取键对应的值")
    public String getItem(@Comment(value = "键") String key){
        String tmp = storage.get(key);
        String re = null;
        if(tmp==null && !storage.containsKey(key)){
            try{
                re = cache(key);
            }catch (Exception e){
                // ignore
            }
        }else {
            re = tmp;
        }
        return re;
    }
    @Comment(value = "移除键值对")
    public void removeItem(@Comment(value = "键") String key){
        changes.add(key);
        storage.remove(key);
    }
    @Comment(value = "强制保存所有键值对到硬盘，bn本身会每3秒自动保存")
    public void save(){
        for(String each:changes){
            if(storage.get(each)==null){
                new File(savePath+"/"+each).delete();
            }else {
                Utils.writeWithString(new File(savePath+"/"+each),storage.get(each));
            }
        }
        changes.clear();
    }
    @Comment(value = "获取所有的键")
    public String[] getKeys(){
        return storage.keySet().toArray(new String[0]);
    }
}
