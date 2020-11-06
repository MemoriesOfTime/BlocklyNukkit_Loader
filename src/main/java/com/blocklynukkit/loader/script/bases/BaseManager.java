package com.blocklynukkit.loader.script.bases;

import com.sun.istack.internal.Nullable;

import javax.script.ScriptEngine;

public class BaseManager {
    @Nullable
    public ScriptEngine scriptEngine;
    public BaseManager(ScriptEngine scriptEngine){
        this.scriptEngine=scriptEngine;
    }
}
