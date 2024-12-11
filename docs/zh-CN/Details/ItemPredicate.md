# 物品谓词

第三人称下，模组会根据玩家手持的物品和使用情况判断是否进入瞄准模式。

约定：

-   **物品谓词（ItemPredicate）** 是指 `net.minecraft.advancements.critereon.ItemPredicate` 对象，它代表一种规则，可以判断一个物品栈（ItemStack）是否符合其规则
-   **[物品模式（ItemPattern）](#item-pattern)** 是人工编辑的字符串，可以被解析为物品谓词  
	格式： `[#][namespace:]<id>[nbt]` 或 `<nbt>`

在游戏运行时，模组会维护 3 个物品谓词集合：

-   **手持时瞄准** 当玩家手持的任意物品符合其中任意物品谓词时，进入瞄准模式
-   **使用时瞄准** 当玩家正在使用的物品符合其中任意物品谓词时，进入瞄准模式
-   **使用时暂时进入第一人称** 当玩家正在使用的物品符合其中任意模式时，暂时进入第一人称

集合中的物品谓词有 2 种来源：

-   **模组配置** 可以在模组的配置文件或游戏内配置界面中添加物品模式，模组会自动将它们解析为物品谓词。
-   **[资源包](./ResourcePack.md)** 包括此模组内置的资源包、其他模组内置的资源包，以及玩家手动安装的其他资源包。

## 物品模式 {#item-pattern}

物品模式是人工编辑的字符串，可以被解析为物品谓词，用来判断一个物品栈（ItemStack）是否符合规则

格式：`[#][namespace:]<id>[nbt]` 或 `<nbt>`

| 格式                     | 含义                              | 示例                                                    |
| ------------------------ | --------------------------------- | ------------------------------------------------------- |
| `[namespace:]<id>`       | 指定物品 ID                       | `egg`, `minecraft:egg`                                  |
| `#[namespace:]<id>`      | 拥有特定标签                      | `#minecraft:boat`, `#boat`                              |
| `[namespace:]<id><nbt>`  | 指定物品ID，且 NBT 符合特定结构   | `crossbow{Charged:1b}` `minecraft:crossbow{Charged:1b}` |
| `#[namespace:]<id><nbt>` | 拥有特定标签，且 NBT 符合特定结构 | `#boats{Charged:1b}` `#minecraft:boats{Charged:1b}`     |
| `<nbt>`                  | NBT 符合指定结构                  | `{Charged:1b}`                                          |

物品 ID 中的命名空间可以省略，在模组配置中，命名空间的缺省值是 `minecraft`，在资源包中，默认命名空间就是资源文件所在的命名空间。

## 示例

| 物品模式               | 含义                                            |
| ---------------------- | ----------------------------------------------- |
| `minecraft:egg`        | 鸡蛋                                            |
| `egg`                  | 鸡蛋                                            |
| `crossbow`             | 弩（无论是否已装填）                            |
| `crossbow{Charged:1b}` | 已装填的弩                                      |
| `{Charged:1b}`         | NBT 标签里有 Charged 属性，且值为 1b 的任意物品 |
| `#boats`               | 所有船，包括竹筏                                |