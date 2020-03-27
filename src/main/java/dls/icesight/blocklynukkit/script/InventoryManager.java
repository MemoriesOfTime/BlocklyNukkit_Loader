package dls.icesight.blocklynukkit.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockHopper;
import cn.nukkit.block.BlockTrappedChest;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeInventory;
import dls.icesight.blocklynukkit.EventLoader;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    public FakeInventory addInv(boolean isDoubleChest, Item[] item){
        FakeInventory inv = addInv(isDoubleChest);
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        inv.addListener(EventLoader::onSlotChange);
        return inv;
    }
    public FakeInventory addInv(boolean isDoubleChest){
        FakeInventory inv;
        if (isDoubleChest){
            inv = new DoubleChestFakeInventory();
        }else{
            inv = new ChestFakeInventory();
        }
        return inv;
    }
    public void showFakeInv(Player player,FakeInventory inv){
        player.addWindow(inv);
    }
    public Inventory editInv(Inventory inv, HashMap<Integer, Item> invContent){
        inv.setContents(invContent);
        return inv;
    }
    public Inventory editInv(Inventory inv, Item[] item){
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        return inv;
    }
    public Inventory editInv(Inventory inv, int slot, Item item){
        inv.setItem(slot,item);
        return inv;
    }
    public Inventory getBlockInv(Position pos){
        BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityContainer){
            Inventory inv = null;
            for(int i=0;i<((BlockEntityContainer) blockEntity).getSize();i++){
                inv.setItem(i,((BlockEntityContainer) blockEntity).getItem(i));
            }
            return inv;
        }
        return null;
    }
    public Inventory getPlayerInv(Player player){
        Inventory inv = player.getInventory();
        return inv;
    }
}
