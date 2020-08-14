## 1.2.8.3

Bugs Fixed

- 修复了bnnpc浮空走路bug
- 修复了浮空物品空事件报错
- 现在的报错信息比以前好看多了

manager

- <E> callFunction(String functionname,Object... args) --callFunction会返回函数的返回值了
- <E> getVariableFrom(String scriptName,String varName) --根据bn插件名和变量名获取变量内容
- void putVariableTo(String scriptName,String varName,<E> var) --把变量值以指定变量名放到指定bn插件中
- double getCPULoad()
- int getCPUCores()
- double getMemoryTotalSizeMB()
- double getMemoryUsedSizeMB()
- void forceDisconnect(Player player)
- Array<String> getEventFunctions(Event event)
- void getServerMotd(String host, int port, String callback) --根据服务器IP和端口获取在线人数信息

blockitem

- void setItemColor(Item item,int r,int g,int b)
- void setItemUnbreakable(Item item,boolean unbreakable)

inventory

- Item getEntityHelmet(Entity entity)
- Item getEntityChestplate(Entity entity)
- Item getEntityLeggings(Entity entity)
- Item getEntityBoots(Entity entity)
- Item getEntityItemInHand(Entity entity)
- Item getEntityItemInOffHand(Entity entity)
- void setEntityItemHelmet(Entity entity,Item item)
- void setEntityItemChestplate(Entity entity,Item item)
- void setEntityItemLeggings(Entity entity,Item item)
- void setEntityItemBoots(Entity entity,Item item)
- void setEntityItemInHand(Entity entity,Item item)
- void setEntityItemInOffHand(Entity entity,Item item)
- Item getInventorySlot(Inventory inv,int slot)

window

- void forceClearWindow(Player player)
- int getEventResponseIndex(PlayerFormRespondedEvent event)

entity

- boolean isPlayer(Entity e)
- void spawnFallingBlock(Position pos, Block block, boolean enableGravity,boolean canBePlaced)

gameapi --新的基对象

- void createGame(String name,boolean useTeam,String startGameCallBack,String endGameCallBack,String mainLoopCallBack,String deathCallBack)
    -- 创建一个小游戏房间
- void joinGame(Player player, String gameName) --让玩家进入指定名称的小游戏，自动匹配房间
- void leaveGame(Player player) --让玩家从小游戏房间离开
- boolean isPlayerInGame(Player player) --玩家是否正在玩某个小游戏
- GameBase getPlayerRoom(Player player) --获取玩家正在玩的小游戏对象
- Array<GameBase> getAllRoomByName(String gameName) --获取游戏名称相同的所有小游戏房间对象组成的数组
- Array<String> getAllGameNames() --获取所有正在运行的小游戏房间的名字组成的数组
- Messager getMessager(String prefix)
- Messager getGameMessager(GameBase game)
- Multiline getMultiline(String messageType)
- InventoryMenu createInventoryMenu(String inventoryType, String title)
- FormMenu createFormMenu(String title, String content)
- void addMenuItem(InventoryMenu menu, int slot, Item item, String inventoryCallback)
- void addMenuButton(FormMenu menu,String buttonText,String imageData,String formCallback)
- Scoreboard getScoreboard(Player p)
- void setObjective(Scoreboard sb, String objectiveName,String displayName)

GameBase --小游戏房间对象


EventLoader --73 new

