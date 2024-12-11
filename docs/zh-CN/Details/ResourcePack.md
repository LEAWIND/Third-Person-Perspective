# 资源包

## 物品模式

本模组内置资源包中仅包含适用于原版 MC 的[物品模式](./ItemPredicate)。

### 手持时瞄准

文件路径：`assets/<命名空间>/item_patterns/hold_to_aim/<任意名称>.json`

当玩家手持的任意物品符合其中任意模式时，进入瞄准模式。

以下是本模组内置资源包中该文件的内容

:::details `assets/minecraft/item_patterns/hold_to_aim/vanilla.json`

```json
[
	"minecraft:bucket",
	"minecraft:water_bucket",
	"minecraft:lava_bucket",
	"minecraft:cod_bucket",
	"minecraft:salmon_bucket",
	"minecraft:tropical_fish_bucket",
	"minecraft:pufferfish_bucket",
	"minecraft:axolotl_bucket",
	"minecraft:tadpole_bucket",
	"minecraft:lily_pad",
	"minecraft:frogspawn",
	"minecraft:crossbow{Charged:1b}",
	"minecraft:ender_pearl",
	"minecraft:snowball",
	"minecraft:egg",
	"minecraft:splash_potion",
	"minecraft:lingering_potion",
	"minecraft:experience_bottle"
]
```

:::

### 使用时瞄准

文件路径：`assets/<命名空间>/item_patterns/use_to_aim/<任意名称>.json`

当玩家正在使用的物品符合其中任意模式时，进入瞄准模式

以下是本模组内置资源包中该文件的内容

:::details `assets/minecraft/item_patterns/use_to_aim/vanilla.json`

```json
["minecraft:bow", "minecraft:trident"]
```

:::

### 使用时暂时进入第一人称

文件路径：`assets/<命名空间>/item_patterns/use_to_first_person/<任意名称>.json`

当玩家正在使用的物品符合其中任意模式时，暂时进入第一人称

以下是本模组内置资源包中该文件的内容

:::details `assets/minecraft/item_patterns/use_to_first_person/vanilla.json`

```json
["minecraft:spyglass"]
```

:::
