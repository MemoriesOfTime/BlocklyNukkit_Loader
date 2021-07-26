package com.blocklynukkit.loader.other;

import cn.nukkit.Player;
import com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.*;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.utils.TextFormat;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.AddonsAPI.CustomItemInfo;
import com.blocklynukkit.loader.other.AddonsAPI.DiggerNBT;
import com.blocklynukkit.loader.other.Items.ItemComponentEntry;
import com.blocklynukkit.loader.other.packets.BNResourcePackStackPacket;
import com.blocklynukkit.loader.other.packets.ItemComponentPacket;
import com.blocklynukkit.loader.other.packets.ProxyStartGamePacket;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

public final class ProxyPlayer extends Player {
    public PermissibleBase perm;
    public ProxyPlayer(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(interfaz, clientID, socketAddress);
        try {
            Field permField = Player.class.getDeclaredField("perm");
            permField.setAccessible(true);
            perm = (PermissibleBase) permField.get(super.getPlayer());
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            //ignore
        }
    }
    @Override
    protected void completeLoginSequence(){
        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        Level level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"));
        if(level != null){
            this.spawnPosition = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
        }else{
            this.spawnPosition = this.level.getSafeSpawn();
        }

        spawnPosition = this.getSpawn();

        StartGamePacket startGamePacket = new ProxyStartGamePacket();
        startGamePacket.entityUniqueId = this.id;
        startGamePacket.entityRuntimeId = this.id;
        startGamePacket.playerGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) this.y;
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.seed = -1;
        startGamePacket.dimension = /*(byte) (this.level.getDimension() & 0xff)*/0;
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.server.getDifficulty();
        startGamePacket.spawnX = spawnPosition.getFloorX();
        startGamePacket.spawnY = spawnPosition.getFloorY();
        startGamePacket.spawnZ = spawnPosition.getFloorZ();
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.gameRules = getLevel().getGameRules();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = 1; //0 old, 1 infinite, 2 flat
        this.dataPacket(startGamePacket);

        this.dataPacket(new BiomeDefinitionListPacket());
        this.dataPacket(new AvailableEntityIdentifiersPacket());
        this.inventory.sendCreativeContents();
        this.getAdventureSettings().update();

        this.sendAttributes();

        this.sendPotionEffects(this);
        this.sendData(this);

        this.loggedIn = true;