- BlockFadeEvent
- BlockFallEvent
- BlockFromToEvent
- BlockGrowEvent
- BlockIgniteEvent
- BlockPistonChangeEvent
- BlockRedstoneEvent
- DoorToggleEvent
- CreatureSpawnEvent
- CreeperPowerEvent
- EntityArmorChangeEvent
- EntityBlockChangeEvent
- EntityCombustByBlockEvent
- EntityCombustByEntityEvent
- EntityCombustEvent
- EntityDamageByBlockEvent
- EntityDamageByChildEntityEvent
- EntityExplodeEvent
- EntityMotionEvent
- EntityPortalEnterEvent
- EntityRegainHealthEvent
- EntityShootBowEvent
- EntityVehicleEnterEvent
- EntityVehicleExitEvent
- ExplosionPrimeEvent
- BrewEvent
- EnchantItemEvent
- InventoryMoveItemEvent
- StartBrewEvent
- ChunkLoadEvent
- ChunkPopulateEvent
- LevelInitEvent
- LevelLoadEvent
- LevelSaveEvent
- LevelUnloadEvent
- SpawnChangeEvent
- ThunderChangeEvent
- WeatherChangeEvent
- PlayerAchievementAwardedEvent
- PlayerAnimationEvent
- PlayerAsyncPreLoginEvent
- PlayerBlockPickEvent
- PlayerBucketEmptyEvent
- PlayerBucketFillEvent
- PlayerChangeSkinEvent
- PlayerChunkRequestEvent
- PlayerCreationEvent
- PlayerDropItemEvent
- PlayerEatFoodEvent
- PlayerEditBookEvent
- PlayerFoodLevelChangeEvent
- PlayerGameModeChangeEvent
- PlayerGlassBottleFillEvent
- PlayerInvalidMoveEvent
- PlayerItemConsumeEvent
- PlayerLocallyInitializedEvent
- PlayerMapInfoRequestEvent
- PlayerMouseOverEntityEvent
- PlayerServerSettingsRequestEvent
- PlayerSettingsRespondedEvent
- PluginDisableEvent
- PluginEnableEvent
- PotionApplyEvent
- PotionCollideEvent
- PlayerDataSerializeEvent
- RemoteServerCommandEvent
- EntityEnterVehicleEvent
- EntityExitVehicleEvent
- VehicleCreateEvent
- VehicleDamageEvent
- VehicleDestroyEvent
- VehicleMoveEvent
- VehicleUpdateEvent
- LightningStrikeEvent

## 1.2.8.2

Bug Fixed

- 修复了bnnpc和bn浮空字莫名其妙消失的问题

New

- 现在可以在js代码的开头加上一行注释//pragma es9来开启es9语言特性，但是会有些许性能损失，损失不大，可以放心使用
- 此功能仍然是试验功能，如果报错，请立即反馈，感谢
- bninstall命令已经弃用，所有库全部打包进bn解释器jar，这样做是为了节约内存空间。

CustomWindowBuilder

- Custom showAsSetting(Player p, String callback)
- Custom buildDropdown(String title,String inner,int index)

EventLoader

- ChunkUnloadEvent

entity

- void setPlayerExp(Player player,int exp)
- int getPlayerExp(Player player)
- void setPlayerExpLevel(Player player,int lel)
- int getPlayerExpLevel(Player player)
- void setPlayerHunger(Player player,int hunger)
- int getPlayerHunger(Player player)

window

- void makeTipsVar(String varname,String providerCallback)
- void makeTipsStatic(String varname,String toReplace)

## 1.2.8.1

Entity

- BNNPC buildNPC(Position pos,String name,String skinID,int calltick,String callfunction,String attackfunction)
- void showFloatingItem(Position pos,Item item)
- void removeFloatingItem(Position pos,Item item)

windowbuilder

- Custom buildSlider(String title,double min,double max)
- Custom buildSlider(String title,double min,double max,int step)
- Custom buildSlider(String title,double min,double max,int step,double defaultvalue)
- Custom buildStepSlider(String title,String options)
- Custom buildStepSlider(String title,String options,int index)

window

- String getEventCustomVar(PlayerFormRespondedEvent event,int id,String mode)
- mode可以为input toggle dropdown slider stepslider

BNNPC

- void displaySwing()
- void setSwim(boolean swim)
- void setSwim()
- void setTickCallback(String callback)
- void setAttackCallback(String callback)

manager

- void bStats(String pluginName,String pluginVer,String authorName,int pluginid)
- void callFunction(String functionname,Object... args) --修复了错误的拼写

world

- void loadScreenTP(Player player,Position pos)
- void loadScreenTP(Player player,Position pos,int loadScreenTick)
- void clearChunk(Position pos)

EventLoader

- PlayerHeldEvent
- InventoryClickEvent

Bug Fixed

- manager.kickPlayer不再会显示"kicked by admin"前缀了
- bnnpc打人会正确地摇动手臂了
- callFunction拼写是正确的了
- database现在真的可用了，所有库都会被正确安装

## 1.2.8.0_LTS
类库管理器

- 现在类库管理器可以直接安装模块了，暂时只有python和database两个模块
- 使用命令 bninstall 模块名 安装这个类库

window

- void setBelowName(Player player,String str)

manager

- void loadJar(String path)

world

- void setOceanGenerator(int seaLevel)

entity

- BNNPC buildNPC(Position pos,String name,String skinID)
- BNNPC buildNPC(Position pos,String name,String skinID,int calltick,String callfunction)

BNNPC

