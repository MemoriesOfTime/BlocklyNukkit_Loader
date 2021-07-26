package com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class FloatArrayTag extends Tag{
    public float[] data;

    public FloatArrayTag(String name) {
        super(name);
    }

    public FloatArrayTag(String name, float[] data) {
        super(name);
        this.data = data;
    }

    public FloatArrayTag(float... data) {
        super("");
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(data.length);
        for (float aData : data) {
            dos.writeFloat(aData);
        }
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        data = new float[length];
        for (int i = 0; i < length; i++) {
            data[i] = dis.readInt();
        }
    }

    @Override
    public String toString() {
        return "FloatArrayTag " + this.getName() + " [" + data.length + " bytes]";
    }

    @Override
    public byte getId() {
        return 12;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            FloatArrayTag floatArrayTag = (FloatArrayTag) obj;
            return ((data == null && floatArrayTag.data == null) || (data != null && Arrays.equals(data, floatArrayTag.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        float[] cp = new float[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new FloatArrayTag(getName(), cp);
    }

    @Override
    public Object parseValue() {
        return this.data;
    }
}
