package com.blocklynukkit.loader.other.AddonsAPI;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import org.jetbrains.annotations.NotNull;

/**
 * BlocklyNukkit 自定义简单物品，兼容 Nukkit-MOT 原生 API
 */
public class BNCustomItem extends ItemCustom {

    private final int maxStack;
    private final CreativeItemCategory category;
    private final boolean allowOffHand;
    private final boolean handEquipped;

    public BNCustomItem(@NotNull String id, String name, String textureName,
                        int maxStack, CreativeItemCategory category,
                        boolean allowOffHand, boolean handEquipped) {
        super(id, name, textureName);
        this.maxStack = maxStack;
        this.category = category;
        this.allowOffHand = allowOffHand;
        this.handEquipped = handEquipped;
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStack;
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.simpleBuilder(this, this.category)
                .allowOffHand(this.allowOffHand)
                .handEquipped(this.handEquipped)
                .build();
    }
}
