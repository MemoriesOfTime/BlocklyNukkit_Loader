package com.blocklynukkit.loader.other.Items;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import com.blocklynukkit.loader.Loader;

import java.util.HashMap;
import java.util.Map;

/**
 * BN 物品注册管理器
 * 负责管理数字 ID 与命名空间 ID 的映射，以保持对旧脚本的兼容
 */
public class BNItemRegistry {

    private static final String NAMESPACE = "blocklynukkit";

    // 数字 ID -> 命名空间 ID 映射
    private static final Map<Integer, String> legacyIdToNamespace = new HashMap<>();
    // 命名空间 ID -> 数字 ID 映射
    private static final Map<String, Integer> namespaceToLegacyId = new HashMap<>();

    /**
     * 生成命名空间 ID
     */
    public static String generateNamespaceId(int legacyId, String name) {
        // 清理名称，只保留字母数字和下划线
        String cleanName = name.toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_");
        return NAMESPACE + ":" + cleanName + "_" + legacyId;
    }

    /**
     * 注册简单物品
     */
    public static boolean registerSimpleItem(int legacyId, String name, int maxStackSize,
                                             String type, boolean handEquipped,
                                             boolean allowOffHand, String initFunction) {
        String namespaceId = generateNamespaceId(legacyId, name);
        CreativeItemCategory category = parseCategory(type);

        try {
            BNCustomItem item = new BNCustomItem(namespaceId, name, legacyId,
                    maxStackSize, category, handEquipped, allowOffHand, initFunction);

            var result = Item.registerCustomItem(item.getClass());
            if (result.ok()) {
                registerMapping(legacyId, namespaceId);
                Loader.getlogger().info("Registered custom item: " + namespaceId + " (legacy ID: " + legacyId + ")");
                return true;
            } else {
                Loader.getlogger().warning("Failed to register item: " + namespaceId + " - " + result.error());
                return false;
            }
        } catch (Exception e) {
            Loader.getlogger().warning("Error registering item: " + namespaceId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注册工具物品
     */
    public static boolean registerToolItem(int legacyId, String name, int toolType,
                                           int toolTier, int durability, int attackDamage,
                                           boolean allowOffHand, String initFunction) {
        String namespaceId = generateNamespaceId(legacyId, name);

        try {
            BNCustomTool item = new BNCustomTool(namespaceId, name, legacyId,
                    toolType, toolTier, durability, attackDamage, allowOffHand, initFunction);

            var result = Item.registerCustomItem(item.getClass());
            if (result.ok()) {
                registerMapping(legacyId, namespaceId);
                Loader.getlogger().info("Registered custom tool: " + namespaceId + " (legacy ID: " + legacyId + ")");
                return true;
            } else {
                Loader.getlogger().warning("Failed to register tool: " + namespaceId + " - " + result.error());
                return false;
            }
        } catch (Exception e) {
            Loader.getlogger().warning("Error registering tool: " + namespaceId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注册盔甲物品
     */
    public static boolean registerArmorItem(int legacyId, String name, int armorType,
                                            int armorTier, int durability, int armorPoints,
                                            boolean allowOffHand, String initFunction) {
        String namespaceId = generateNamespaceId(legacyId, name);

        try {
            BNCustomArmor item = new BNCustomArmor(namespaceId, name, legacyId,
                    armorType, armorTier, durability, armorPoints, allowOffHand, initFunction);

            var result = Item.registerCustomItem(item.getClass());
            if (result.ok()) {
                registerMapping(legacyId, namespaceId);
                Loader.getlogger().info("Registered custom armor: " + namespaceId + " (legacy ID: " + legacyId + ")");
                return true;
            } else {
                Loader.getlogger().warning("Failed to register armor: " + namespaceId + " - " + result.error());
                return false;
            }
        } catch (Exception e) {
            Loader.getlogger().warning("Error registering armor: " + namespaceId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注册食物/饮品物品
     */
    public static boolean registerEdibleItem(int legacyId, String name, int maxStackSize,
                                             int nutrition, int useTime, boolean isDrink,
                                             boolean allowOffHand, String initFunction) {
        String namespaceId = generateNamespaceId(legacyId, name);

        try {
            BNCustomEdible item = new BNCustomEdible(namespaceId, name, legacyId,
                    maxStackSize, nutrition, useTime, isDrink, allowOffHand, initFunction);

            var result = Item.registerCustomItem(item.getClass());
            if (result.ok()) {
                registerMapping(legacyId, namespaceId);
                Loader.getlogger().info("Registered custom edible: " + namespaceId + " (legacy ID: " + legacyId + ")");
                return true;
            } else {
                Loader.getlogger().warning("Failed to register edible: " + namespaceId + " - " + result.error());
                return false;
            }
        } catch (Exception e) {
            Loader.getlogger().warning("Error registering edible: " + namespaceId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注册 ID 映射
     */
    private static void registerMapping(int legacyId, String namespaceId) {
        legacyIdToNamespace.put(legacyId, namespaceId);
        namespaceToLegacyId.put(namespaceId, legacyId);
    }

    /**
     * 通过数字 ID 获取命名空间 ID
     */
    public static String getNamespaceId(int legacyId) {
        return legacyIdToNamespace.get(legacyId);
    }

    /**
     * 通过命名空间 ID 获取数字 ID
     */
    public static Integer getLegacyId(String namespaceId) {
        return namespaceToLegacyId.get(namespaceId);
    }

    /**
     * 检查数字 ID 是否已注册
     */
    public static boolean isRegistered(int legacyId) {
        return legacyIdToNamespace.containsKey(legacyId);
    }

    /**
     * 解析物品类别
     */
    private static CreativeItemCategory parseCategory(String type) {
        if (type == null) return CreativeItemCategory.ITEMS;
        switch (type.toLowerCase()) {
            case "construction":
                return CreativeItemCategory.CONSTRUCTION;
            case "nature":
                return CreativeItemCategory.NATURE;
            case "equipment":
                return CreativeItemCategory.EQUIPMENT;
            case "items":
            default:
                return CreativeItemCategory.ITEMS;
        }
    }

    /**
     * 清理所有注册
     */
    public static void clear() {
        legacyIdToNamespace.clear();
        namespaceToLegacyId.clear();
    }
}
