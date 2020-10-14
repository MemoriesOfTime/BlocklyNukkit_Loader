package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;

public class NodeJSNotFoundLoader extends ExtendScriptLoader {
    public NodeJSNotFoundLoader(Loader loader){
        super(loader);
    }
    public NodeJSNotFoundLoader(){
        super(Loader.plugin);
    }
    public void eval(String str,boolean isPath){
        mkwarn();
    }
    public void newDocker(String dockerName,String str,boolean isPath){
        mkwarn();
    }
    public String callDockerFunction(String function,String... args){
        mkwarn();
        return "Node.js Not Found";
    }
    public void closeDocker(String dockerName){
        mkwarn();
    }
    public void mkwarn(){
        if (Server.getInstance().getLanguage().getName().contains("中文")){
            Loader.getlogger().warning(TextFormat.RED+"Node.js模块未安装！请去下载安装后再使用！下载链接：");
            Loader.getlogger().warning(TextFormat.RED+"Windows64版本: https://tools.blocklynukkit.com/NodeBN_Windows64.jar");
            Loader.getlogger().warning(TextFormat.RED+"Windows32版本: null");
            Loader.getlogger().warning(TextFormat.RED+"Linux64版本: null");
        }else {
            Loader.getlogger().warning(TextFormat.RED+"Node.js module not found! Please install node.js plugin first! Download link:");
            Loader.getlogger().warning(TextFormat.RED+"For Windows64: https://tools.blocklynukkit.com/NodeBN_Windows64.jar");
            Loader.getlogger().warning(TextFormat.RED+"For Windows32: null");
            Loader.getlogger().warning(TextFormat.RED+"For Linux64: null");
        }
    }
}
