package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.blocklynukkit.loader.EventLoader;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeInventory;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

public class InventoryManager {
    public Inventory addInv(boolean isDoubleChest, Item[] item, String name){
        ChestFakeInventory inv;
        if (isDoubleChest){
            inv = new DoubleChestFakeInventory();
        }else{
            inv = new ChestFakeInventory();
        }
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        if (!name.isEmpty()){
            inv.setName(name);
        }
        inv.addListener(EventLoader::onSlotChange);
        return inv;
    }
    public List getItemsInInv(Inventory inv){
        ArrayList<Item> arrayList=new ArrayList<>(inv.getContents().values());
        return arrayList;
    }
    public void showFakeInv(Player player,FakeInventory inv){
        if(inv!=null){
            player.addWindow(inv);
        }
    }
    public Inventory editInvByMap(Inventory inv, Map<Integer, Item> invContent){
        inv.setContents(invContent);
        return inv;
    }
    public Inventory editInv(Inventory inv, Item[] item){
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        return inv;
    }
    public Inventory editInvBySlot(Inventory inv, int slot, Item item){
        inv.setItem(slot,item);
        return inv;
    }
    public Inventory addItemToInv(Inventory inv, Item item) {
        if (inv.canAddItem(item)) {
            inv.addItem(item);
        }
        return inv;
    }
    public Inventory removeItemFromInv(Inventory inv, Item item){
        inv.removeItem(item);
        return inv;
    }
    public boolean containsItemInInv(Inventory inv,Item item){
        return inv.contains(item);
    }
    public Inventory getBlockInv(Position pos){
        pos.fromObject(new Vector3(pos.getFloorX(),pos.getFloorY(),pos.getFloorZ()),pos.getLevel());
        BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityContainer){
            ChestFakeInventory inv;
            blockEntity.saveNBT();
            if (blockEntity instanceof BlockEntityChest && ((BlockEntityChest) blockEntity).isPaired()){
                inv = new DoubleChestFakeInventory();
                inv.setContents(((BlockEntityChest) blockEntity).getInventory().getContents());
            }else {
                inv = new ChestFakeInventory();
                for(int i=0;i<((BlockEntityContainer) blockEntity).getSize();i++){
                    inv.setItem(i,((BlockEntityContainer) blockEntity).getItem(i));
                }
            }
            inv.addListener(EventLoader::onSlotChange);
            return inv;
        }
        return null;
    }
    public void setBlockInv(Position pos, Inventory inv){
        pos.fromObject(new Vector3(pos.getFloorX(),pos.getFloorY(),pos.getFloorZ()),pos.getLevel());
        BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityContainer){
            if (blockEntity instanceof BlockEntityChest){
                ((BlockEntityChest) blockEntity).getInventory().setContents(inv.getContents());
                Inventory chest = ((BlockEntityChest)blockEntity).getRealInventory();
                chest.setContents(inv.getContents());
            }else{
                for(int i=0;i<inv.getSize();i++){
                    ((BlockEntityContainer) blockEntity).setItem(i,inv.getItem(i));
                }
            }
            blockEntity.saveNBT();
        }
    }
    public Inventory getPlayerInv(Player player) {
        DoubleChestFakeInventory inv = new DoubleChestFakeInventory();
        inv.setContents(player.getInventory().getContents());
        inv.addListener(EventLoader::onSlotChange);
        return inv;
    }
    public void setPlayerInv(Player player, Inventory inv){
        player.getInventory().setContents(inv.getContents());
    }
    public Item getEntityHelmet(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getHelmet();
        }else{
            return null;
        }
    }
    public Item getEntityChestplate(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getChestplate();
        }else{
            return null;
        }
    }
    public Item getEntityLeggings(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getLeggings();
        }else{
            return null;
        }
    }
    public Item getEntityBoots(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getBoots();
        }else{
            return null;
        }
    }
    public Item getEntityItemInHand(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getItemInHand();
        }else{
            return null;
        }
    }
    public Item getEntityItemInOffHand(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getOffhandInventory().getItem(0);
        }else{
            return null;
        }
    }
    public void setEntityItemChestplate(Entity entity,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setChestplate(item);
        }
    }
    public void setEntityItemLeggings(Entity entity,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setLeggings(item);
        }
    }
    public void setEntityItemHelmet(Entity entity,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setHelmet(item);
        }
    }
    public void setEntityItemBoots(Entity entity,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setBoots(item);
        }
    }
    public void setEntityItemInHand(Entity entity,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setItemInHand(item);
        }
    }
    public void setEntityItemInOffHand(Entity entity,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getOffhandInventory().setItem(0,item);
        }
    }
    public Item getInventorySlot(Inventory inv,int slot){
        return inv.getItem(slot);
    }

}
