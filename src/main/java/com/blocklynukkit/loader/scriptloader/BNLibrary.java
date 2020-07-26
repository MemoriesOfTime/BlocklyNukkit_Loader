package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


public class BNLibrary {
    public static String LibFolder = Loader.plugin.getDataFolder()+"/lib";
    public List<LibEntry> Libraries = new ArrayList();
    public BNLibrary(){
        Libraries.add(new LibEntry("python","pythonForBN.jar",
                new String[]{"jython","Python","Jython","python2","Python2","Python 2","python 2","Python 2.7","python 2.7","Python2.7","python2.7","py","PY","py2","PY2","py2.7","PY2.7",
                        "BNpython","pythonForBN","pythonForBN.jar","BNpython2","BNpython2.7","python.jar","Python.jar","Python2.jar","python2.jar"},
                "http://tools.blocklynukkit.com/pythonForBN.jar"));
        Libraries.add(new LibEntry("database",new String[]{"databaseForBN.jar","databaseUtils.jar"},
                new String[]{"database","db","DB","dblib","dblib.jar","DBLib.jar","DBLIB.jar","DbLib.jar","dbLib.jar","dbLib","DBLIB","DBlib","DbLib","BNdatabase","DataBase","SQL","sql",
                "mysql","MYSQL","mySQL","MYSQL","MySQL","Mysql","MySql","SQLite","sqlite","SQLITE","databaseForBN","databaseForBN.jar"},
                new String[]{"http://tools.blocklynukkit.com/databaseForBN.jar","http://tools.blocklynukkit.com/databaseUtils.jar"}));
        Libraries.add(new LibEntry("php",new String[]{
                "phpForBN-core.jar","phpForBN-debugger.jar","phpForBN-engine.jar","phpForBN-libs.jar"
        },new String[]{
                "PHP","the_best_language","Php","PHp","pHp","php5","PHP5","PHP7","php7","PHP7.2","php7.2","phpForBN"
        },new String[]{
                "http://tools.blocklynukkit.com/libs/phpForBN-core.jar",
                "http://tools.blocklynukkit.com/libs/phpForBN-debugger.jar",
                "http://tools.blocklynukkit.com/libs/phpForBN-engine.jar",
                "http://tools.blocklynukkit.com/libs/phpForBN-libs.jar"
        }));
        loadLibs();
    }
    public void loadLibs(){
        for(LibEntry entry:Libraries){
            if(entry.exists()){
                entry.load();
            }
        }
    }
    public void install(String libName){
        for(LibEntry entry:Libraries){
            if(entry.isthis(libName)){
                if(entry.exists()){
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Server.getInstance().getLogger().info("依赖库"+entry.MainName+" ("+libName+") 已经安装！");
                    }else {
                        Server.getInstance().getLogger().info("The library"+entry.MainName+" ("+libName+") has already installed!");
                    }
                }else {
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Server.getInstance().getLogger().info("正在下载"+entry.MainName+" ("+entry.FileName[0]+"等)...");
                        Server.getInstance().getLogger().info("这可能花费几分钟时间...");
                    }else {
                        Server.getInstance().getLogger().info("Downloading "+entry.MainName+" ("+entry.FileName[0]+" and more) ...");
                        Server.getInstance().getLogger().info("It will cost several minutes...");
                    }
                    boolean f = entry.download();if(f)return;
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Server.getInstance().getLogger().info("安装依赖库"+entry.MainName+" ("+libName+") 中...");
                    }else {
                        Server.getInstance().getLogger().info("Installing "+entry.MainName+" ("+libName+") ...");
                    }

                    entry.load();
                    if (Server.getInstance().getLanguage().getName().contains("中文")){
                        Server.getInstance().getLogger().info("依赖库"+entry.MainName+" ("+libName+") 安装完成！");
                    }else {
                        Server.getInstance().getLogger().info("Library "+entry.MainName+" ("+libName+") successfully installed!");
                    }
                }
                return;
            }
        }
        if (Server.getInstance().getLanguage().getName().contains("中文")){
            Server.getInstance().getLogger().info("找不到名为"+libName+"的依赖库");
        }else {
            Server.getInstance().getLogger().info("The library "+libName+" not found");
        }
    }
    public boolean hasLib(String libName){
        for(LibEntry entry:Libraries){
            if(entry.isthis(libName) && entry.exists()){
                return true;
            }
        }
        return false;
    }
}
class LibEntry{
    public String MainName;
    public String[] FileName;
    public String[] OtherNames;
    public String[] DownloadUrl;
    public LibEntry(String mainName,String fileName,String[] otherNames,String downloadUrl){
        this.MainName = mainName;
        this.DownloadUrl = new String[]{downloadUrl};
        this.FileName=new String[]{fileName};
        this.OtherNames = otherNames;
    }
    public LibEntry(String mainName,String[] fileNames,String[] otherNames,String[] downloadUrls){
        this.MainName = mainName;
        this.DownloadUrl = downloadUrls;
        this.FileName=fileNames;
        this.OtherNames = otherNames;
    }
    public boolean exists(){
        boolean ex = new File(BNLibrary.LibFolder+"/"+FileName[0]).exists();
        for(int i=1;i<FileName.length;i++){
            ex = ex&&new File(BNLibrary.LibFolder+"/"+FileName[i]).exists();
        }
        return ex;
    }
    public boolean download(){
        for(int i=0;i<DownloadUrl.length;i++){
            try {
                Utils.downLoadFromUrl(DownloadUrl[i],FileName[i],BNLibrary.LibFolder);
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    Server.getInstance().getLogger().info(DownloadUrl[i]+"下载完成");
                }else {
                    Server.getInstance().getLogger().info(DownloadUrl[i]+" successfully downloaded!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    Loader.getlogger().warning(TextFormat.RED+"从"+DownloadUrl[i]+" 安装依赖库"+MainName+" ("+FileName[i]+") 到 "+BNLibrary.LibFolder+"/"+FileName[i]+" 失败");
                }else {
                    Loader.getlogger().warning(TextFormat.RED+"Failed to install library "+MainName+" ("+FileName[i]+") to path "+BNLibrary.LibFolder+"/"+FileName[i]+" from "+DownloadUrl[i]);
                }
                return true;
            }
        }return false;
    }
    public boolean isthis(String s){
        s=s.trim();
        if(s.equals(MainName)){
            return true;
        }else {
            for (String each:OtherNames){
                if(each.equals(s)){
                    return true;
                }
            }
        }
        return false;
    }
    public void load(){
        try{
            for(int i=0;i<FileName.length;i++){
                URL urls[] = new URL[]{new File(BNLibrary.LibFolder+"/"+FileName[i]).toURL()};
                URLClassLoader urlLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class sysclass = URLClassLoader.class;
                Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(urlLoader, urls);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
