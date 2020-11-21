package com.blocklynukkit.loader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
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
import com.blocklynukkit.loader.script.event.FakeSlotChangeEvent;
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
                    Loader.scriptObjectMirrorCallback.get(a).call(Loader.scriptObjectMirrorCallback.get(a),event);
                }
            }else {
                synchronized (Loader.acceptCloseCallback){
                    if(Loader.functioncallback.containsKey((Integer) event.getFormID())){
                        String fun = Loader.functioncallback.get(event.getFormID());
                        if(fun==null)return;
                        if(Loader.acceptCloseCallback.get(fun)!=null&&Loader.acceptCloseCallback.get(fun)){
                            Loader.callEventHandler(event,fun);
                        }
                    }

                }
            }
        }
        synchronized (Loader.windowCallbackMap){
            if(Loader.windowCallbackMap.containsKey(event.getFormID())){
                Loader.windowCallbackMap.get(event.getFormID()).call(event);
            }
        }
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonChangeEvent(BlockPistonChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockUpdateEvent(BlockUpdateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockSpreadEvent(BlockSpreadEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoorToggleEvent(DoorToggleEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstoneEvent(BlockRedstoneEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
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
        plugin.callEventHandler(event, "EntityDamageEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntity().getName().equals("BNNPC"))event.setAttackCooldown(0);
        plugin.callEventHandler(event, event.getClass().getSimpleName());
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
    public void onBlockIgniteEvent(BlockIgniteEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFade(BlockFadeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFall(BlockFallEvent event){
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
    public void onPotionCollideEvent(PotionCollideEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketReceive(DataPacketReceiveEvent event){
        if(event.getPacket().pid()== ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET){
            plugin.callEventHandler(event,"PlayerLocallyInitializedEvent");
        }else if(event.getPacket().pid()==ProtocolInfo.EMOTE_LIST_PACKET){
            System.out.println("玩家"+event.getPlayer().getName()+"携带的表情动作uuid列表:");
            ((EmoteListPacket)event.getPacket()).pieceIds.forEach(e->{
                if(!(e.equals("4c8ae710-df2e-47cd-814d-cc7bf21a3d67")||
                        e.equals("42fde774-37d4-4422-b374-89ff13a6535a")||
                        e.equals("9a469a61-c83b-4ba9-b507-bdbe64430582")||
                        e.equals("ce5c0300-7f03-455d-aaf1-352e4927b54d")||
                        e.equals("d7519b5a-45ec-4d27-997c-89d402c6b57f")||
                        e.equals("86b34976-8f41-475b-a386-385080dc6e83")||
                        e.equals("6d9f24c0-6246-4c92-8169-4648d1981cbb")||
                        e.equals("7cec98d8-55cc-44fe-b0ae-2672b0b2bd37"))){
                    System.out.println(e);
                }
            });
        }
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketSend(DataPacketSendEvent event){
        DataPacket tmp = event.getPacket();
        if(tmp.pid()==ProtocolInfo.START_GAME_PACKET){
            StartGamePacket packet = (StartGamePacket)tmp;
            packet.eduEditionOffer = 1;
        }
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
    public void onBlockFormTo(BlockFromToEvent event){
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
    public static void onSlotChange(com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent event){
        Loader.plugin.callEventHandler(new FakeSlotChangeEvent(event),"FakeSlotChangeEvent","FakeSlotChangeEvent");
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
        if(event==null||event.getChunk()==null)return;
        for(Entity entity:event.getChunk().getEntities().values()){
            if(entity==null)continue;
            if(entity.getName()==null)continue;
            if(entity.getName().equals("BNNPC")||entity.getName().equals("BNFloatingText")){
                event.setCancelled();
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreeperPowerEvent(CreeperPowerEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityArmorChangeEvent(EntityArmorChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBlockChangeEvent(EntityBlockChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustByBlockEvent(EntityCombustByBlockEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustEvent(EntityCombustEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByChildEntityEvent(EntityDamageByChildEntityEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityMotionEvent(EntityMotionEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPortalEnterEvent(EntityPortalEnterEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBowEvent(EntityShootBowEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityVehicleEnterEvent(EntityVehicleEnterEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityVehicleExitEvent(EntityVehicleExitEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionPrimeEvent(ExplosionPrimeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBrewEvent(BrewEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItemEvent(EnchantItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStartBrewEvent(StartBrewEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoadEvent(ChunkLoadEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkPopulateEvent(ChunkPopulateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelInitEvent(LevelInitEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelLoadEvent(LevelLoadEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelSaveEvent(LevelSaveEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelUnloadEvent(LevelUnloadEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnChangeEvent(SpawnChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThunderChangeEvent(ThunderChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChangeEvent(WeatherChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAnimationEvent(PlayerAnimationEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncPreLoginEvent(PlayerAsyncPreLoginEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBlockPickEvent(PlayerBlockPickEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeSkinEvent(PlayerChangeSkinEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChunkRequestEvent(PlayerChunkRequestEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCreationEvent(PlayerCreationEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    ////////////////////////
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEatFoodEvent(PlayerEatFoodEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEditBookEvent(PlayerEditBookEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFoodLevelChangeEvent(PlayerFoodLevelChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    //////////////222222
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMouseOverEntityEvent(PlayerMouseOverEntityEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerServerSettingsRequestEvent(PlayerServerSettingsRequestEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionApplyEvent(PotionApplyEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDataSerializeEvent(PlayerDataSerializeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginEnableEvent(PluginEnableEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginDisableEvent(PluginDisableEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoteServerCommandEvent(RemoteServerCommandEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityEnterVehicleEvent(EntityEnterVehicleEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExitVehicleEvent(EntityExitVehicleEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleCreateEvent(VehicleCreateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamageEvent(VehicleDamageEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDestroyEvent(VehicleDestroyEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleMoveEvent(VehicleMoveEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleUpdateEvent(VehicleUpdateEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLightningStrikeEvent(LightningStrikeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKickEvent(PlayerKickEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMapInfoRequestEvent(PlayerMapInfoRequestEvent event){
        plugin.callEventHandler(event, event.getClass().getSimpleName());
    }
}
