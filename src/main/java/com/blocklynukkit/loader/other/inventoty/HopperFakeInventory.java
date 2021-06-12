package com.blocklynukkit.loader.other.inventoty;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import com.nukkitx.fakeinventories.inventory.FakeInventory;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;

public class HopperFakeInventory extends FakeInventory {
    private String name;

    public HopperFakeInventory() {
        this((InventoryHolder)null);
    }

    public HopperFakeInventory(InventoryType type) {
        super(type);
    }

    public HopperFakeInventory(InventoryHolder holder) {
        super(InventoryType.HOPPER, holder, null);
    }

    public HopperFakeInventory(InventoryType type, InventoryHolder holder) {
        super(type, holder);
    }

    public HopperFakeInventory(InventoryHolder holder, String title) {
        super(InventoryType.HOPPER, holder, title);
    }

    public HopperFakeInventory(InventoryType type, InventoryHolder holder, String title) {
        super(type, holder, title);
    }

    @Override
    protected List<BlockVector3> onOpenBlock(Player who) {
        BlockVector3 blockPosition = new BlockVector3((int)who.x, (int)who.y + 2, (int)who.z);
        this.placeHopper(who, blockPosition);
        return Collections.singletonList(blockPosition);
    }

    protected void placeHopper(Player who, BlockVector3 pos) {
        UpdateBlockPacket updateBlock = new UpdateBlockPacket();
        updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(154, 0);
        updateBlock.flags = 11;
        updateBlock.x = pos.x;
        updateBlock.y = pos.y;
        updateBlock.z = pos.z;
        who.dataPacket(updateBlock);
        BlockEntityDataPacket blockEntityData = new BlockEntityDataPacket();
        blockEntityData.x = pos.x;
        blockEntityData.y = pos.y;
        blockEntityData.z = pos.z;
        blockEntityData.namedTag = getNbt(pos, this.getName());
        who.dataPacket(blockEntityData);
    }

    private static byte[] getNbt(BlockVector3 pos, String name) {
        CompoundTag nbt = (new CompoundTag()).putList(new ListTag("Items")).putString("id", "Hopper").putInt("x", (int)pos.x).putInt("y", (int)pos.y).putInt("z", (int)pos.z).putString("CustomName", name == null ? "Hopper" : name);
        try {
            return NBTIO.write(nbt, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException var4) {
            throw new RuntimeException("Unable to create NBT for hopper");
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
