package com.blocklynukkit.loader.other.AddonsAPI;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomArmor;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import org.jetbrains.annotations.NotNull;

/**
 * BlocklyNukkit 自定义盔甲，兼容 Nukkit-MOT 原生 API
 */
public class BNCustomArmor extends ItemCustomArmor {

    private final int maxDurability;
    private final int armorPoints;
    private final int armorSlot; // 0-helmet, 1-chestplate, 2-leggings, 3-boots
    private final int armorTier;
    private final CreativeItemCategory category;
    private final boolean allowOffHand;

    public BNCustomArmor(@NotNull String id, String name, String textureName,
                         int maxDurability, int armorPoints, int armorSlot, int armorTier,
                         CreativeItemCategory category, boolean allowOffHand) {
        super(id, name, textureName);
        this.maxDurability = maxDurability;
        this.armorPoints = armorPoints;
        this.armorSlot = armorSlot;
        this.armorTier = armorTier;
        this.category = category;
        this.allowOffHand = allowOffHand;
    }

    @Override
    public int getMaxDurability() {
        return this.maxDurability;
    }

    @Override
    public int getArmorPoints() {
        return this.armorPoints;
    }

    @Override
    public int getTier() {
        return this.armorTier;
    }

    @Override
    public boolean isHelmet() {
        return this.armorSlot == 0;
    }

    @Override
    public boolean isChestplate() {
        return this.armorSlot == 1;
    }

    @Override
    public boolean isLeggings() {
        return this.armorSlot == 2;
    }

    @Override
    public boolean isBoots() {
        return this.armorSlot == 3;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.armorBuilder(this, this.category)
                .allowOffHand(this.allowOffHand)
                .build();
    }
}
