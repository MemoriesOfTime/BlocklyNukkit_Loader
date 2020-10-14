package com.blocklynukkit.loader.scriptloader.bases;

import java.util.LinkedHashMap;
import java.util.List;

public interface Packager {
    LinkedHashMap<String,String> unpack(byte[] Package);
    byte[] pack(LinkedHashMap<String,String> codes);
}
