package com.blocklynukkit.loader.other.Items;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import com.sun.istack.internal.Nullable;

import java.io.IOException;

public class ItemComponentEntry {
    public String name;
    public CompoundTag nbt;
    public ItemComponentEntry(String name,CompoundTag nbt){
        this.name=name;
        this.nbt=nbt;
    }
    @Nullable
    public byte[] toBytes(){
        try {
            return NBTIO.write(nbt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
