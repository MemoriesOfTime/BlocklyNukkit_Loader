package com.blocklynukkit.loader.other.AddonsAPI.resource;

import java.util.Arrays;

public abstract class ResourceData {
    public ResourceDataType dataType;
    public byte[] content = null;

    public ResourceData(){
        dataType = ResourceDataType.NONE;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public ResourceDataType getDataType() {
        return dataType;
    }

    public void setDataType(ResourceDataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(content);
        return hash;
    }
}
