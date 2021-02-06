package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.scriptloader.bases.Interpreter;
import com.google.gson.GsonBuilder;
import javassist.*;
import jdk.nashorn.api.scripting.*;

import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.blocklynukkit.loader.Loader.*;

public class JavaScriptLoader extends ExtendScriptLoader implements Interpreter {
    public String polyfilljs = null;
    public JavaScriptLoader(Loader plugin){
        super(plugin);
    }
    public void loadplugins(){
        //加载js
        for (File file : Objects.requireNonNull(new File("./plugins/BlocklyNukkit").listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().endsWith(".js")&&!file.getName().contains("bak")){
                String js = Utils.readToString(file);
                List<String> pragmas= getPragma(js);
                if(pragmas.contains("pragma autoload false")){
                    return;
                }
                if (Server.getInstance().getLanguage().getName().contains("中文"))
                    getlogger().warning("加载BN插件: " + file.getName());
                else
                    getlogger().warning("loading BN plugin: " + file.getName());
                putJavaScriptEngine(file.getName(),js);
            }else if(file.getName().endsWith(".py")&&!file.getName().contains("bak") && !plugins.containsKey("PyBN")){
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning("无法加载:" + file.getName()+"! 缺少python依赖库");
                    getlogger().warning("请到 https://tools.blocklynukkit.com/PyBN.jar下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + file.getName()+" python libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PyBN.jar");
                }
            }else if(file.getName().endsWith(".php")&&!file.getName().contains("bak") && !plugins.containsKey("PHPBN")){
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning("无法加载:" + file.getName()+"! 缺少PHP依赖库");
                    getlogger().warning("请到 https://tools.blocklynukkit.com/PHPBN.jar下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + file.getName()+" PHP libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PHPBN.jar");
                }
            }else if(file.getName().endsWith(".wasm")&&!file.getName().contains("bak") && !plugins.containsKey("WebassemblyBN")){
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    getlogger().warning("无法加载:" + file.getName()+"! 缺少Webassembly依赖库");
                    getlogger().warning("请到 https://tools.blocklynukkit.com/Webassembly.jar下载依赖插件");
                }
                else{
                    getlogger().warning("cannot load BN plugin:" + file.getName()+" webassembly libs not found!");
                    getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/WebassemblyBN.jar");
                }
            }
        }
    }
    @Override
    public void putEngine(String name,String code){
        this.putJavaScriptEngine(name, code);
    }
    public void putJavaScriptEngine(String name,String js){
        js = formatExportJavaSript(name,js);
        engineMap.put(name,new NashornScriptEngineFactory().getScriptEngine());
        if (engineMap.get(name) == null) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript引擎加载出错！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript interpreter crashed!");
            return;
        }
        if (!(engineMap.get(name) instanceof Invocable)) {
            if (Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript引擎版本过低！");
            if (!Server.getInstance().getLanguage().getName().contains("中文"))
                getlogger().error("JavaScript interpreter's version is too low!");
            return;
        }
        try {
            if(js.contains("//pragma es9")||js.contains("//pragma es8")||js.contains("//pragma es7")||js.contains("//pragma es6")||js.contains("//pragma es2015")||
                    js.contains("// pragma es9")||js.contains("// pragma es8")||js.contains("// pragma es7")||js.contains("// pragma es6")||js.contains("// pragma es2015")||
                    js.contains("//pragma polyfill")||js.contains("//pragma babel")||js.contains("//pragma Polyfill")||js.contains("//pragma Babel")||
                    js.contains("// pragma polyfill")||js.contains("// pragma babel")||js.contains("// pragma Polyfill")||js.contains("// pragma Babel")){
                engineMap.get(name).put("javax.script.filename","babel-polyfill");
                engineMap.get(name).eval(getPolyfilljs());
            }
            engineMap.get(name).put("lambdaCount",-1);
            engineMap.get(name).put("baseInterpreterBNJavaScriptEngine",engineMap.get(name));
            ((NashornScriptEngine)engineMap.get(name)).compile("function F(f){lambdaCount++;baseInterpreterBNJavaScriptEngine.put('Lambda_"+Utils.getMD5(name.getBytes())+"_'+lambdaCount,f);return 'Lambda_"+Utils.getMD5(name.getBytes())+"_'+lambdaCount;}").eval();
            putBaseObject(name);
            engineMap.get(name).put("javax.script.filename",name);
            engineMap.get(name).put("console",engineMap.get(name).get("logger"));
            engineMap.get(name).eval(js);
        } catch (ScriptException e) {
            previousException = e;
            if (Server.getInstance().getLanguage().getName().contains("中文")){
                Loader.getlogger().warning("在初始化\""+name+"\"时");
                Loader.getlogger().warning("在第"+e.getLineNumber()+"行第"+e.getColumnNumber()+"列发生错误:");
                Loader.getlogger().warning(e.getMessage());
                Loader.getlogger().warning("使用命令showstacktrace来查看错误堆栈信息");
            }else {
                Loader.getlogger().warning("In initialization of \""+name+"\"");
                Loader.getlogger().warning("at line "+e.getLineNumber()+" column "+e.getColumnNumber()+" occurred an error:");
                Loader.getlogger().warning(e.getMessage());
                Loader.getlogger().warning("use command showstacktrace to see the stacktrace information");
            }
        }
        bnpluginset.add(name);
    }
    public String formatExportJavaSript(String name,String code){
        String[] lines = code.split("\n");
        String output = "";String tmp;
        Map<String,String[]> exportFunctions = new HashMap<>();
        for(String line:lines){
            tmp = line.trim();
            if(tmp.startsWith("export function")){
                output+=(line.replaceFirst("export ","")+"\n");
                tmp = tmp.replaceFirst("export function ","");
                String funName = tmp.split("\\(")[0].trim();
                String[] funArgs = tmp.split("\\(")[1].split("\\)")[0]
                        .trim().split(",");
                for(int i=0;i<funArgs.length;i++){
                    funArgs[i]=funArgs[i].trim();
                }
                exportFunctions.put(funName,funArgs);
            }else {
                output+=(line+"\n");
            }
        }
        Pattern pattern = Pattern.compile("^[A-Za-z_$]+[A-Za-z_$.\\d]+$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.matches()){
            CtClass bn = JavaExporter.makeExportJava(name.endsWith(".js")?name:(name+".js"),exportFunctions);
            if(bn!=null) bnClasses.put(name,bn);
        }
        return output;
    }
    @Override
    public String toString(Object var){
        if(var instanceof ScriptObjectMirror){
            ScriptObjectMirror js = (ScriptObjectMirror)var;
            if(js.isArray()){
                String out = "[";
                for(int i=0;i<js.size();i++){
                    out += (","+js.getSlot(i).toString());
                }
                return out.replaceFirst(",","")+"]";
            }else if(js.toString().startsWith("[object ")){
                Map<String,String> out = new HashMap<>();
                for(String each:js.getOwnKeys(true)){
                    out.put(each,js.getMember(each).toString());
                }
                return new GsonBuilder().setPrettyPrinting().create().toJson(out);
            }else {
                return js.toString();
            }
        }else if(var.toString().equals(getClass().getName() + "@" + Integer.toHexString(hashCode()))){
            return new GsonBuilder().setPrettyPrinting().create().toJson(var);
        }else {
            return var.toString();
        }
    }
    @Override
    public boolean isThisLanguage(Object var){
        return var instanceof JSObject;
    }

    public String getPolyfilljs(){
        if(polyfilljs==null){
            polyfilljs="";
            InputStream is2=this.getClass().getResourceAsStream("/polyfill.js");
            BufferedReader br2=new BufferedReader(new InputStreamReader(is2));
            String s2="";
            try{
                while((s2=br2.readLine())!=null)polyfilljs+=s2;
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return polyfilljs;
    }

    @Override
    public List<String> getPragma(String code){
        List<String> pragma = new ArrayList<>();
        String[] lines = code.split("\n");
        for(String line:lines){
            if(line.trim().startsWith("//")){
                String toCheck = line.replaceFirst("//","").trim();
                if(toCheck.startsWith("pragma")){
                    toCheck = toCheck.replaceAll(" +","");
                    if(toCheck.startsWith("pragma end")){
                        break;
                    }
                    pragma.add(toCheck.toLowerCase());
                }
            }
        }
        return pragma;
    }
}
