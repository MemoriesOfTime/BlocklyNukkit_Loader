package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.blocklynukkit.loader.utils.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.Vector;

public class BNMiscBuild extends Command {
    public static String md = "# %className%  \n" +
            "\n" +
            "**@%classType%** %className%  \n" +
            "\n" +
            "## 成员变量  \n" +
            "\n" +
            "|成员变量|修饰符|类型|\n" +
            "|-|-|-|\n" +
            "%fields%  " +
            "\n" +
            "## 成员函数  \n" +
            "\n" +
            "|成员函数|修饰符|返回值|参数|\n" +
            "|-|-|-|-|\n" +
            "%methods%  " +
            "\n" +
            "\n";
    public static int len = 0;
    public BNMiscBuild() {
        super("bnmiscbuild","其他构建","bnmiscbuild listClass");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        try{
            if(!sender.isPlayer()) {
                if (args.length == 1) {
                    if (args[0].equals("listClass")) {
                        int tmpant = 0;
                        Field f = ClassLoader.class.getDeclaredField("classes");
                        f.setAccessible(true);
                        Vector<Class<?>> classesThread = (Vector<Class<?>>) f.get(this.getClass().getClassLoader());
                        Vector<Class<?>> classesSystem = (Vector<Class<?>>) f.get(ClassLoader.getSystemClassLoader());
                        synchronized (classesThread) {
                            for (Class<?> c : classesThread) {
                                //                                try{
                                String nn = c.getCanonicalName();
                                if(nn==null)continue;
                                if (!(nn.startsWith("cn.n") || nn.startsWith("com.b"))) {
                                    continue;
                                }
                                tmpant++;
                                if (tmpant < len) {
                                    continue;
                                }
                                String fieldString = "";
                                String methodString = "";
                                for (Field field : c.getFields()) {
                                    fieldString += ("|" + field.getName() + "|" + Modifier.toString(field.getModifiers()) + "|" + field.getType().getSimpleName() + "|\n");
                                }
                                for (Field field : c.getDeclaredFields()) {
                                    if (Modifier.isPrivate(field.getModifiers()))
                                        fieldString += ("|" + field.getName() + "|" + Modifier.toString(field.getModifiers()) + "|" + field.getType().getSimpleName() + "|\n");
                                }
                                for (Method method : c.getMethods()) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (Parameter parameter : method.getParameters()) {
                                        stringBuilder.append(parameter.getType().getSimpleName()).append(' ').append(parameter.getName()).append(", ");
                                    }
                                    String argsStr = stringBuilder.toString().endsWith(", ") ? Utils.replaceLast(stringBuilder.toString(), ", ", "") : stringBuilder.toString();
                                    methodString += ("|" + method.getName() + "|" + Modifier.toString(method.getModifiers()) + "|" + method.getReturnType().getSimpleName() + "|" + argsStr + "|\n");
                                }
                                for (Method method : c.getDeclaredMethods()) {
                                    if (Modifier.isPrivate(method.getModifiers())) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for (Parameter parameter : method.getParameters()) {
                                            stringBuilder.append(parameter.getType().getSimpleName()).append(' ').append(parameter.getName()).append(", ");
                                        }
                                        String argsStr = stringBuilder.toString().endsWith(", ") ? Utils.replaceLast(stringBuilder.toString(), ", ", "") : stringBuilder.toString();
                                        if (method.getParameters().length == 0) argsStr = "void";
                                        methodString += ("|" + method.getName() + "|" + Modifier.toString(method.getModifiers()) + "|" + method.getReturnType().getSimpleName() + "|" + argsStr + "|\n");
                                    }
                                }
                                String type;
                                if (c.isInterface()) {
                                    type = "Interface";
                                } else if (c.isArray()) {
                                    type = "Array";
                                } else if (c.isAnnotation()) {
                                    type = "Annotation";
                                } else if (c.isEnum()) {
                                    type = "Enum";
                                } else if (c.isMemberClass()) {
                                    type = "MemberClass";
                                } else {
                                    type = "Class";
                                }
                                String out = "# " + nn + "  \n" +
                                        "\n" +
                                        "**@" + type + "** " + nn + "  \n" +
                                        "\n" +
                                        "## 成员变量  \n" +
                                        "\n" +
                                        "|成员变量|修饰符|类型|\n" +
                                        "|-|-|-|\n" +
                                        fieldString + "  " +
                                        "\n" +
                                        "## 成员函数  \n" +
                                        "\n" +
                                        "|成员函数|修饰符|返回值|参数|\n" +
                                        "|-|-|-|-|\n" +
                                        methodString + "  " +
                                        "\n" +
                                        "\n";
                                Utils.writeWithString(new File("./javadoc/" + nn + ".md"), out);
                                System.out.println(nn);
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                    Thread.sleep(100);
//                                }
                            }
                        }
                        synchronized (classesSystem) {
                            for (Class<?> c : classesSystem) {
                                //                                try{
                                String nn = c.getCanonicalName();
                                if(nn==null)continue;
                                if (!(nn.startsWith("cn.n") || nn.startsWith("com.b"))) {
                                    continue;
                                }
                                tmpant++;
                                if (tmpant < len) {
                                    continue;
                                }
                                String fieldString = "";
                                String methodString = "";
                                for (Field field : c.getFields()) {
                                    fieldString += ("|" + field.getName() + "|" + Modifier.toString(field.getModifiers()) + "|" + field.getType().getSimpleName() + "|\n");
                                }
                                for (Field field : c.getDeclaredFields()) {
                                    if (Modifier.isPrivate(field.getModifiers()))
                                        fieldString += ("|" + field.getName() + "|" + Modifier.toString(field.getModifiers()) + "|" + field.getType().getSimpleName() + "|\n");
                                }
                                for (Method method : c.getMethods()) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (Parameter parameter : method.getParameters()) {
                                        stringBuilder.append(parameter.getType().getSimpleName()).append(' ').append(parameter.getName()).append(", ");
                                    }
                                    String argsStr = stringBuilder.toString().endsWith(", ") ? Utils.replaceLast(stringBuilder.toString(), ", ", "") : stringBuilder.toString();
                                    methodString += ("|" + method.getName() + "|" + Modifier.toString(method.getModifiers()) + "|" + method.getReturnType().getSimpleName() + "|" + argsStr + "|\n");
                                }
                                for (Method method : c.getDeclaredMethods()) {
                                    if (Modifier.isPrivate(method.getModifiers())) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for (Parameter parameter : method.getParameters()) {
                                            stringBuilder.append(parameter.getType().getSimpleName()).append(' ').append(parameter.getName()).append(", ");
                                        }
                                        String argsStr = stringBuilder.toString().endsWith(", ") ? Utils.replaceLast(stringBuilder.toString(), ", ", "") : stringBuilder.toString();
                                        if (method.getParameters().length == 0) argsStr = "void";
                                        methodString += ("|" + method.getName() + "|" + Modifier.toString(method.getModifiers()) + "|" + method.getReturnType().getSimpleName() + "|" + argsStr + "|\n");
                                    }
                                }
                                String type;
                                if (c.isInterface()) {
                                    type = "Interface";
                                } else if (c.isArray()) {
                                    type = "Array";
                                } else if (c.isAnnotation()) {
                                    type = "Annotation";
                                } else if (c.isEnum()) {
                                    type = "Enum";
                                } else if (c.isMemberClass()) {
                                    type = "MemberClass";
                                } else {
                                    type = "Class";
                                }
                                String out = "# " + nn + "  \n" +
                                        "\n" +
                                        "**@" + type + "** " + nn + "  \n" +
                                        "\n" +
                                        "## 成员变量  \n" +
                                        "\n" +
                                        "|成员变量|修饰符|类型|\n" +
                                        "|-|-|-|\n" +
                                        fieldString + "  " +
                                        "\n" +
                                        "## 成员函数  \n" +
                                        "\n" +
                                        "|成员函数|修饰符|返回值|参数|\n" +
                                        "|-|-|-|-|\n" +
                                        methodString + "  " +
                                        "\n" +
                                        "\n";
                                Utils.writeWithString(new File("./javadoc/" + nn + ".md"), out);
                                System.out.println(nn);
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                    Thread.sleep(100);
//                                }
                            }
                        }
                        len = tmpant;
                    }
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
