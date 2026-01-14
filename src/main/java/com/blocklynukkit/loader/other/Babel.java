package com.blocklynukkit.loader.other;

import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.utils.Utils;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Babel {
    public ScriptEngine babelRuntime;
    public Babel(){
        babelRuntime = new NashornScriptEngineFactory().getScriptEngine();
        try {
            InputStream is=this.getClass().getResourceAsStream("/babel.min.js");
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String s="";String babeljs="";
            while((s=br.readLine())!=null)babeljs+=s;
            babelRuntime.eval(babeljs);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String transform(String escode){
        babelRuntime.put("BNinputESCode",escode);
        try {
            return (String)babelRuntime.eval("Babel.transform(BNinputESCode,{ presets: ['es2015'] }).code");
        } catch (ScriptException e) {
           Loader.getlogger().error(TextFormat.RED+"Failed to transform!");
           e.printStackTrace();
        }
        return escode;
    }
}