- void turnRound(double yaw)
- void headUp(double pitch)
- void setEnableAttack(boolean attack)
- void setEnableAttack()
- void setEnableHurt(boolean hurt)
- void setEnableHurt()
- void displayHurt()
- void start()
- void setEnableGravity(boolean gravity)
- void setEnableGravity()
- void setG(double newg)
- void lookAt(Position pos)
- Player getNearestPlayer()
- boolean isSneak()
- void setSneak(boolean sneak)
- void setSneak()
- void jump()
- void setJumpHigh(double j)
- void setEnableKnockBack(boolean knock)
- void setEnableKnockBack()
- void setKnockBase(double base)
- boolean canMoveTo(Position to)
- boolean findAndMove(Position to)
- void setSpeed(double s)
- void setRouteMax(int m)
- void stopMove()
- void hit(Entity entity)

bug fixed:

- entity的effect有些药水不显示问题，但是仍然有些药水效果因为nk不支持无法显示
- world生成VOID和OCEAN出错问题
- 天域世界配置丢失问题
- ssh报错问题
- 现在窗口管理器的操作函数都返回自身，可以直接在代码里连缀了


## 1.2.7.4

Languages

现在可以使用python2.7来制作插件了
添加了对python开发插件的完全支持，只需要下载额外的py支持包即可使用python插件
对于python开发插件的支持将与JavaScript保持同步，python与js使用同一套bn类库，所有js的bn类库(除了Java模块)之外都可以在python中直接调用，无需import
支持全部的python2.7原生标准语法和标准库，运行时与js相同，编译为java字节码运行，不必担心效率低下问题
pythonForBN支持模块下载：https://tools.blocklynukkit.com/pythonForBN.jar
下载后直接放到./plugins/BlocklyNukkit文件夹下面即可

EventLoader

- PlayerInteractEntityEvent
- PlayerDamageByPlayerEvent
- EntityKilledByEntityEvent
- PlayerDamageByEntityEvent
- EntityKilledByEntityEvent
- EntityKilledByPlayerEvent
- PlayerRespawnEvent

window

- void setPlayerBossBar(Player player,String text,float len)
- void removePlayerBossBar(Player player)
- double getLengthOfPlayerBossBar(Player player)
- String getTextOfPlayerBossBar(Player player)

manager

- void createPermission(String per,String description,String defaultper)
- void removePermission(String per)
- boolean checkPlayerPermission(String per,Player player)
- String MD5Encryption(String str)
- String SHA1Encryption(String str)
- void createCommand(String name, String description, String callbackFunctionName, String per)
- void newCommand(String name, String description, Function jsFunction,String per)

entity

- int getNetworkID(Entity entity)
- String getIDName(Entity entity)
- void spawnEntity(String name,Position pos)

notemusic

- HornSongPlayer buildHorn(Song song, Position pos, boolean isloop, boolean isautodestroy)
- void addPlayerToHorn(HornSongPlayer SongPlayer, Player player)
- void removePlayerToHorn(HornSongPlayer SongPlayer, Player player)
- Array getPlayerInHorn(HornSongPlayer radioSongPlayer)
- void setHornStatus(HornSongPlayer radioSongPlayer, boolean isplaying)
- Song getSongInHorn(HornSongPlayer radioSongPlayer)

world

- genLevel新增"OCEAN"海洋世界生成器

bug fixed

- setNameTagAlwaysVisable error


## 1.2.7.2
manager

- String formatJSON(String json)
- 修复writeFile函数无法自动创建路径的错误


## 1.2.7  

manager

- String readFile(String path)
- void writeFile(String path,String text)
- boolean isFileSame(String path1,String path2)
- String JSONtoYAML(String json)
- String YAMLtoJSON(String yaml)
- void newCommand(String name, String description, Function fun)
- int setTimeout(Function fun,int delay,<E+>... args)
- void clearTimeout(int id)
- int setInterval(Function fun,int delay,<E+>... args)
- void clearInterval(int id)
- void isWindows()
- int getPlayerGameMode(Player player)

Loader

- \_\_NAME\_\_ 表示加载的js文件的名称(可防御低级改名倒卖)
- 新版发布后，24小时强制更新

Custom/Modal/Simple (WindowBuilder)

- void showToPlayerCallLambda(Player p, Function fun)

blockitem

- Array<Enchantment> getItemEnchant(Item item)
- int getEnchantID(Enchantment enchantment)
- int getEnchantLevel(Enchantment enchantment)

EventLoader

- PlayerJumpEvent
- PlayerToggleGlideEvent
- PlayerToggleSwimEvent
- PlayerToggleSneakEvent
- PlayerToggleSprintEvent

