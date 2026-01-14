package com.blocklynukkit.loader.other.AddonsAPI;

import cn.nukkit.block.BlockSolid;
import cn.nukkit.block.custom.container.BlockContainer;
import cn.nukkit.item.Item;

/**
 * BlocklyNukkit 自定义方块基类，兼容 Nukkit-MOT 原生 API
 */
public class BNCustomBlock extends BlockSolid implements BlockContainer {

    private final String identifier;
    private final int nukkitId;
    private final String blockName;
    private final double blockHardness;
    private final double blockResistance;
    private final int blockToolType;
    private final int mineTier;
    private final boolean silkTouchable;
    private final int dropMinExp;
    private final int dropMaxExp;

    public BNCustomBlock(String identifier, int nukkitId, String name, double hardness,
                         double resistance, int toolType, int mineTier,
                         boolean silkTouchable, int dropMinExp, int dropMaxExp) {
        this.identifier = identifier;
        this.nukkitId = nukkitId;
        this.blockName = name;
        this.blockHardness = hardness;
        this.blockResistance = resistance;
        this.blockToolType = toolType;
        this.mineTier = mineTier;
        this.silkTouchable = silkTouchable;
        this.dropMinExp = dropMinExp;
        this.dropMaxExp = dropMaxExp;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getNukkitId() {
        return this.nukkitId;
    }

    @Override
    public int getId() {
        return this.nukkitId;
    }

    @Override
    public String getName() {
        return this.blockName;
    }

    @Override
    public double getHardness() {
        return this.blockHardness;
    }

    @Override
    public double getResistance() {
        return this.blockResistance;
    }

    @Override
    public int getToolType() {
        return this.blockToolType;
    }

    @Override
    public int getDropExp() {
        if (dropMinExp == dropMaxExp) {
            return dropMinExp;
        }
        return new cn.nukkit.math.NukkitRandom().nextRange(dropMinExp, dropMaxExp);
    }

    @Override
    public boolean canHarvestWithHand() {
        return mineTier == 0;
    }

    @Override
    public boolean canSilkTouch() {
        return this.silkTouchable;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() >= this.mineTier) {
            if (this.blockToolType == 0
                    || (this.blockToolType == 1 && item.isSword())
                    || (this.blockToolType == 2 && item.isShovel())
                    || (this.blockToolType == 3 && item.isPickaxe())
                    || (this.blockToolType == 4 && item.isAxe())
                    || (this.blockToolType == 5 && item.isShears())) {
                return new Item[]{Item.get(this.nukkitId)};
            }
        }
        return new Item[]{Item.get(0)};
    }
}
