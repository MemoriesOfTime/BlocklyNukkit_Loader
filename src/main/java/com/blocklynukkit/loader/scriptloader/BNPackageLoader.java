package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.scriptloader.bases.*;
import com.blocklynukkit.loader.utils.GZIPUtils;
import com.blocklynukkit.loader.utils.Utils;

import java.io.*;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.blocklynukkit.loader.Loader.getlogger;

public class BNPackageLoader extends ExtendScriptLoader implements BytePackager, MutiRunner, SingleRunner, StringPackager {
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
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().warning("加载BN插件包: " + file.getName());
                        else
                            getlogger().warning("loading BN plugin-package: " + file.getName());
                        File bnp = file;
                        this.runPlugins(this.unpack(Utils.readToString(bnp)));
                    } catch (final Exception e) {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            plugin.getLogger().error("无法加载： " + file.getName(), e);
                        else
                            plugin.getLogger().error("cannot load:" + file.getName(), e);
                    }
                }else if(file.getName().endsWith(".bnpx")&&!file.getName().contains("bak")){
                    try {
                        if (Server.getInstance().getLanguage().getName().contains("中文"))
                            getlogger().warning("加载BN插件包: " + file.getName());
                        else
                            getlogger().warning("loading BN plugin-package: " + file.getName());
                        File bnp = file;
                        FileInputStream fileInputStream = new FileInputStream(bnp);
                        byte[] content = new byte[fileInputStream.available()];
                        fileInputStream.read(content);
                        this.runPlugins(this.unpack(content));
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
    public byte[] pack2Byte(LinkedHashMap<String,String> code){
        try{
            ByteArrayOutputStream Bytes = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(Bytes);
            gzip.write("bnp".getBytes("UTF-8"));
            for(Map.Entry<String,String> entry:code.entrySet()){
                gzip.write("-->$newPlugin@".getBytes("UTF-8"));
                gzip.write(entry.getKey().getBytes("UTF-8"));
                gzip.write("#->:".getBytes("UTF-8"));
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
                if(each.trim().length()==0)continue;
                String parts[] = each.split("#->:",2);
                output.put(parts[0],parts[1]);
            }
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public LinkedHashMap<String, String> unpack(String Package) {
        LinkedHashMap<String,String> output = new LinkedHashMap<>();
        String packText = Package;
        if(!packText.startsWith("bnp"))return null;
        String[] codes = packText.replaceFirst("bnp","").split("-->\\$newPlugin@");
        for(String each:codes){
            if(each.trim().length()==0)continue;
            String parts[] = each.split("#->:");
            output.put(parts[0],parts[1]);
        }
        return output;
    }
    @Override
    public String pack2String(LinkedHashMap<String, String> codes) {
        String out = "bnp";
        for(Map.Entry<String,String> entry:codes.entrySet()){
            out += ("-->$newPlugin@");
            out += (entry.getKey());
            out += ("#->:");
            out += (entry.getValue());
        }
        return out;
    }
    @Override
    public void runPlugins(LinkedHashMap<String,String> plugins){
        for(Map.Entry<String,String> entry:plugins.entrySet()){
            if(entry.getKey().endsWith(".js")){
                new JavaScriptLoader(plugin).putEngine(entry.getKey(),entry.getValue());
            }else if(entry.getKey().endsWith(".lua")){
                new LuaLoader(plugin).putEngine(entry.getKey(),entry.getValue());
            }else if(entry.getKey().endsWith(".py")){
                if(!plugins.containsKey("PyBN")){
                    new PythonLoader(plugin).putEngine(entry.getKey(),entry.getValue());
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
                if(!plugins.containsKey("PHPBN")){
                    new PHPLoader(plugin).putEngine(entry.getKey(),entry.getValue());
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
                if(!out.exists()) {
                    try {
                        if(!out.getParentFile().exists()) out.getParentFile().mkdirs();
                        out.createNewFile();
                        Utils.writeWithString(out,entry.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
