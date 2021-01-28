package com.blocklynukkit.loader.other.data;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
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
    private void initKeys(){
        for(String each: Objects.requireNonNull(dir.list())){
            storage.put(each,null);
        }
    }
    public void cacheAll(){
        for(String each: Objects.requireNonNull(dir.list())){
            cache(each);
        }
    }
    public String cache(String key){
        String re = Utils.readToString(savePath+"/"+key);
        storage.put(key, re);
        return re;
    }
    public void setItem(String key,String item){
        storage.put(key, item);
        changes.add(key);
    }
    public String getItem(String key){
        String tmp = storage.get(key);
        String re = null;
        if(tmp==null && storage.containsKey(key)){
            try{
                re = cache(key);
            }catch (Exception e){
                // ignore
            }
        }
        return re;
    }
    public void removeItem(String key){
        changes.add(key);
        storage.remove(key);
    }
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
    public String[] getKeys(){
        return storage.keySet().toArray(new String[0]);
    }
}
