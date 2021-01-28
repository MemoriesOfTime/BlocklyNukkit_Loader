package com.blocklynukkit.loader.other.data;

import com.blocklynukkit.loader.utils.Utils;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStorage<K,V> extends ConcurrentHashMap<K,V> {
    public void setItem(K key,V item){
        this.put(key, item);
    }
    public V getItem(K key){
        return this.get(key);
    }
    public void removeItem(K key){
        this.remove(key);
    }
    public Object[] getKeys(){
        return this.keySet().toArray();
    }
}
