package com.blocklynukkit.loader.scriptloader.scriptengines;

import com.blocklynukkit.loader.other.BNLogger;
import io.github.kawamuray.wasmtime.*;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.wasi.Wasi;
import io.github.kawamuray.wasmtime.wasi.WasiConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.github.kawamuray.wasmtime.WasmValType.*;

public class WasmScriptEngine extends AbstractScriptEngine implements ScriptEngine, Invocable {
    public Linker linker;
    public Store store;
    public Wasi wasi;
    public WasiConfig wasiConfig;
    public Memory memory;
    public WasmScriptEngine self;

    private static final String modulePrefix = "BlocklyNukkit_Plugin";
    public boolean evaluated = false;

    public Int2ObjectOpenHashMap<Object> javaStack = new Int2ObjectOpenHashMap<>();
    public int javaStackHead = 0;
    public HashMap<String, Integer> keyMap = new HashMap<>();
    public HashMap<String, String> implementJavaFunctionMap = new HashMap<>();
    public HashMap<String, Method> methodMap = new HashMap<>();
    public HashMap<String, Constructor> constructorMap = new HashMap<>();
    public HashMap<String, Field> fieldMap = new HashMap<>();

    public WasmScriptEngine(Config config){
        Engine engine = new Engine(config);
        this.store = new Store(engine);
        this.linker = new Linker(store);
        MemoryType.Limit limit = new MemoryType.Limit(1024);
        this.memory = new Memory(store,new MemoryType(limit));
        this.memory = new Memory(store,new MemoryType(limit));
        memory.grow(1024*15);
        linker.define("env","memory",Extern.fromMemory(memory));
        this.buildWasi();
        this.linkBNFunction();
        this.putInfo();
        self = this;
    }

    public WasmScriptEngine(){
        Config config = new Config();
        config.wasmBulkMemory(true);
        Engine engine = new Engine(config);
        this.store = new Store(engine);
        this.linker = new Linker(store);
        MemoryType.Limit limit = new MemoryType.Limit(1024);
        this.memory = new Memory(store,new MemoryType(limit));
        memory.grow(1024*15);
        linker.define("env","memory",Extern.fromMemory(memory));
        this.buildWasi();
        this.linkBNFunction();
        this.putInfo();
        self = this;
    }

    public WasmScriptEngine(Store store,Linker linker){
        this.store = store;
        this.linker = linker;
        MemoryType.Limit limit = new MemoryType.Limit(1024);
        this.memory = new Memory(store,new MemoryType(limit));
        this.memory = new Memory(store,new MemoryType(limit));
        memory.grow(1024*15);
        linker.define("env","memory",Extern.fromMemory(memory));
        this.buildWasi();
        this.linkBNFunction();
        this.putInfo();
        self = this;
    }

    private void buildWasi(){
        wasiConfig = new WasiConfig(new String[]{},new WasiConfig.PreopenDir[]{});
        wasi = new Wasi(store,wasiConfig);
        wasi.addToLinker(linker);
    }

    public int javaStackPush(Object object){
        javaStack.put(javaStackHead,object);
        javaStackHead++;
        return javaStackHead-1;
    }

