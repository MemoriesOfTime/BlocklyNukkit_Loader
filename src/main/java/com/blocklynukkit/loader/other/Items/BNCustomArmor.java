package com.blocklynukkit.loader.other.Items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomArmor;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import com.blocklynukkit.loader.Loader;
import org.jetbrains.annotations.NotNull;

/**
 * BN 自定义盔甲物品
 */
public class BNCustomArmor extends ItemCustomArmor {

    private final int legacyId;
    private final int armorType; // 1-helmet, 2-chestplate, 3-leggings, 4-boots
    private final int armorTier;
    private final int durability;
    private final int armorPoints;
    private final boolean allowOffHand;
    private final String initFunction;

    public BNCustomArmor(@NotNull String id, String name, int legacyId, int armorType,
                         int armorTier, int durability, int armorPoints,
                         boolean allowOffHand, String initFunction) {
        super(id, name);
        this.legacyId = legacyId;
        this.armorType = armorType;
        this.armorTier = armorTier;
        this.durability = durability;
        this.armorPoints = armorPoints;
        this.allowOffHand = allowOffHand;
        this.initFunction = initFunction;

        doInit();
    }

    private void doInit() {
        if (initFunction != null && !initFunction.isEmpty()) {
            try {
                Loader.getFunctionManager().callFunction(initFunction, this);
            } catch (Exception e) {
                Loader.getlogger().warning("Failed to call init function: " + initFunction);
            }
        }
    }

    @Override
    public int getMaxDurability() {
        return durability;
    }

    @Override
    public int getArmorPoints() {
        return armorPoints;
    }

    @Override
    public int getTier() {
        return armorTier;
    }

    @Override
    public boolean isHelmet() {
        return armorType == 1;
    }

    @Override
    public boolean isChestplate() {
        return armorType == 2;
    }

    @Override
    public boolean isLeggings() {
        return armorType == 3;
    }

    @Override
    public boolean isBoots() {
        return armorType == 4;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.armorBuilder(this, CreativeItemCategory.EQUIPMENT)
                .allowOffHand(allowOffHand)
                .build();
    }

    public int getLegacyId() {
        return legacyId;
    }
}