        this.level.sendTime(this);

        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));

        if (this.isOp() || this.hasPermission("nukkit.textcolor")) {
            this.setRemoveFormat(false);
        }

        this.server.addOnlinePlayer(this);
        this.server.onPlayerCompleteLoginSequence(this);
    }

    @Override
    protected void doFirstSpawn() {
        super.doFirstSpawn();
        ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
        itemComponentPacket.entries = new ItemComponentEntry[Loader.registerItemIds.size()];
        for(int i=0,size = Loader.registerItemInfos.size();i<Loader.registerItemIds.size();i++){
            Item item = Item.get(Loader.registerItemIds.get(i));
            if(i > size - 1){
                continue;
            }
            CustomItemInfo customItemInfo = Loader.registerItemInfos.get((int)Loader.registerItemIds.get(i));
            CompoundTag root = new CompoundTag("");
            root.putCompound("components",new CompoundTag()
                    .putCompound("item_properties",new CompoundTag()
                            .putBoolean("allow_off_hand",customItemInfo.isCanOnOffhand())
                            .putBoolean("hand_equipped",customItemInfo.isDisplayAsTool())
                            .putInt("creative_category",customItemInfo.getType())
                            .putInt("max_stack_size",item.getMaxStackSize()))
                    .putCompound("minecraft:icon",new CompoundTag()
                            .putString("texture",item.getName()))
            );
//            if(customItemInfo.isForceScale()){
//                System.out.println("HANDER" + customItemInfo.getZoom());
////                FloatArrayTag __a = new FloatArrayTag(0.075f * customItemInfo.getZoom(), 0.125f * customItemInfo.getZoom(), 0.075f * customItemInfo.getZoom());
////                FloatArrayTag __b = new FloatArrayTag();
////                FloatArrayTag __c = new FloatArrayTag(0.075f * customItemInfo.getZoom(), 0.125f * customItemInfo.getZoom(), 0.075f * customItemInfo.getZoom());
////                ListTag<FloatTag> __a = new ListTag<>();
////                    __a.add(new FloatTag("",0.075f * customItemInfo.getZoom()));__a.add(new FloatTag("",0.125f * customItemInfo.getZoom()));__a.add(new FloatTag("",0.075f * customItemInfo.getZoom()));
////                ListTag<FloatTag> __b = new ListTag<>();
////                    __b.add(new FloatTag("", 0.45f));__b.add(new FloatTag("", 1.6f));__b.add(new FloatTag("", -0.7f));
////                ListTag<FloatTag> __c = new ListTag<>();
////                    __c.add(new FloatTag("",2f));__c.add(new FloatTag("",4f));__c.add(new FloatTag("",8f));
//                IntArrayTag __a = new IntArrayTag("", new int[]{5, 5, 5});
//                IntArrayTag __b = new IntArrayTag("", new int[]{5, 5, 5});
//                IntArrayTag __c = new IntArrayTag("", new int[]{5, 5, 5});
//                IntArrayTag __d = new IntArrayTag("", new int[]{0,0,0});
//                root.getCompound("components")
//                        .putCompound("minecraft:render_offsets"
//                        , new CompoundTag().putCompound("main_hand"
//                                , new CompoundTag().putCompound("first_person"
//                                        , new CompoundTag().put("scale", __a))
//                                .putCompound("third_person"//.put("position", __b)
//                                        , new CompoundTag().put("position", __b).put("scale", __c))
//                        ).putCompound("off_hand"
//                                , new CompoundTag().putCompound("first_person"
//                                        , new CompoundTag().put("position", __d).put("rotation", __d).put("scale", __d))
//                                .putCompound("third_person"
//                                        , new CompoundTag().put("position", __d).put("rotation", __d).put("scale", __d))));
//            }
            if(customItemInfo.isTool()){
                root.getCompound("components")
                        .putCompound("minecraft:durability",new CompoundTag()
                                .putInt("max_durability",customItemInfo.getDurability())
                        );
                root.getCompound("components").getCompound("item_properties")
                        .putInt("damage",customItemInfo.getAttackDamage());
                if(customItemInfo.getToolType() == 3){
                    root.getCompound("components")
                            .putCompound("minecraft:digger", DiggerNBT.getPickaxeDiggerNBT(customItemInfo.getToolTier()));
                }else if(customItemInfo.getToolType() == 4){
                    root.getCompound("components")
                            .putCompound("minecraft:digger", DiggerNBT.getAxeDiggerNBT(customItemInfo.getToolTier()));
                }else if(customItemInfo.getToolType() == 2){
                    root.getCompound("components")
                            .putCompound("minecraft:digger", DiggerNBT.getShovelDiggerNBT(customItemInfo.getToolTier()));
                }
            }else if(customItemInfo.isFood() || customItemInfo.isDrink()){
                if(customItemInfo.isDrink() && customItemInfo.getNutrition() == 0){
                    root.getCompound("components")
                            .putCompound("minecraft:food",new CompoundTag()
                                    .putInt("nutrition",customItemInfo.getNutrition())
                                    .putBoolean("can_always_eat",true)
                            );
                }else{
                    root.getCompound("components")
                            .putCompound("minecraft:food",new CompoundTag()
                                    .putInt("nutrition",customItemInfo.getNutrition())
                            );
                }
                root.getCompound("components").getCompound("item_properties")
                        .putInt("use_duration", customItemInfo.getEatTick())
                        .putInt("use_animation",customItemInfo.isFood()?1:2);
            }else if(customItemInfo.isArmor()){
                if(customItemInfo.isHelmet()){
                    root.getCompound("components").getCompound("item_properties")
                            .putString("wearable_slot", "slot.armor.head");
                    root.getCompound("components")
                            .putCompound("minecraft:durability",new CompoundTag()
                                    .putInt("max_durability",customItemInfo.getDurability()))
                            .putCompound("minecraft:armor", new CompoundTag()
                                    .putInt("protection", item.getArmorPoints())
                            .putCompound("minecraft:wearable",new CompoundTag()
                                    .putBoolean("dispensable", true)
                                    .putString("slot", "slot.armor.head")));
                }else if(customItemInfo.isChest()){
                    root.getCompound("components").getCompound("item_properties")
                            .putString("wearable_slot", "slot.armor.chest");
                    root.getCompound("components")
                            .putCompound("minecraft:durability",new CompoundTag()
                                    .putInt("max_durability",customItemInfo.getDurability()))
                            .putCompound("minecraft:armor", new CompoundTag()
                                    .putInt("protection", item.getArmorPoints())
                            .putCompound("minecraft:wearable",new CompoundTag()
                                    .putBoolean("dispensable", true)
                                    .putString("slot", "slot.armor.chest")));
                }else if(customItemInfo.isLeggings()){
                    root.getCompound("components").getCompound("item_properties")
                            .putString("wearable_slot", "slot.armor.legs");
                    root.getCompound("components")
                            .putCompound("minecraft:durability",new CompoundTag()
                                    .putInt("max_durability",customItemInfo.getDurability()))
                            .putCompound("minecraft:wearable",new CompoundTag()
                                    .putBoolean("dispensable", true)
                                    .putString("slot", "slot.armor.legs"))
                            .putCompound("minecraft:armor", new CompoundTag()
                                    .putInt("protection", item.getArmorPoints()));
                }else if(customItemInfo.isBoots()){
                    root.getCompound("components").getCompound("item_properties")
                            .putString("wearable_slot", "slot.armor.feet");
                    root.getCompound("components")
                            .putCompound("minecraft:durability",new CompoundTag()
                                    .putInt("max_durability",customItemInfo.getDurability()))
                            .putCompound("minecraft:wearable",new CompoundTag()
                                    .putBoolean("dispensable", true)
                                    .putString("slot", "slot.armor.feet"))
                            .putCompound("minecraft:armor", new CompoundTag()
                                    .putInt("protection", item.getArmorPoints()));
                }
            }
            root.putShort("minecraft:identifier",i);
            ItemComponentEntry entry = new ItemComponentEntry("blocklynukkit:"+item.getName(),root);
            itemComponentPacket.entries[i] = entry;
        }
        this.dataPacket(itemComponentPacket);
    }

    @Override
    public void handleDataPacket(DataPacket packet){
        if(packet.pid() == ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET){
            ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
            if (responsePacket.responseStatus == ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS) {
                BNResourcePackStackPacket stackPacket = new BNResourcePackStackPacket();
                stackPacket.mustAccept = this.server.getForceResources();
                stackPacket.resourcePackStack = this.server.getResourcePackManager().getResourceStack();
                this.dataPacket(stackPacket);
                return;
            }
        }
        super.handleDataPacket(packet);
        try{
            if(packet.pid() == ProtocolInfo.PLAYER_ACTION_PACKET){
                PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                if(playerActionPacket.action == 0){

                }
            }
        }catch (Exception e){
            //ignore
        }
    }

    private static int getClientFriendlyGamemode(int gamemode) {
        gamemode &= 3;
        return gamemode == 3 ? 1 : gamemode;
    }
}
