package com.blocklynukkit.loader.other.AddonsAPI;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomTool;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import org.jetbrains.annotations.NotNull;

/**
 * BlocklyNukkit 自定义工具，兼容 Nukkit-MOT 原生 API
 */
public class BNCustomTool extends ItemCustomTool {

    private final int maxDurability;
    private final int attackDamage;
    private final int tier;
    private final int speed;
    private final int toolType; // 1-sword, 2-shovel, 3-pickaxe, 4-axe, 5-hoe
    private final CreativeItemCategory category;
    private final boolean allowOffHand;

    public BNCustomTool(@NotNull String id, String name, String textureName,
                        int maxDurability, int attackDamage, int tier, int speed,
                        int toolType, CreativeItemCategory category, boolean allowOffHand) {
        super(id, name, textureName);
        this.maxDurability = maxDurability;
        this.attackDamage = attackDamage;
        this.tier = tier;
        this.speed = speed;
        this.toolType = toolType;
        this.category = category;
        this.allowOffHand = allowOffHand;
    }

    @Override
    public int getMaxDurability() {
        return this.maxDurability;
    }

    @Override
    public int getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public boolean isSword() {
        return this.toolType == 1;
    }

    @Override
    public boolean isShovel() {
        return this.toolType == 2;
    }

    @Override
    public boolean isPickaxe() {
        return this.toolType == 3;
    }

    @Override
    public boolean isAxe() {
        return this.toolType == 4;
    }

    @Override
    public boolean isHoe() {
        return this.toolType == 5;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.toolBuilder(this, this.category)
                .allowOffHand(this.allowOffHand)
                .build();
    }
}
