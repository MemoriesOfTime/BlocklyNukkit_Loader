package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceJSON extends ResourceData {
    public ResourceJSON(String json){
        this.dataType = ResourceDataType.JSON;
        this.content = json.getBytes(StandardCharsets.UTF_8);
    }
}
