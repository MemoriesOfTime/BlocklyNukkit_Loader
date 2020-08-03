package com.blocklynukkit.loader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.level.ChunkUnloadEvent;
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
import com.blocklynukkit.loader.script.event.EntityDamageByPlayerEvent;
import com.blocklynukkit.loader.script.event.EntityKilledByEntityEvent;
import com.blocklynukkit.loader.script.event.PlayerDamageByPlayerEvent;
import com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent;
import com.xxmicloxx.NoteBlockAPI.SongDestroyingEvent;
import com.xxmicloxx.NoteBlockAPI.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.SongStoppedEvent;
import com.blocklynukkit.loader.script.event.StoneSpawnEvent;

public class EventLoader implements Listener {

    private Loader plugin;

    public EventLoader(Loader plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(PlayerInteractEntityEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwim(PlayerToggleSwimEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGlide(PlayerToggleGlideEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSneak(PlayerToggleSneakEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSprint(PlayerToggleSprintEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFlight(PlayerToggleFlightEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnterBed(PlayerBedEnterEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeaveBed(PlayerBedLeaveEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerPreLoginEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFormResponse(PlayerFormRespondedEvent event){
        synchronized (Loader.functioncallback){
            if(!event.wasClosed()&&event.getResponse()!=null){
                if(Loader.functioncallback.keySet().contains((Integer) event.getFormID())){
                    int a = event.getFormID();
                    Loader.callEventHandler(event, Loader.functioncallback.get(a));
                }
                if(Loader.scriptObjectMirrorCallback.keySet().contains((Integer) event.getFormID())){
                    int a = event.getFormID();
                    Loader.scriptObjectMirrorCallback.get(a).call(Loader.windowManager,event);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventory(InventoryTransactionEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event){
//        Player player=event.getPlayer();
//        DummyBossBar.Builder builder=new DummyBossBar.Builder(player);
//        player.createBossBar()
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntity(EntitySpawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDespawn(EntityDespawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
        if(event.getDamager() instanceof Player && (event.getDamager().getNetworkId()==63 || event.getDamager().getNetworkId()==319)){
            plugin.callEventHandler(new EntityDamageByPlayerEvent(event),"EntityDamageByPlayerEvent","EntityDamageByPlayerEvent");
            if(event.getEntity() instanceof Player && (event.getEntity().getNetworkId()==63 || event.getEntity().getNetworkId()==319)){
                plugin.callEventHandler(new PlayerDamageByPlayerEvent(new EntityDamageByPlayerEvent(event)),"PlayerDamageByPlayerEvent","PlayerDamageByPlayerEvent");
            }
        }else {
            if(event.getEntity() instanceof Player && (event.getEntity().getNetworkId()==63 || event.getEntity().getNetworkId()==319)){
                plugin.callEventHandler(new PlayerDamageByPlayerEvent(new EntityDamageByPlayerEvent(event)),"PlayerDamageByEntityEvent","PlayerDamageByEntityEvent");
            }
        }
        if(event.getEntity().getHealth()-event.getFinalDamage()<0.5){
            plugin.callEventHandler(new EntityKilledByEntityEvent(event),"EntityKilledByEntityEvent","EntityKilledByEntityEvent");
            if(event.getDamager() instanceof Player && (event.getDamager().getNetworkId()==63 || event.getDamager().getNetworkId()==319)){
                plugin.callEventHandler(new EntityKilledByEntityEvent(event),"EntityKilledByPlayerEvent","EntityKilledByPlayerEvent");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTeleport(EntityTeleportEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawnEvent(ItemDespawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawnEvent(ItemSpawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjecttileHit(ProjectileHitEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityLevelChangeEvent(EntityLevelChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInventoryChange(EntityInventoryChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionPrime(EntityExplosionPrimeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormEvent(BlockFormEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockGrow(BlockGrowEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameDrop(ItemFrameDropItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstoneUpdateEvent(RedstoneUpdateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClock(InventoryCloseEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryPickupArrow(InventoryPickupArrowEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIntentoryPickupItem(InventoryPickupItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionApply(PotionApplyEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionCollideEvent(PotionCollideEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketReceive(DataPacketReceiveEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketSend(DataPacketSendEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQueryRegenerate(QueryRegenerateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockForm(BlockFormEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
    public static void onSlotChange(FakeSlotChangeEvent event){
        Loader.plugin.call(event.getClass().getSimpleName(),event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongEnd(SongEndEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongDestroy(SongDestroyingEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongStop(SongStoppedEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event){
        if(event!=null)if(event.getChunk()!=null)return;
        for(Entity entity:event.getChunk().getEntities().values()){
            String type = entity.getName();
            if(type.equals("BNNPC")||type.equals("BNFloatingText")){
                event.setCancelled();
                break;
            }
        }
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSetting(PlayerSettingsRespondedEvent event){
        if(!event.wasClosed()&&event.getResponse()!=null){
            Loader.callEventHandler(event,Loader.serverSettingCallback.get(event.getPlayer().getName()));
        }
    }
}
