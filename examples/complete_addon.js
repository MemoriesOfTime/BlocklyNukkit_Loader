/**
 * BlocklyNukkit 完整插件示例
 * Complete Addon Example for BlocklyNukkit
 *
 * 这个示例展示了如何创建一个完整的自定义内容插件
 * This example shows how to create a complete custom content addon
 *
 * 包含: 自定义物品套装 + 自定义矿石 + 事件处理
 * Includes: Custom item set + Custom ore + Event handling
 */

// ===============================================
// 配置区 (Configuration)
// ===============================================
var CONFIG = {
    // 物品ID起始 (建议使用较大数字避免冲突)
    ITEM_ID_START: 2000,
    // 方块ID起始 (必须 >= 10000)
    BLOCK_ID_START: 20000,
    // 是否启用调试日志
    DEBUG: true
};

// 调试日志函数
function debug(msg) {
    if (CONFIG.DEBUG) {
        logger.info("[RubyAddon] " + msg);
    }
}

// ===============================================
// 第一部分: 注册自定义矿石和方块
// Part 1: Register Custom Ores and Blocks
// ===============================================

debug("正在注册自定义方块...");

// 红宝石矿石 - 在地下生成
blockitem.registerSolidBlock(
    CONFIG.BLOCK_ID_START + 1,  // ID: 20001
    "Ruby Ore",                  // 名称
    3.0,                         // 硬度
    3.0,                         // 爆炸抗性
    3,                           // 工具类型 (镐)
    true,                        // 可精准采集
    3, 7,                        // 经验范围
    4                            // 需要铁镐
);

// 红宝石块 - 合成用
blockitem.registerSolidBlock(
    CONFIG.BLOCK_ID_START + 2,  // ID: 20002
    "Ruby Block",
    5.0, 6.0,
    3, true,
    0, 0,
    4
);

debug("方块注册完成!");

// ===============================================
// 第二部分: 注册自定义物品
// Part 2: Register Custom Items
// ===============================================

debug("正在注册自定义物品...");

// 红宝石 - 基础材料
blockitem.registerSimpleItem(
    CONFIG.ITEM_ID_START + 1,    // ID: 2001
    "Ruby",
    64,
    "items",
    false,
    false
);

// 红宝石碎片 - 矿石掉落
blockitem.registerSimpleItem(
    CONFIG.ITEM_ID_START + 2,    // ID: 2002
    "Ruby Fragment",
    64,
    "items"
);

// ===============================================
// 第三部分: 注册红宝石工具套装
// Part 3: Register Ruby Tool Set
// ===============================================

// 红宝石剑
blockitem.registerToolItem(
    CONFIG.ITEM_ID_START + 10,   // ID: 2010
    "Ruby Sword",
    "sword",
    5,                           // 钻石级
    1800,                        // 耐久
    9,                           // 攻击力
    false,
    null
);

// 红宝石镐
blockitem.registerToolItem(
    CONFIG.ITEM_ID_START + 11,   // ID: 2011
    "Ruby Pickaxe",
    "pickaxe",
    5,
    2000,
    6,
    false,
    null
);

// 红宝石斧
blockitem.registerToolItem(
    CONFIG.ITEM_ID_START + 12,   // ID: 2012
    "Ruby Axe",
    "axe",
    5,
    2000,
    7,
    false,
    null
);

// 红宝石铲
blockitem.registerToolItem(
    CONFIG.ITEM_ID_START + 13,   // ID: 2013
    "Ruby Shovel",
    "shovel",
    5,
    2000,
    5,
    false,
    null
);

// 红宝石锄
blockitem.registerToolItem(
    CONFIG.ITEM_ID_START + 14,   // ID: 2014
    "Ruby Hoe",
    "hoe",
    5,
    2000,
    1,
    false,
    null
);

debug("工具注册完成!");

// ===============================================
// 第四部分: 注册红宝石盔甲套装
// Part 4: Register Ruby Armor Set
// ===============================================

// 红宝石头盔
blockitem.registerArmorItem(
    CONFIG.ITEM_ID_START + 20,   // ID: 2020
    "Ruby Helmet",
    "helmet",
    5,                           // 钻石级
    450,                         // 耐久
    4,                           // 盔甲值
    false
);

// 红宝石胸甲
blockitem.registerArmorItem(
    CONFIG.ITEM_ID_START + 21,   // ID: 2021
    "Ruby Chestplate",
    "chest",
    5,
    600,
    9,
    false
);

// 红宝石护腿
blockitem.registerArmorItem(
    CONFIG.ITEM_ID_START + 22,   // ID: 2022
    "Ruby Leggings",
    "leggings",
    5,
    550,
    7,
    false
);

// 红宝石靴子
blockitem.registerArmorItem(
    CONFIG.ITEM_ID_START + 23,   // ID: 2023
    "Ruby Boots",
    "boots",
    5,
    500,
    4,
    false
);

debug("盔甲注册完成!");

// ===============================================
// 第五部分: 注册食物
// Part 5: Register Food
// ===============================================

// 红宝石苹果 - 特殊食物
blockitem.registerFoodItem(
    CONFIG.ITEM_ID_START + 30,   // ID: 2030
    "Ruby Apple",
    16,
    10,                          // 恢复10饥饿值
    20,                          // 1秒食用时间
    true
);

debug("食物注册完成!");

// ===============================================
// 第六部分: 事件处理 - 自定义掉落
// Part 6: Event Handling - Custom Drops
// ===============================================

