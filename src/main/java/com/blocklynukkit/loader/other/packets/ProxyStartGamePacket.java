package com.blocklynukkit.loader.other.packets;

import com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import com.blocklynukkit.loader.other.AddonsAPI.bnnbt.NBTIO;
import cn.nukkit.network.protocol.StartGamePacket;
import com.blocklynukkit.loader.Loader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;

import static com.blocklynukkit.loader.Loader.ItemPalette;

public final class ProxyStartGamePacket extends StartGamePacket {
    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.playerGamemode);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.yaw);
        this.putLFloat(this.pitch);

        this.putVarInt(this.seed);
        this.putLShort(0x00); // SpawnBiomeType - Default
        this.putString("plains"); // UserDefinedBiomeName
        this.putVarInt(this.dimension);
        this.putVarInt(this.generator);
        this.putVarInt(this.worldGamemode);
        this.putVarInt(this.difficulty);
        this.putBlockVector3(this.spawnX, this.spawnY, this.spawnZ);
        this.putBoolean(this.hasAchievementsDisabled);
        this.putVarInt(this.dayCycleStopTime);
        this.putVarInt(this.eduEditionOffer);
        this.putBoolean(this.hasEduFeaturesEnabled);
        this.putString(""); // Education Edition Product ID
        this.putLFloat(this.rainLevel);
        this.putLFloat(this.lightningLevel);
        this.putBoolean(this.hasConfirmedPlatformLockedContent);
        this.putBoolean(this.multiplayerGame);
        this.putBoolean(this.broadcastToLAN);
        this.putVarInt(this.xblBroadcastIntent);
        this.putVarInt(this.platformBroadcastIntent);
        this.putBoolean(this.commandsEnabled);
        this.putBoolean(this.isTexturePacksRequired);
        this.putGameRules(this.gameRules);
        this.putLInt(0); // Experiment count
        this.putBoolean(true); // Were experiments previously toggled
        this.putBoolean(this.bonusChest);
        this.putBoolean(this.hasStartWithMapEnabled);
        this.putVarInt(this.permissionLevel);
        this.putLInt(this.serverChunkTickRange);
        this.putBoolean(this.hasLockedBehaviorPack);
        this.putBoolean(this.hasLockedResourcePack);
        this.putBoolean(this.isFromLockedWorldTemplate);
        this.putBoolean(this.isUsingMsaGamertagsOnly);
        this.putBoolean(this.isFromWorldTemplate);
        this.putBoolean(this.isWorldTemplateOptionLocked);
        this.putBoolean(this.isOnlySpawningV1Villagers);
        this.putString(this.vanillaVersion);
        this.putLInt(16); // Limited world width
        this.putLInt(16); // Limited world height
        this.putBoolean(false); // Nether type
        this.putBoolean(true); // Experimental Gameplay

        this.putString(this.levelId);
        this.putString(this.worldName);
        this.putString(this.premiumWorldTemplateId);
        this.putBoolean(this.isTrial);
        this.putUnsignedVarInt(this.isMovementServerAuthoritative ? 1 : 0); // 2 - rewind
        this.putVarInt(0); // RewindHistorySize
        this.putBoolean(false); // isServerAuthoritativeBlockBreaking
        this.putLLong(this.currentTick);
        this.putVarInt(this.enchantmentSeed);
        this.putUnsignedVarInt(Loader.registerCustomBlocks); // Custom blocks
        for(int i : Loader.registerBlockIds){
            if(i < 256 && i > 0){
                continue;
            }
            Item itemBlock = Item.get(i);
            this.putString("blocklynukkit:"+itemBlock.getName());
            com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.ListTag<com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.Tag> origin =  new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.ListTag<>("origin")
                    .add(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.FloatTag("", -8.0f))
                    .add(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.FloatTag("", 0.0f))
                    .add(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.FloatTag("", -8.0f));
            com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.ListTag<com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.Tag> size = new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.ListTag<>("size")
                    .add(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.FloatTag("", 16.0f))
                    .add(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.FloatTag("", 16.0f))
                    .add(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.FloatTag("", 16.0f));
            try {
                this.put(NBTIO.write(new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag().putCompound("components"
                        , new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag().putCompound("minecraft:entity_collision"
                                , new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag()
                                        .putBoolean("enabled", false)
                                        .putList(origin).putList(size)
                                        )
                        .putCompound("minecraft:material_instances"
                                , new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag()
                                        .putCompound("mappings", new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag())
                                        .putCompound("materials", new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag().putCompound("*"
                                                , new com.blocklynukkit.loader.other.AddonsAPI.bnnbt.tag.CompoundTag().putBoolean("ambient_occlusion", true)
                                                        .putBoolean("face_dimming", true)
                                                        .putString("render_method", "opaque")
                                                        .putString("texture", "dirt"/*TODO Change texture*/))))
                        .putCompound("minecraft:unit_cube", new CompoundTag()))
                        , ByteOrder.LITTLE_ENDIAN, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(ItemPalette == null){
            RuntimeItemMapping runtimeItemMapping = null;
            try {
                runtimeItemMapping = RuntimeItems.getMapping();
            }catch (NoSuchMethodError error){
                try {
                    Method getRuntimeItemMappingMethod = RuntimeItems.class.getMethod("getRuntimeMapping");
                    runtimeItemMapping = (RuntimeItemMapping) getRuntimeItemMappingMethod.invoke(RuntimeItemMapping.class);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                ItemPalette = runtimeItemMapping.getItemPalette();
            }catch (NoSuchMethodError error){
                try {
                    Method getItemDataPaletteMethod = RuntimeItemMapping.class.getMethod("getItemDataPalette");
                    ItemPalette = (byte[]) getItemDataPaletteMethod.invoke(runtimeItemMapping);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.put(ItemPalette);
        this.putString(this.multiplayerCorrelationId);
        this.putBoolean(this.isInventoryServerAuthoritative);
        this.putString("BlocklyNukkit");
    }
}
