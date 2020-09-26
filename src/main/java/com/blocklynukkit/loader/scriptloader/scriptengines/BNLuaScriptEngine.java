package com.blocklynukkit.loader.scriptloader.scriptengines;

import org.luaj.vm2.Lua;
import org.luaj.vm2.script.LuaScriptEngineFactory;
import org.luaj.vm2.script.LuajContext;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.*;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class BNLuaScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable, Invocable {
    //直接复制luajava项目，劳资又不用这些管你呢
    private static final String __ENGINE_VERSION__   = Lua._VERSION;
    private static final String __NAME__             = "Luaj";
    private static final String __SHORT_NAME__       = "Luaj";
    private static final String __LANGUAGE__         = "lua";
    private static final String __LANGUAGE_VERSION__ = "5.2";
    private static final String __ARGV__             = "arg";
    private static final String __FILENAME__         = "?";

    private static final ScriptEngineFactory myFactory = new LuaScriptEngineFactory();

    private LuajContext context;

    public BNLuaScriptEngine() {
        context = new LuajContext();
        context.setBindings(createBindings(), ScriptContext.ENGINE_SCOPE);
        setContext(context);

        put(LANGUAGE_VERSION, __LANGUAGE_VERSION__);
        put(LANGUAGE, __LANGUAGE__);
        put(ENGINE, __NAME__);
        put(ENGINE_VERSION, __ENGINE_VERSION__);
        put(ARGV, __ARGV__);
        put(FILENAME, __FILENAME__);
        put(NAME, __SHORT_NAME__);
        put("THREADING", null);
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return compile(new StringReader(script));
    }

    @Override
    public CompiledScript compile(Reader script) throws ScriptException {
        try {
            InputStream is = new Utf8Encoder(script);
            try {
                final Globals g = context.globals;
                final LuaFunction f = g.load(script, "script").checkfunction();
                return new LuajCompiledScript(f, g);
            } catch ( LuaError lee ) {
                throw new ScriptException(lee.getMessage() );
            } finally {
                is.close();
            }
        } catch ( Exception e ) {
            throw new ScriptException("eval threw "+e.toString());
        }
    }

    @Override
    public Object eval(Reader reader, Bindings bindings) throws ScriptException {
        return ((LuajCompiledScript) compile(reader)).eval(context.globals, bindings);
    }

    @Override
    public Object eval(String script, Bindings bindings) throws ScriptException {
        return eval(new StringReader(script), bindings);
    }

    @Override
    protected ScriptContext getScriptContext(Bindings nn) {
        throw new IllegalStateException("LuajScriptEngine should not be allocating contexts.");
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public Object eval(String script, ScriptContext context)
            throws ScriptException {
        return eval(new StringReader(script), context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context)
            throws ScriptException {
        return compile(reader).eval(context);
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return myFactory;
    }


    class LuajCompiledScript extends CompiledScript {
        final LuaFunction function;
        final Globals compiling_globals;
        LuajCompiledScript(LuaFunction function, Globals compiling_globals) {
            this.function = function;
            this.compiling_globals = compiling_globals;
        }

        public ScriptEngine getEngine() {
            return BNLuaScriptEngine.this;
        }

        public Object eval() throws ScriptException {
            return eval(getContext());
        }

        public Object eval(Bindings bindings) throws ScriptException {
            return eval(((LuajContext) getContext()).globals, bindings);
        }

        public Object eval(ScriptContext context) throws ScriptException {
            return eval(((LuajContext) context).globals, context.getBindings(ScriptContext.ENGINE_SCOPE));
        }

        Object eval(Globals g, Bindings b) throws ScriptException {
            g.setmetatable(new BindingsMetatable(b));
            LuaFunction f = function;
            if (f.isclosure())
                f = new LuaClosure(f.checkclosure().p, g);
            else {
                try {
                    f = f.getClass().newInstance();
                } catch (Exception e) {
                    throw new ScriptException(e);
                }
                f.initupvalue1(g);
            }
            return toJava(f.invoke(LuaValue.NONE));
        }
    }

    // lua脑抽了非得要byte[]才能编译

    private final class Utf8Encoder extends InputStream {
        private final Reader r;
        private final int[] buf = new int[2];
        private int n;

        private Utf8Encoder(Reader r) {
            this.r = r;
        }

        public int read() throws IOException {
            if ( n > 0 )
                return buf[--n];
            int c = r.read();
            if ( c < 0x80 )
                return c;
            n = 0;
            if ( c < 0x800 ) {
                buf[n++] = (0x80 | ( c      & 0x3f));
                return     (0xC0 | ((c>>6)  & 0x1f));
            } else {
                buf[n++] = (0x80 | ( c      & 0x3f));
                buf[n++] = (0x80 | ((c>>6)  & 0x3f));
                return     (0xE0 | ((c>>12) & 0x0f));
            }
        }
    }

    static class BindingsMetatable extends LuaTable {

        BindingsMetatable(final Bindings bindings) {
            this.rawset(LuaValue.INDEX, new TwoArgFunction() {
                public LuaValue call(LuaValue table, LuaValue key) {
                    if (key.isstring())
                        return toLua(bindings.get(key.tojstring()));
                    else
                        return this.rawget(key);
                }
            });
            this.rawset(LuaValue.NEWINDEX, new ThreeArgFunction() {
                public LuaValue call(LuaValue table, LuaValue key, LuaValue value) {
                    if (key.isstring()) {
                        final String k = key.tojstring();
                        final Object v = toJava(value);
                        if (v == null)
                            bindings.remove(k);
                        else
                            bindings.put(k, v);
                    } else {
                        this.rawset(key, value);
                    }
                    return LuaValue.NONE;
                }
            });
        }
    }

    static public LuaValue toLua(Object javaValue) {
        return javaValue == null? LuaValue.NIL:
                javaValue instanceof LuaValue? (LuaValue) javaValue:
                        CoerceJavaToLua.coerce(javaValue);
    }

    static public Object toJava(LuaValue luajValue) {
        switch ( luajValue.type() ) {
            case LuaValue.TNIL: return null;
            case LuaValue.TSTRING: return luajValue.tojstring();
            case LuaValue.TUSERDATA: return luajValue.checkuserdata(Object.class);
            case LuaValue.TNUMBER: return luajValue.isinttype()?
                    (Object) new Integer(luajValue.toint()):
                    (Object) new Double(luajValue.todouble());
            default: return luajValue;
        }
    }

    static private Object toJava(Varargs v) {
        final int n = v.narg();
        switch (n) {
            case 0: return null;
            case 1: return toJava(v.arg1());
            default:
                Object[] o = new Object[n];
                for (int i=0; i<n; ++i)
                    o[i] = toJava(v.arg(i+1));
                return o;
        }
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        //TODO: tm劳资又不用这个方法写个毛线
        return null;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        LuaValue fun = context.globals.get(LuaValue.valueOf(name));
        if(fun.isfunction()){
            try{
                if(args.length==0){
                    return toJava(fun.invoke());
                }else {
                    LuaValue[] luaArgs = new LuaValue[args.length];
                    for(int i=0;i<luaArgs.length;i++){
                        luaArgs[i] = toLua(args[i]);
                    }
                    return toJava(fun.invoke(luaArgs));
                }
            }catch (LuaError e){
                Pattern pattern = Pattern.compile("script:[0-9]+ ");
                Matcher matcher = pattern.matcher(e.getMessage());
                if(matcher.find()){
                    int line = Integer.parseInt(matcher.group(0).replaceFirst("script:","").replaceAll(" ",""));
                    ScriptException exception;
                    try {
                        //tm为啥报错
                         exception = new ScriptException(e.getMessage()==null?"Error":e.getMessage(),"Lua",line,-1);
                         throw exception;
                    }catch (java.lang.IllegalArgumentException ill){
                        throw new ScriptException(e.getMessage());
                    }
                }else {
                    throw new ScriptException(e.getMessage());
                }
            }
        }else {
            throw new NoSuchMethodException();
        }
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        //TODO: tm劳资又不用这个方法写个毛线
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        //TODO: tm劳资又不用这个方法写个毛线
        return null;
    }
}
