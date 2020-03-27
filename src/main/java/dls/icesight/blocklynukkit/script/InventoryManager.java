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
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeInventory;
import dls.icesight.blocklynukkit.EventLoader;
import dls.icesight.blocklynukkit.Loader;

import java.util.Map;

public class InventoryManager {

    public FakeInventory addInv(boolean isDoubleChest, Item[] item){
        FakeInventory inv;
        if (isDoubleChest){
            inv = new DoubleChestFakeInventory();
        }else{
            inv = new ChestFakeInventory();
        }
        for(int i=0;i<inv.getSize()&&i<item.length;i++){
            inv.setItem(i,item[i]);
        }
        inv.addListener(EventLoader::onSlotChange);
        return inv;
    }
    public void showFakeInv(Player player,FakeInventory inv){
        player.addWindow(inv);
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
    public FakeInventory getBlockInv(Position pos){
        pos.fromObject(new Vector3(pos.getFloorX(),pos.getFloorY(),pos.getFloorZ()),pos.getLevel());
        BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityContainer){
            ChestFakeInventory inv = new ChestFakeInventory();
            for(int i=0;i<((BlockEntityContainer) blockEntity).getSize();i++){
                inv.setItem(i,((BlockEntityContainer) blockEntity).getItem(i));
            }
            blockEntity.close();
            return inv;
        }
        return null;
    }
    public void setBlockInv(Position pos, Inventory inv){
        BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);
        if(blockEntity instanceof BlockEntityContainer){
            for(int i=0;i<inv.getSize();i++){
                ((BlockEntityContainer) blockEntity).setItem(i,inv.getItem(i));
            }
        }
    }
    public FakeInventory getPlayerInv(Player player) {
        DoubleChestFakeInventory inv = new DoubleChestFakeInventory();
        inv.setContents(player.getInventory().getContents());
        return inv;
    }
    public void setPlayerInv(Player player, Inventory inv){
        player.getInventory().setContents(inv.getContents());
    }
}
