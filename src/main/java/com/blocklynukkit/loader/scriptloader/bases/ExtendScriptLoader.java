package com.blocklynukkit.loader.scriptloader.bases;

import com.blocklynukkit.loader.Loader;

public abstract class ExtendScriptLoader {
    public Loader plugin;
    public ExtendScriptLoader(Loader loader){
        plugin=loader;
    }
}
