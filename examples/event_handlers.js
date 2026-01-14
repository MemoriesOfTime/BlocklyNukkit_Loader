/**
 * BlocklyNukkit 事件处理示例
 * Event Handlers Example for BlocklyNukkit
 *
 * 将此文件放入 plugins/BlocklyNukkit/ 目录即可加载
 * Place this file in plugins/BlocklyNukkit/ directory to load
 *
 * BlocklyNukkit 自动将同名函数绑定到对应事件
 * BlocklyNukkit automatically binds functions with matching names to events
 */

// ===============================================
// 1. 玩家事件 (Player Events)
// ===============================================

// 玩家加入服务器
// Player joins server
function PlayerJoinEvent(event) {
    var player = event.getPlayer();
    var playerName = player.getName();

    // 发送欢迎消息
    player.sendMessage("欢迎来到服务器, " + playerName + "!");

    // 广播消息给所有玩家
    server.broadcastMessage(playerName + " 加入了游戏!");

    // 日志记录
    logger.info("玩家 " + playerName + " 加入了服务器");
}

// 玩家退出服务器
// Player quits server
function PlayerQuitEvent(event) {
    var player = event.getPlayer();
    logger.info("玩家 " + player.getName() + " 离开了服务器");
}

// 玩家聊天
// Player chat
function PlayerChatEvent(event) {
    var player = event.getPlayer();
    var message = event.getMessage();

    // 记录聊天日志
    logger.info("[聊天] " + player.getName() + ": " + message);

    // 可以修改消息格式
    // event.setFormat("[自定义] %s: %s");

    // 可以取消事件阻止发送
    // if (message.contains("敏感词")) {
    //     event.setCancelled(true);
    //     player.sendMessage("请勿发送敏感内容!");
    // }
}

// 玩家移动
// Player move (注意: 高频事件，避免复杂操作)
function PlayerMoveEvent(event) {
    // 仅在特定条件下处理，避免性能问题
    var player = event.getPlayer();
    var to = event.getTo();

    // 示例: 检测玩家高度
    if (to.getY() > 200) {
        // player.sendMessage("你飞得太高了!");
    }
}

// 玩家交互方块
// Player interacts with block
function PlayerInteractEvent(event) {
    var player = event.getPlayer();
    var block = event.getBlock();
    var item = event.getItem();

    if (block != null) {
        // logger.info(player.getName() + " 点击了方块: " + block.getName());
    }
}

// 玩家放置方块
// Player places block
function BlockPlaceEvent(event) {
    var player = event.getPlayer();
    var block = event.getBlock();

    logger.info(player.getName() + " 放置了: " + block.getName());
}

// 玩家破坏方块
// Player breaks block
function BlockBreakEvent(event) {
    var player = event.getPlayer();
    var block = event.getBlock();

    logger.info(player.getName() + " 破坏了: " + block.getName() + " (ID: " + block.getId() + ")");

    // 示例: 检测是否破坏了自定义方块
    var blockId = block.getId();
    if (blockId >= 10000) {
        player.sendMessage("你破坏了一个自定义方块!");
    }
}

// ===============================================
// 2. 实体事件 (Entity Events)
// ===============================================

// 实体受伤
// Entity damage
function EntityDamageEvent(event) {
    var entity = event.getEntity();
    var damage = event.getDamage();
    var cause = event.getCause();

    // 检查是否是玩家
    if (entity instanceof Packages.cn.nukkit.Player) {
        // logger.info("玩家 " + entity.getName() + " 受到 " + damage + " 点伤害");
    }
}

// 实体被实体攻击
// Entity damaged by entity
function EntityDamageByEntityEvent(event) {
    var damager = event.getDamager();
    var entity = event.getEntity();
    var damage = event.getDamage();

    // 检查是否是玩家攻击玩家 (PvP)
    if (damager instanceof Packages.cn.nukkit.Player &&
        entity instanceof Packages.cn.nukkit.Player) {
        logger.info("PvP: " + damager.getName() + " -> " + entity.getName());
    }
}

// 实体死亡
// Entity death
function EntityDeathEvent(event) {
    var entity = event.getEntity();
    logger.info("实体死亡: " + entity.getName());
}

// 玩家死亡
// Player death
function PlayerDeathEvent(event) {
    var player = event.getEntity();
    var message = event.getDeathMessage();

    logger.info("玩家死亡: " + player.getName());
    // 可以修改死亡消息
    // event.setDeathMessage(player.getName() + " 不幸去世了...");
}

// ===============================================
// 3. 物品事件 (Item Events)
// ===============================================

// 玩家拾取物品
// Player picks up item
function InventoryPickupItemEvent(event) {
    var item = event.getItem().getItem();
    logger.info("拾取物品: " + item.getName() + " x" + item.getCount());
}

// 玩家丢弃物品
// Player drops item
function PlayerDropItemEvent(event) {
    var player = event.getPlayer();
    var item = event.getItem();
    logger.info(player.getName() + " 丢弃了: " + item.getName());
}

// 玩家使用物品 (吃食物等)
// Player uses item (eating food etc)
function PlayerItemConsumeEvent(event) {
    var player = event.getPlayer();
    var item = event.getItem();
    logger.info(player.getName() + " 使用了: " + item.getName());
}

// ===============================================
// 4. 服务器事件 (Server Events)
// ===============================================

// 服务器命令执行
// Server command execution
function ServerCommandEvent(event) {
    var sender = event.getSender();
    var command = event.getCommand();
    logger.info("控制台命令: " + command);
}

// 玩家命令执行
// Player command execution
function PlayerCommandPreprocessEvent(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    logger.info("玩家命令: " + player.getName() + " -> " + message);
}

// ===============================================
// 5. BlocklyNukkit 特有事件 (BlocklyNukkit Special Events)
// ===============================================

// BN初始化完成事件
// BN initialization completed
function BNInitializedEvent(event) {
    logger.info("BlocklyNukkit 初始化完成!");
}

// ===============================================
// 6. 使用 manager 注册自定义事件处理器
// Register custom event handlers using manager
// ===============================================

// 使用 manager.bindEventHandler 可以将任意函数绑定到事件
// Use manager.bindEventHandler to bind any function to an event
manager.bindEventHandler("PlayerJoinEvent", "myCustomJoinHandler");

function myCustomJoinHandler(event) {
    var player = event.getPlayer();
    player.sendMessage("这是来自自定义处理器的消息!");
}

// ===============================================
// 7. 延时任务和定时任务
// Delayed and scheduled tasks
// ===============================================

// 使用 manager.setTimeout 延时执行
// Use manager.setTimeout for delayed execution
// manager.setTimeout("delayedFunction", 100); // 100刻后执行(5秒)

function delayedFunction() {
    logger.info("延时任务执行!");
}

// 使用 manager.setInterval 定时执行
// Use manager.setInterval for repeated execution
// manager.setInterval("repeatedFunction", 200); // 每200刻执行(10秒)

function repeatedFunction() {
    logger.info("定时任务执行!");
}

// ===============================================
// 插件加载完成提示
// Plugin loaded notification
// ===============================================
logger.info("=================================");
logger.info("Event Handlers Example 已加载!");
logger.info("所有事件处理器已注册");
logger.info("=================================");
