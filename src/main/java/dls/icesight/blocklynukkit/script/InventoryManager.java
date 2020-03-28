package dls.icesight.blocklynukkit.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeInventory;
import dls.icesight.blocklynukkit.EventLoader;
import dls.icesight.blocklynukkit.Loader;

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

}
