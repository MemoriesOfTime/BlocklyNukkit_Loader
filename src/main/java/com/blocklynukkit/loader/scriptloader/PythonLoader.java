package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.scriptloader.scriptengines.BNPyScriptEngine;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.scriptloader.bases.Interpreter;
import com.google.gson.GsonBuilder;
import javassist.CtClass;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.jsr223.PyScriptEngineFactory;
import org.python.util.PythonInterpreter;

import javax.script.Invocable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.blocklynukkit.loader.Loader.bnClasses;

public class PythonLoader extends ExtendScriptLoader implements Interpreter {
    public PythonLoader(Loader plugin){
        super(plugin);
    }
    public void loadplugins(){
        if(plugin.plugins.containsKey("PyBN")){
            try{
                for (File file : Objects.requireNonNull(new File("./plugins/BlocklyNukkit").listFiles())) {
                    if(file.isDirectory()) continue;
                    if(file.getName().endsWith(".py")&&!file.getName().contains("bak")){
                        try (final InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"UTF-8")) {
                            if (Server.getInstance().getLanguage().getName().contains("中文"))
                                plugin.getLogger().warning("加载BN插件: " + file.getName());
                            else
                                plugin.getLogger().warning("loading BN plugin: " + file.getName());
//                            plugin.engineMap.put(file.getName(),new PyScriptEngineFactory().getScriptEngine());
//                            if (plugin.engineMap.get(file.getName()) == null) {
//                                if (Server.getInstance().getLanguage().getName().contains("中文"))
//                                    plugin.getLogger().error("Python引擎加载出错！");
//                                if (!Server.getInstance().getLanguage().getName().contains("中文"))
//                                    plugin.getLogger().error("Python interpreter crashed!");
//                                return;
//                            }
//                            if (!(plugin.engineMap.get(file.getName()) instanceof Invocable)) {
//                                if (Server.getInstance().getLanguage().getName().contains("中文"))
//                                    plugin.getLogger().error("Python引擎版本过低！");
//                                if (!Server.getInstance().getLanguage().getName().contains("中文"))
//                                    plugin.getLogger().error("Python interpreter's version is too low!");
//                                return;
//                            }
                            putPythonEngine(file.getName(),Utils.readToString(file));
//                            plugin.putBaseObject(file.getName());
//                            PythonInterpreter ip = (PythonInterpreter) Utils.getPrivateField(plugin.engineMap.get(file.getName()),"interp");
//                            ip.setIn(System.in);
//                            ip.setOut(System.out);
//                            ip.setErr(System.out);
//                            ip.execfile(new FileInputStream(file));
//                            plugin.bnpluginset.add(file.getName());
                        } catch (final Exception e) {
                            if (Server.getInstance().getLanguage().getName().contains("中文")){
                                plugin.getLogger().error("无法加载： " + file.getName(), e);}
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
    @Override
    public void putEngine(String name,String code){
        this.putPythonEngine(name, code);
    }

    public void putPythonEngine(String name,String py){
        py = formatExportPython(name, py);
        plugin.engineMap.put(name,new BNPyScriptEngine(new PyScriptEngineFactory()));
        BNPyScriptEngine engine = (BNPyScriptEngine) plugin.engineMap.get(name);
        if (engine == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python interpreter crashed!");
            return;
        }
        if (!(engine instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                plugin.getlogger().error("Python interpreter's version is too low!");
            return;
        }
        plugin.putBaseObject(name);
        PythonInterpreter ip = engine.interp;
        ip.setIn(System.in);
        ip.setOut(System.out);
        ip.setErr(System.out);
        ip.exec(Py.newStringUTF8(py));
        plugin.bnpluginset.add(name);
    }

    public String formatExportPython(String name,String code){
        String[] lines = code.split("\n");
        String output = "";String tmp;
        Map<String,String[]> exportFunctions = new HashMap<>();
        boolean willExport = false;
        for(String line:lines){
            tmp = line.trim();
            if(willExport){
                willExport=false;
                output+=(line+"\n");
                tmp = tmp.replaceFirst("def ","");
                String funName = tmp.split("\\(")[0].trim();
                String[] funArgs = tmp.split("\\(")[1].split("\\)")[0]
                        .trim().split(",");
                for(int i=0;i<funArgs.length;i++){
                    funArgs[i]=funArgs[i].trim();
                }
                exportFunctions.put(funName,funArgs);
            }else if(tmp.startsWith("@export")||tmp.startsWith("@Export")){
                output+=("\n");
                willExport=true;
            }else {
                output+=(line+"\n");
            }
        }
        Pattern pattern = Pattern.compile("^[A-Za-z_$]+[A-Za-z_$.\\d]+$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.matches()){
            CtClass bn = JavaExporter.makeExportJava(name.endsWith(".py")?name:(name+".py"),exportFunctions);
            if(bn!=null) bnClasses.put(name,bn);
        }
        super.toString();
        return output;
    }
    @Override
    public String toString(Object var){
        if(var.toString().equals(getClass().getName() + "@" + Integer.toHexString(hashCode()))){
            return new GsonBuilder().setPrettyPrinting().create().toJson(var);
        }else {
            return var.toString();
        }
    }
    @Override
    public boolean isThisLanguage(Object var){
        return var instanceof PyObject;
    }

}
