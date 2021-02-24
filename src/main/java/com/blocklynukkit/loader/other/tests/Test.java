package com.blocklynukkit.loader.other.tests;

import com.blocklynukkit.loader.other.Babel;
import com.blocklynukkit.loader.scriptloader.transformer.JsArrowFunctionTransformer;
import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;

public class Test {
    public static void main(String[] args){
        Babel babel = new Babel();
        System.out.println(babel.transform("class Person{//定义了一个名字为Person的类\n" +
                "    constructor(name,age){//constructor是一个构造方法，用来接收参数\n" +
                "        this.name = name;//this代表的是实例对象\n" +
                "        this.age=age;\n" +
                "    }\n" +
                "    say(){//这是一个类的方法，注意千万不要加上function\n" +
                "        return \"我的名字叫\" + this.name+\"今年\"+this.age+\"岁了\";\n" +
                "    }\n" +
                "}\n"));
    }
}
