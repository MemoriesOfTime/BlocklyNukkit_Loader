package com.blocklynukkit.loader.other.packets;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.MainLogger;
import com.blocklynukkit.loader.other.Items.ItemComponentEntry;

import java.io.IOException;
import java.nio.ByteOrder;

public class ItemComponentPacket extends DataPacket {
    public ItemComponentEntry[] entries = ItemComponentEntry.EMPTY_ARRAY;

    @Override
    public byte pid() {
        return (byte) 0xa2;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.entries.length);
        try {
            for (ItemComponentEntry entry : this.entries) {
                this.putString(entry.getName());
                this.put(NBTIO.write(entry.getData(), ByteOrder.LITTLE_ENDIAN, true));
            }
        } catch (IOException e) {
            MainLogger.getLogger().error("Error while encoding NBT data of ItemComponentPacket", e);
        }
    }
}
