package com.blocklynukkit.loader.scriptloader.scriptengines;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.List;

public class WasmScriptEngineFactory implements ScriptEngineFactory {
    private static final String [] EXTENSIONS = {
            "wasm", ".wasm",
    };

    private static final String [] MIMETYPES = {
            "application/wasm"
    };

    private static final String [] NAMES = {
            "wasm", "webassembly","wasi","wasmtime","wasmtime-java"
    };

    private List<String> extensions;
    private List<String> mimeTypes;
    private List<String> names;

    public WasmScriptEngineFactory(){
        extensions = Arrays.asList(EXTENSIONS);
        mimeTypes = Arrays.asList(MIMETYPES);
        names = Arrays.asList(NAMES);
    }

    @Override
    public String getEngineName() {
        return "wasmtime-java";
    }

    @Override
    public String getEngineVersion() {
        return "0.3.0-BlocklyNukkit";
    }

    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getLanguageName() {
        return "webassembly";
    }

    @Override
    public String getLanguageVersion() {
        return "1.0";
    }

    @Override
    public Object getParameter(String key) {
        return "Not Implemented";
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        return "Not Implemented";
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "Not Implemented";
    }

    @Override
    public String getProgram(String... statements) {
        return "Not Implemented";
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new WasmScriptEngine();
    }
}
