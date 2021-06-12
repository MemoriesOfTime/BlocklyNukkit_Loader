package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;

import java.io.IOException;
import java.io.InputStream;

public class ResourceLogo extends ResourceData {
    public ResourceLogo(){
        this.dataType = ResourceDataType.PICTURE;
        InputStream logoStream = com.blocklynukkit.loader.Loader.class.getResourceAsStream("/pack_icon.png");
        try {
            this.content = new byte[logoStream.available()];
            logoStream.read(this.content);
            logoStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
