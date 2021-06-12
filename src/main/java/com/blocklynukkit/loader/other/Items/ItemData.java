package com.blocklynukkit.loader.other.Items;

public class ItemData {
    public String name;
    public int id;
    Integer oldId;
    Integer oldData;
    public String toString() {
        return "RuntimeItems.Entry(name=" + this.name + ", id=" + this.id + ", oldId=" + this.oldId + ", oldData=" + this.oldData + ")";
    }
}