// 方块破坏事件 - 处理红宝石矿石掉落
function BlockBreakEvent(event) {
    var block = event.getBlock();
    var player = event.getPlayer();
    var blockId = block.getId();

    // 检查是否破坏了红宝石矿石
    if (blockId == CONFIG.BLOCK_ID_START + 1) {
        // 获取玩家手持物品
        var item = player.getInventory().getItemInHand();

        // 检查是否使用正确的工具
        if (item.isPickaxe() && item.getTier() >= 4) {
            // 清除默认掉落
            event.setDrops([]);

            // 检查是否有精准采集附魔
            if (item.hasEnchantment(Packages.cn.nukkit.item.enchantment.Enchantment.ID_SILK_TOUCH)) {
                // 掉落矿石方块本身
                var oreBlock = Packages.cn.nukkit.item.Item.get(blockId);
                event.setDrops([oreBlock]);
            } else {
                // 掉落1-3个红宝石碎片
                var dropCount = Math.floor(Math.random() * 3) + 1;
                var rubyFragment = Packages.cn.nukkit.item.Item.get(CONFIG.ITEM_ID_START + 2, 0, dropCount);
                event.setDrops([rubyFragment]);
            }

            debug(player.getName() + " 挖掘了红宝石矿石，掉落 " + dropCount + " 个碎片");
        } else {
            // 工具不正确，不掉落任何东西
            event.setDrops([]);
            player.sendMessage("§c需要铁镐或更好的工具来挖掘红宝石矿石!");
        }
    }
}

// ===============================================
// 第七部分: 命令处理
// Part 7: Command Handling
// ===============================================

// 玩家命令处理
function PlayerCommandPreprocessEvent(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    var args = message.split(" ");
    var cmd = args[0].toLowerCase();

    // 处理自定义命令
    if (cmd == "/rubyaddon" || cmd == "/ra") {
        event.setCancelled(true);

        if (args.length < 2) {
            player.sendMessage("§6=== Ruby Addon 帮助 ===");
            player.sendMessage("§e/ra give <item> §7- 获取红宝石物品");
            player.sendMessage("§e/ra info §7- 显示插件信息");
            return;
        }

        var subCmd = args[1].toLowerCase();

        if (subCmd == "info") {
            player.sendMessage("§6=== Ruby Addon 信息 ===");
            player.sendMessage("§7物品ID范围: " + CONFIG.ITEM_ID_START + " - " + (CONFIG.ITEM_ID_START + 50));
            player.sendMessage("§7方块ID范围: " + CONFIG.BLOCK_ID_START + " - " + (CONFIG.BLOCK_ID_START + 10));
        }
        else if (subCmd == "give" && args.length >= 3) {
            var itemName = args[2].toLowerCase();
            var itemId = -1;

            // 物品名称映射
            var itemMap = {
                "ruby": CONFIG.ITEM_ID_START + 1,
                "fragment": CONFIG.ITEM_ID_START + 2,
                "sword": CONFIG.ITEM_ID_START + 10,
                "pickaxe": CONFIG.ITEM_ID_START + 11,
                "axe": CONFIG.ITEM_ID_START + 12,
                "shovel": CONFIG.ITEM_ID_START + 13,
                "hoe": CONFIG.ITEM_ID_START + 14,
                "helmet": CONFIG.ITEM_ID_START + 20,
                "chestplate": CONFIG.ITEM_ID_START + 21,
                "leggings": CONFIG.ITEM_ID_START + 22,
                "boots": CONFIG.ITEM_ID_START + 23,
                "apple": CONFIG.ITEM_ID_START + 30
            };

            if (itemMap[itemName] != undefined) {
                itemId = itemMap[itemName];
                var count = args.length >= 4 ? parseInt(args[3]) : 1;
                var item = Packages.cn.nukkit.item.Item.get(itemId, 0, count);
                player.getInventory().addItem(item);
                player.sendMessage("§a已给予: " + item.getName() + " x" + count);
            } else {
                player.sendMessage("§c未知物品: " + itemName);
                player.sendMessage("§7可用物品: ruby, fragment, sword, pickaxe, axe, shovel, hoe, helmet, chestplate, leggings, boots, apple");
            }
        }
    }
}

// ===============================================
// 第八部分: 玩家加入提示
// Part 8: Player Join Notification
// ===============================================

function PlayerJoinEvent(event) {
    var player = event.getPlayer();
    // 延迟1秒发送消息，避免被其他消息刷掉
    manager.setTimeout(F(function() {
        player.sendMessage("§6[Ruby Addon] §e欢迎! 使用 /ra help 查看帮助");
    }), 20);
}

// ===============================================
// 插件加载完成
// Plugin Loaded
// ===============================================
logger.info("§a========================================");
logger.info("§6  Ruby Addon §a已成功加载!");
logger.info("§7  作者: BlocklyNukkit Example");
logger.info("§7  版本: 1.0.0");
logger.info("§a========================================");
logger.info("§e已注册内容:");
logger.info("§7  - 2 个自定义方块 (ID: 20001-20002)");
logger.info("§7  - 2 个基础物品 (ID: 2001-2002)");
logger.info("§7  - 5 个工具 (ID: 2010-2014)");
logger.info("§7  - 4 个盔甲 (ID: 2020-2023)");
logger.info("§7  - 1 个食物 (ID: 2030)");
logger.info("§e使用命令 /ra 或 /rubyaddon 查看帮助");
logger.info("§a========================================");
