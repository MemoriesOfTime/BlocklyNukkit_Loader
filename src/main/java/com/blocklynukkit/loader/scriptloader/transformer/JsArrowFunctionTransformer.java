package com.blocklynukkit.loader.scriptloader.transformer;

import com.blocklynukkit.loader.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class JsArrowFunctionTransformer {
    private String code;
    private char stringMode = ' ';
    private char commentMode = ' ';
    private final char[] chars;
    private final List<String> strConverts = new LinkedList<>();
    private final List<String> arrayFunctionConverts = new LinkedList<>();
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
                    //code = code.replaceFirst(Pattern.quote(stringBuilder.toString()), "BNES6_StrKey");
                    stringMode = ' ';
                }
                //双斜杠注释跨行处理
                if(commentMode == '/'){
                    commentMode = ' ';
                    code = StringUtils.replace(code,stringBuilder.toString(),"",1);
                    //code = code.replaceFirst(Pattern.quote(stringBuilder.toString()),"");
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
                //code = code.replaceFirst(Pattern.quote(stringBuilder.toString()),toReplace.toString());
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
                        function.append('(').append(StringUtils.replace(arrayFunCode, "=>", "", 1)).append(')');
                    }else {
                        function.append(StringUtils.replace(arrayFunCode, "=>", "", 1));
                    }
                    arrayFunctionConverts.add(function.toString());
                    code = StringUtils.replace(code,arrayFunCode,"BNES6_ArrayFunction",1);
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
            //code = code.replaceFirst("BNES6_ArrayFunction",fun);
        }
        for(String str:strConverts){
            code = StringUtils.replace(code,"BNES6_StrKey",
                    str.replaceAll("\"","\\\"").replaceAll("'","\\'"),1);
            //code = code.replaceFirst("BNES6_StrKey",str.replaceAll("\"","\\\\\"").replaceAll("'","\\\\'"));
        }
        return code;
    }
}
