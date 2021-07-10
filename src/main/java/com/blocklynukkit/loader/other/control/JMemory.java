package com.blocklynukkit.loader.other.control;

import com.blocklynukkit.loader.api.Comment;

public class JMemory {
    @Comment(value = "获取JVM最大可用内存大小")
    public long getMax(){
        return Runtime.getRuntime().maxMemory();
    }

    @Comment(value = "获取JVM剩余内存大小")
    public long getFree(){
        return Runtime.getRuntime().freeMemory();
    }

    @Comment(value = "获取JVM总内存大小")
    public long getTotal(){
        return Runtime.getRuntime().totalMemory();
    }

    @Comment(value = "进行内存清理")
    public void gc(){
        Runtime.getRuntime().gc();
    }
}
