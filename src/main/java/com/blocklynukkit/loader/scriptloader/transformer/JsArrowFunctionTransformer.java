package com.blocklynukkit.loader.scriptloader.transformer;

public class JsArrowFunctionTransformer {
    private int pos = 0;
    private int length;
    private String code;
    private boolean inString = false;
    private char stringEnd = '"';
    private boolean inComment = false;
    private char[] chars;
    public JsArrowFunctionTransformer(String code){
        this.code = code;
        length = code.length();
        code = (" "+code+" ");
        chars = code.toCharArray();
    }
    public String transform(){
        char previous;
        char current;
        char next;
        for(int i=1;i<length;i++){
            previous = chars[i-1];
            current = chars[i];
            next = chars[i+1];
            switch (current){
                case '"':
                    if(previous!='\\'){
                        if(!(inString||inComment)){
                            inString = true;
                            stringEnd = '"';
                        }
                    }
                    break;
                case '\'':
                    if(previous!='\\'){
                        if(!(inString||inComment)){
                            inString = true;
                            stringEnd = '\'';
                        }
                    }
                    break;
            }
        }
        return "";
    }
}
