package com.blocklynukkit.loader.other.control;

import com.blocklynukkit.loader.api.Comment;

public class JVM {
    @Comment(value = "创建新的java类")
    public JClass newJVMClass(@Comment(value = "java类名") String className
            ,@Comment("继承自的父类类名") String extendFromClass
            ,@Comment("实现的接口类名") String... interfaceClasses){
        return new JClass(className, extendFromClass, interfaceClasses);
    }

    @Comment(value = "创建新的java类")
    public JClass newJVMClass(@Comment(value = "java类名") String className){
        return new JClass(className, null);
    }

    @Comment(value = "以jvm形式获取java类对象")
    public Class<?> getJVMClass(@Comment(value = "java类名") String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Comment(value = "强制关闭JVM")
    public void close(){
        close(0);
    }

    @Comment(value = "强制关闭JVM")
    public void close(@Comment(value = "退出返回值，默认为0") int returnCode){
        Runtime.getRuntime().exit(returnCode);
    }

    @Comment(value = "获取JVM内存情况对象")
    public JMemory getMemory(){
        return new JMemory();
    }
}
