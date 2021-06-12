package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.block.BlockHopper;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.EventLoader;
import com.blocklynukkit.loader.other.inventoty.HopperFakeInventory;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeInventory;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;

public final class InventoryManager extends BaseManager {
    public InventoryManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
    @Comment(value = "新建一个箱子物品栏")
    public ChestFakeInventory addInv(@Comment(value = "是否为大箱子") boolean isDoubleChest
            ,@Comment(value = "包含的物品，需要使用`Java.to`函数转换") Item[] item
            ,@Comment(value = "物品栏标题") String name){
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
    @Comment(value = "新建一个漏斗物品栏")
    public HopperFakeInventory addHopperInv(@Comment(value = "包含的物品，需要使用`Java.to`函数转换") Item[] item
            ,@Comment(value = "物品栏标题") String name){
        HopperFakeInventory inv = new HopperFakeInventory();
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        if (!name.isEmpty()){
            inv.setName(name);
        }
        inv.addListener(EventLoader::onSlotChange);
        return inv;
    }
    @Comment(value = "获取物品栏中的所有物品")
    public List<Item> getItemsInInv(@Comment(value = "物品栏对象") Inventory inv){
        ArrayList<Item> arrayList=new ArrayList<>(inv.getContents().values());
        return arrayList;
    }
    @Comment(value = "向玩家展示虚拟物品栏")
    public void showFakeInv(@Comment(value = "要向展示的玩家") Player player,@Comment(value = "展示的虚拟物品栏") FakeInventory inv){
        if(inv!=null){
            player.addWindow(inv);
        }
    }
    @Comment(value = "通过Map编辑物品栏的物品，会直接操作输入的物品栏对象")
    public Inventory editInvByMap(@Comment(value = "物品栏对象") Inventory inv
            ,@Comment(value = "内容Map<槽位:number,物品:cn.nukkit.item.Item>") Map<Integer, Item> invContent){
        inv.setContents(invContent);
        return inv;
    }
    @Comment(value = "编辑物品栏中的物品，会直接操作输入的物品栏对象")
    public Inventory editInv(@Comment(value = "物品栏对象") Inventory inv
            ,@Comment(value = "所有的物品数组，需要通过`Java.to`函数转换") Item[] item){
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        return inv;
    }
    @Comment(value = "编辑物品栏指定槽位上的物品，会直接操作输入的物品栏对象")
    public Inventory editInvBySlot(@Comment(value = "物品栏对象") Inventory inv
            ,@Comment(value = "槽位") int slot
            ,@Comment(value = "要设置的物品") Item item){
        inv.setItem(slot,item);
        return inv;
    }
    @Comment(value = "向物品栏添加物品，会直接操作输入的物品栏对象")
    public Inventory addItemToInv(@Comment(value = "物品栏对象") Inventory inv
            ,@Comment(value = "要添加的物品") Item item) {
        if (inv.canAddItem(item)) {
            inv.addItem(item);
        }
        return inv;
    }
    @Comment(value = "将指定物品从物品栏移除，会直接操作输入的物品栏对象")
    public Inventory removeItemFromInv(@Comment(value = "物品栏对象") Inventory inv
            ,@Comment(value = "要移除的物品") Item item){
        inv.removeItem(item);
        return inv;
    }
    @Comment(value = "检测物品是否在物品栏中")
    public boolean containsItemInInv(@Comment(value = "物品栏对象") Inventory inv
            ,@Comment(value = "要检测的物品") Item item){
        return inv.contains(item);
    }
    @Comment(value = "获取指定位置上的方块所包含的物品栏对象，返回值是指定位置方块的物品栏的拷贝")
    public Inventory getBlockInv(@Comment(value = "坐标") Position pos){
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
    @Comment(value = "设置指定位置的方块的物品栏对象")
    public void setBlockInv(@Comment(value = "坐标") Position pos,@Comment(value = "要被设置到方块上的物品栏")  Inventory inv){ ;
        pos.fromObject(new Vector3(pos.getFloorX(),pos.getFloorY(),pos.getFloorZ()),pos.getLevel());
        BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityContainer){
            if (blockEntity instanceof BlockEntityChest){
                BlockEntityChest chest = ((BlockEntityChest) blockEntity);
                chest.getInventory().setContents(inv.getContents());
                Inventory chestr = ((BlockEntityChest)blockEntity).getRealInventory();
                chestr.setContents(inv.getContents());
                for (int i=0;i<chest.getInventory().getSize();i++){
                    chest.getInventory().setItem(i,inv.getItem(i));
                }
                for (int i=0;i<chest.getRealInventory().getSize();i++){
                    chest.getRealInventory().setItem(i,inv.getItem(i));
                }
                ((BlockEntityChest) blockEntity).saveNBT();
            }else if(blockEntity instanceof BlockEntityHopper){
                BlockEntityHopper hopper = (BlockEntityHopper)blockEntity;
                for(int i=0;i<hopper.getInventory().getSize();i++){
                    hopper.setItem(i,inv.getItem(i));
                }
                for(int i=0;i<hopper.getInventory().getSize();i++){
                    hopper.getInventory().setItem(i,inv.getItem(i));
                }
                hopper.saveNBT();
            }else{
                for(int i=0;i<inv.getSize();i++){
                    ((BlockEntityContainer) blockEntity).setItem(i,inv.getItem(i));
                }
            }
            blockEntity.saveNBT();
        }
    }
    @Comment(value = "获取玩家的背包物品栏，返回的新物品栏是玩家物品栏的拷贝而非玩家真实背包")
    public Inventory getPlayerInv(@Comment(value = "玩家对象") Player player) {
        DoubleChestFakeInventory inv = new DoubleChestFakeInventory();
        if(player==null)return null;
        if(player.getInventory()==null)return null;
        if(player.getInventory().getContents()==null)return null;
        inv.setContents(player.getInventory().getContents());
        inv.addListener(EventLoader::onSlotChange);
        return inv;
    }
    @Comment(value = "设置玩家的物品栏")
    public void setPlayerInv(@Comment(value = "玩家对象") Player player,@Comment(value = "新的物品栏") Inventory inv){
        player.getInventory().setContents(inv.getContents());
    }
    @Comment(value = "获取实体头盔槽的物品")
    public Item getEntityHelmet(@Comment(value = "实体对象") Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getHelmet();
        }else{
            return null;
        }
    }
    @Comment(value = "获取实体胸甲槽的物品")
    public Item getEntityChestplate(@Comment(value = "实体对象") Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getChestplate();
        }else{
            return null;
        }
    }
    @Comment(value = "获取实体护腿槽的物品")
    public Item getEntityLeggings(@Comment(value = "实体对象") Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getLeggings();
        }else{
            return null;
        }
    }
    @Comment(value = "获取实体靴子槽的物品")
    public Item getEntityBoots(@Comment(value = "实体对象") Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getBoots();
        }else{
            return null;
        }
    }
    @Comment(value = "获取实体手中的物品")
    public Item getEntityItemInHand(@Comment(value = "实体对象") Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getInventory().getItemInHand();
        }else{
            return null;
        }
    }
    @Comment(value = "获取实体副手中的物品")
    public Item getEntityItemInOffHand(Entity entity){
        if(entity instanceof EntityHuman){
            return ((EntityHuman)entity).getOffhandInventory().getItem(0);
        }else{
            return null;
        }
    }
    @Comment(value = "设置实体胸甲槽中的物品")
    public void setEntityItemChestplate(@Comment(value = "实体对象") Entity entity
            ,Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setChestplate(item);
        }
    }
    @Comment(value = "设置实体护腿槽中的物品")
    public void setEntityItemLeggings(@Comment(value = "实体对象") Entity entity
            ,@Comment(value = "物品对象") Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setLeggings(item);
        }
    }
    @Comment(value = "设置实体头盔槽中的物品")
    public void setEntityItemHelmet(@Comment(value = "实体对象") Entity entity
            ,@Comment(value = "物品对象") Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setHelmet(item);
        }
    }
    @Comment(value = "设置实体靴子槽中的物品")
    public void setEntityItemBoots(@Comment(value = "实体对象") Entity entity
            ,@Comment(value = "物品对象") Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setBoots(item);
        }
    }
    @Comment(value = "设置实体手中的物品")
    public void setEntityItemInHand(@Comment(value = "实体对象") Entity entity
            ,@Comment(value = "物品对象") Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getInventory().setItemInHand(item);
        }
    }
    @Comment(value = "设置实体副手中的物品")
    public void setEntityItemInOffHand(@Comment(value = "实体对象") Entity entity
            ,@Comment(value = "物品对象") Item item){
        if(entity instanceof EntityHuman){
            ((EntityHuman)entity).getOffhandInventory().setItem(0,item);
        }
    }
    @Comment(value = "获取物品栏指定槽位上的物品")
    public Item getInventorySlot(@Comment(value = "物品栏对象") Inventory inv,@Comment(value = "槽位号") int slot){
        return inv.getItem(slot);
    }

}
