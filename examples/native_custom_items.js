/**
 * BlocklyNukkit 原生自定义物品示例 (Nukkit-MOT Native API)
 * Native Custom Items Example using Nukkit-MOT API
 *
 * 此示例展示如何使用 Nukkit-MOT 原生自定义物品 API
 * This example shows how to use Nukkit-MOT native custom item API
 *
 * 与传统方式的区别:
 * - 使用字符串ID (如 "myaddon:ruby") 而非数字ID
 * - 物品自动显示在创造模式物品栏
 * - 更好的多版本兼容性
 * - 需要材质包支持
 *
 * 要求:
 * - nukkit.yml 中 enableExperimentMode: true
 * - 需要对应的资源包 (材质)
 */

// ===============================================
// 导入所需的 Java 类
// Import required Java classes
// ===============================================

// Nukkit-MOT 原生类
var Item = Packages.cn.nukkit.item.Item;
var CreativeItemCategory = Packages.cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;

// BlocklyNukkit 包装类
var BNCustomItem = Packages.com.blocklynukkit.loader.other.AddonsAPI.BNCustomItem;
var BNCustomTool = Packages.com.blocklynukkit.loader.other.AddonsAPI.BNCustomTool;
var BNCustomEdible = Packages.com.blocklynukkit.loader.other.AddonsAPI.BNCustomEdible;
var BNCustomArmor = Packages.com.blocklynukkit.loader.other.AddonsAPI.BNCustomArmor;

// ===============================================
// 辅助函数
// Helper functions
// ===============================================

/**
 * 注册自定义物品的辅助函数
 * Helper function to register custom items
 */
function registerItem(itemClass) {
    try {
        Item.registerCustomItem(itemClass);
        logger.info("[NativeItems] Registered: " + itemClass.getName());
        return true;
    } catch (e) {
        logger.warning("[NativeItems] Failed to register item: " + e.getMessage());
        return false;
    }
}

// ===============================================
// 1. 注册简单物品 (Simple Items)
// ===============================================

/**
 * 创建自定义简单物品类
 *
 * 参数说明:
 * @param id          - 字符串ID，格式: "命名空间:物品名" (如 "myaddon:ruby")
 * @param name        - 物品显示名称
 * @param textureName - 材质名称 (对应资源包中的材质)
 * @param maxStack    - 最大堆叠数量
 * @param category    - 创造模式类别
 * @param allowOffHand - 是否可装备在副手
 * @param handEquipped - 是否显示为工具 (竖着拿)
 */

// 示例: 自定义宝石类
var EmeraldGem = Java.extend(BNCustomItem, {
    // 无需额外覆写，使用基类实现
});

// 创建宝石实例并注册
var emeraldGemClass = new EmeraldGem(
    "blocklynukkit:emerald_gem",    // ID
    "Emerald Gem",                   // 名称
    "emerald_gem",                   // 材质名 (需要资源包)
    64,                              // 最大堆叠
    CreativeItemCategory.ITEMS,      // 类别
    true,                            // 可副手
    false                            // 不显示为工具
).getClass();

// registerItem(emeraldGemClass);  // 取消注释以启用

// ===============================================
// 2. 注册工具物品 (Tool Items)
// ===============================================

/**
 * 创建自定义工具类
 *
 * 工具类型:
 * 1 = 剑 (sword)
 * 2 = 铲 (shovel)
 * 3 = 镐 (pickaxe)
 * 4 = 斧 (axe)
 * 5 = 锄 (hoe)
 *
 * 工具等级:
 * 0 = 空手, 1 = 木, 2 = 金, 3 = 石, 4 = 铁, 5 = 钻石, 6 = 下界合金
 */

var CustomSword = Java.extend(BNCustomTool, {});

var customSwordClass = new CustomSword(
    "blocklynukkit:crystal_sword",  // ID
    "Crystal Sword",                 // 名称
    "crystal_sword",                 // 材质名
    2000,                            // 耐久值
    10,                              // 攻击伤害
    5,                               // 工具等级 (钻石级)
    1,                               // 速度
    1,                               // 工具类型 (剑)
    CreativeItemCategory.EQUIPMENT,  // 类别
    false                            // 不可副手
).getClass();

// registerItem(customSwordClass);

var CustomPickaxe = Java.extend(BNCustomTool, {});

var customPickaxeClass = new CustomPickaxe(
    "blocklynukkit:crystal_pickaxe",
    "Crystal Pickaxe",
    "crystal_pickaxe",
    2500,                            // 高耐久
    6,                               // 攻击伤害
    5,                               // 钻石级
    1,
    3,                               // 工具类型 (镐)
    CreativeItemCategory.EQUIPMENT,
    false
).getClass();

// registerItem(customPickaxeClass);

// ===============================================
// 3. 注册食物物品 (Food Items)
// ===============================================

/**
 * 创建自定义食物类
 *
 * @param isDrink     - 是否为饮品 (影响食用动画)
 * @param canAlwaysEat - 是否可在饱食时食用
 */

var CustomFood = Java.extend(BNCustomEdible, {});

var customFoodClass = new CustomFood(
    "blocklynukkit:magic_fruit",    // ID
    "Magic Fruit",                   // 名称
    "magic_fruit",                   // 材质名
    16,                              // 最大堆叠
    8,                               // 饥饿值
    20,                              // 食用时间 (刻)
    false,                           // 不是饮品
    true,                            // 可随时食用
    CreativeItemCategory.ITEMS,
    true                             // 可副手
).getClass();

