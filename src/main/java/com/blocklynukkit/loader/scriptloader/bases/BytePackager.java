package com.blocklynukkit.loader.scriptloader.bases;

import java.util.LinkedHashMap;
import java.util.List;

public interface BytePackager {
    LinkedHashMap<String,String> unpack(byte[] Package);
    byte[] pack2Byte(LinkedHashMap<String,String> codes);
}
