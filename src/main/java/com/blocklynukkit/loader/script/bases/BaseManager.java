package com.blocklynukkit.loader.script.bases;

import com.blocklynukkit.loader.utils.Utils;
import com.sun.istack.internal.Nullable;

import javax.script.ScriptEngine;

public class BaseManager {
    @Nullable
    public ScriptEngine scriptEngine;
    public BaseManager(ScriptEngine scriptEngine){
        this.scriptEngine=scriptEngine;
    }
    public String getScriptName(){
        if(scriptEngine.getClass().getSimpleName().equals("BNPHPScriptEngine")){
            try {
                return (String) Utils.getPrivateField(scriptEngine,"engineName");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (String) scriptEngine.get("javax.script.filename");
    }
}
