package com.blocklynukkit.loader.other.Items;

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
 * BN 自定义食物/饮品物品
 */
public class BNCustomEdible extends ItemCustomEdible {

    private final int legacyId;
    private final int maxStackSize;
    private final int nutrition;
    private final int useTime; // 食用/饮用时间(tick)
    private final boolean drink;
    private final boolean allowOffHand;
    private final String initFunction;

    public BNCustomEdible(@NotNull String id, String name, int legacyId, int maxStackSize,
                          int nutrition, int useTime, boolean isDrink,
                          boolean allowOffHand, String initFunction) {
        super(id, name);
        this.legacyId = legacyId;
        this.maxStackSize = maxStackSize;
        this.nutrition = nutrition;
        this.useTime = useTime;
        this.drink = isDrink;
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
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public boolean isDrink() {
        return drink;
    }

    @Override
    public boolean canAlwaysEat() {
        return drink; // 饮料可以随时饮用
    }

    @Override
    public Map.Entry<Plugin, Food> getFood() {
        Food food = new FoodNormal(nutrition, nutrition * 0.6f);
        return new AbstractMap.SimpleEntry<>(Loader.plugin, food);
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.edibleBuilder(this, CreativeItemCategory.ITEMS)
                .allowOffHand(allowOffHand)
                .build();
    }

    public int getLegacyId() {
        return legacyId;
    }

    public int getUseTime() {
        return useTime;
    }
}
