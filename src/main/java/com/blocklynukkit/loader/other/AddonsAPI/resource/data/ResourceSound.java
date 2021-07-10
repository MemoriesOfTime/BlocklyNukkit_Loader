package com.blocklynukkit.loader.other.AddonsAPI.resource.data;

import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceData;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceDataType;

import java.io.FileInputStream;
import java.io.IOException;

public class ResourceSound extends ResourceData {
    public ResourceSound(String soundPath){
        this.dataType = ResourceDataType.SOUND;
        try {
            FileInputStream stream = new FileInputStream(soundPath);
            this.content = new byte[stream.available()];
            stream.read(this.content);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
