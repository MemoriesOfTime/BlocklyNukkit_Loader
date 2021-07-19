package com.blocklynukkit.loader.other;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

public final class Timing extends AbstractTiming{
    private final Long2LongOpenHashMap ids = new Long2LongOpenHashMap();
    private long index = 0;
    public long start(){
        ids.put(index++, System.nanoTime());
        return index;
    }
    public long end(long id){
        return System.nanoTime() - ids.remove(id);
    }
    public void finish(long id){
        System.out.println((System.nanoTime() - ids.remove(id))/1000000d + "ms");
    }
    public void finish(String info){
        System.out.println(info + (System.nanoTime() - ids.remove(index - 1))/1000000d + "ms");
    }
}
