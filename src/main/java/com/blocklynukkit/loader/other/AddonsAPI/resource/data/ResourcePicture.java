package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;

import java.io.FileInputStream;
import java.io.IOException;

public class ResourcePicture extends ResourceData {
    public ResourcePicture(String picturePath){
        this.dataType = ResourceDataType.PICTURE;
        try {
            FileInputStream stream = new FileInputStream(picturePath);
            this.content = new byte[stream.available()];
            stream.read(this.content);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
