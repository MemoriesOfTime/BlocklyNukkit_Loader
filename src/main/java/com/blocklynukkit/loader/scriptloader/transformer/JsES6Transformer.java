package com.blocklynukkit.loader.scriptloader.transformer;

import com.blocklynukkit.loader.utils.StringUtils;
import com.blocklynukkit.loader.utils.Utils;
import com.google.gson.*;

import java.util.*;

public class JsES6Transformer {
    private String code;
    public JsES6Transformer(String code){
        this.code = " " + code + " ";
    }
    public String transform(){
        char stringMode = ' ';
        char commentMode = ' ';
        final char[] chars = code.toCharArray();
        final List<String> strConverts = new LinkedList<>();
        final List<String> arrayFunctionConverts = new LinkedList<>();
        final List<String> forOfConverts = new LinkedList<>();
        final Set<String> packagesImported = new HashSet<>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        char current;
        char previous;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < chars.length-1; i++) {
            current = chars[i];
            previous = chars[i-1];
            //识别字符串
            if((current == '\"' || current == '\'') && previous != '\\' && commentMode == ' '){
                if(stringMode == ' '){
                    stringBuilder = new StringBuilder();
                    stringMode = current;
                }else{
                    if(stringMode == current){
                        stringBuilder.append(current);
                        strConverts.add(stringBuilder.toString());
                        code = StringUtils.replace(code,stringBuilder.toString(),"BNES6_StrKey",1);
                        //code = code.replaceFirst(Pattern.quote(stringBuilder.toString()), "BNES6_StrKey");
                        stringMode = ' ';
                    }
                }
            }
            //跨行处理
            if(current == '\n' && previous != '\\'){
                //字符串跨行处理
                if(stringMode != ' '){
                    stringMode = ' ';
                    strConverts.add(stringBuilder.toString());
                    code = StringUtils.replace(code,stringBuilder.toString(),"BNES6_StrKey",1);
                    stringMode = ' ';
                }
                //双斜杠注释跨行处理
                if(commentMode == '/'){
                    commentMode = ' ';
                    code = StringUtils.replace(code,stringBuilder.toString(),"",1);
                }
            }
            //多行注释处理
            if(current == '/' && previous == '*'){
                stringBuilder.append('/');
                String comment = stringBuilder.toString();
                //确保行数不出问题
                StringBuilder toReplace = new StringBuilder();
                for(int j=0;j<comment.length();j++){
                    if(comment.charAt(j) == '\n') toReplace.append('\n');
                }
                code = StringUtils.replace(code,stringBuilder.toString(),toReplace.toString(),1);
                commentMode = ' ';
            }
            //如果是字符串就计入字符串缓冲区
            if(stringMode != ' ' || commentMode != ' '){
                stringBuilder.append(current);
            }else{
                //如果不是就进行语义分析
                //for of循环分析
                if(i > 1 && current == 'r' && previous == 'o' && chars[i-2] == 'f'){
                    boolean isForLoop = false;
                    boolean isForIn = false;
                    char now = chars[i];
                    for(int j=i+1;now!='\n'&&now!=';';j++){
                        now = chars[j];
                        if(now == '('){
                            isForLoop = true;
                            break;
                        }else if(now != ' '){
                            break;
                        }
                    }
                    if(isForLoop){
                        int endPos = -1;
                        now = chars[i];
                        for(int j=i+1;now!='\n'&&now!=';';j++){
                            now = chars[j];
                            if(now == 'f' && chars[j-2] == ' ' && chars[j-1] == 'o' && chars.length > j+1 && chars[j+1] == ' '){
                                isForIn = true;
                                endPos = j+1;
                                break;
                            }
                        }
                        if(isForIn){
                            StringBuilder builder = new StringBuilder();
                            for(int j=i+1;j<endPos;j++){
                                builder.append(chars[j]);
                            }
                            forOfConverts.add("for each"+builder.toString());
                            code = StringUtils.replace(code,"for"+builder.toString(),"BNES6_ForOfLoop",1);
                        }
                    }
                }
                //箭头函数分析
                if(current == '>' && previous == '='){
                    boolean inParm = false;
                    boolean singleParm = true;
                    char now;
                    StringBuilder source = new StringBuilder();
                    StringBuilder function = new StringBuilder("function ");
                    source.append(current).append(previous);
                    for(int j=i-2;;j--){
                        now = chars[j];
                        if(now == '\n' && chars[j-1] != '\\')break;
                        source.append(now);
                        if(now == ')'){inParm = true;singleParm = false;}
                        if(inParm && now == '(')break;
                        if(!inParm && "!=-[]{}/'\"\\/@#+;:%&^,.<>?|*()`~".contains(""+now)){
                            source.deleteCharAt(source.length()-1);
                            break;
                        }
                    }
                    source.reverse();
                    String arrayFunCode = source.toString();
                    if(singleParm){
                        function.append('(').append(StringUtils.replace(arrayFunCode, "=>", "", 1)).append(')');
                    }else {
                        function.append(StringUtils.replace(arrayFunCode, "=>", "", 1));
                    }
                    arrayFunctionConverts.add(function.toString());
                    code = StringUtils.replace(code,arrayFunCode,"BNES6_ArrayFunction",1);
                }
                //jvm包导入分析
                if(current == '.'){
                    String preIndex = "";
                    if(i > 2 && (previous == 'n' && chars[i-2] == 'c')){
                        preIndex = "cn";
                    } else if(i > 3 && (previous == 'm' && chars[i-2] == 'o' && chars[i-3] == 'c')){
                        preIndex = "com";
                    } else if(i > 4 && (previous == 'a' && chars[i-2] == 'v' && chars[i-3] == 'a' && chars[i-4] == 'j')){
                        preIndex = "java";
                    }
                    if(!"".equals(preIndex)){
                        StringBuilder clazzName = new StringBuilder(preIndex);
                        char tmp;
                        for(int j = i;;j++){
                            tmp = chars[j];
                            if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._".indexOf(tmp) != -1){
                                clazzName.append(tmp);
                            }else{
                                break;
                            }
                        }
                        String clazz = clazzName.toString();
                        try{
                            classLoader.loadClass(clazz);
                            packagesImported.add(clazz);
                        } catch (ClassNotFoundException e) {
                            //ignore
                        }
                    }
                }
                //双斜杠注释处理
                if(current == '/' && previous == '/'){
                    stringBuilder = new StringBuilder("//");
                    commentMode = '/';
                }
                //多行注释处理
                if(current == '*' && previous == '/'){
                    stringBuilder = new StringBuilder("/*");
                    commentMode = '*';
                }
            }
        }
        //返回处理后的代码
        for(String fun:arrayFunctionConverts){
            code = StringUtils.replace(code,"BNES6_ArrayFunction",fun,1);
        }
        for(String forOf:forOfConverts){
            code = StringUtils.replace(code,"BNES6_ForOfLoop", Utils.replaceLast(forOf," of"," in"),1);
        }
        for(String str:strConverts){
            code = StringUtils.replace(code,"BNES6_StrKey",
                    str.replaceAll("\"","\\\"").replaceAll("'","\\'"),1);
        }
        if(!packagesImported.isEmpty()){
            int index = 0;
            JsonObject root = new JsonObject();
            for(String packageName:packagesImported){
                String[] entries = packageName.split("\\.");
                JsonObject parent = root;
                for(int i=0;i<entries.length;i++){
                    if(parent.has(entries[i])){
                        parent = parent.getAsJsonObject(entries[i]);
                    }else{
                        if(i == entries.length-1){
                            parent.add(entries[i], new JsonPrimitive("$_"+index));
                        }else{
                            JsonObject tmp = new JsonObject();
                            parent.add(entries[i], tmp);
                            parent = tmp;
                        }
                    }
                }
                index++;
            }
            String imports = (new Gson()).toJson(root);
            index = 0;
            for(String packageName:packagesImported){
                imports = StringUtils.replace(imports, "\"$_"+index+"\"", "Java.type('"+packageName+"')",1);
                index++;
            }
            StringBuilder importsBuilder = new StringBuilder("var _imports=" + imports + ";");
            for(Map.Entry<String, JsonElement> entry:root.entrySet()){
                importsBuilder.append("var ").append(entry.getKey()).append("=_imports.").append(entry.getKey()).append(";");
            }
            imports = importsBuilder.toString();
            code = imports + code;
        }
        return code;
    }
}
