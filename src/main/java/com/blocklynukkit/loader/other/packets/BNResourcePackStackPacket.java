package com.blocklynukkit.loader.other.packets;

import cn.nukkit.Server;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.resourcepacks.ResourcePack;

import java.util.Arrays;

public class BNResourcePackStackPacket extends DataPacket {
    public static final byte NETWORK_ID = 7;
    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];
    public boolean isExperimental = false;
    public String gameVersion = Server.getInstance().getVersion().replaceAll("v","");

    public BNResourcePackStackPacket() {
    }

    public void decode() {
    }

    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putUnsignedVarInt((long)this.behaviourPackStack.length);
        ResourcePack[] var1 = this.behaviourPackStack;
        int var2 = var1.length;

        int var3;
        ResourcePack entry;
        for(var3 = 0; var3 < var2; ++var3) {
            entry = var1[var3];
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString("");
        }

        this.putUnsignedVarInt((long)this.resourcePackStack.length);
        var1 = this.resourcePackStack;
        var2 = var1.length;

        for(var3 = 0; var3 < var2; ++var3) {
            entry = var1[var3];
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString("");
        }

        this.putString(this.gameVersion);
        this.putLInt(2);
        this.putString("data_driven_items");
        this.putBoolean(true);
        this.putString("experimental_custom_ui");
        this.putBoolean(true);

        this.putBoolean(true);
    }

    public byte pid() {
        return 7;
    }

    public String toString() {
        return "ResourcePackStackPacket(mustAccept=" + this.mustAccept + ", behaviourPackStack=" + Arrays.deepToString(this.behaviourPackStack) + ", resourcePackStack=" + Arrays.deepToString(this.resourcePackStack) + ", isExperimental=" + this.isExperimental + ", gameVersion=" + this.gameVersion + ")";
    }
}

