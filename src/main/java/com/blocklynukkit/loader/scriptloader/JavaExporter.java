package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import javassist.*;

import java.util.Map;

public class JavaExporter {
    public static CtClass makeExportJava(String name,Map<String,String[]> exportFunctions){
        return makeExportJava(name, exportFunctions, null);
    }
    public static CtClass makeExportJava(String name,Map<String,String[]> exportFunctions,String moduleName){
        if(!exportFunctions.isEmpty()){
            ClassPool classPool = ClassPool.getDefault();
            CtClass bnClass = null;
            CtClass moduleClass = null;
            try {
                classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
                classPool.insertClassPath(new ClassClassPath(Loader.class));
                classPool.importPackage("com.blocklynukkit.loader");
                if(classPool.getOrNull(name)!=null)return classPool.getCtClass(name);
                bnClass = classPool.makeClass(name);
                moduleClass = classPool.makeClass(moduleName);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            processMethod(name,bnClass,exportFunctions);
            if(moduleName != null) processMethod(moduleName,moduleClass,exportFunctions);
            return bnClass;
        }
        return null;
    }
    @SuppressWarnings("all")
    private static void processMethod(String name,CtClass bnClass,Map<String,String[]> exportFunctions){
        try {
            //添加默认构造函数
            CtConstructor constructor = new CtConstructor(new CtClass[]{},bnClass);
            constructor.setBody("{}");
            bnClass.addConstructor(constructor);
            //循环添加导出方法
            for(Map.Entry<String,String[]> entry:exportFunctions.entrySet()){Object[] a = new java.lang.Object[]{};
                String announctionArgs = "";
                String usageArgs = "new java.lang.Object[]{";
                for(String each:entry.getValue()){
                    if(each.trim().length()==0)continue;
                    announctionArgs+=(",java.lang.Object "+each);
                    usageArgs+=(","+each);
                }
                announctionArgs = announctionArgs.replaceFirst(",","");
                usageArgs = usageArgs.replaceFirst(",","");
                usageArgs = ("\""+entry.getKey()+"\","+usageArgs+"}");
                String src = "public static java.lang.Object "+entry.getKey()+"("+announctionArgs+"){\n" +
                        "        return com.blocklynukkit.loader.Loader.getFunctionManager().callFunction("+usageArgs+");\n" +
                        "    }";
                CtMethod ctMethod = CtMethod.make(src,bnClass);
                bnClass.addMethod(ctMethod);
            }
            //添加通配调用方法
            CtMethod caller = CtMethod.make("public static java.lang.Object call(java.lang.String funName,java.lang.Object[] args){" +
                    "   return com.blocklynukkit.loader.Loader.getFunctionManager().callFunction(funName,args);" +
                    "}",bnClass);
            caller.setModifiers(caller.getModifiers() | Modifier.VARARGS);
            bnClass.addMethod(caller);
            //导入到jvm中
            bnClass.toClass();
            bnClass.defrost();
        } catch (CannotCompileException e) {
            e.printStackTrace();
            if (Server.getInstance().getLanguage().getName().contains("中文")){
                Loader.getlogger().error(TextFormat.RED+"错误："+name+"中的函数无法导出到jvm中，请检查函数导出标记及函数名是否符合jvm规范");
            }else {
                Loader.getlogger().error(TextFormat.RED+"Error：Cannot export functions in "+name+" to jvm. Please check out if the functions are compilable.");
            }
        }
    }
}
