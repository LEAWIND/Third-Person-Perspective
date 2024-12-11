# Resource Packs

## Built-in Resource Pack

The built-in resource pack of this mod only includes [ItemPatterns](./ItemPredicate) that are applicable to vanilla MC.

### Aim While Holding

File Path: `assets/<namespace>/item_patterns/hold_to_aim/<any_name>.json`

When any item the player is holding matches any pattern in this file, aim mode is activated.

Here is the content of this file in the built-in resource pack of the mod:

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

### Aim While Using

File Path: `assets/<namespace>/item_patterns/use_to_aim/<any_name>.json`

When the item the player is using matches any pattern in this file, aim mode is activated.

Here is the content of this file in the built-in resource pack of the mod:

:::details `assets/minecraft/item_patterns/use_to_aim/vanilla.json`

```json
["minecraft:bow", "minecraft:trident"]
```

:::

### Temporarily Enter First-Person While Using

File Path: `assets/<namespace>/item_patterns/use_to_first_person/<any_name>.json`

When the item the player is using matches any pattern in this file, the view temporarily switches to first-person.

Here is the content of this file in the built-in resource pack of the mod:

:::details `assets/minecraft/item_patterns/use_to_first_person/vanilla.json`

```json
["minecraft:spyglass"]
```

:::
