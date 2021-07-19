package com.blocklynukkit.loader.other;

import java.io.File;

public abstract class AbstractTiming {
    private static boolean debug = false;
    static {
        debug = new File("./debug.inf").exists();
    }
    public abstract long start();
    public abstract long end(long id);
    public abstract void finish(long id);
    public abstract void finish(String info);
    public static AbstractTiming get(){
        return debug ? new Timing() : new FakeTiming();
    }
}
