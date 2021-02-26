package com.blocklynukkit.loader.other.tests;

import com.blocklynukkit.loader.other.Babel;
import com.blocklynukkit.loader.scriptloader.transformer.JsArrowFunctionTransformer;
import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;

public class Test {
    public static void main(String[] args){
        long start = System.currentTimeMillis();
        Babel babel = new Babel();
        String escode = "class Person{\n" +
                "    constructor(name,age){\n" +
                "        this.name=name;\n" +
                "        this.age=age;\n" +
                "    }\n" +
                "    say(){\n" +
                "        return \"我的名字叫\"+this.name+\"今年\"+this.age+\"岁了\";\n" +
                "    }\n" +
                "    information(){\n" +
                "        ['BN','NB','求各位发电'].map(i=>console.log(i));\n" +
                "    }\n" +
                "}\n";
        System.out.println(escode);
        System.out.println(System.currentTimeMillis()-start);
        System.out.println(babel.transform(escode));
        System.out.println(System.currentTimeMillis()-start);
    }
}
