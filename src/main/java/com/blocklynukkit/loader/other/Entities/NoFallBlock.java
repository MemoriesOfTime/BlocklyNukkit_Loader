package com.blocklynukkit.loader.other.Entities;

import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public class NoFallBlock extends EntityFallingBlock {
    public boolean canplace = true;
    public NoFallBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    public NoFallBlock(FullChunk chunk,CompoundTag nbt,boolean canplace){
        super(chunk, nbt);
        this.canplace=canplace;
    }
    @Override
    public float getGravity(){
        return 0f;
    }
    @Override
    public boolean onUpdate(int currentTick){
        if(canplace){
            if (this.onGround) {
                Vector3 pos = (new Vector3(this.x - 0.5D, this.y, this.z - 0.5D)).round();
                this.close();
                Block block = this.level.getBlock(pos);
                Vector3 floorPos = (new Vector3(this.x - 0.5D, this.y, this.z - 0.5D)).floor();
                Block floorBlock = this.level.getBlock(floorPos);
                if (this.getBlock() == 78 && floorBlock.getId() == 78 && (floorBlock.getDamage() & 7) != 7) {
                    int mergedHeight = (floorBlock.getDamage() & 7) + 1 + (this.getDamage() & 7) + 1;
                    EntityBlockChangeEvent event;
                    if (mergedHeight > 8) {
                        event = new EntityBlockChangeEvent(this, floorBlock, Block.get(78, 7));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            this.level.setBlock(floorPos, event.getTo(), true);
                            Vector3 abovePos = floorPos.up();
                            Block aboveBlock = this.level.getBlock(abovePos);
                            if (aboveBlock.getId() == 0) {
                                EntityBlockChangeEvent event2 = new EntityBlockChangeEvent(this, aboveBlock, Block.get(78, mergedHeight - 8 - 1));
                                this.server.getPluginManager().callEvent(event2);
                                if (!event2.isCancelled()) {
                                    this.level.setBlock(abovePos, event2.getTo(), true);
                                }
                            }
                        }
                    } else {
                        event = new EntityBlockChangeEvent(this, floorBlock, Block.get(78, mergedHeight - 1));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            this.level.setBlock(floorPos, event.getTo(), true);
                        }
                    }
                }
                return false;
            }
            return true;
        }else {
            if(this.isClosed()){
                return false;
            }else {
                return true;
            }
        }
    }
}
