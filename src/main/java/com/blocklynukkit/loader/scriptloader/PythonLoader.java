package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.sun.istack.internal.NotNull;
import org.python.core.Py;
import org.python.jsr223.PyScriptEngineFactory;
import org.python.util.PythonInterpreter;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class PythonLoader {
    public Loader plugin;
    public PythonLoader(@NotNull Loader plugin){
        this.plugin=plugin;
    }
    public void loadplugins(){
        if(plugin.plugins.containsKey("PyBN")){
            try{
                for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
                    if(file.isDirectory()) continue;
                    if(file.getName().endsWith(".py")&&!file.getName().contains("bak")){
                        try (final InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                            if (Server.getInstance().getLanguage().getName().contains("中文"))
                                plugin.getLogger().warning("加载BN插件: " + file.getName());
                            else
                                plugin.getLogger().warning("loading BN plugin: " + file.getName());
                            plugin.engineMap.put(file.getName(),new PyScriptEngineFactory().getScriptEngine());
                            if (plugin.engineMap.get(file.getName()) == null) {
                                if (Server.getInstance().getLanguage().getName().contains("中文"))
                                    plugin.getLogger().error("Python引擎加载出错！");
                                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                                    plugin.getLogger().error("Python interpreter crashed!");
                                return;
                            }
                            if (!(plugin.engineMap.get(file.getName()) instanceof Invocable)) {
                                if (Server.getInstance().getLanguage().getName().contains("中文"))
                                    plugin.getLogger().error("Python引擎版本过低！");
                                if (!Server.getInstance().getLanguage().getName().contains("中文"))
                                    plugin.getLogger().error("Python interpreter's version is too low!");
                                return;
                            }
                            plugin.putBaseObject(file.getName());
                            PythonInterpreter ip = (PythonInterpreter) Utils.getPrivateField(plugin.engineMap.get(file.getName()),"interp");
                            ip.setIn(System.in);
                            ip.setOut(System.out);
                            ip.setErr(System.out);
                            ip.execfile(new FileInputStream(file));

                            plugin.bnpluginset.add(file.getName());
                        } catch (final Exception e) {
                            if (Server.getInstance().getLanguage().getName().contains("中文"))
                                plugin.getLogger().error("无法加载： " + file.getName(), e);
                            else
                                plugin.getLogger().error("cannot load:" + file.getName(), e);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void putPythonEngine(String name,String py){
        plugin.engineMap.put(name,new PyScriptEngineFactory().getScriptEngine());
        if (plugin.engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python interpreter crashed!");
            return;
        }
        if (!(plugin.engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python interpreter's version is too low!");
            return;
        }
        plugin.putBaseObject(name);
        PythonInterpreter ip = null;
        try {
            ip = (PythonInterpreter) Utils.getPrivateField(plugin.engineMap.get(name),"interp");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ip.setIn(System.in);
        ip.setOut(System.out);
        ip.setErr(System.out);
        ip.exec(Py.newStringUTF8(py));
        plugin.bnpluginset.add(name);
    }
}
