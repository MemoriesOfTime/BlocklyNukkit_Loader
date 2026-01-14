/**
 * BlocklyNukkit 自定义方块示例
 * Custom Blocks Example for BlocklyNukkit
 *
 * 将此文件放入 plugins/BlocklyNukkit/ 目录即可加载
 * Place this file in plugins/BlocklyNukkit/ directory to load
 *
 * 注意: 自定义方块ID必须 >= 10000 (Nukkit-MOT 要求)
 * Note: Custom block ID must be >= 10000 (Nukkit-MOT requirement)
 *
 * 确保服务器配置中启用了实验模式:
 * Make sure experiment mode is enabled in server config:
 * nukkit.yml -> enableExperimentMode: true
 */

// ===============================================
// 1. 注册基础自定义方块 (Register Basic Custom Block)
// ===============================================

// 注册一个简单的矿石方块
// Register a simple ore block
blockitem.registerSolidBlock(
    10001,          // 方块ID (必须 >= 10000)
    "Ruby Ore",     // 方块名称
    3.0,            // 硬度 (数值越大越难挖)
    3.0,            // 爆炸抗性
    3,              // 工具类型: 0-无, 1-剑, 2-铲, 3-镐, 4-斧, 5-剪刀
    true,           // 是否可精准采集
    3,              // 最小掉落经验
    7,              // 最大掉落经验
    4               // 挖掘等级: 0-空手, 1-木, 2-金, 3-石, 4-铁, 5-钻石
);

// 注册宝石块 - 较硬的装饰方块
// Register gem block - harder decorative block
blockitem.registerSolidBlock(
    10002,          // 方块ID
    "Ruby Block",   // 方块名称
    5.0,            // 硬度 (比钻石块稍硬)
    6.0,            // 爆炸抗性
    3,              // 需要镐挖掘
    true,           // 可精准采集
    0,              // 无经验掉落
    0,              // 无经验掉落
    4               // 需要铁镐或更好
);

// 注册普通装饰方块 - 可用任何工具挖掘
// Register normal decorative block - can be mined with any tool
blockitem.registerSolidBlock(
    10003,          // 方块ID
    "Magic Stone",  // 方块名称
    1.5,            // 中等硬度
    1.5,            // 中等爆炸抗性
    0,              // 无特定工具要求
    true,           // 可精准采集
    0,              // 无经验
    0,              // 无经验
    0               // 空手可挖
);

// 注册泥土类方块 - 用铲子挖掘
// Register dirt-like block - use shovel
blockitem.registerSolidBlock(
    10004,          // 方块ID
    "Mystic Soil",  // 方块名称
    0.5,            // 较软
    0.5,            // 低爆炸抗性
    2,              // 铲子
    false,          // 不可精准采集
    0,              // 无经验
    0,              // 无经验
    0               // 空手可挖
);

// 注册木质方块 - 用斧头挖掘
// Register wood-like block - use axe
blockitem.registerSolidBlock(
    10005,          // 方块ID
    "Ancient Wood", // 方块名称
    2.0,            // 木头硬度
    2.0,            // 木头抗性
    4,              // 斧头
    true,           // 可精准采集
    0,              // 无经验
    0,              // 无经验
    0               // 空手可挖
);

// 注册高级矿石 - 需要钻石镐
// Register advanced ore - requires diamond pickaxe
blockitem.registerSolidBlock(
    10006,          // 方块ID
    "Void Crystal Ore",
    50.0,           // 非常硬 (比黑曜石还硬)
    1200.0,         // 极高爆炸抗性
    3,              // 需要镐
    true,           // 可精准采集
    10,             // 最小经验
    20,             // 最大经验
    5               // 需要钻石镐
);

// ===============================================
// 方块硬度参考 (Block Hardness Reference)
// ===============================================
/*
 * 原版方块硬度参考:
 * Vanilla block hardness reference:
 *
 * 泥土/沙子: 0.5
 * 圆石: 2.0
 * 木头: 2.0
 * 石头: 1.5
 * 铁块: 5.0
 * 钻石块: 5.0
 * 黑曜石: 50.0
 * 基岩: -1 (不可破坏)
 *
 * 工具等级要求:
 * Tool tier requirements:
 *
 * 0 = 空手 (Hand)
 * 1 = 木质 (Wood)
 * 2 = 金质 (Gold)
 * 3 = 石质 (Stone)
 * 4 = 铁质 (Iron)
 * 5 = 钻石 (Diamond)
 * 6 = 下界合金 (Netherite)
 */

// ===============================================
// 插件加载完成提示
// Plugin loaded notification
// ===============================================
logger.info("=================================");
logger.info("Custom Blocks Example 已加载!");
logger.info("已注册的方块ID: 10001-10006");
logger.info("使用 /give <玩家> <ID> 来获取方块");
logger.info("=================================");
