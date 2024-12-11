# Item Predicate

In third-person perspective, this mod determines whether to enter aim mode based on the item held by the player and using state.

Conventions:

-   **Item predicate** refers to `net.minecraft.advancements.criteria.ItemPredicate` object, which represents a kind of rules that can determine if an ItemStack matches its criteria.
-   **[Item pattern](#item-pattern)** is a human-edited string that can be parsed into an Item-Predicate.  
	Format: `[#][namespace:]<id>[nbt]` or `<nbt>`

During gameplay, this mod maintains 3 sets of item predicates:

-   **Aim While Holding**: When any item the player is holding matches any item predicate in this set, aim mode is activated.
-   **Aim While Using**: When the item the player is using matches any item predicate in this set, aim mode is activated.
-   **Temporarily Enter First-Person While Using**: When the item the player is using matches any item predicate in this set, it temporarily switches to first person perspective.

The item predicates in these sets come from 2 sources:

* **Mod Configuration**: Item-patterns can be added in the mod's configuration file or through the configuration interface in game, and the mod will automatically parse them into item predicates.
* **[Resource Packs](./ResourcePack.md)**: This includes mod's built-in resource pack, and any additional resource packs manually added by user.

## Item Pattern {#item-pattern}

An Item-Pattern is a human-edited string that can be parsed into an `ItemPredicate` to determine if an `ItemStack` matches the specified criteria.

Format: `[#][namespace:]<id>[nbt]` or `<nbt>`

| Format                   | Meaning                                           | Examples                                                 |
| ------------------------ | ------------------------------------------------- | -------------------------------------------------------- |
| `[namespace:]<id>`       | Specifies the item ID                             | `egg`, `minecraft:egg`                                   |
| `#[namespace:]<id>`      | Refers to items with a specific tag               | `#minecraft:boat`, `#boat`                               |
| `[namespace:]<id><nbt>`  | Specifies the item ID and requires a specific NBT | `crossbow{Charged:1b}`, `minecraft:crossbow{Charged:1b}` |
| `#[namespace:]<id><nbt>` | Refers to items with a specific tag and NBT       | `#boats{Charged:1b}`, `#minecraft:boats{Charged:1b}`     |
| `<nbt>`                  | Requires a specific NBT structure                 | `{Charged:1b}`                                           |

The namespace in the item ID can be omitted. In mod configurations, the default namespace is `minecraft`. In resource packs, the default namespace is the one where the resource file is located.

## Examples

| Item Pattern           | Meaning                                        |
| ---------------------- | ---------------------------------------------- |
| `minecraft:egg`        | An egg                                         |
| `egg`                  | An egg                                         |
| `crossbow`             | A crossbow (whether charged or not)            |
| `crossbow{Charged:1b}` | A charged crossbow                             |
| `{Charged:1b}`         | Any item with an NBT tag `Charged` set to `1b` |
| `#boats`               | All boats, including rafts                     |
