package com.blocklynukkit.loader.other.Entities;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Clothes;
import com.blocklynukkit.loader.other.data.MemoryStorage;

import java.nio.charset.StandardCharsets;

public class BNModel extends EntityHuman {
    public String tickCallback = null;
    public int callTick;
    public String attackCallback = null;
    public String interactCallback = null;
    public float length = 0.6f;
    public float width = 0.6f;
    public float height = 1.8f;
    public MemoryStorage<String, Object> dataStorage = new MemoryStorage<>();

    public BNModel(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
        super.close();
    }

    public BNModel(FullChunk chunk, CompoundTag nbt, Clothes clothes, double length, double width, double height, double scale, String tickCallback, int callTick, String attackCallback, String interactCallback) {
        super(chunk, nbt.putString("NameTag", "").putString("name", "BNNPC")
                .putCompound("Skin", new CompoundTag()).putBoolean("ishuman", true).putBoolean("npc", true)
                .putFloat("scale", (float) scale));
        Skin sk = clothes.build();
        nbt.putByteArray("Data",sk.getSkinData().data);
        nbt.putString("ModelID", sk.getSkinId())
                .putString("GeometryName", clothes.gen)
                .putByteArray("GeometryData", sk.getGeometryData().getBytes(StandardCharsets.UTF_8));
        nbt.putString("SkinResourcePatch","{\"geometry\" : {\"default\" : \""+clothes.gen+"\"}}\n");
        this.setSkin(clothes.build());
        this.setNameTag("");
        this.setNameTagVisible(false);
        this.setNameTagAlwaysVisible(false);
        this.setScale((float) scale);
        this.attackCallback = attackCallback;
        this.interactCallback = interactCallback;
        this.callTick = callTick;
        this.tickCallback = tickCallback;
        this.spawnToAll();
        this.length = (float) length;
        this.width = (float) width;
        this.height = (float) height;
    }

    public void resetModelSkin(String modelSkinName){
        Skin tmp = new Clothes(modelSkinName).build();
        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.oldSkinName = this.getSkin().getGeometryData();
        packet.newSkinName = tmp.getGeometryData();
        packet.skin = tmp;
        packet.uuid = this.getUniqueId();
        this.setSkin(tmp);
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(),packet);
    }

    @Override
    public float getLength(){
        return length;
    }

    @Override
    public float getWidth(){
        return width;
    }

    @Override
    public float getHeight(){
        return height;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        Loader.plugin.call(attackCallback, this, source);
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            if(tickCallback != null && currentTick % callTick==0){
                Loader.plugin.call(tickCallback, this, currentTick);
                this.spawnToAll();
            }
            return super.onUpdate(currentTick);
        }
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        Loader.plugin.call(interactCallback, this, player, item, clickedPos);
        return true;
    }

    @Override
    public String getName(){
        return "BNModel";
    }
}
