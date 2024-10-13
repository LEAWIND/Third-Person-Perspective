# 📖 详细

## 物品谓词

第三人称下，模组会根据玩家手持的物品和使用情况判断是否进入瞄准模式。

约定：

-   **物品谓词（ItemPredicate）** 是指 `net.minecraft.advancements.critereon.ItemPredicate` 对象，它代表一种规则，可以判断一个物品栈（ItemStack）是否符合其规则
-   **[物品模式（ItemPattern）](./ItemPattern)** 是人工编辑的字符串，可以被解析为物品谓词  
    格式： `[#][namespace:]<id>[nbt]` 或 `<nbt>`

在游戏运行时，模组会维护 3 个物品谓词集合：

-   **手持时瞄准** 当玩家手持的任意物品符合其中任意物品谓词时，进入瞄准模式
-   **使用时瞄准** 当玩家正在使用的物品符合其中任意物品谓词时，进入瞄准模式
-   **使用时暂时进入第一人称** 当玩家正在使用的物品符合其中任意模式时，暂时进入第一人称

集合中的物品谓词有 2 种来源：

-   **模组配置** 可以在模组的配置文件或游戏内配置界面中添加物品模式，模组会自动将它们解析为物品谓词。
-   **[资源包](./ResourcePack.md)** 包括此模组内置的资源包、其他模组内置的资源包，以及玩家手动安装的其他资源包。