// registerItem(customFoodClass);

// 自定义饮品
var CustomDrink = Java.extend(BNCustomEdible, {});

var customDrinkClass = new CustomDrink(
    "blocklynukkit:energy_drink",
    "Energy Drink",
    "energy_drink",
    16,
    4,                               // 较低饥饿值
    30,                              // 较长饮用时间
    true,                            // 是饮品
    true,                            // 可随时饮用
    CreativeItemCategory.ITEMS,
    true
).getClass();

// registerItem(customDrinkClass);

// ===============================================
// 4. 注册盔甲物品 (Armor Items)
// ===============================================

/**
 * 创建自定义盔甲类
 *
 * 盔甲槽位:
 * 0 = 头盔 (helmet)
 * 1 = 胸甲 (chestplate)
 * 2 = 护腿 (leggings)
 * 3 = 靴子 (boots)
 *
 * 盔甲等级:
 * 0 = 无, 1 = 皮革, 2 = 铁, 3 = 锁链, 4 = 金, 5 = 钻石, 6 = 下界合金
 */

var CustomHelmet = Java.extend(BNCustomArmor, {});

var customHelmetClass = new CustomHelmet(
    "blocklynukkit:crystal_helmet",
    "Crystal Helmet",
    "crystal_helmet",
    500,                             // 耐久值
    4,                               // 盔甲值
    0,                               // 槽位 (头盔)
    5,                               // 钻石级
    CreativeItemCategory.EQUIPMENT,
    false
).getClass();

// registerItem(customHelmetClass);

var CustomChestplate = Java.extend(BNCustomArmor, {});

var customChestplateClass = new CustomChestplate(
    "blocklynukkit:crystal_chestplate",
    "Crystal Chestplate",
    "crystal_chestplate",
    600,
    9,                               // 高盔甲值
    1,                               // 槽位 (胸甲)
    5,
    CreativeItemCategory.EQUIPMENT,
    false
).getClass();

// registerItem(customChestplateClass);

var CustomLeggings = Java.extend(BNCustomArmor, {});

var customLeggingsClass = new CustomLeggings(
    "blocklynukkit:crystal_leggings",
    "Crystal Leggings",
    "crystal_leggings",
    550,
    7,
    2,                               // 槽位 (护腿)
    5,
    CreativeItemCategory.EQUIPMENT,
    false
).getClass();

// registerItem(customLeggingsClass);

var CustomBoots = Java.extend(BNCustomArmor, {});

var customBootsClass = new CustomBoots(
    "blocklynukkit:crystal_boots",
    "Crystal Boots",
    "crystal_boots",
    500,
    4,
    3,                               // 槽位 (靴子)
    5,
    CreativeItemCategory.EQUIPMENT,
    false
).getClass();

// registerItem(customBootsClass);

// ===============================================
// 5. 完整示例: 水晶套装
// Full Example: Crystal Set
// ===============================================

/**
 * 要启用此示例，请:
 * 1. 创建对应的资源包 (材质文件)
 * 2. 取消下方注册代码的注释
 * 3. 重启服务器
 */

function registerCrystalSet() {
    logger.info("[NativeItems] Registering Crystal Set...");

    var success = 0;
    var total = 7;

    // 注册所有物品
    // if (registerItem(emeraldGemClass)) success++;
    // if (registerItem(customSwordClass)) success++;
    // if (registerItem(customPickaxeClass)) success++;
    // if (registerItem(customFoodClass)) success++;
    // if (registerItem(customHelmetClass)) success++;
    // if (registerItem(customChestplateClass)) success++;
    // if (registerItem(customLeggingsClass)) success++;
    // if (registerItem(customBootsClass)) success++;

    logger.info("[NativeItems] Registered " + success + "/" + total + " items");
}

// 取消注释以启用水晶套装
// registerCrystalSet();

// ===============================================
// 对比: 传统方式 vs 原生方式
// Comparison: Traditional vs Native
// ===============================================

/*
传统方式 (使用 blockitem):
- 使用数字ID (如 2001)
- 通过 Javassist 动态生成类
- 兼容性好，无需资源包

blockitem.registerSimpleItem(2001, "Ruby", 64, "items", false, false);
blockitem.registerToolItem(2010, "Ruby Sword", "sword", 5, 1500, 8, false, null);

原生方式 (使用 Nukkit-MOT API):
- 使用字符串ID (如 "myaddon:ruby")
- 直接继承原生类
- 需要资源包支持
- 更好的多版本兼容性
- 自动显示在创造模式物品栏

var MyItem = Java.extend(BNCustomItem, {});
var myItemClass = new MyItem("myaddon:ruby", "Ruby", "ruby_texture", 64,
                             CreativeItemCategory.ITEMS, false, false).getClass();
Item.registerCustomItem(myItemClass);
*/

// ===============================================
// 插件加载提示
// Plugin loaded notification
// ===============================================
logger.info("=================================");
logger.info("Native Custom Items Example 已加载!");
logger.info("此示例展示 Nukkit-MOT 原生自定义物品 API");
logger.info("要启用物品注册，请取消代码中的注释");
logger.info("并创建对应的资源包");
logger.info("=================================");
