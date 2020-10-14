package com.blocklynukkit.loader.scriptloader.bases;

public interface Interpreter {
    void putEngine(String name,String code);
    String toString(Object var);
    boolean isThisLanguage(Object var);
}
