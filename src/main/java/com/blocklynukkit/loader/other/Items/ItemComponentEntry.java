package com.blocklynukkit.loader.other.Items;

import cn.nukkit.nbt.tag.CompoundTag;


public class ItemComponentEntry {
    public static final ItemComponentEntry[] EMPTY_ARRAY = new ItemComponentEntry[0];

    public final String name;
    public final CompoundTag data;

    public ItemComponentEntry(String name, CompoundTag data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public CompoundTag getData() {
        return data;
    }
}
