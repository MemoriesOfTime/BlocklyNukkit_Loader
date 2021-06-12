package com.blocklynukkit.loader.other.data;

import com.blocklynukkit.loader.api.Comment;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryStorage<K,V> extends ConcurrentHashMap<K,V> {
    @Comment(value = "设置键值对")
    public void setItem(@Comment(value = "键") K key,@Comment(value = "值") V item){
        this.put(key, item);
    }
    @Comment(value = "获取键值对")
    public V getItem(@Comment(value = "键") K key){
        return this.get(key);
    }
    @Comment(value = "移除键值对")
    public void removeItem(@Comment(value = "键") K key){
        this.remove(key);
    }
    @Comment(value = "获取所有键")
    public Object[] getKeys(){
        return this.keySet().toArray();
    }
}
