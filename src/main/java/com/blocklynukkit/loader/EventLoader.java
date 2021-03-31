package com.blocklynukkit.loader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
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
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.BinaryStream;
import com.blocklynukkit.loader.other.Items.ItemComponentEntry;
import com.blocklynukkit.loader.other.Items.ItemData;
import com.blocklynukkit.loader.other.generator.render.BaseRender;
import com.blocklynukkit.loader.other.packets.ItemComponentPacket;
import com.blocklynukkit.loader.script.event.FakeSlotChangeEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xxmicloxx.NoteBlockAPI.SongDestroyingEvent;
import com.xxmicloxx.NoteBlockAPI.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.SongStoppedEvent;
import com.blocklynukkit.loader.script.event.StoneSpawnEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
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
        plugin.callEventHandler(event,"PlayerInteractEntityEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event){
        plugin.callEventHandler(event,"PlayerTeleportEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwim(PlayerToggleSwimEvent event){
        plugin.callEventHandler(event,"PlayerToggleSwimEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGlide(PlayerToggleGlideEvent event){
        plugin.callEventHandler(event,"PlayerToggleGlideEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSneak(PlayerToggleSneakEvent event){
        plugin.callEventHandler(event,"PlayerToggleSneakEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSprint(PlayerToggleSprintEvent event){
        plugin.callEventHandler(event,"PlayerToggleSprintEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFlight(PlayerToggleFlightEvent event){
        plugin.callEventHandler(event,"PlayerToggleFlightEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnterBed(PlayerBedEnterEvent event){
        plugin.callEventHandler(event,"PlayerBedEnterEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeaveBed(PlayerBedLeaveEvent event){
        plugin.callEventHandler(event,"PlayerBedLeaveEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent event){
        plugin.callEventHandler(event, "PlayerChatEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerPreLoginEvent event){
        plugin.callEventHandler(event, "PlayerPreLoginEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event){
        plugin.callEventHandler(event,"PlayerQuitEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){
        plugin.callEventHandler(event, "PlayerJoinEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event){
        plugin.callEventHandler(event, "BlockBreakEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event){
        plugin.callEventHandler(event, "BlockPlaceEvent");
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
        plugin.callEventHandler(event,"PlayerFormRespondedEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonChangeEvent(BlockPistonChangeEvent event){
        plugin.callEventHandler(event, "BlockPistonChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockUpdateEvent(BlockUpdateEvent event){
        plugin.callEventHandler(event, "BlockUpdateEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockSpreadEvent(BlockSpreadEvent event){
        plugin.callEventHandler(event, "BlockSpreadEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoorToggleEvent(DoorToggleEvent event){
        plugin.callEventHandler(event, "DoorToggleEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstoneEvent(BlockRedstoneEvent event){
        plugin.callEventHandler(event, "BlockRedstoneEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        plugin.callEventHandler(event, "PlayerCommandPreprocessEvent");
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
        plugin.callEventHandler(event, "InventoryTransactionEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event){
        plugin.callEventHandler(event, "PlayerItemHeldEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        plugin.callEventHandler(event, "PlayerInteractEvent");
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
        plugin.callEventHandler(event, "ServerCommandEvent");
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
        plugin.callEventHandler(event, "PlayerMoveEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntity(EntitySpawnEvent event){
        plugin.callEventHandler(event, "EntitySpawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDespawn(EntityDespawnEvent event){
        plugin.callEventHandler(event, "EntityDespawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event){
        plugin.callEventHandler(event, "EntityDamageEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntity().getName().equals("BNNPC"))event.setAttackCooldown(0);
        plugin.callEventHandler(event, "EntityDamageByEntityEvent");
        plugin.callEventHandler(event, "EntityDamageEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event){
        plugin.callEventHandler(event, "PlayerDeathEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event){
        plugin.callEventHandler(event, "EntityDeathEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTeleport(EntityTeleportEvent event){
        plugin.callEventHandler(event, "EntityTeleportEvent");
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawnEvent(ItemDespawnEvent event){
        plugin.callEventHandler(event, "ItemDespawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawnEvent(ItemSpawnEvent event){
        plugin.callEventHandler(event, "ItemSpawnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjecttileHit(ProjectileHitEvent event){
        plugin.callEventHandler(event, "ProjectileHitEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        plugin.callEventHandler(event, "ProjectileLaunchEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityLevelChangeEvent(EntityLevelChangeEvent event){
        plugin.callEventHandler(event, "EntityLevelChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInventoryChange(EntityInventoryChangeEvent event){
        plugin.callEventHandler(event, "EntityInventoryChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionPrime(EntityExplosionPrimeEvent event){
        plugin.callEventHandler(event, "EntityExplosionPrimeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event){
        plugin.callEventHandler(event, "BlockBurnEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormEvent(BlockFormEvent event){
        plugin.callEventHandler(event,"BlockFormEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgniteEvent(BlockIgniteEvent event){
        plugin.callEventHandler(event, "BlockIgniteEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFade(BlockFadeEvent event){
        plugin.callEventHandler(event, "BlockFadeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFall(BlockFallEvent event){
        plugin.callEventHandler(event, "BlockFallEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockGrow(BlockGrowEvent event){
        plugin.callEventHandler(event, "BlockGrowEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent event){
        plugin.callEventHandler(event, "SignChangeEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameDrop(ItemFrameDropItemEvent event){
        plugin.callEventHandler(event, "ItemFrameDropItemEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event){
        plugin.callEventHandler(event, "LeavesDecayEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstoneUpdateEvent(RedstoneUpdateEvent event){
        plugin.callEventHandler(event, "RedstoneUpdateEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event){
        plugin.callEventHandler(event, "CraftItemEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event){
        plugin.callEventHandler(event, "InventoryOpenEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClock(InventoryCloseEvent event){
        plugin.callEventHandler(event, "InventoryCloseEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event){
        plugin.callEventHandler(event, "FurnaceBurnEvent");
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
        plugin.callEventHandler(event, "FurnaceSmeltEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryPickupArrow(InventoryPickupArrowEvent event){
        plugin.callEventHandler(event, "InventoryPickupArrowEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIntentoryPickupItem(InventoryPickupItemEvent event){
        plugin.callEventHandler(event, "InventoryPickupItemEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionCollideEvent(PotionCollideEvent event){
        plugin.callEventHandler(event, "PotionCollideEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketReceive(DataPacketReceiveEvent event){
        if(event.getPacket().pid()== ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET){
            plugin.callEventHandler(event,"PlayerLocallyInitializedEvent");
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
        plugin.callEventHandler(event, "DataPacketReceiveEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketSend(DataPacketSendEvent event){
//        DataPacket tmp = event.getPacket();
//        if(tmp.pid()==ProtocolInfo.START_GAME_PACKET){
//            event.setCancelled();
//            StartGamePacket packet = (StartGamePacket)tmp;
//            packet.eduEditionOffer = 1;
//            packet.hasEduFeaturesEnabled = true;
//            //获取原版物品元数据
//            InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
//            if (stream == null) {
//                throw new AssertionError("Unable to locate RuntimeID table");
//            } else {
//                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
//                Gson gson = new Gson();
//                Type collectionType = (new TypeToken<Collection<ItemData>>(){}).getType();
//                Collection<ItemData> entries = gson.fromJson(reader, collectionType);
//                BinaryStream paletteBuffer = new BinaryStream();
//                paletteBuffer.putUnsignedVarInt((long)(entries.size()+Loader.registerItemIds.size()));
//                for(int data:Loader.registerItemIds){
//                    paletteBuffer.putString("blocklynukkit:"+Item.get(data).getName());
//                    paletteBuffer.putLShort(data);
//                    paletteBuffer.putBoolean(true);
//                }
//                for (ItemData data : entries) {
//                    paletteBuffer.putString(data.name);
//                    paletteBuffer.putLShort(data.id);
//                    paletteBuffer.putBoolean(false);
//                }
//                byte[] itemData = paletteBuffer.getBuffer();
//                packet.reset();
//                packet.putEntityUniqueId(packet.entityUniqueId);
//                packet.putEntityRuntimeId(packet.entityRuntimeId);
//                packet.putVarInt(packet.playerGamemode);
//                packet.putVector3f(packet.x, packet.y, packet.z);
//                packet.putLFloat(packet.yaw);
//                packet.putLFloat(packet.pitch);
//                packet.putVarInt(packet.seed);
//                packet.putLShort(0x00); // SpawnBiomeType - Default
//                packet.putString("plains"); // UserDefinedBiomeName
//                packet.putVarInt(packet.dimension);
//                packet.putVarInt(packet.generator);
//                packet.putVarInt(packet.worldGamemode);
//                packet.putVarInt(packet.difficulty);
//                packet.putBlockVector3(packet.spawnX, packet.spawnY, packet.spawnZ);
//                packet.putBoolean(packet.hasAchievementsDisabled);
//                packet.putVarInt(packet.dayCycleStopTime);
//                packet.putVarInt(packet.eduEditionOffer);
//                packet.putBoolean(packet.hasEduFeaturesEnabled);
//                packet.putString(""); // Education Edition Product ID
//                packet.putLFloat(packet.rainLevel);
//                packet.putLFloat(packet.lightningLevel);
//                packet.putBoolean(packet.hasConfirmedPlatformLockedContent);
//                packet.putBoolean(packet.multiplayerGame);
//                packet.putBoolean(packet.broadcastToLAN);
//                packet.putVarInt(packet.xblBroadcastIntent);
//                packet.putVarInt(packet.platformBroadcastIntent);
//                packet.putBoolean(packet.commandsEnabled);
//                packet.putBoolean(packet.isTexturePacksRequired);
//                packet.putGameRules(packet.gameRules);
//                packet.putLInt(0); // Experiment count
//                packet.putBoolean(false); // Were experiments previously toggled
//                packet.putBoolean(packet.bonusChest);
//                packet.putBoolean(packet.hasStartWithMapEnabled);
//                packet.putVarInt(packet.permissionLevel);
//                packet.putLInt(packet.serverChunkTickRange);
//                packet.putBoolean(packet.hasLockedBehaviorPack);
//                packet.putBoolean(packet.hasLockedResourcePack);
//                packet.putBoolean(packet.isFromLockedWorldTemplate);
//                packet.putBoolean(packet.isUsingMsaGamertagsOnly);
//                packet.putBoolean(packet.isFromWorldTemplate);
//                packet.putBoolean(packet.isWorldTemplateOptionLocked);
//                packet.putBoolean(packet.isOnlySpawningV1Villagers);
//                packet.putString(packet.vanillaVersion);
//                packet.putLInt(16); // Limited world width
//                packet.putLInt(16); // Limited world height
//                packet.putBoolean(false); // Nether type
//                packet.putBoolean(false); // Experimental Gameplay
//                packet.putString(packet.levelId);
//                packet.putString(packet.worldName);
//                packet.putString(packet.premiumWorldTemplateId);
//                packet.putBoolean(packet.isTrial);
//                packet.putVarInt(packet.isMovementServerAuthoritative ? 1 : 0); // 2 - rewind
//                packet.putLLong(packet.currentTick);
//                packet.putVarInt(packet.enchantmentSeed);
//                packet.putUnsignedVarInt(0); // Custom blocks
//                packet.put(itemData);
//                packet.putString(packet.multiplayerCorrelationId);
//                packet.putBoolean(packet.isInventoryServerAuthoritative);
//                packet.encode();
//            }
//            event.getPlayer().dataPacket(packet);
//        }
        plugin.callEventHandler(event, "DataPacketSendEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQueryRegenerate(QueryRegenerateEvent event){
        plugin.callEventHandler(event, "QueryRegenerateEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockForm(BlockFormEvent event){
        plugin.callEventHandler(event, "BlockFormEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormTo(BlockFromToEvent event){
        plugin.callEventHandler(event, "BlockFromToEvent");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLiquid(LiquidFlowEvent event){
        plugin.callEventHandler(event, "LiquidFlowEvent");
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
        plugin.callEventHandler(event, "SongEndEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongDestroy(SongDestroyingEvent event){
        plugin.callEventHandler(event, "SongDestroyingEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSongStop(SongStoppedEvent event){
        plugin.callEventHandler(event, "SongStoppedEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        plugin.callEventHandler(event, "InventoryClickEvent");
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
        plugin.callEventHandler(event, "ChunkUnloadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSetting(PlayerSettingsRespondedEvent event){
        if(!event.wasClosed()&&event.getResponse()!=null){
            Loader.callEventHandler(event,Loader.serverSettingCallback.get(event.getPlayer().getName()));
        }
        plugin.callEventHandler(event, "PlayerSettingsRespondedEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event){
        plugin.callEventHandler(event, "CreatureSpawnEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreeperPowerEvent(CreeperPowerEvent event){
        plugin.callEventHandler(event, "CreeperPowerEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityArmorChangeEvent(EntityArmorChangeEvent event){
        plugin.callEventHandler(event, "EntityArmorChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBlockChangeEvent(EntityBlockChangeEvent event){
        plugin.callEventHandler(event, "EntityBlockChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustByBlockEvent(EntityCombustByBlockEvent event){
        plugin.callEventHandler(event, "EntityCombustByBlockEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event){
        plugin.callEventHandler(event, "EntityCombustByEntityEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustEvent(EntityCombustEvent event){
        plugin.callEventHandler(event, "EntityCombustEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event){
        plugin.callEventHandler(event, "EntityDamageByBlockEvent");
        plugin.callEventHandler(event, "EntityDamageEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByChildEntityEvent(EntityDamageByChildEntityEvent event){
        plugin.callEventHandler(event, "EntityDamageByChildEntityEvent");
        plugin.callEventHandler(event, "EntityDamageByEntityEvent");
        plugin.callEventHandler(event, "EntityDamageEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        plugin.callEventHandler(event, "EntityExplodeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityMotionEvent(EntityMotionEvent event){
        plugin.callEventHandler(event, "EntityMotionEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPortalEnterEvent(EntityPortalEnterEvent event){
        plugin.callEventHandler(event, "EntityPortalEnterEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event){
        plugin.callEventHandler(event, "EntityRegainHealthEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBowEvent(EntityShootBowEvent event){
        plugin.callEventHandler(event, "EntityShootBowEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityVehicleEnterEvent(EntityVehicleEnterEvent event){
        plugin.callEventHandler(event, "EntityVehicleEnterEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityVehicleExitEvent(EntityVehicleExitEvent event){
        plugin.callEventHandler(event, "EntityVehicleExitEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionPrimeEvent(ExplosionPrimeEvent event){
        plugin.callEventHandler(event, "ExplosionPrimeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBrewEvent(BrewEvent event){
        plugin.callEventHandler(event, "BrewEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItemEvent(EnchantItemEvent event){
        plugin.callEventHandler(event, "EnchantItemEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event){
        plugin.callEventHandler(event, "InventoryMoveItemEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStartBrewEvent(StartBrewEvent event){
        plugin.callEventHandler(event, "StartBrewEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoadEvent(ChunkLoadEvent event){
        plugin.callEventHandler(event, "ChunkLoadEvent");
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
        plugin.callEventHandler(event, "ChunkPopulateEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelInitEvent(LevelInitEvent event){
        plugin.callEventHandler(event, "LevelInitEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelLoadEvent(LevelLoadEvent event){
        plugin.callEventHandler(event, "LevelLoadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelSaveEvent(LevelSaveEvent event){
        plugin.callEventHandler(event, "LevelSaveEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelUnloadEvent(LevelUnloadEvent event){
        plugin.callEventHandler(event, "LevelUnloadEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnChangeEvent(SpawnChangeEvent event){
        plugin.callEventHandler(event, "SpawnChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThunderChangeEvent(ThunderChangeEvent event){
        plugin.callEventHandler(event, "ThunderChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChangeEvent(WeatherChangeEvent event){
        plugin.callEventHandler(event, "WeatherChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event){
        plugin.callEventHandler(event, "PlayerAchievementAwardedEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAnimationEvent(PlayerAnimationEvent event){
        plugin.callEventHandler(event, "PlayerAnimationEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncPreLoginEvent(PlayerAsyncPreLoginEvent event){
        plugin.callEventHandler(event, "PlayerAsyncPreLoginEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBlockPickEvent(PlayerBlockPickEvent event){
        plugin.callEventHandler(event, "PlayerBlockPickEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event){
        plugin.callEventHandler(event, "PlayerBucketEmptyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event){
        plugin.callEventHandler(event, "PlayerBucketFillEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeSkinEvent(PlayerChangeSkinEvent event){
        plugin.callEventHandler(event, "PlayerChangeSkinEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChunkRequestEvent(PlayerChunkRequestEvent event){
        plugin.callEventHandler(event, "PlayerChunkRequestEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCreationEvent(PlayerCreationEvent event){
        plugin.callEventHandler(event, "PlayerCreationEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        plugin.callEventHandler(event, "PlayerDropItemEvent");
    }
    ////////////////////////
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEatFoodEvent(PlayerEatFoodEvent event){
        plugin.callEventHandler(event, "PlayerEatFoodEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEditBookEvent(PlayerEditBookEvent event){
        plugin.callEventHandler(event, "PlayerEditBookEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFoodLevelChangeEvent(PlayerFoodLevelChangeEvent event){
        plugin.callEventHandler(event, "PlayerFoodLevelChangeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event){
        plugin.callEventHandler(event,"PlayerGameModeChangeEvent");
    }
    //////////////222222
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMouseOverEntityEvent(PlayerMouseOverEntityEvent event){
        plugin.callEventHandler(event, "PlayerMouseOverEntityEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerServerSettingsRequestEvent(PlayerServerSettingsRequestEvent event){
        plugin.callEventHandler(event, "PlayerServerSettingsRequestEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionApplyEvent(PotionApplyEvent event){
        plugin.callEventHandler(event, "PotionApplyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDataSerializeEvent(PlayerDataSerializeEvent event){
        plugin.callEventHandler(event, "PlayerDataSerializeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginEnableEvent(PluginEnableEvent event){
        plugin.callEventHandler(event, "PluginEnableEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginDisableEvent(PluginDisableEvent event){
        plugin.callEventHandler(event, "PluginDisableEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoteServerCommandEvent(RemoteServerCommandEvent event){
        plugin.callEventHandler(event, "RemoteServerCommandEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityEnterVehicleEvent(EntityEnterVehicleEvent event){
        plugin.callEventHandler(event, "EntityEnterVehicleEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExitVehicleEvent(EntityExitVehicleEvent event){
        plugin.callEventHandler(event, "EntityExitVehicleEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleCreateEvent(VehicleCreateEvent event){
        plugin.callEventHandler(event, "VehicleCreateEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamageEvent(VehicleDamageEvent event){
        plugin.callEventHandler(event,"VehicleDamageEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDestroyEvent(VehicleDestroyEvent event){
        plugin.callEventHandler(event, "VehicleDestroyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleMoveEvent(VehicleMoveEvent event){
        plugin.callEventHandler(event, "VehicleMoveEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleUpdateEvent(VehicleUpdateEvent event){
        plugin.callEventHandler(event, "VehicleUpdateEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLightningStrikeEvent(LightningStrikeEvent event){
        plugin.callEventHandler(event, "LightningStrikeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event){
        plugin.callEventHandler(event, "PlayerItemConsumeEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKickEvent(PlayerKickEvent event){
        plugin.callEventHandler(event, "PlayerKickEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMapInfoRequestEvent(PlayerMapInfoRequestEvent event){
        plugin.callEventHandler(event, "PlayerMapInfoRequestEvent");
    }
}
