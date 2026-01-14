# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

BlocklyNukkit Loader 是一个 Nukkit Minecraft 基岩版服务器插件，为非 Java 开发者提供多语言脚本支持。支持 JavaScript、Lua、Python、PHP、Node.js 和 WebAssembly 编写服务器插件。

## 构建命令

```bash
# 完整构建
mvn clean package

# 快速构建（跳过测试）
mvn package -DskipTests

# 仅编译
mvn compile
```

构建输出：`target/BlocklyNukkit-1.0.0.jar`

## 项目结构

```
src/main/java/com/blocklynukkit/loader/
├── Loader.java                    # 插件主入口，继承 PluginBase
├── EventLoader.java               # 核心事件分发（100+ Nukkit 事件）
├── CompatibleEventLoader.java     # 兼容性事件加载器
│
├── scriptloader/                  # 脚本加载系统
│   ├── ExtendScriptLoader.java    # 脚本加载基类
│   ├── ScriptLoader.java          # 通用脚本加载器
│   └── scriptengines/             # 各语言脚本引擎
│       ├── JavaScriptLoader.java  # JavaScript (Nashorn/GraalVM)
│       ├── LuaScriptEngine.java   # Lua (luaj-jse)
│       ├── PythonScriptEngine.java # Python (Jython)
│       ├── PHPScriptEngine.java   # PHP (Quercus)
│       ├── NodeScriptEngine.java  # Node.js (外部进程)
│       ├── WasmScriptEngine.java  # WebAssembly (wasmtime)
│       └── BNPackageLoader.java   # BN 打包格式
│
├── script/                        # 脚本 API 和工具
│   ├── FunctionManager.java       # 函数调用核心管理器
│   ├── BlockItemManager.java      # 自定义方块/物品管理
│   ├── WindowManager.java         # GUI 窗口管理
│   ├── Babel.java                 # ES6→ES5 转换器
│   └── ...
│
├── other/                         # 扩展功能模块
│   ├── AddonsAPI/                 # 附加内容 API
│   │   ├── bnnbt/                 # NBT 标签系统
│   │   ├── BNResourcePack.java    # 资源包处理
│   │   └── ...
│   ├── ai/                        # 实体 AI 和寻路
│   ├── cmd/                       # 内置命令
│   ├── Entities/                  # 自定义实体
│   │   ├── BNNPC.java             # NPC 实体
│   │   ├── FloatingText.java      # 浮动文字
│   │   └── FloatingItem.java      # 浮动物品
│   ├── net/                       # 网络功能
│   │   ├── HttpServer.java        # HTTP 服务器
│   │   ├── WsServer.java          # WebSocket 服务器
│   │   └── SMTPSender.java        # SMTP 邮件发送
│   ├── chemistry/                 # 化学元素系统
│   ├── generator/                 # 自定义地形生成器
│   │   ├── VoidGenerator.java     # 虚空世界
│   │   ├── SkyLand.java           # 空岛世界
│   │   └── OceanGenerator.java    # 海洋世界
│   └── packets/                   # 自定义数据包
│
└── utils/                         # 工具类
    ├── GZIPUtils.java             # GZIP 压缩
    └── MyFileHandler.java         # 文件处理
```

## 核心架构

### 入口和生命周期

- **Loader.java** - 插件主类，继承 `PluginBase`
  - 管理全局静态资源（脚本引擎缓存、玩家数据、自定义物品等）
  - 初始化依赖插件检查和自动下载
  - 版本兼容性检查（支持 Nukkit 协议 419+ / 1.16.100+）

### 事件系统

- **EventLoader.java** - 事件分发核心，监听 100+ 个 Nukkit 原生事件
- **CompatibleEventLoader.java** - 兼容性事件加载器
- 所有事件使用 `@EventHandler(priority = EventPriority.HIGHEST)` 捕获后转发给脚本系统

### 脚本加载器系统

```
ExtendScriptLoader (基类)
    ├── JavaScriptLoader   # Nashorn/GraalVM
    ├── LuaScriptEngine    # luaj-jse
    ├── PythonScriptEngine # Jython (需 PyBN 插件)
    ├── PHPScriptEngine    # Quercus (需 PHPBN 插件)
    ├── NodeScriptEngine   # 外部 Node.js 进程
    ├── WasmScriptEngine   # wasmtime-java
    └── BNPackageLoader    # BN 打包格式
```

**JS 脚本加载流程**：读取文件 → Pragma 指令检查 → ES6 转 ES5（Babel）→ Javassist 字节码注入 → 缓存到 scriptEngineMap

## 关键全局资源 (Loader.java)

