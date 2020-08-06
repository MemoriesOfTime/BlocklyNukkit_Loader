package com.blocklynukkit.loader.scriptloader.scriptengines;

import org.develnext.jphp.scripting.JPHPContext;
import org.develnext.jphp.scripting.JPHPScriptEngine;
import org.develnext.jphp.scripting.JPHPScriptEngineFactory;
import org.develnext.jphp.scripting.util.ReaderInputStream;
import php.runtime.Information;
import php.runtime.Memory;
import php.runtime.env.Context;
import php.runtime.env.Environment;
import php.runtime.launcher.Launcher;
import php.runtime.memory.support.MemoryUtils;
import php.runtime.reflection.FunctionEntity;
import php.runtime.memory.ArrayMemory;
import php.runtime.reflection.ModuleEntity;

import javax.script.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class BNPHPScriptEngine extends AbstractScriptEngine implements Compilable,Invocable {
    private static final String __ENGINE_VERSION__   = Information.CORE_VERSION;
    private static final String __NAME__             = Information.NAME;
    private static final String __SHORT_NAME__       = "jphp";
    private static final String __LANGUAGE__         = "php";
    private static final String __LANGUAGE_VERSION__ = Information.LIKE_PHP_VERSION;

    private ScriptEngineFactory factory = null;
    private Environment environment;

    public BNPHPScriptEngine() {
        super();

        Launcher launcher = new Launcher();
        try {
            launcher.run(false);
        } catch (Throwable e) {
            //pass
        }
        environment = new Environment(launcher.getCompileScope(), System.out);
        environment.getDefaultBuffer().setImplicitFlush(true);

        JPHPContext ctx = new JPHPContext();
        ctx.setBindings(createBindings(), ScriptContext.ENGINE_SCOPE);
        setContext(ctx);

        put(LANGUAGE_VERSION, __LANGUAGE_VERSION__);
        put(LANGUAGE, __LANGUAGE__);
        put(ENGINE, __NAME__);
        put(ENGINE_VERSION, __ENGINE_VERSION__);
        put(NAME, __SHORT_NAME__);
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return eval(new StringReader(script), context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext _ctx) throws ScriptException {
        return compile(reader).eval(_ctx);
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return compile(new StringReader(script));
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        try {
            Memory[] mem = new Memory[args.length];
            for(int i=0;i<args.length;i++){
                mem[i]=Memory.wrap(environment,args[i]);
            }
            environment.invokeMethod(Memory.wrap(environment,thiz),name,mem);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        Object o=null;
        try {

            Memory[] mem = new Memory[args.length];
            for(int i=0;i<args.length;i++){
                mem[i]=Memory.wrap(environment,args[i]);
            }
            FunctionEntity functionEntity = environment.fetchFunction(name);
            if(functionEntity==null){
                throw new NoSuchMethodException("function "+name+" not found");
            }else {
                o = functionEntity.invoke(environment,environment.trace(),mem);
            }
        } catch (Throwable throwable) {
            throw new ScriptException((Exception)throwable);
        }
        return o;
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return null;
    }

    @Override
    public <T> T getInterface(Object  thiz,Class <T> clasz){
        return null;
    }

    @Override
    public CompiledScript compile(Reader reader) throws ScriptException {
        try {
            InputStream is = new ReaderInputStream(reader);
            Context context = new Context(is);
            ModuleEntity module = environment.importModule(context);
            return new BNPHPCompiledScript(module, environment);
        } catch (IOException e) {
            throw new ScriptException(e);
        } catch (Throwable e) {e.printStackTrace();
            throw new ScriptException(new Exception(e));
        }
    }

    @Override
    public Bindings createBindings() {
        return new BNPHPBindings(environment.getGlobals());
    }

    @Override
    public synchronized ScriptEngineFactory getFactory() {
        if (factory == null) {
            factory = new JPHPScriptEngineFactory();
        }
        return factory;
    }

    public void setFactory(JPHPScriptEngineFactory f) {
        factory = f;
    }

    public class BNPHPBindings implements Bindings {

        private ArrayMemory globals;

        public BNPHPBindings(ArrayMemory globals) {
            this.globals = globals;
        }

        @Override
        public Object put(String name, Object value) {
            return globals.putAsKeyString(name, MemoryUtils.valueOf(value));
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> toMerge) {
            for (String key : toMerge.keySet()) {
                put(key, toMerge.get(key));
            }
        }

        @Override
        public void clear() {
            globals.clear();
        }

        @Override
        public Set<String> keySet() {
            Set<String> set = new HashSet<String>(size());
            for (Object k : globals.keySet()) {
                set.add(k.toString());
            }
            return set;
        }

        @Override
        public Collection<Object> values() {
            return new ArrayList<Object>(Arrays.asList(globals.values()));
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>(size());
            for (Object k : globals.keySet()) {
                set.add(new AbstractMap.SimpleEntry<String, Object>(k.toString(), get(k)));
            }
            return set;
        }

        @Override
        public int size() {
            return globals.size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return globals.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return globals.getByScalar(key);
        }

        @Override
        public Object remove(Object key) {
            return globals.removeByScalar(key);
        }
    }

    public class BNPHPCompiledScript extends CompiledScript {

        private ModuleEntity module;
        private Environment environment;
        public BNPHPCompiledScript(ModuleEntity m, Environment env) {
            module = m;
            environment = env;
        }

        @Override
        public Object eval(ScriptContext context) throws ScriptException {
            try {
                try {
                    return module.include(environment);
                } catch (Exception e) {
                    environment.catchUncaught(e);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                } finally {
                    try {
                        environment.doFinal();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }
            } catch (Throwable e) {
                throw new ScriptException(new Exception(e));
            }
            return null;
        }

        @Override
        public ScriptEngine getEngine() {
            return BNPHPScriptEngine.this;
        }
    }
}
