package com.blocklynukkit.loader.other.packets;

import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
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
            ListTag<Tag> origin =  new ListTag<>("origin")
                    .add(new FloatTag("", -8.0f))
                    .add(new FloatTag("", 0.0f))
                    .add(new FloatTag("", -8.0f));
            ListTag<Tag> size = new ListTag<>("size")
                    .add(new FloatTag("", 16.0f))
                    .add(new FloatTag("", 16.0f))
                    .add(new FloatTag("", 16.0f));
            try {
                this.put(NBTIO.write(new CompoundTag().putCompound("components"
                        , new CompoundTag().putCompound("minecraft:entity_collision"
                                , new CompoundTag()
                                        .putBoolean("enabled", false)
                                        .putList(origin).putList(size)
                                        )
                        .putCompound("minecraft:material_instances"
                                , new CompoundTag()
                                        .putCompound("mappings", new CompoundTag())
                                        .putCompound("materials", new CompoundTag().putCompound("*"
                                                , new CompoundTag().putBoolean("ambient_occlusion", true)
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