```java
public static Map<String, ScriptEngine> engineMap;        // 脚本引擎缓存
public static Map<String, HashSet<String>> privatecalls;  // 私有方法调用
public static Map<String, CtClass> bnClasses;             // Javassist 字节码类
public static Int2ObjectOpenHashMap<HttpServer> httpServers; // HTTP 服务器
public static List<WsServer> wsServerList;                // WebSocket 服务器
```

## 依赖配置详解

### Maven 中央仓库依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| `org.java-websocket:Java-WebSocket` | 1.5.2 | WebSocket 服务器实现 |
| `org.javassist:javassist` | 3.28.0-GA | 运行时字节码操作，用于脚本类生成 |
| `mysql:mysql-connector-java` | 5.1.46 | MySQL 数据库 JDBC 驱动 |
| `com.eclipsesource.j2v8:j2v8_win32_x86_64` | 4.5.0 | V8 JavaScript 引擎 (Windows) |
| `com.eclipsesource.j2v8:j2v8_linux_x86_64` | 4.5.0 | V8 JavaScript 引擎 (Linux) |
| `org.luaj:luaj-jse` | 3.0.1 | Lua 脚本引擎 |
| `com.carrotsearch:java-sizeof` | 0.0.5 | 对象内存大小计算 |
| `com.sun.xml.bind:jaxb-impl` | 2.2.11 | XML 绑定实现 |
| `org.jetbrains:annotations` | 13.0 | @NotNull/@Nullable 注解 |
| `com.github.oshi:oshi-core` | 5.5.0 | 系统硬件信息读取 |
| `org.openjdk.nashorn:nashorn-core` | 15.4 | JavaScript 引擎 (Java 17 独立模块) |

### Provided 作用域依赖（运行时由服务端提供）

| 依赖 | 版本 | 用途 |
|------|------|------|
| `cn.nukkit:Nukkit` | MOT-SNAPSHOT | Nukkit 服务端核心 API |
| `me.onebone:economyapi` | 2.0.5 | 经济系统 API |

### System 作用域依赖（本地 JAR 文件）

位于 `nukkit/plugins/` 目录：

| 依赖 | 文件 | 用途 |
|------|------|------|
| PlaceholderAPI | PlaceholderAPI-1.3-SNAPSHOT.jar | 占位符 API |
| ScoreboardAPI | ScoreboardAPI-1.3-SNAPSHOT.jar | 计分板 API |
| FakeInventories | fakeinventories-1.0.3.jar | 虚拟背包 API |
| c3p0 | c3p0-0.9.5.5.jar | 数据库连接池 |
| mchange-commons-java | mchange-commons-java-0.2.19.jar | c3p0 依赖库 |
| wasmtime-java | wasmtime-java-0.3.0-fixed.jar | WebAssembly 运行时 |
| FlatLaf | flatlaf-0.41.jar | 现代 Swing UI 主题 |
| GameAPI | GameAPI-1.0-SNAPSHOT.jar | 游戏 API 扩展 |
| Jython | pythonForBN.jar | Python 脚本引擎 |
| Quercus | quercus.jar | PHP 脚本引擎 |

### 可选运行时依赖

- `graaljs.jar` - GraalVM JavaScript 引擎（高性能，30MB+）

## 版本兼容性

- **Java**: 17+（需要 nashorn-core 独立模块）
- **Nukkit 协议**: 419+（1.16.100+）
- **已测试服务端**: 1.16.100 ~ 1.19.20+
- **Maven**: 3.6+

## 构建配置说明

### Java 17 模块系统配置

`.mvn/jvm.config` 包含必要的模块访问参数：

```
--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
...
```

### 编译器配置

```xml
<configuration>
    <source>17</source>
    <target>17</target>
    <compilerArgs>
        <compilerArg>-parameters</compilerArg>  <!-- 保留方法参数名 -->
        <compilerArg>-proc:none</compilerArg>   <!-- 禁用注解处理器 -->
    </compilerArgs>
</configuration>
```

## 注意事项

- 修改事件处理时注意 EventLoader 中的优先级设置
- 脚本引擎使用缓存机制，修改后需要 `/bnreload` 重载
- GraalVM 相关 JAR 较大（30MB+），位于 `nukkit/plugins/` 目录
- ES6 转换使用内置的 babel.min.js（`src/main/resources/`）
- Java 17 模块系统需要额外的 `--add-opens` 参数
- Nashorn 从 JDK 内置迁移到独立模块 `org.openjdk.nashorn`

## 内置命令

| 命令 | 功能 |
|------|------|
| `/bnreload` | 重载所有脚本 |
| `/bnpluginslist` | 列出已加载的脚本插件 |
| `/bnhelp` | 显示帮助信息 |

## 资源文件

`src/main/resources/` 包含：
- `babel.min.js` - ES6→ES5 转换器
- `plugin.yml` - 插件描述文件
- 其他配置文件
