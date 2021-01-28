package com.blocklynukkit.loader.scriptloader.bases;

import java.util.List;

public interface Interpreter {
    void putEngine(String name,String code);
    String toString(Object var);
    boolean isThisLanguage(Object var);
    List<String> getPragma(String code);
}
