/**
 * BlocklyNukkit 自定义物品示例
 * Custom Items Example for BlocklyNukkit
 *
 * 将此文件放入 plugins/BlocklyNukkit/ 目录即可加载
 * Place this file in plugins/BlocklyNukkit/ directory to load
 *
 * 可用的全局对象 (Available global objects):
 * - server: Nukkit Server 实例
 * - plugin: BlocklyNukkit Loader 插件实例
 * - manager: FunctionManager (主要API)
 * - logger: 日志记录器
 * - blockitem: BlockItemManager (物品/方块注册)
 * - world: LevelManager (世界管理)
 * - entity: EntityManager (实体管理)
 * - inventory: InventoryManager (背包管理)
 * - database: DatabaseManager (数据库)
 */

// ===============================================
// 1. 注册简单物品 (Register Simple Item)
// ===============================================

// 最简单的注册方式 - 只需要ID和名称
// Simplest registration - only ID and name required
blockitem.registerSimpleItem(
    2001,           // 物品ID (建议 >= 2000，避免与内置物品冲突)
    "Ruby"          // 物品名称
);

// 带类别的注册
// Registration with category
blockitem.registerSimpleItem(
    2002,           // 物品ID
    "Sapphire",     // 物品名称
    "items"         // 类别: construction, nature, equipment, items
);

// 完整参数注册
// Full parameter registration
blockitem.registerSimpleItem(
    2003,           // 物品ID
    "Magic Crystal",// 物品名称
    16,             // 最大堆叠数量
    "equipment",    // 类别
    true,           // 是否显示为工具(竖着拿)
    true            // 是否可装备在副手
);

// 带初始化回调函数的注册
// Registration with initialization callback
blockitem.registerSimpleItem(
    2004,           // 物品ID
    "Enchanted Gem",// 物品名称
    64,             // 最大堆叠数量
    "items",        // 类别
    false,          // 是否显示为工具
    true,           // 是否可装备在副手
    "onEnchantedGemInit" // 初始化回调函数名
);

// 初始化回调函数 - 当物品被创建时调用
// Initialization callback - called when item is created
function onEnchantedGemInit(item) {
    logger.info("Enchanted Gem 物品已创建: " + item.getName());
    // 可以在这里设置物品的自定义NBT数据等
}

// ===============================================
// 2. 注册工具物品 (Register Tool Item)
// ===============================================

// 注册自定义剑
// Register custom sword
blockitem.registerToolItem(
    2010,           // 物品ID
    "Ruby Sword",   // 物品名称
    "sword",        // 工具类型: sword, shovel, pickaxe, axe, hoe
    5,              // 工具等级: 0-空手, 1-木, 2-金, 3-石, 4-铁, 5-钻石, 6-下界合金
    1500,           // 耐久值
    8,              // 攻击伤害
    false,          // 是否可装备在副手
    null            // 初始化函数 (可选)
);

// 注册自定义镐
// Register custom pickaxe
blockitem.registerToolItem(
    2011,           // 物品ID
    "Ruby Pickaxe", // 物品名称
    "pickaxe",      // 工具类型
    5,              // 工具等级
    2000,           // 耐久值
    6,              // 攻击伤害
    false,          // 是否可装备在副手
    "onRubyPickaxeInit"
);

function onRubyPickaxeInit(item) {
    logger.info("Ruby Pickaxe 已创建，耐久: " + item.getMaxDurability());
}

// ===============================================
// 3. 注册食物物品 (Register Food Item)
// ===============================================

// 注册自定义食物
// Register custom food
blockitem.registerFoodItem(
    2020,           // 物品ID
    "Magic Apple",  // 物品名称
    64,             // 最大堆叠数量
    8,              // 恢复的饥饿值
    20,             // 食用时间(游戏刻，20刻=1秒)
    true            // 是否可装备在副手
);

// 带回调的食物注册
blockitem.registerFoodItem(
    2021,           // 物品ID
    "Golden Berry",  // 物品名称
    16,             // 最大堆叠数量
    4,              // 恢复的饥饿值
    10,             // 食用时间
    false,          // 是否可装备在副手
    "onGoldenBerryInit"
);

function onGoldenBerryInit(item) {
    logger.info("Golden Berry 已注册");
}

// ===============================================
// 4. 注册饮品物品 (Register Drink Item)
// ===============================================

// 注册自定义饮品
// Register custom drink
blockitem.registerDrinkItem(
    2030,           // 物品ID
    "Energy Potion",// 物品名称
    16,             // 最大堆叠数量
    2,              // 恢复的饥饿值
    30,             // 饮用时间(游戏刻)
    true            // 是否可装备在副手
);

// ===============================================
// 5. 注册盔甲物品 (Register Armor Item)
// ===============================================

// 注册自定义头盔
// Register custom helmet
blockitem.registerArmorItem(
    2040,           // 物品ID
    "Ruby Helmet",  // 物品名称
    "helmet",       // 盔甲类型: helmet, chest, leggings, boots
    5,              // 盔甲等级: 0-无, 1-皮革, 2-铁, 3-锁链, 4-金, 5-钻石, 6-下界合金
    400,            // 耐久值
    4,              // 盔甲值
    false           // 是否可装备在副手
);

// 注册自定义胸甲
// Register custom chestplate
blockitem.registerArmorItem(
    2041,           // 物品ID
    "Ruby Chestplate",
    "chest",
    5,
    500,
    8,
    false
);

// 注册自定义护腿
// Register custom leggings
blockitem.registerArmorItem(
    2042,           // 物品ID
    "Ruby Leggings",
    "leggings",
    5,
    450,
    6,
    false
);

// 注册自定义靴子
// Register custom boots
blockitem.registerArmorItem(
    2043,           // 物品ID
    "Ruby Boots",
    "boots",
    5,
    350,
    3,
    false
);

// ===============================================
// 插件加载完成提示
// Plugin loaded notification
// ===============================================
logger.info("=================================");
logger.info("Custom Items Example 已加载!");
logger.info("已注册的物品ID: 2001-2004, 2010-2011, 2020-2021, 2030, 2040-2043");
logger.info("=================================");
