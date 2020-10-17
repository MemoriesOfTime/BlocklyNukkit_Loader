package com.blocklynukkit.loader.scriptloader.bases;

import java.util.LinkedHashMap;

public interface StringPackager {
    LinkedHashMap<String,String> unpack(String Package);
    String pack2String(LinkedHashMap<String,String> codes);
}
