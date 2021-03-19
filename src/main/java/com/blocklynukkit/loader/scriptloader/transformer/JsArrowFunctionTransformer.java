package com.blocklynukkit.loader.scriptloader.transformer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsArrowFunctionTransformer {
    private String code;
    private char stringMode = ' ';
    private char commentMode = ' ';
    private char[] chars;
    private List<String> strConverts = new LinkedList<>();
    private List<String> arrayFunctionConverts = new LinkedList<>();
    public JsArrowFunctionTransformer(String code){
        this.code = " " + code + " ";
        chars = code.toCharArray();
    }
    public String transform(){
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
                        code = code.replaceFirst(Pattern.quote(stringBuilder.toString()), "BNES6_StrKey");
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
                    code = code.replaceFirst(Pattern.quote(stringBuilder.toString()), "BNES6_StrKey");
                    stringMode = ' ';
                }
                //双斜杠注释跨行处理
                if(commentMode == '/'){
                    commentMode = ' ';
                    code = code.replaceFirst(Pattern.quote(stringBuilder.toString()),"");
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
                code = code.replaceFirst(Pattern.quote(stringBuilder.toString()),toReplace.toString());
            }
            //如果是字符串就计入字符串缓冲区
            if(stringMode != ' ' || commentMode != ' '){
                stringBuilder.append(current);
            }else{
                //如果不是就进行语义分析
                if(current == '>' && previous == '='){
                    //这里就是箭头函数
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
                        function.append('(').append(arrayFunCode.replaceFirst("=>","")).append(')');
                    }else {
                        function.append(arrayFunCode.replaceFirst("=>",""));
                    }
                    arrayFunctionConverts.add(function.toString());
                    code = code.replaceFirst(Pattern.quote(arrayFunCode),"BNES6_ArrayFunction");
                    System.out.println(source.toString());
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
            code = code.replaceFirst(Pattern.quote("BNES6_ArrayFunction"),fun);
        }
        for(String str:strConverts){
            code = code.replaceFirst(Pattern.quote("BNES6_StrKey"),str);
        }
        return code;
    }
}
