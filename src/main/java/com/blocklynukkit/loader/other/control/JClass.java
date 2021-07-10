package com.blocklynukkit.loader.other.control;

import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javassist.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class JClass {
    private CtClass clazz;
    private Class trueClass = null;
    public static Int2ObjectMap<Object> objStore = new Int2ObjectOpenHashMap<>();

    public JClass(CtClass originClass){
        clazz = originClass;
    }

    public JClass(String className, String superClassName, String... interfaceNames){
        ClassPool classPool = ClassPool.getDefault();
        try {
            classPool.insertClassPath(com.blocklynukkit.loader.Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.importPackage("com.blocklynukkit.loader");

            if(classPool.getOrNull(className) != null){
                throw new RuntimeException(Utils.translate(
                        className+"已经存在，无法重新定义",
                        className+" has already been defined."
                ));
            }

            this.clazz = classPool.makeClass(className);

            if(superClassName != null){
                CtClass superClass = classPool.getCtClass(superClassName);
                if(superClass == null){
                    throw new RuntimeException(Utils.translate(
                            "父类"+className+"无法找到",
                            "Super class "+className+" not found"
                    ));
                }
                try {
                    this.clazz.setSuperclass(superClass);
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }

            if(interfaceNames != null && interfaceNames.length > 0){
                CtClass interfaceClass;
                for(String eachInterface:interfaceNames){
                    interfaceClass = classPool.getCtClass(eachInterface);
                    if(interfaceClass == null){
                        throw new RuntimeException(Utils.translate(
                                "接口类"+className+"无法找到",
                                "Interface class "+className+" not found"
                        ));
                    }
                    this.clazz.addInterface(interfaceClass);
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public JClass addConstructor(String modifier, String proxyFunction, String... argumentClasses){
        StringBuilder src = new StringBuilder();
        src.append(modifier).append(' ').append(clazz.getSimpleName()).append('(');
        for(int i=0;i<argumentClasses.length;i++){
            if(i != 0) src.append(',');
            src.append(argumentClasses[i]).append(' ').append("arg").append(i);
        }
        src.append(')').append('{').append("com.blocklynukkit.loader.Loader.getFunctionManager().callFunction")
                .append('(').append('"').append(proxyFunction).append('"').append(',')
                .append("new java.lang.Object[]{").append("this");
        for(int i=0;i<argumentClasses.length;i++){
            src.append(',').append("arg").append(i);
        }
        src.append('}').append(')').append(';').append('}');
        try {
            clazz.addConstructor(CtNewConstructor.make(src.toString(), clazz));
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JClass addField(String modifier, String fieldClass, String fieldName){
        StringBuilder src = new StringBuilder();
        src.append(modifier).append(' ').append(fieldClass).append(' ').append(fieldName);
        src.append(';');
        try {
            clazz.addField(CtField.make(src.toString(), clazz));
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JClass addField(String modifier, String fieldClass, String fieldName, Object defaultValue){
        CtField.Initializer initializer = null;
        StringBuilder src = new StringBuilder();
        src.append(modifier).append(' ').append(fieldClass).append(' ').append(fieldName).append('=');
        if(defaultValue != null){
            if(defaultValue instanceof String){
                initializer = CtField.Initializer.constant((String) defaultValue);
            }else if(defaultValue instanceof Integer){
                initializer = CtField.Initializer.constant((int) defaultValue);
            }else if(defaultValue instanceof Short){
                initializer = CtField.Initializer.constant((short) defaultValue);
            }else if(defaultValue instanceof Character){
                initializer = CtField.Initializer.constant((char) defaultValue);
            }else if(defaultValue instanceof Long){
                initializer = CtField.Initializer.constant((long) defaultValue);
            }else if(defaultValue instanceof Float){
                initializer = CtField.Initializer.constant((float) defaultValue);
            }else if(defaultValue instanceof Double){
                initializer = CtField.Initializer.constant((double) defaultValue);
            }else if(defaultValue instanceof Class){
                try {
                    initializer = CtField.Initializer.byNew(ClassPool.getDefault().getCtClass(((Class) defaultValue).getName()));
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                int size = objStore.size();
                objStore.put(size, defaultValue);
                try {
                    initializer = CtField.Initializer.byCall(ClassPool.getDefault().getCtClass("com.blocklynukkit.loader.other.control.JClass"), "getStore", new String[]{String.valueOf(size)});
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else{
            src.append("null");
        }
        src.append(';');
        try {
            clazz.addField(CtField.make(src.toString(), clazz), initializer);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JClass addMethod(String modifier, String returnClass, String methodName, String proxyFunction, String... argumentClasses){
        StringBuilder src = new StringBuilder();
        src.append(modifier).append(' ').append(returnClass).append(' ').append(methodName).append('(');
        for(int i=0;i<argumentClasses.length;i++){
            if(i != 0) src.append(',');
            src.append(argumentClasses[i]).append(' ').append("arg").append(i);
        }
        src.append(')').append('{').append("com.blocklynukkit.loader.Loader.getFunctionManager().callFunction")
                .append('(').append('"').append(proxyFunction).append('"').append(',')
                .append("new java.lang.Object[]{").append("this");
        for(int i=0;i<argumentClasses.length;i++){
            src.append(',').append("arg").append(i);
        }
        src.append('}').append(')').append(';').append('}');
        try {
            clazz.addMethod(CtMethod.make(src.toString(), clazz));
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JClass finish(){
        try {
            trueClass = clazz.toClass();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Object newInstance(Object... args){
        if(args == null || args.length == 0){
            try {
                return trueClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }else{
            for(Constructor constructor:trueClass.getDeclaredConstructors()){
                if(constructor.getParameterCount() == args.length){
                    Parameter[] parameters = constructor.getParameters();
                    boolean ok = true;
                    for(int i=0;i<parameters.length;i++){
                        if(!parameters[i].getType().equals(args[i].getClass())){
                            ok = false; break;
                        }
                    }
                    if(ok){
                        try {
                            return constructor.newInstance(args);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                 }
            }
        }
        return null;
    }

    public static Object getStore(int index){
        return objStore.get(index);
    }

    public static Object getStore(String index){
        return objStore.get(Integer.parseInt(index));
    }
}
