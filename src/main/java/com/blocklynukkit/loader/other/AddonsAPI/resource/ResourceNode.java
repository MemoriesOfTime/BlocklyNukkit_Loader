package com.blocklynukkit.loader.other.AddonsAPI.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourceNode {
    public Map<String, ResourceData> data = new HashMap<>();

    public ResourceNode write(ZipOutputStream mcpack){
        try {
            for(Map.Entry<String, ResourceData> entry : data.entrySet()){
                mcpack.putNextEntry(new ZipEntry(entry.getKey()));
                mcpack.write(entry.getValue().content);
                mcpack.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ResourceNode putData(String path, ResourceData data){
        this.data.put(path, data);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = Integer.MIN_VALUE;
        for(Map.Entry<String, ResourceData> entry:data.entrySet()){
            hash += entry.getKey().hashCode();
            hash += entry.getValue().hashCode();
        }
        return hash;
    }
}
