package com.blocklynukkit.loader.scriptloader.scriptengines;

import org.python.core.*;
import org.python.jsr223.PyScriptEngineScope;
import org.python.util.PythonInterpreter;

import javax.script.*;
import java.io.Reader;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class BNPyScriptEngine extends AbstractScriptEngine implements Compilable, Invocable, AutoCloseable{
    public PythonInterpreter interp = null;
    private Map<String,PyFunction> lambdaHashMap = new HashMap<>();public int lambdaCount = -1;
    private ScriptEngineFactory factory;
    private BNPyScriptEngine thiz;

    public BNPyScriptEngine(ScriptEngineFactory factory) {
        this.factory = factory;
        try {
            Constructor<PyScriptEngineScope> con = null;
            con = PyScriptEngineScope.class.getDeclaredConstructor(ScriptEngine.class,ScriptContext.class);
            con.setAccessible(true);
            this.interp = PythonInterpreter.threadLocalStateInterpreter(con.newInstance(this,context));
            thiz = this;
            this.put("BaseInterpreterBNPyScriptEngine",thiz);
            interp.exec(Py.newStringUTF8("def F(f):\n    return BaseInterpreterBNPyScriptEngine.newLambda(f)\n"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object eval(String script, ScriptContext context) throws ScriptException {
        return eval(compileScript(script, context), context);
    }

    private Object eval(PyCode code, ScriptContext context) throws ScriptException {
        try {
            interp.setIn(context.getReader());
            interp.setOut(context.getWriter());
            interp.setErr(context.getErrorWriter());
            Constructor<PyScriptEngineScope> con = PyScriptEngineScope.class.getDeclaredConstructor(ScriptEngine.class,ScriptContext.class);
            con.setAccessible(true);
            interp.setLocals(con.newInstance(this,context));
            String filename = (String) context.getAttribute(ScriptEngine.FILENAME);
            String[] argv = (String[]) context.getAttribute(ScriptEngine.ARGV);
            if (argv != null || filename != null) {
                PyList pyargv = new PyList();
                if (filename != null) {
                    pyargv.append(Py.java2py(filename));
                }
                if (argv != null) {
                    for (int i = 0; i < argv.length; i++) {
                        pyargv.append(Py.java2py(argv[i]));
                    }
                }
                interp.getSystemState().argv = pyargv;
            }

            return interp.eval(code).__tojava__(Object.class);
        } catch (PyException pye) {
            throw scriptException(pye);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return eval(compileScript(reader, context), context);
    }

    public Bindings createBindings() {
        return new SimpleBindings();
    }

    public ScriptEngineFactory getFactory() {
        return factory;
    }

    public CompiledScript compile(String script) throws ScriptException {
        return new PyCompiledScript(compileScript(script, context));
    }

    public CompiledScript compile(Reader reader) throws ScriptException {
        return new PyCompiledScript(compileScript(reader, context));
    }

    @Override
    public Object get(String key){
        if(lambdaHashMap.containsKey(key)){
            return lambdaHashMap.get(key);
        }
        return super.get(key);
    }

    public String newLambda(PyFunction fun){
        lambdaCount++;
        String lambdaName = "Lambda_"+lambdaCount;
        this.lambdaHashMap.put(lambdaName,fun);
        return lambdaName;
    }

    private PyCode compileScript(String script, ScriptContext context) throws ScriptException {
        try {
            String filename = (String) context.getAttribute(ScriptEngine.FILENAME);
            if (filename == null) {
                return interp.compile(script);
            } else {
                interp.getLocals().__setitem__(Py.newString("__file__"), Py.newString(filename));
                return interp.compile(script, filename);
            }
        } catch (PyException pye) {
            throw scriptException(pye);
        }
    }

    private PyCode compileScript(Reader reader, ScriptContext context) throws ScriptException {
        try {
            String filename = (String) context.getAttribute(ScriptEngine.FILENAME);
            if (filename == null) {
                return interp.compile(reader);
            } else {
                interp.getLocals().__setitem__(Py.newString("__file__"), Py.newString(filename));
                return interp.compile(reader, filename);
            }
        } catch (PyException pye) {
            throw scriptException(pye);
        }
    }

    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException,
            NoSuchMethodException {
        try {
            Constructor<PyScriptEngineScope> con = PyScriptEngineScope.class.getDeclaredConstructor(ScriptEngine.class,ScriptContext.class);
            con.setAccessible(true);
            interp.setLocals(con.newInstance(this,context));
            if (!(thiz instanceof PyObject)) {
                thiz = Py.java2py(thiz);
            }
            PyObject method = ((PyObject) thiz).__findattr__(name);
            if (method == null) {
                throw new NoSuchMethodException(name);
            }
            //return method.__call__(Py.javas2pys(args)).__tojava__(Object.class);
            PyObject result;
            if(args != null) {
                result = method.__call__(Py.javas2pys(args));
            } else {
                result = method.__call__();
            }
            return result.__tojava__(Object.class);
        } catch (PyException pye) {
            throw scriptException(pye);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object invokeFunction(String name, Object... args) throws ScriptException,
            NoSuchMethodException {
        if(lambdaHashMap.containsKey(name)){
            if(args.length==0){
                return lambdaHashMap.get(name).__call__().__tojava__(Object.class);
            }else {
                return lambdaHashMap.get(name).__call__(Py.javas2pys(args)).__tojava__(Object.class);
            }
        }
        try {
            Constructor<PyScriptEngineScope> con = PyScriptEngineScope.class.getDeclaredConstructor(ScriptEngine.class,ScriptContext.class);
            con.setAccessible(true);
            interp.setLocals(con.newInstance(this,context));
            PyObject function = interp.get(name);
            if (function == null) {
                throw new NoSuchMethodException(name);
            }
            PyObject result;
            if(args != null) {
                result = function.__call__(Py.javas2pys(args));
            } else {
                result = function.__call__();
            }
            return result.__tojava__(Object.class);
        } catch (PyException pye) {
            throw scriptException(pye);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getInterface(Class<T> clazz) {
        return getInterface(new PyModule("__jsr223__", interp.getLocals()), clazz);
    }

    public <T> T getInterface(Object obj, Class<T> clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("object expected");
        }
        if (clazz == null || !clazz.isInterface()) {
            throw new IllegalArgumentException("interface expected");
        }
        Constructor<PyScriptEngineScope> con = null;
        try {
            con = PyScriptEngineScope.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        con.setAccessible(true);
        try {
            interp.setLocals(con.newInstance(this,context));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        final PyObject thiz = Py.java2py(obj);
        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[] { clazz },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try {
                            Constructor<PyScriptEngineScope> con = PyScriptEngineScope.class.getDeclaredConstructor(ScriptEngine.class,ScriptContext.class);
                            con.setAccessible(true);
                            interp.setLocals(con.newInstance(this,context));
                            PyObject pyMethod = thiz.__findattr__(method.getName());
                            if (pyMethod == null)
                                throw new NoSuchMethodException(method.getName());
                            PyObject result;
                            if(args != null) {
                                result = pyMethod.__call__(Py.javas2pys(args));
                            } else {
                                result = pyMethod.__call__();
                            }
                            return result.__tojava__(Object.class);
                        } catch (PyException pye) {
                            throw scriptException(pye);
                        }
                    }
                });
        return proxy;
    }

    private static ScriptException scriptException(PyException pye) {
        ScriptException se = null;
        try {
            pye.normalize();

            PyObject type = pye.type;
            PyObject value = pye.value;
            PyTraceback tb = pye.traceback;

            if (__builtin__.isinstance(value, Py.SyntaxError)) {
                PyObject filename = value.__findattr__("filename");
                PyObject lineno = value.__findattr__("lineno");
                PyObject offset = value.__findattr__("offset");
                value = value.__findattr__("msg");

                se = new ScriptException(
                        Py.formatException(type, value),
                        filename == null ? "<script>" : filename.toString(),
                        lineno == null ? 0 : lineno.asInt(),
                        offset == null ? 0 : offset.asInt());
            } else if (tb != null) {
                String filename;
                if (tb.tb_frame == null || tb.tb_frame.f_code == null) {
                    filename = null;
                } else {
                    filename = tb.tb_frame.f_code.co_filename;
                }
                se = new ScriptException(
                        Py.formatException(type, value),
                        filename,
                        tb.tb_lineno);
            } else {
                se = new ScriptException(Py.formatException(type, value));
            }
            se.initCause(pye);
            return se;
        } catch (Exception ee) {
            se = new ScriptException(pye);
        }
        return se;
    }

    private class PyCompiledScript extends CompiledScript {
        private PyCode code;

        PyCompiledScript(PyCode code) {
            this.code = code;
        }

        @Override
        public ScriptEngine getEngine() {
            return thiz;
        }

        @Override
        public Object eval(ScriptContext ctx) throws ScriptException {
            return thiz.eval(code, ctx);
        }
    }

    public void close() {
        interp.close();
    }
}
