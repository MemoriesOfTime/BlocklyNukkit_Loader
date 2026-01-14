package com.blocklynukkit.loader.other.Items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomTool;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import com.blocklynukkit.loader.Loader;
import org.jetbrains.annotations.NotNull;

/**
 * BN 自定义工具物品
 */
public class BNCustomTool extends ItemCustomTool {

    private final int legacyId;
    private final int toolType; // 1-sword, 2-shovel, 3-pickaxe, 4-axe, 5-hoe
    private final int toolTier;
    private final int durability;
    private final int attackDamage;
    private final boolean allowOffHand;
    private final String initFunction;

    public BNCustomTool(@NotNull String id, String name, int legacyId, int toolType,
                        int toolTier, int durability, int attackDamage,
                        boolean allowOffHand, String initFunction) {
        super(id, name);
        this.legacyId = legacyId;
        this.toolType = toolType;
        this.toolTier = toolTier;
        this.durability = durability;
        this.attackDamage = attackDamage;
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
    public int getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getTier() {
        return toolTier;
    }

    @Override
    public boolean isSword() {
        return toolType == 1;
    }

    @Override
    public boolean isShovel() {
        return toolType == 2;
    }

    @Override
    public boolean isPickaxe() {
        return toolType == 3;
    }

    @Override
    public boolean isAxe() {
        return toolType == 4;
    }

    @Override
    public boolean isHoe() {
        return toolType == 5;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.toolBuilder(this, CreativeItemCategory.EQUIPMENT)
                .speed(getToolTierSpeed())
                .allowOffHand(allowOffHand)
                .build();
    }

    private int getToolTierSpeed() {
        switch (toolTier) {
            case 1: return 2;   // 木
            case 2: return 12;  // 金
            case 3: return 4;   // 石
            case 4: return 6;   // 铁
            case 5: return 8;   // 钻石
            case 6: return 9;   // 下界合金
            default: return 1;
        }
    }

    public int getLegacyId() {
        return legacyId;
    }
}
