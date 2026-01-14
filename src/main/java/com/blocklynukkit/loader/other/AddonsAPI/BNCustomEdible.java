package com.blocklynukkit.loader.other.AddonsAPI;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomEdible;
import cn.nukkit.item.food.Food;
import cn.nukkit.item.food.FoodNormal;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import cn.nukkit.plugin.Plugin;
import com.blocklynukkit.loader.Loader;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;

/**
 * BlocklyNukkit 自定义食物/饮品，兼容 Nukkit-MOT 原生 API
 */
public class BNCustomEdible extends ItemCustomEdible {

    private final int maxStack;
    private final int nutrition;
    private final float saturation;
    private final int eatTime;
    private final boolean isDrink;
    private final boolean canAlwaysEat;
    private final CreativeItemCategory category;
    private final boolean allowOffHand;

    public BNCustomEdible(@NotNull String id, String name, String textureName,
                          int maxStack, int nutrition, int eatTime,
                          boolean isDrink, boolean canAlwaysEat,
                          CreativeItemCategory category, boolean allowOffHand) {
        super(id, name, textureName);
        this.maxStack = maxStack;
        this.nutrition = nutrition;
        this.saturation = nutrition * 0.6f;
        this.eatTime = eatTime;
        this.isDrink = isDrink;
        this.canAlwaysEat = canAlwaysEat;
        this.category = category;
        this.allowOffHand = allowOffHand;
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStack;
    }

    @Override
    public boolean isDrink() {
        return this.isDrink;
    }

    @Override
    public boolean canAlwaysEat() {
        return this.canAlwaysEat;
    }

    @Override
    public Map.Entry<Plugin, Food> getFood() {
        return new AbstractMap.SimpleEntry<>(
                Loader.plugin,
                new FoodNormal(this.nutrition, this.saturation).addRelative(this.getId())
        );
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.edibleBuilder(this, this.category)
                .allowOffHand(this.allowOffHand)
                .build();
    }
}