    private String readStringFromMemory(int ptr){
        ByteBuffer buffer = buffer();
        byte[] save = new byte[8192];int len=0;
        byte tmp;
        while (true){
            tmp = buffer.get(ptr);
            save[len] = tmp;
            ptr++;len++;
            if(tmp == '\0' || ptr == buffer.limit()){
                break;
            }
        }
        try {
            return new String(save,0,len-1,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return new String(save,0,len-1);
        }
    }

    private void writeStringToMemory(int ptr,String toWrite){
        ByteBuffer buffer = buffer();
        byte[] str = new byte[0];
        try {
            str = toWrite.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for(int i=0;i<str.length;i++){
            buffer.put(ptr+i,str[i]);
        }
        buffer.putChar(ptr+str.length,'\0');
    }

    private ByteBuffer buffer(){
        ByteBuffer buffer = memory.buffer();
        if (buffer.order() != ByteOrder.LITTLE_ENDIAN) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buffer;
    }

    private int byte2int(byte high_h, byte high_l, byte low_h, byte low_l) {
        return (high_h & 0xff) << 24 | (high_l & 0xff) << 16 | (low_h & 0xff) << 8 | low_l & 0xff;
    }

    private void putInfo(){
        put(LANGUAGE_VERSION, "1.0");
        put(LANGUAGE, "webassembly");
        put(ENGINE, "wasmtime");
        put(ENGINE_VERSION, "wasi");
        put(ARGV, "arg");
        put(FILENAME, this.get("javax.script.filename")==null?"?":this.get("javax.script.filename"));
        put(NAME, "wasm");
    }

    private void linkBNFunction(){
        //void implementJFunction(char* as, char* use);
        //全部都使用单字节字符，即ascii
        linker.define("env","implementJFunction",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(as,use)->{
                    implementJavaFunctionMap.put(readStringFromMemory(as)
                            ,readStringFromMemory(use));
                }
        )));
        //void getJString(int jStackIndex, char* to);
        linker.define("env","getJString",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,to)->{
                    writeStringToMemory(to,javaStack.get((int)jStackIndex).toString());
                }
        )));
        //int getJInt(int jStackIndex);
        linker.define("env","getJInt",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex) -> (int)javaStack.get((int)jStackIndex)
        )));
        //long long int getJLong(int jStackIndex);
        linker.define("env","getJLong",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I64,(jStackIndex) -> (long)javaStack.get((int)jStackIndex)
        )));
        //float getJFloat(int jStackIndex);
        linker.define("env","getJFloat",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,F32,(jStackIndex) -> (float)javaStack.get((int)jStackIndex)
        )));
        //double getJDouble(int jStackIndex);
        linker.define("env","getJDouble",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,F64,(jStackIndex) -> (double)javaStack.get((int)jStackIndex)
        )));
        //void getJIntArray(int jStackIndex, int* to);
        linker.define("env","getJIntArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,to)->{
                    ByteBuffer buffer = buffer();
                    int[] ints = (int[]) javaStack.get((int)jStackIndex);
                    for(int i=0;i<ints.length;i++){
                        buffer.putInt(to+i*4,ints[i]);
                    }
                }
        )));
        //void getJLongArray(int jStackIndex, long* to);
        linker.define("env","getJLongArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,to)->{
                    ByteBuffer buffer = buffer();
                    long[] longs = (long[]) javaStack.get((int)jStackIndex);
                    for(int i=0;i<longs.length;i++){
                        buffer.putLong(to+i*8,longs[i]);
                    }
                }
        )));
        //void getJFloatArray(int jStackIndex, float* to);
        linker.define("env","getJFloatArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,to)->{
                    ByteBuffer buffer = buffer();
                    float[] floats = (float[]) javaStack.get((int)jStackIndex);
                    for(int i=0;i<floats.length;i++){
                        buffer.putFloat(to+i*4,floats[i]);
                    }
                }
        )));
        //void getJDoubleArray(int jStackIndex, double* to);
        linker.define("env","getJDoubleArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,to)->{
                    ByteBuffer buffer = buffer();
                    double[] doubles = (double[]) javaStack.get((int)jStackIndex);
                    for(int i=0;i<doubles.length;i++){
                        buffer.putDouble(to+i*8,doubles[i]);
                    }
                }
        )));
        //int getJStaticMethod(char* className, char* name, char* parameter);
        linker.define("env","getJStaticMethod",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,I32,(clazzName,name,parameter)->{
                    String className = readStringFromMemory(clazzName);
                    String methodName = readStringFromMemory(name);
                    String sig = readStringFromMemory(parameter);
                    Method method = null;
                    if(methodMap.containsKey(className+"::"+methodName+"::"+sig)){
                        method = methodMap.get(className+"::"+methodName+"::"+sig);
                    }else {
                        try {
                            for(Method each:Class.forName(className).getMethods()){
                                String[] sigEntries = sig.split(";");
                                if(each.getName().equals(methodName) && each.getParameterCount()==sigEntries.length){
                                    boolean allCatch = true;
                                    Parameter[] parameters = each.getParameters();
                                    for(int i=0;i<sigEntries.length;i++){
                                        if(!(parameters[i].getType().getSimpleName().equals(sigEntries[i])
                                                ||parameters[i].getType().getName().equals(sigEntries[i])
                                                ||sigEntries[i].equals("*"))){
                                            allCatch = false;
                                            break;
                                        }
                                    }
                                    if (allCatch) {
                                        method = each;
                                        methodMap.put(className+"::"+methodName+"::"+sig,each);
                                        break;
                                    }
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    return javaStackPush(method);
                }
        )));
        //int getJMethod(int jStackIndex, char* name, char* parameter);
        linker.define("env","getJMethod",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,I32,(jStackIndex,name,parameter)->{
                    String methodName = readStringFromMemory(name);
                    String sig = readStringFromMemory(parameter);
                    Object obj = javaStack.get((int)jStackIndex);
                    String className = obj.getClass().getName();
                    Method method = null;
                    if(methodMap.containsKey(className+"::"+methodName+"::"+sig)){
                        method = methodMap.get(className+"::"+methodName+"::"+sig);
                    }else {
                        String[] sigEntries = sig.split(";");
                        for(Method each:obj.getClass().getMethods()){
                            if(each.getName().equals(methodName) && each.getParameterCount()==sigEntries.length){
                                boolean allCatch = true;
                                Parameter[] parameters = each.getParameters();
                                for(int i=0;i<sigEntries.length;i++){
                                    if(!(parameters[i].getType().getSimpleName().equals(sigEntries[i])
                                            ||parameters[i].getType().getName().equals(sigEntries[i])
                                            ||sigEntries[i].equals("*"))){
                                        allCatch = false;
                                        break;
                                    }
                                }
                                if (allCatch) {
                                    method = each;
                                    methodMap.put(className+"::"+methodName+"::"+sig,each);
                                    break;
                                }
                            }
                        }
                    }
                    return javaStackPush(method);
                }
        )));
        //int invokeJMethod(int jStackIndex,int jStackObjIndex, int len, int* args);
        linker.define("env","invokeJMethod",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,I32,I32,(jStackIndex,jStackObjIndex,len,args)->{
                    Method method = (Method) javaStack.get((int)jStackIndex);
                    Object[] arg = new Object[(int)len];
                    ByteBuffer buffer = buffer();
                    for(int i=0;i<len;i++){
                        int index = buffer.getInt(args+i*4);//byte2int(buffer.get(args+i*4+3),buffer.get(args+i*4+2),buffer.get(args+i*4+1),buffer.get(args+i*4));
                        arg[i] = javaStack.get(index);
                    }
                    int re ;
                    try {
                        if(arg.length==0){
                            re = javaStackPush(method.invoke(javaStack.get((int)jStackObjIndex)));
                        } else {
                            re = javaStackPush(method.invoke(javaStack.get((int)jStackObjIndex),arg));
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        re = javaStackPush(null);
                        e.printStackTrace();
                    }
                    return re;
                }
        )));
        //int pushIntToJavaStack(int toPush);
        linker.define("env","pushIntToJavaStack",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32, this::javaStackPush
        )));
        //int pushLongToJavaStack(long toPush);
        linker.define("env","pushLongToJavaStack",Extern.fromFunc(WasmFunctions.wrap(
                store,I64,I32, this::javaStackPush
        )));
        //int pushFloatToJavaStack(float toPush);
        linker.define("env","pushFloatToJavaStack",Extern.fromFunc(WasmFunctions.wrap(
                store,F32,I32, this::javaStackPush
        )));
        //int pushDoubleToJavaStack(double toPush);
        linker.define("env","pushDoubleToJavaStack",Extern.fromFunc(WasmFunctions.wrap(
                store,F64,I32, this::javaStackPush
        )));
        //int pushStringToJavaStack(char* toPush);
        linker.define("env","pushStringToJavaStack",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32, toPush -> this.javaStackPush(this.readStringFromMemory(toPush))
        )));
        //int newJObject(char* className, int len, int* args);
        linker.define("env","newJObject",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,I32,(className,len,args)->{
                    try {
                        String clazzName = readStringFromMemory(className);
                        Class clazz = Class.forName(clazzName);
                        Object[] arg = new Object[(int)len];
                        StringBuilder sig = new StringBuilder();
                        ByteBuffer buffer = buffer();
                        int re = -1;
                        for(int i=0;i<len;i++){
                            int index = buffer.getInt(args+i*4);//byte2int(buffer.get(args+i*4+3),buffer.get(args+i*4+2),buffer.get(args+i*4+1),buffer.get(args+i*4));
                            arg[i] = javaStack.get(index);
                            sig.append(arg[i].getClass().getName()).append(";");
                        }
                        if(constructorMap.containsKey(clazzName+"::"+sig)){
                            re = javaStackPush(constructorMap.get(clazzName+"::"+sig).newInstance(arg));
                        }else {
                            for(Constructor each:clazz.getConstructors()){
                                String[] sigEntries = sig.toString().split(";");
                                if(each.getParameterCount()==sigEntries.length){
                                    boolean allCatch = true;
                                    Parameter[] parameters = each.getParameters();
                                    for(int i=0;i<sigEntries.length;i++){
                                        if(!(parameters[i].getType().getSimpleName().equals(sigEntries[i])
                                                ||parameters[i].getType().getName().equals(sigEntries[i])
                                                ||sigEntries[i].equals("*"))){
                                            allCatch = false;
                                            break;
                                        }
                                    }
                                    if (allCatch) {
                                        re = javaStackPush(each.newInstance(arg));
                                        constructorMap.put(clazzName+"::"+sig,each);
                                        break;
                                    }
                                }
                            }
                        }
                        return re;
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        return -1;
                    }
                }
        )));
        //int getJIndexByKey(char* key);
        linker.define("env","getJIndexByKey",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(key) -> {
                    String k = readStringFromMemory(key);
                    return keyMap.getOrDefault(k, -1);
                }
        )));
        //void castJObject(int jStackIndex, char* toCast);
        linker.define("env","castJObject",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,toCast)->{
                    try {
                        String className = readStringFromMemory(toCast);
                        Object obj = javaStack.get((int)jStackIndex);
                        if(className.equals("bool")||className.equals("boolean")){
                            if(obj instanceof Integer){
                                boolean b = ((int)obj!=0);
                                javaStack.put((int)jStackIndex,(Boolean)b);
                            }else if(obj instanceof Long){
                                boolean b = ((long)obj!=0);
                                javaStack.put((int)jStackIndex,(Boolean)b);
                            }else if(obj instanceof Short){
                                boolean b = ((short)obj!=0);
                                javaStack.put((int)jStackIndex,(Boolean)b);
                            }
                        }else {
                            Class clazz = Class.forName(className);
                            Object re = clazz.cast(obj);
                            javaStack.put((int)jStackIndex,re);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        )));
        //void deleteJObject(int jStackIndex);
        linker.define("env","deleteJObject",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,(jStackIndex)-> javaStack.remove(jStackIndex)
        )));
        //void logInfo(char* info);
        linker.define("env","logInfo",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,(info)-> ((BNLogger)this.get("logger")).info(readStringFromMemory(info))
        )));
        //void logWarning(char* info);
        linker.define("env","logWarning",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,(info)-> ((BNLogger)this.get("logger")).warning(readStringFromMemory(info))
        )));
        //int newJArray(char* className, int len);
        linker.define("env","newJArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,(className,len)->{
                    try {
                        String clazzName = readStringFromMemory(className);
                        Class clazz = Class.forName(clazzName);
                        int re = -1;
                        re = javaStackPush(Array.newInstance(clazz,len));
                        return re;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
        )));
        //void setJObjectKey(int jStackIndex, char* key);
        linker.define("env","setJObjectKey",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,key)->{
                    if(javaStack.containsKey((int)jStackIndex)){
                        keyMap.put(readStringFromMemory(key),jStackIndex);
                    }
                }
        )));
        //int getJObjectFromJArray(int jStackIndex,int index);
        linker.define("env","getJObjectFromJArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,(jStackIndex,index)->{
                    if(javaStack.containsKey((int)jStackIndex)){
                        return javaStackPush(Array.get(javaStack.get((int)jStackIndex),index));
                    }
                    return -1;
                }
        )));
        //bool isJObjectEqualsNull(int jStackIndex);
        linker.define("env","isJObjectEqualsNull",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,jStackIndex -> javaStack.get((int)jStackIndex)==null?1:0
        )));
        //void setJArrayByIndex(int jStackIndex, int jObjStackIndex,int index);
        linker.define("env","setJArrayByIndex",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,(jStackIndex,jObjStackIndex,index)->{
                    if(javaStack.containsKey((int)jStackIndex) && javaStack.containsKey((int)jObjStackIndex)){
                        Array.set(javaStack.get((int)jStackIndex),index,javaStack.get((int)jObjStackIndex));
                    }
                }
        )));
        //int getJObjectField(int jStackIndex, char* fieldName);
        linker.define("env","getJObjectField",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,(jStackIndex,fieldName)->{
                    try{
                        if(javaStack.containsKey((int)jStackIndex)){
                            Object object = javaStack.get((int)jStackIndex);
                            String name = readStringFromMemory(fieldName);
                            String fdSig = object.getClass().getName()+"::"+fieldName;
                            if(fieldMap.containsKey(fdSig)){
                                return javaStackPush(fieldMap.get(fdSig).get(object));
                            }else {
                                Field field = object.getClass().getDeclaredField(name);
                                fieldMap.put(fdSig, field);
                                return javaStackPush(field.get(object));
                            }
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
        )));
        //int getJClassField(char* className, char* fieldName);
        linker.define("env","getJClassField",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,I32,(className,fieldName)->{
                    try{
                        String clazzName = readStringFromMemory(className);
                        String name = readStringFromMemory(fieldName);
                        Class clazz = Class.forName(clazzName);
                        String fdSig = clazzName+"::"+name;
                        if(fieldMap.containsKey(fdSig)){
                            return javaStackPush(fieldMap.get(fdSig).get(null));
                        }else {
                            Field field = clazz.getField(name);
                            fieldMap.put(fdSig, field);
                            return javaStackPush(field.get(null));
                        }
                    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
        )));
        //void getJObjectArray(int jStackIndex,int* to);
        linker.define("env","getJObjectArray",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,(jStackIndex,to)->{
                    Object jArray = javaStack.get((int)jStackIndex);
                    ByteBuffer buffer = buffer();
                    for(int i=0;i<Array.getLength(jArray);i++){
                        buffer.putInt(to+i*4,javaStackPush(Array.get(jArray,i)));
                    }
                }
        )));
        //int getJArrayLength(int jStackIndex);
        linker.define("env","getJArrayLength",Extern.fromFunc(WasmFunctions.wrap(
                store,I32,I32,jStackIndex -> Array.getLength(javaStack.get((int)jStackIndex))
        )));
    }

    public String currentModuleName(){
        return modulePrefix;
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        if(implementJavaFunctionMap.containsKey(name)){
            Val[] vals = new Val[args.length];
            for(int i=0;i<vals.length;i++){
                Object arg = args[i];
                if(arg instanceof Integer){
                    vals[i] = (Val.fromI32((int) arg));
                }else if(arg instanceof Long){
                    vals[i] = (Val.fromI64((long) arg));
                }else if(arg instanceof Float){
                    vals[i] = (Val.fromF32((float) arg));
                }else if(arg instanceof Double){
                    vals[i] = (Val.fromF64((double) arg));
                }else {
                    int index = javaStackPush(arg);
                    vals[i] = (Val.fromI32(index));
                }
            }
            Extern extern = linker.getOneByName(currentModuleName(),implementJavaFunctionMap.get(name));
            Func func = extern.func();
            Val[] returns = func.call(vals);
            return returns.length>0?returns[0]:null;
        }
        return null;
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return null;
    }

    public Object eval(byte[] binary) throws ScriptException{
        if(evaluated) throw new ScriptException("A wasm program has already been evaluated!");
        try {
            evaluated = true;
            linker.module(currentModuleName(), Module.fromBinary(store.engine(),binary));
            linker.getOneByName(currentModuleName(),"_initialize").func().call();
            linker.getOneByName(currentModuleName(),"init").func().call();
            return 0;
        } catch (WasmtimeException e){
            e.printStackTrace();
            throw new ScriptException(e.getMessage());
        }
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        if(evaluated) throw new ScriptException("A wasm program has already been evaluated!");
        try{
            evaluated = true;
            if(script.startsWith("(module")){
                linker.module(currentModuleName(),new Module(store.engine(),script.getBytes(StandardCharsets.UTF_8)));
            }else {
                byte[] wasmBinaries = Base64.getDecoder().decode(script);
                linker.module(currentModuleName(), Module.fromBinary(store.engine(),wasmBinaries));
            }
            linker.getOneByName(currentModuleName(),"_initialize").func().call();
            linker.getOneByName(currentModuleName(),"init").func().call();
            return 0;
        } catch (WasmtimeException e){
            e.printStackTrace();
            throw new ScriptException(e.getMessage());
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try{
            StringBuilder builder = new StringBuilder();
            char[] tmp = new char[2048];
            while (true){
                int r = reader.read(tmp);
                if(r == -1){
                    break;
                }else {
                    builder.append(tmp,0,r);
                }
            }
            return this.eval(builder.toString(),context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WasmtimeException e){
            e.printStackTrace();
            throw new ScriptException(e.getMessage());
        }
        return null;
    }

    @Override
    public Object get(String key) {
        if(implementJavaFunctionMap.containsKey(key))return implementJavaFunctionMap.get(key);
        return javaStack.get((int)keyMap.getOrDefault(key,-1));
    }

    @Override
    public void put(String key, Object value) {
        int size = javaStackHead;
        keyMap.put(key,size);
        javaStack.put(size,value);
        javaStackHead++;
    }

    @Override
    public Bindings createBindings() {
        return new Bindings() {
            @Override
            public Object put(String name, Object value) {
                self.put(name, value);return value;
            }

            @Override
            public void putAll(Map<? extends String, ?> toMerge) {
                toMerge.forEach(this::put);
            }

            @Override
            public boolean containsKey(Object key) {
                return self.get(key.toString())!=null;
            }

            @Override
            public Object get(Object key) {
                return self.get(key.toString());
            }

            @Override
            public Object remove(Object key) {
                return self.javaStack.remove((int)self.keyMap.get(key.toString()));
            }

            @Override
            public int size() {
                return self.keyMap.size();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return self.javaStack.containsValue(value);
            }

            @Override
            public void clear() {
                self.keyMap.values().forEach(v -> javaStack.remove((int)v));
                self.keyMap.clear();
            }

            @Override
            public Set<String> keySet() {
                return self.keyMap.keySet();
            }

            @Override
            public Collection<Object> values() {
                List o = new ArrayList();
                self.keyMap.values().forEach(v -> o.add(javaStack.get((int)v)));
                return o;
            }

            @Override
            public Set<Entry<String, Object>> entrySet() {
                Set<Entry<String, Object>> o = new HashSet<>();
                self.keyMap.forEach((k,v)-> o.add(makeEntry(k,javaStack.get((int)v))));
                return o;
            }

            private Entry<String, Object> makeEntry(String key,Object value){
                return new Entry<String, Object>() {
                    Object v = value;
                    @Override
                    public String getKey() {
                        return key;
                    }

                    @Override
                    public Object getValue() {
                        return v;
                    }

                    @Override
                    public Object setValue(Object value) {
                        v = value;
                        return v;
                    }
                };
            }

        };
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return new WasmScriptEngineFactory();
    }

    @Override
    public void finalize(){
        this.store.engine().dispose();
        this.linker.dispose();
        this.store.dispose();
    }
}
