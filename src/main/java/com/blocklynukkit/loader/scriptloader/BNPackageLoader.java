package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.blocklynukkit.loader.scriptloader.bases.MutiRunner;
import com.blocklynukkit.loader.scriptloader.bases.Packager;
import com.blocklynukkit.loader.scriptloader.bases.SingleRunner;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.blocklynukkit.loader.Loader.*;
import static com.blocklynukkit.loader.Loader.getlogger;

public class BNPackageLoader extends ExtendScriptLoader implements Packager, MutiRunner, SingleRunner {
    public BNPackageLoader(Loader loader){
        super(loader);
    }
    @Override
    public void loadplugins(){
        try{
            for (File file : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
                if(file.isDirectory()) continue;
                if(file.getName().endsWith(".bnp")&&!file.getName().contains("bak")){
                    try {
                        File bnp = file;
                        FileInputStream fileInputStream = new FileInputStream(bnp);
                        byte[] bytes = new byte[fileInputStream.available()];
                        fileInputStream.read(bytes);
                        this.runPlugins(this.unpack(bytes));
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
    @Override
    public byte[] pack(LinkedHashMap<String,String> code){
        try{
            ByteArrayOutputStream Bytes = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(Bytes);
            gzip.write("bnp".getBytes("UTF-8"));
            for(Map.Entry<String,String> entry:code.entrySet()){
                gzip.write("-->$newPlugin@".getBytes("UTF-8"));
                gzip.write(entry.getKey().getBytes("UTF-8"));
                gzip.write("*->:".getBytes("UTF-8"));
                gzip.write(entry.getValue().getBytes("UTF-8"));
            }
            gzip.close();
            return Bytes.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public LinkedHashMap<String,String> unpack(byte[] Package) {
        try {
            String packText = "";
            LinkedHashMap<String,String> output = new LinkedHashMap<>();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(Package);
            GZIPInputStream bnpack = new GZIPInputStream(in);
            byte[] buffer = new byte[512];int n;
            while ((n = bnpack.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            packText =  out.toString("UTF-8");
            if(!packText.startsWith("bnp"))return null;
            for(String each:packText
                    .replaceFirst("bnp","").split("-->\\$newPlugin@")){
                String parts[] = each.split("\\*->",2);
                output.put(parts[0],parts[1]);
            }
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void runPlugins(LinkedHashMap<String,String> plugins){
        for(Map.Entry<String,String> entry:plugins.entrySet()){
            if(entry.getKey().endsWith(".js")){
                new JavaScriptLoader(plugin);
            }else if(entry.getKey().endsWith(".lua")){
                new LuaLoader(plugin);
            }else if(entry.getKey().endsWith(".py")){
                if(plugins.containsKey("PyBN")){
                    new PythonLoader(plugin);
                }else {
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        getlogger().warning("无法加载:" + entry.getKey()+"! 缺少python依赖库");
                        getlogger().warning("请到 https://tools.blocklynukkit.com/PyBN.jar下载依赖插件");
                    }
                    else{
                        getlogger().warning("cannot load BN plugin:" + entry.getKey()+"! python libs not found!");
                        getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PyBN.jar");
                    }
                }
            }else if(entry.getKey().endsWith(".php")){
                if(plugins.containsKey("PHPBN")){
                    new PHPLoader(plugin);
                }else {
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        getlogger().warning("无法加载:" + entry.getKey()+"! 缺少PHP依赖库");
                        getlogger().warning("请到 https://tools.blocklynukkit.com/PHPBN.jar下载依赖插件");
                    }
                    else{
                        getlogger().warning("cannot load BN plugin:" + entry.getKey()+"! PHP libs not found!");
                        getlogger().warning("please download python lib plugin at https://tools.blocklynukkit.com/PHPBN.jar");
                    }
                }
            }else if(entry.getKey().endsWith(".json")||entry.getKey().endsWith(".yml")
                    ||entry.getKey().endsWith(".yaml")||entry.getKey().endsWith(".properties")
                    ||entry.getKey().endsWith(".xml")||entry.getKey().endsWith(".txt")){
                File out = new File(entry.getKey());
                if(out.exists()) {
                    try {
                        out.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
