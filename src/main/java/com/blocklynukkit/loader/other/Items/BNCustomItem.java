package com.blocklynukkit.loader.other.Items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import com.blocklynukkit.loader.Loader;
import org.jetbrains.annotations.NotNull;

/**
 * BN 简单自定义物品
 */
public class BNCustomItem extends ItemCustom {

    private final int maxStackSize;
    private final CreativeItemCategory category;
    private final boolean handEquipped;
    private final boolean allowOffHand;
    private final String initFunction;
    private final int legacyId;

    public BNCustomItem(@NotNull String id, String name, int legacyId, int maxStackSize,
                        CreativeItemCategory category, boolean handEquipped,
                        boolean allowOffHand, String initFunction) {
        super(id, name);
        this.legacyId = legacyId;
        this.maxStackSize = maxStackSize;
        this.category = category;
        this.handEquipped = handEquipped;
        this.allowOffHand = allowOffHand;
        this.initFunction = initFunction;

        // 执行初始化回调
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
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.simpleBuilder(this, category)
                .allowOffHand(allowOffHand)
                .handEquipped(handEquipped)
                .build();
    }

    public int getLegacyId() {
        return legacyId;
    }
}
