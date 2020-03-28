package dls.icesight.blocklynukkit;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent;
import dls.icesight.blocklynukkit.script.StoneSpawnEvent;

import javax.script.Invocable;
import javax.script.ScriptException;

public class EventLoader implements Listener {

    private Loader plugin;

    public EventLoader(Loader plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler
    public void onLeaveBed(PlayerBedLeaveEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler
    public void onChat(PlayerChatEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler
    public void onLogin(PlayerPreLoginEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event){
        for(int a:Loader.functioncallback.keySet()){
            if(event.getFormID()==a){
                plugin.callEventHandler(event,Loader.functioncallback.get(a));
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onInventory(InventoryTransactionEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        plugin.callEventHandler(event, event.getEventName());
        PlayerInteractEvent.Action action = event.getAction();
        if(action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)){
            plugin.callEventHandler(event, "RightClickBlockEvent");
        }else if(action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)){
            plugin.callEventHandler(event,"LeftClickBlockEvent");
        }else if(action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR)){
            plugin.callEventHandler(event,"ClickOnAirEvent");
        }else if(action.equals(PlayerInteractEvent.Action.PHYSICAL)){
            plugin.callEventHandler(event,"PhysicalTouchEvent");
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
//        Player player=event.getPlayer();
//        DummyBossBar.Builder builder=new DummyBossBar.Builder(player);
//        player.createBossBar()
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntity(EntitySpawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntityDespawn(EntityDespawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onProjecttileHit(ProjectileHitEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntityLevelChangeEvent(EntityLevelChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onEntityInventoryChange(EntityInventoryChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onExplosionPrime(EntityExplosionPrimeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onBlockFormEvent(BlockFormEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onItemFrameDrop(ItemFrameDropItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onRedstoneUpdateEvent(RedstoneUpdateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onInventoryClock(InventoryCloseEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onInventoryPickupArrow(InventoryPickupArrowEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onIntentoryPickupItem(InventoryPickupItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onPotionApply(PotionApplyEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onPotionCollideEvent(PotionCollideEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onDataPacketReceive(DataPacketReceiveEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onDataPacketSend(DataPacketSendEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onQueryRegenerate(QueryRegenerateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler
    public void onLiquid(LiquidFlowEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
        Position position = Position.fromObject(new Vector3(
                        event.getBlock().x,event.getBlock().y,event.getBlock().z
        ),event.getBlock().getLevel());
        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                if(position.getLevelBlock().getId()==4||position.getLevelBlock().getId()==1){
                    plugin.callEventHandler(new StoneSpawnEvent(position,position.getLevelBlock()),"StoneSpawnEvent","StoneSpawnEvent");
                }
            }
        },5);
    }
    //
    public static void onSlotChange(FakeSlotChangeEvent event){
        try {
            ((Invocable)Loader.plugin.engine).invokeFunction(event.getClass().getSimpleName(),event);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
