package com.blocklynukkit.loader.scriptloader;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import javassist.*;

import java.util.Map;

public class JavaExporter {
    public static CtClass makeExportJava(String name,Map<String,String[]> exportFunctions){
        if(!exportFunctions.isEmpty()){
            ClassPool classPool = ClassPool.getDefault();
            try {
                classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.importPackage("com.blocklynukkit.loader");
            CtClass bnClass = classPool.makeClass(name);
            try {
                //添加默认构造函数
                CtConstructor constructor = new CtConstructor(new CtClass[]{},bnClass);
                constructor.setBody("{}");
                bnClass.addConstructor(constructor);
                //循环添加导出方法
                for(Map.Entry<String,String[]> entry:exportFunctions.entrySet()){Object[] a = new java.lang.Object[]{};
                    String announctionArgs = "";
                    String usageArgs = "new java.lang.Object[]{";//",\""+entry.getKey()+"\"";
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
                //导入到jvm中
                bnClass.toClass();
            } catch (CannotCompileException e) {
                e.printStackTrace();
                if (Server.getInstance().getLanguage().getName().contains("中文")){
                    Loader.getlogger().error(TextFormat.RED+"错误："+name+"中的函数无法导出到jvm中，请检查函数导出标记及函数名是否符合jvm规范");
                }else {
                    Loader.getlogger().error(TextFormat.RED+"Error：Cannot export functions in "+name+" to jvm. Please check out if the functions are compilable.");
                }
            }
            return bnClass;
        }
        return null;
    }
}
