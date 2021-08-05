package com.blocklynukkit.loader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.event.server.*;
import cn.nukkit.event.vehicle.*;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.*;
import cn.nukkit.scheduler.Task;
import com.blocklynukkit.loader.other.ProxyPlayer;
import com.blocklynukkit.loader.other.generator.render.BaseRender;
import com.blocklynukkit.loader.script.event.FakeSlotChangeEvent;
import com.blocklynukkit.loader.script.event.StartFishingEvent;
import cn.nukkit.event.player.PlayerFishEvent;
import com.xxmicloxx.NoteBlockAPI.SongDestroyingEvent;
import com.xxmicloxx.NoteBlockAPI.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.SongStoppedEvent;
import com.blocklynukkit.loader.script.event.StoneSpawnEvent;

import java.util.Map;

public class EventLoader implements Listener {

    private Loader plugin;
    private EntityDamageByEntityEvent previous;

    public EventLoader(Loader plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event){
        plugin.callEventHandler(event,"PlayerRespawnEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(PlayerInteractEntityEvent event){
        Loader.callEventHandler(event,"PlayerInteractEntityEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event){
        Loader.callEventHandler(event,"PlayerTeleportEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwim(PlayerToggleSwimEvent event){
        Loader.callEventHandler(event,"PlayerToggleSwimEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGlide(PlayerToggleGlideEvent event){
        plugin.callEventHandler(event,"PlayerToggleGlideEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSneak(PlayerToggleSneakEvent event){
        Loader.callEventHandler(event,"PlayerToggleSneakEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSprint(PlayerToggleSprintEvent event){
        Loader.callEventHandler(event,"PlayerToggleSprintEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFlight(PlayerToggleFlightEvent event){
        Loader.callEventHandler(event,"PlayerToggleFlightEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnterBed(PlayerBedEnterEvent event){
        Loader.callEventHandler(event,"PlayerBedEnterEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeaveBed(PlayerBedLeaveEvent event){
        Loader.callEventHandler(event,"PlayerBedLeaveEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent event){
        Loader.callEventHandler(event, "PlayerChatEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerPreLoginEvent event){
        Loader.callEventHandler(event, "PlayerPreLoginEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event){
        Loader.callEventHandler(event,"PlayerQuitEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){
        Loader.callEventHandler(event, "PlayerJoinEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event){
        Loader.callEventHandler(event, "BlockBreakEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event){
        Loader.callEventHandler(event, "BlockPlaceEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFormResponse(PlayerFormRespondedEvent event){
        synchronized (Loader.functioncallback){
            if(!event.wasClosed()&&event.getResponse()!=null){
                if(Loader.functioncallback.keySet().contains((Integer) event.getFormID())){
                    int a = event.getFormID();
                    Loader.callEventHandler(event, Loader.functioncallback.get(a));
                }
            }
        }
        synchronized (Loader.windowCallbackMap){
            if(Loader.windowCallbackMap.containsKey(event.getFormID())){
                Loader.windowCallbackMap.get(event.getFormID()).call(event);
            }
        }
        Loader.callEventHandler(event,"PlayerFormRespondedEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonChangeEvent(BlockPistonChangeEvent event){
        Loader.callEventHandler(event, "BlockPistonChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockUpdateEvent(BlockUpdateEvent event){
        Loader.callEventHandler(event, "BlockUpdateEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockSpreadEvent(BlockSpreadEvent event){
        Loader.callEventHandler(event, "BlockSpreadEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoorToggleEvent(DoorToggleEvent event){
        Loader.callEventHandler(event, "DoorToggleEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstoneEvent(BlockRedstoneEvent event){
        Loader.callEventHandler(event, "BlockRedstoneEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        Loader.callEventHandler(event, "PlayerCommandPreprocessEvent");
        if(event.isCancelled())return;
        if(event.getMessage().equals("/version")||event.getMessage().equals("version")||
                event.getMessage().equals("/version ")||event.getMessage().equals("version ")){
            Player sender = event.getPlayer();
            sender.sendMessage(new TranslationContainer("nukkit.server.info.extended", sender.getServer().getName(),
                    sender.getServer().getNukkitVersion(),
                    Loader.fakeNukkitCodeVersion,
                    sender.getServer().getApiVersion(),
                    sender.getServer().getVersion(),
                    String.valueOf(ProtocolInfo.CURRENT_PROTOCOL)));
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventory(InventoryTransactionEvent event){
        Loader.callEventHandler(event, "InventoryTransactionEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event){
        Loader.callEventHandler(event, "PlayerItemHeldEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        Loader.callEventHandler(event, "PlayerInteractEvent");
        PlayerInteractEvent.Action action = event.getAction();
        if(action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)){
            Loader.callEventHandler(event, "RightClickBlockEvent");
        }else if(action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)){
            Loader.callEventHandler(event,"LeftClickBlockEvent");
        }else if(action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR)){
            Loader.callEventHandler(event,"ClickOnAirEvent");
        }else if(action.equals(PlayerInteractEvent.Action.PHYSICAL)){
            Loader.callEventHandler(event,"PhysicalTouchEvent");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event){
        Loader.callEventHandler(event, "ServerCommandEvent");
        if(event.isCancelled())return;
        if(event.getCommand().equals("/version")||event.getCommand().equals("version")||
        event.getCommand().equals("/version ")||event.getCommand().equals("version ")){
            CommandSender sender = event.getSender();
            sender.sendMessage(new TranslationContainer("nukkit.server.info.extended", sender.getServer().getName(),
                    sender.getServer().getNukkitVersion(),
                    Loader.fakeNukkitCodeVersion,
                    sender.getServer().getApiVersion(),
                    sender.getServer().getVersion(),
                    String.valueOf(ProtocolInfo.CURRENT_PROTOCOL)));
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event){
//        Player player=event.getPlayer();
//        DummyBossBar.Builder builder=new DummyBossBar.Builder(player);
//        player.createBossBar()
        Loader.callEventHandler(event, "PlayerMoveEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntity(EntitySpawnEvent event){
        Loader.callEventHandler(event, "EntitySpawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDespawn(EntityDespawnEvent event){
        Loader.callEventHandler(event, "EntityDespawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event){
        Loader.callEventHandler(event, "EntityDamageEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Loader.callEventHandler(event, "EntityDamageByEntityEvent");
        Loader.callEventHandler(event, "EntityDamageEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event){
        Loader.callEventHandler(event, "PlayerDeathEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event){
        Loader.callEventHandler(event, "EntityDeathEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTeleport(EntityTeleportEvent event){
        Loader.callEventHandler(event, "EntityTeleportEvent");
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawnEvent(ItemDespawnEvent event){
        Loader.callEventHandler(event, "ItemDespawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawnEvent(ItemSpawnEvent event){
        Loader.callEventHandler(event, "ItemSpawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjecttileHit(ProjectileHitEvent event){
        Loader.callEventHandler(event, "ProjectileHitEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        Loader.callEventHandler(event, "ProjectileLaunchEvent");
        if(event.getEntity() instanceof EntityFishingHook && event.getEntity().shootingEntity instanceof Player){
            Loader.callEventHandler(new StartFishingEvent(event),"StartFishingEvent","StartFishingEvent");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFish(PlayerFishEvent event){
        Loader.callEventHandler(event, "PlayerFishEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityLevelChangeEvent(EntityLevelChangeEvent event){
        Loader.callEventHandler(event, "EntityLevelChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInventoryChange(EntityInventoryChangeEvent event){
        Loader.callEventHandler(event, "EntityInventoryChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionPrime(EntityExplosionPrimeEvent event){
        Loader.callEventHandler(event, "EntityExplosionPrimeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event){
        Loader.callEventHandler(event, "BlockBurnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormEvent(BlockFormEvent event){
        Loader.callEventHandler(event,"BlockFormEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgniteEvent(BlockIgniteEvent event){
        Loader.callEventHandler(event, "BlockIgniteEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFade(BlockFadeEvent event){
        Loader.callEventHandler(event, "BlockFadeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFall(BlockFallEvent event){
        Loader.callEventHandler(event, "BlockFallEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockGrow(BlockGrowEvent event){
        Loader.callEventHandler(event, "BlockGrowEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent event){
        Loader.callEventHandler(event, "SignChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameDrop(ItemFrameDropItemEvent event){
        Loader.callEventHandler(event, "ItemFrameDropItemEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event){
        Loader.callEventHandler(event, "LeavesDecayEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstoneUpdateEvent(RedstoneUpdateEvent event){
        Loader.callEventHandler(event, "RedstoneUpdateEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event){
        Loader.callEventHandler(event, "CraftItemEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event){
        Loader.callEventHandler(event, "InventoryOpenEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClock(InventoryCloseEvent event){
        Loader.callEventHandler(event, "InventoryCloseEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event){
        Loader.callEventHandler(event, "FurnaceBurnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event){
        Item out = null;
        for(Map.Entry<Item,Item> entry:Loader.furnaceMap.entrySet()){
            if(entry.getKey().equals(event.getSource(),true,true)){
                Item res = event.getFurnace().getInventory().getResult();
                if((res!=null||res.getId()!=0)&&!res.equals(entry.getValue(),true,true))continue;
                if(res==null||res.getId()==0||res.getCount()==0){
                    out = entry.getValue();
                }else {
                    out=entry.getValue();
                    out.setCount(res.getCount()+1);
                }
            }
        }
        if(out!=null){
            event.setResult(out);
        }
        Loader.callEventHandler(event, "FurnaceSmeltEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryPickupArrow(InventoryPickupArrowEvent event){
        Loader.callEventHandler(event, "InventoryPickupArrowEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIntentoryPickupItem(InventoryPickupItemEvent event){
        Loader.callEventHandler(event, "InventoryPickupItemEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionCollideEvent(PotionCollideEvent event){
        Loader.callEventHandler(event, "PotionCollideEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketReceive(DataPacketReceiveEvent event){
        if(event.getPacket().pid()== ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET){
            Loader.callEventHandler(event,"PlayerLocallyInitializedEvent");
        }else if(event.getPacket().pid() == ProtocolInfo.RESOURCE_PACKS_INFO_PACKET){
            ResourcePacksInfoPacket packet = (ResourcePacksInfoPacket) event.getPacket();

        }
//        else if(event.getPacket().pid()==ProtocolInfo.EMOTE_LIST_PACKET){
//            System.out.println("玩家"+event.getPlayer().getName()+"携带的表情动作uuid列表:");
//            ((EmoteListPacket)event.getPacket()).pieceIds.forEach(e->{
//                if(!(e.equals("4c8ae710-df2e-47cd-814d-cc7bf21a3d67")||
//                        e.equals("42fde774-37d4-4422-b374-89ff13a6535a")||
//                        e.equals("9a469a61-c83b-4ba9-b507-bdbe64430582")||
//                        e.equals("ce5c0300-7f03-455d-aaf1-352e4927b54d")||
//                        e.equals("d7519b5a-45ec-4d27-997c-89d402c6b57f")||
//                        e.equals("86b34976-8f41-475b-a386-385080dc6e83")||
//                        e.equals("6d9f24c0-6246-4c92-8169-4648d1981cbb")||
//                        e.equals("7cec98d8-55cc-44fe-b0ae-2672b0b2bd37"))){
//                    System.out.println(e);
//                }
//            });
//        }
        Loader.callEventHandler(event, "DataPacketReceiveEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketSend(DataPacketSendEvent event){
        Loader.callEventHandler(event, "DataPacketSendEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQueryRegenerate(QueryRegenerateEvent event){
        Loader.callEventHandler(event, "QueryRegenerateEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockForm(BlockFormEvent event){
        Loader.callEventHandler(event, "BlockFormEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormTo(BlockFromToEvent event){
        Loader.callEventHandler(event, "BlockFromToEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLiquid(LiquidFlowEvent event){
        Loader.callEventHandler(event, "LiquidFlowEvent");
        Position position = Position.fromObject(new Vector3(
                event.getBlock().x,event.getBlock().y,event.getBlock().z
        ),event.getBlock().getLevel());
        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                if(position.getLevelBlock().getId()==4||position.getLevelBlock().getId()==1){
                    Loader.callEventHandler(new StoneSpawnEvent(position,position.getLevelBlock()),"StoneSpawnEvent","StoneSpawnEvent");
                }
            }
        },5);
    }
    public static void onSlotChange(com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent event){
        Loader.callEventHandler(new FakeSlotChangeEvent(event),"FakeSlotChangeEvent","FakeSlotChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongEnd(SongEndEvent event){
        Loader.callEventHandler(event, "SongEndEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongDestroy(SongDestroyingEvent event){
        Loader.callEventHandler(event, "SongDestroyingEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongStop(SongStoppedEvent event){
        Loader.callEventHandler(event, "SongStoppedEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        Loader.callEventHandler(event, "InventoryClickEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event){
        if(event==null||event.getChunk()==null)return;
        for(Entity entity:event.getChunk().getEntities().values()){
            if(entity==null)continue;
            if(entity.getName()==null)continue;
            if(entity.getName().equals("BNNPC")||entity.getName().equals("BNFloatingText")||entity.getName().equals("BNModel")){
                event.setCancelled();
            }
        }
        Loader.callEventHandler(event, "ChunkUnloadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSetting(PlayerSettingsRespondedEvent event){
        if(!event.wasClosed()&&event.getResponse()!=null){
            Loader.callEventHandler(event,Loader.serverSettingCallback.get(event.getPlayer().getName()));
        }
        Loader.callEventHandler(event, "PlayerSettingsRespondedEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event){
        Loader.callEventHandler(event, "CreatureSpawnEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreeperPowerEvent(CreeperPowerEvent event){
        Loader.callEventHandler(event, "CreeperPowerEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityArmorChangeEvent(EntityArmorChangeEvent event){
        Loader.callEventHandler(event, "EntityArmorChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBlockChangeEvent(EntityBlockChangeEvent event){
        Loader.callEventHandler(event, "EntityBlockChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustByBlockEvent(EntityCombustByBlockEvent event){
        Loader.callEventHandler(event, "EntityCombustByBlockEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event){
        Loader.callEventHandler(event, "EntityCombustByEntityEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustEvent(EntityCombustEvent event){
        Loader.callEventHandler(event, "EntityCombustEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event){
        Loader.callEventHandler(event, "EntityDamageByBlockEvent");
        Loader.callEventHandler(event, "EntityDamageEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByChildEntityEvent(EntityDamageByChildEntityEvent event){
        Loader.callEventHandler(event, "EntityDamageByChildEntityEvent");
        Loader.callEventHandler(event, "EntityDamageByEntityEvent");
        Loader.callEventHandler(event, "EntityDamageEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        Loader.callEventHandler(event, "EntityExplodeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityMotionEvent(EntityMotionEvent event){
        Loader.callEventHandler(event, "EntityMotionEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPortalEnterEvent(EntityPortalEnterEvent event){
        Loader.callEventHandler(event, "EntityPortalEnterEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event){
        Loader.callEventHandler(event, "EntityRegainHealthEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBowEvent(EntityShootBowEvent event){
        Loader.callEventHandler(event, "EntityShootBowEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityVehicleEnterEvent(EntityVehicleEnterEvent event){
        Loader.callEventHandler(event, "EntityVehicleEnterEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityVehicleExitEvent(EntityVehicleExitEvent event){
        Loader.callEventHandler(event, "EntityVehicleExitEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionPrimeEvent(ExplosionPrimeEvent event){
        Loader.callEventHandler(event, "ExplosionPrimeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBrewEvent(BrewEvent event){
        Loader.callEventHandler(event, "BrewEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItemEvent(EnchantItemEvent event){
        Loader.callEventHandler(event, "EnchantItemEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event){
        Loader.callEventHandler(event, "InventoryMoveItemEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStartBrewEvent(StartBrewEvent event){
        Loader.callEventHandler(event, "StartBrewEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoadEvent(ChunkLoadEvent event){
        Loader.callEventHandler(event, "ChunkLoadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkPopulateEvent(ChunkPopulateEvent event) {
        BaseRender render;
        for(int i=0;i<Loader.levelRenderList.size();i++){
             render = Loader.levelRenderList.get(i);
             if(render.canRend(event.getLevel())){
                 render.rend(event.getLevel(),event.getChunk());
             }
        }
        Loader.callEventHandler(event, "ChunkPopulateEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelInitEvent(LevelInitEvent event){
        Loader.callEventHandler(event, "LevelInitEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelLoadEvent(LevelLoadEvent event){
        Loader.callEventHandler(event, "LevelLoadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelSaveEvent(LevelSaveEvent event){
        Loader.callEventHandler(event, "LevelSaveEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelUnloadEvent(LevelUnloadEvent event){
        Loader.callEventHandler(event, "LevelUnloadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnChangeEvent(SpawnChangeEvent event){
        Loader.callEventHandler(event, "SpawnChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThunderChangeEvent(ThunderChangeEvent event){
        Loader.callEventHandler(event, "ThunderChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChangeEvent(WeatherChangeEvent event){
        Loader.callEventHandler(event, "WeatherChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event){
        Loader.callEventHandler(event, "PlayerAchievementAwardedEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAnimationEvent(PlayerAnimationEvent event){
        Loader.callEventHandler(event, "PlayerAnimationEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncPreLoginEvent(PlayerAsyncPreLoginEvent event){
        Loader.callEventHandler(event, "PlayerAsyncPreLoginEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBlockPickEvent(PlayerBlockPickEvent event){
        Loader.callEventHandler(event, "PlayerBlockPickEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event){
        Loader.callEventHandler(event, "PlayerBucketEmptyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event){
        Loader.callEventHandler(event, "PlayerBucketFillEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeSkinEvent(PlayerChangeSkinEvent event){
        Loader.callEventHandler(event, "PlayerChangeSkinEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChunkRequestEvent(PlayerChunkRequestEvent event){
        Loader.callEventHandler(event, "PlayerChunkRequestEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCreationEvent(PlayerCreationEvent event){
        event.setPlayerClass(ProxyPlayer.class);
        Loader.callEventHandler(event, "PlayerCreationEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        Loader.callEventHandler(event, "PlayerDropItemEvent");
    }
    ////////////////////////
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEatFoodEvent(PlayerEatFoodEvent event){
        Loader.callEventHandler(event, "PlayerEatFoodEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEditBookEvent(PlayerEditBookEvent event){
        Loader.callEventHandler(event, "PlayerEditBookEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFoodLevelChangeEvent(PlayerFoodLevelChangeEvent event){
        Loader.callEventHandler(event, "PlayerFoodLevelChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event){
        Loader.callEventHandler(event,"PlayerGameModeChangeEvent");
    }
    //////////////222222
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMouseOverEntityEvent(PlayerMouseOverEntityEvent event){
        Loader.callEventHandler(event, "PlayerMouseOverEntityEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerServerSettingsRequestEvent(PlayerServerSettingsRequestEvent event){
        Loader.callEventHandler(event, "PlayerServerSettingsRequestEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionApplyEvent(PotionApplyEvent event){
        Loader.callEventHandler(event, "PotionApplyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDataSerializeEvent(PlayerDataSerializeEvent event){
        Loader.callEventHandler(event, "PlayerDataSerializeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginEnableEvent(PluginEnableEvent event){
        Loader.callEventHandler(event, "PluginEnableEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginDisableEvent(PluginDisableEvent event){
        Loader.callEventHandler(event, "PluginDisableEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoteServerCommandEvent(RemoteServerCommandEvent event){
        Loader.callEventHandler(event, "RemoteServerCommandEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityEnterVehicleEvent(EntityEnterVehicleEvent event){
        Loader.callEventHandler(event, "EntityEnterVehicleEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExitVehicleEvent(EntityExitVehicleEvent event){
        Loader.callEventHandler(event, "EntityExitVehicleEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleCreateEvent(VehicleCreateEvent event){
        Loader.callEventHandler(event, "VehicleCreateEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamageEvent(VehicleDamageEvent event){
        Loader.callEventHandler(event,"VehicleDamageEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDestroyEvent(VehicleDestroyEvent event){
        Loader.callEventHandler(event, "VehicleDestroyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleMoveEvent(VehicleMoveEvent event){
        Loader.callEventHandler(event, "VehicleMoveEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleUpdateEvent(VehicleUpdateEvent event){
        Loader.callEventHandler(event, "VehicleUpdateEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLightningStrikeEvent(LightningStrikeEvent event){
        Loader.callEventHandler(event, "LightningStrikeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event){
        Loader.callEventHandler(event, "PlayerItemConsumeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKickEvent(PlayerKickEvent event){
        Loader.callEventHandler(event, "PlayerKickEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMapInfoRequestEvent(PlayerMapInfoRequestEvent event){
        Loader.callEventHandler(event, "PlayerMapInfoRequestEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJump(PlayerJumpEvent event){
        Loader.callEventHandler(event,event.getClass().getSimpleName());
    }
}
