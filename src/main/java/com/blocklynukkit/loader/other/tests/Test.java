package com.blocklynukkit.loader.other.tests;

import cn.nukkit.utils.Config;
import com.blocklynukkit.loader.script.FunctionManager;
import com.blocklynukkit.loader.scriptloader.transformer.JsArrowFunctionTransformer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import cz.habarta.typescript.generator.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class Test {
    static class MethodDef{
        String modifier;
        String returnType;
        int parameterCount;
        String[] parameters;
        String comment;
    }
    public static void main(String[] args){
        Gson gson = new Gson();
        Map<String, List<MethodDef>> info = new HashMap<>();
        for(Method method:FunctionManager.class.getMethods()){
            if(info.containsKey(method.getName())){
                MethodDef methodDef = new MethodDef();
                methodDef.modifier = Modifier.toString(method.getModifiers());
                methodDef.returnType = method.getReturnType().getSimpleName();
                methodDef.parameterCount = method.getParameterCount();
                String[] tmp = new String[method.getParameterCount()];
                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];
                    tmp[i] = parameter.getName()+" : "+parameter.getType().getName();
                }
                methodDef.parameters = tmp;
                methodDef.comment = "";
                List<MethodDef> newArr = info.get(method.getName());
                newArr.add(methodDef);
                info.put(method.getName(), newArr);
            }else{
                MethodDef methodDef = new MethodDef();
                methodDef.modifier = Modifier.toString(method.getModifiers());
                methodDef.returnType = method.getReturnType().getSimpleName();
                methodDef.parameterCount = method.getParameterCount();
                String[] tmp = new String[method.getParameterCount()];
                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];
                    tmp[i] = parameter.getName()+" : "+parameter.getType().getName();
                }
                methodDef.parameters = tmp;
                methodDef.comment = "";
                ArrayList<MethodDef> newArr = new ArrayList<>();
                newArr.add(methodDef);
                info.put(method.getName(), newArr);
            }
            System.out.println(gson.toJson(info));
        }
    }
}
