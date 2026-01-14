# BlocklyNukkit JavaScript 示例

这个目录包含 BlocklyNukkit 的 JavaScript 插件示例。

## 使用方法

1. 将 `.js` 文件复制到 `plugins/BlocklyNukkit/` 目录
2. 重启服务器或重载插件
3. 脚本将自动加载执行

## 示例文件

### custom_items.js
演示如何注册各种类型的自定义物品：
- 简单物品 (Simple Items)
- 工具物品 (Tools)
- 食物物品 (Food)
- 饮品物品 (Drinks)
- 盔甲物品 (Armor)

### custom_blocks.js
演示如何注册自定义方块：
- 矿石类方块
- 装饰性方块
- 不同硬度和工具要求的方块

**注意**: 自定义方块 ID 必须 >= 10000

### event_handlers.js
演示如何处理游戏事件：
- 玩家事件 (加入/退出/聊天/移动)
- 方块事件 (放置/破坏)
- 实体事件 (伤害/死亡)
- 物品事件 (拾取/丢弃)
- 服务器事件

### complete_addon.js
一个完整的插件示例，包含：
- 红宝石矿石和方块
- 完整的红宝石工具套装
- 完整的红宝石盔甲套装
- 自定义矿石掉落逻辑
- 自定义命令 `/rubyaddon`

### native_custom_items.js
使用 Nukkit-MOT 原生 API 注册自定义物品的高级示例：
- 使用字符串 ID (如 `blocklynukkit:ruby`)
- 直接继承 Nukkit-MOT 原生类
- 更好的多版本兼容性
- 需要配套资源包支持

**适用场景**: 需要更精细控制物品属性，或需要更好多版本兼容性时使用

## 全局对象参考

脚本中可用的全局对象：

| 对象 | 说明 |
|------|------|
| `server` | Nukkit Server 实例 |
| `plugin` | BlocklyNukkit Loader 插件 |
| `manager` | FunctionManager - 主要 API |
| `logger` | 日志记录器 |
| `blockitem` | BlockItemManager - 物品/方块注册 |
| `world` | LevelManager - 世界管理 |
| `entity` | EntityManager - 实体管理 |
| `inventory` | InventoryManager - 背包管理 |
| `database` | DatabaseManager - 数据库操作 |
| `window` | WindowManager - UI 窗口 |
| `particle` | ParticleManager - 粒子效果 |

## API 快速参考

### 注册简单物品
```javascript
blockitem.registerSimpleItem(id, name);
blockitem.registerSimpleItem(id, name, stackSize, category, displayAsTool, canOffHand);
```

### 注册工具
```javascript
blockitem.registerToolItem(id, name, toolType, tier, durability, damage, canOffHand, initFunc);
// toolType: "sword", "pickaxe", "axe", "shovel", "hoe"
// tier: 0-空手, 1-木, 2-金, 3-石, 4-铁, 5-钻石, 6-下界合金
```

### 注册食物/饮品
```javascript
blockitem.registerFoodItem(id, name, stackSize, nutrition, eatTime, canOffHand);
blockitem.registerDrinkItem(id, name, stackSize, nutrition, drinkTime, canOffHand);
```

### 注册盔甲
```javascript
blockitem.registerArmorItem(id, name, armorType, tier, durability, armorPoints, canOffHand);
// armorType: "helmet", "chest", "leggings", "boots"
// tier (盔甲等级): 0-无, 1-皮革, 2-铁, 3-锁链, 4-金, 5-钻石, 6-下界合金
```

### 注册方块
```javascript
blockitem.registerSolidBlock(id, name, hardness, resistance, toolType, silkTouchable, minExp, maxExp, mineTier);
// toolType: 0-无, 1-剑, 2-铲, 3-镐, 4-斧, 5-剪刀
```

## 服务器配置要求

确保 `nukkit.yml` 中启用实验模式以支持自定义物品：

```yaml
enableExperimentMode: true
```

## 两种注册方式对比

| 特性 | 传统方式 (blockitem) | 原生方式 (Nukkit-MOT API) |
|------|---------------------|--------------------------|
| ID 类型 | 数字 (如 2001) | 字符串 (如 "myaddon:ruby") |
| 资源包 | 可选 | 必需 |
| 多版本兼容 | 一般 | 更好 |
| 创造模式物品栏 | 需手动添加 | 自动显示 |
| 使用难度 | 简单 | 中等 |
| 适用场景 | 快速开发、无材质需求 | 正式插件、需要精细控制 |

## 注意事项

1. 物品 ID 建议使用 >= 2000 的数值（内置物品已使用到约1100）
2. 方块 ID 必须 >= 10000
3. 避免在高频事件（如 PlayerMoveEvent）中执行复杂操作
4. 使用 `logger.info()` 而不是 `console.log()` 记录日志
5. 原生方式注册的物品需要对应的资源包才能正确显示材质
