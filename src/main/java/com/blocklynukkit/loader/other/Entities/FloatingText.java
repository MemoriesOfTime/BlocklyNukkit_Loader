package com.blocklynukkit.loader.other.Entities;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import com.blocklynukkit.loader.Loader;

public class FloatingText extends EntityMob {
    private int NetWorkId = 61;
    public int CallTick = 20;
    public String CallBack = "FloatingTextUpdate";

    public float getWidth() {
        return 0.1F;
    }

    public float getLength() {
        return 0.1F;
    }

    public float getHeight() {
        return 0.1F;
    }

    public FloatingText(FullChunk chunk, CompoundTag nbt, int callTick, String callBack){
        super(chunk, nbt);
        CallTick = callTick;
        CallBack = callBack;
        this.level.cancelUnloadChunkRequest(this.getChunkX(),this.getChunkZ());
    }
    @Override
    public int getNetworkId() {
        return this.NetWorkId;
    }
    @Override
    public void spawnTo(Player player){
        if(this.chunk != null && !this.closed){
            super.spawnTo(player);
        }
    }
    @Override
    public void saveNBT() {
        super.saveNBT();
    }
    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            this.level.cancelUnloadChunkRequest(this.getChunkX(),this.getChunkZ());
            this.timing.startTiming();
            if(currentTick%CallTick==0){
                Loader.plugin.call(CallBack,this);
                this.spawnToAll();
            }
            boolean hasUpdate = super.onUpdate(currentTick);
            this.timing.stopTiming();
            return hasUpdate;
        }
    }
    @Override
    public void close() {
        this.setNameTagVisible(false);
        this.setNameTagAlwaysVisible(false);
        super.close();
    }
    @Override
    public String getName(){
        return "BNFloatingText";
    }

}
