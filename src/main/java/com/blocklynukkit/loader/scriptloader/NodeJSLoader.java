package com.blocklynukkit.loader.scriptloader;

import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.utils.Utils;
import com.blocklynukkit.loader.scriptloader.bases.ExtendScriptLoader;
import com.eclipsesource.v8.*;

import java.io.File;
import java.util.HashMap;

public class NodeJSLoader extends ExtendScriptLoader {
    public NodeJSLoader(Loader loader){
        super(loader);
    }
    public NodeJSLoader(){
        super(Loader.plugin);
    }
    public HashMap<String, NodeJS> NodeDockers = new HashMap<>();
    public HashMap<String, HashMap<String,V8Object>> Globals = new HashMap<>();
    public HashMap<String, JavaCallback> Exports = new HashMap<>();
    public void eval(File file){
        NodeJS nodeJS = NodeJS.createNodeJS();
        nodeJS.getRuntime().registerJavaMethod(NodeCallBacks.callBNFunction, "callFunction");
        nodeJS.exec(file);
        while(nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
        nodeJS.release();
    }
    public void eval(String str,boolean isPath){
        if(isPath){
            eval(new File(str));
        }else {
            Utils.writeWithString(new File("./plugins/BlocklyNukkit/NodeJS/tmp.js"),str);
            eval(new File("./plugins/BlocklyNukkit/NodeJS/tmp.js"));
        }
    }
    public void newDocker(String dockerName,File file){
        Globals.put(dockerName,new HashMap<>());
        NodeJS nodeJS = NodeJS.createNodeJS();
        nodeJS.getRuntime().registerJavaMethod(NodeCallBacks.callBNFunction,"callFunction");
        nodeJS.getRuntime().registerJavaMethod((receiver, parameters) -> {
            String name = parameters.getString(0);
            V8Object fun = parameters.getObject(1);
            Globals.get(dockerName).put(name,fun);
        },"registerFunction");
        V8Function scriptExecution = (V8Function) Utils.invoke(nodeJS,"createScriptExecutionCallback",new Class[]{File.class},new Object[]{file});
        V8Array parameters = new V8Array(nodeJS.getRuntime());
        V8Object process = (V8Object) nodeJS.getRuntime().get("process");
        parameters.push(scriptExecution);
        process.executeObjectFunction("nextTick", parameters);
        NodeDockers.put(dockerName,nodeJS);
        while(nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
    }
    public void newDocker(String dockerName,String str,boolean isPath){
        if(isPath){
            newDocker(dockerName,new File(str));
        }else {
            Utils.writeWithString(new File("./plugins/BlocklyNukkit/NodeJS/"+dockerName+"_tmp.js"),str);
            newDocker(dockerName,new File("./plugins/BlocklyNukkit/NodeJS/"+dockerName+"_tmp.js"));
        }
    }
    public String callDockerFunction(String function,String... args){
        if(function.contains("::")){
            String docker = function.split("::")[0];
            String functionName = function.split("::")[1];
            NodeJS nodeJS = NodeDockers.get(docker);
            V8Array v8Array = new V8Array(nodeJS.getRuntime());
            for(String each:args){
                v8Array.push(each);
            }
            V8Function v8Function = (V8Function) Globals.get(docker).get(functionName);
            String result =String.valueOf(v8Function.call(nodeJS.getRuntime(),v8Array));
            while (nodeJS.isRunning()){
                nodeJS.handleMessage();
            }
            v8Array.release();
            return result;
        }else {
            for(String docker:Globals.keySet()){
                if(Globals.get(docker).keySet().contains(function)){
                    NodeJS nodeJS = NodeDockers.get(docker);
                    V8Array v8Array = new V8Array(nodeJS.getRuntime());
                    for(String each:args){
                        v8Array.push(each);
                    }
                    V8Function v8Function = (V8Function) Globals.get(docker).get(function);
                    String result =String.valueOf(v8Function.call(nodeJS.getRuntime(),v8Array));
                    while (nodeJS.isRunning()){
                        nodeJS.handleMessage();
                    }
                    v8Array.release();
                    return result;
                }
            }
            return "NO FUNCTION";
        }
    }
    public void closeDocker(String dockerName){
        NodeDockers.get(dockerName).release();
        NodeDockers.remove(dockerName);
        Globals.remove(dockerName);
    }
}
class NodeCallBacks{
    public static JavaCallback callBNFunction = (receiver, parameters) -> {
        String BNFunctionName = parameters.getString(0);
        if(parameters.length()==1){
            return Loader.plugin.callbackString(BNFunctionName);
        }else if(parameters.length()==2){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1));
        }else if(parameters.length()==3){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1),
                    parameters.getString(2));
        }else if(parameters.length()==4){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1),
                    parameters.getString(2),parameters.getString(3));
        }else if(parameters.length()==5){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1),
                    parameters.getString(2),parameters.getString(3),
                    parameters.getString(4));
        }else if(parameters.length()==6){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1),
                    parameters.getString(2),parameters.getString(3),
                    parameters.getString(4),parameters.getString(5));
        }else if(parameters.length()==7){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1),
                    parameters.getString(2),parameters.getString(3),
                    parameters.getString(4),parameters.getString(5),
                    parameters.getString(6));
        }else if(parameters.length()==8){
            return Loader.plugin.callbackString(BNFunctionName,parameters.getString(1),
                    parameters.getString(2),parameters.getString(3),
                    parameters.getString(4),parameters.getString(5),
                    parameters.getString(6),parameters.getString(7));
        }else {
            return "ERROR";
        }
    };
}
