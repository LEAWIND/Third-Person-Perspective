package com.github.leawind.util;


import com.github.leawind.thirdperson.ThirdPerson;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.clothconfig2.impl.builders.StringListBuilder;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * <pre>
 * {@code
 * minecraft:bow
 * minecraft:crossbow{Charged:1b}
 * #minecraft:boats
 * #minecraft:banners{x:-32}
 * bow
 * crossbow{Charged:1b}
 * #boats
 * #banners{x:-32}
 * }
 * </pre>
 *
 * @see ItemParser
 * @see ItemPredicate
 * @see NbtPredicate
 */
public final class ItemPredicateUtil {
	private static final Pattern RGX_NBT     = Pattern.compile("^(\\{.*})$");
	/**
	 * "#&lt;namespace&gt;:&lt;id&gt;{&lt;nbt&gt;}"
	 */
	private static final Pattern RGX_TAG_NBT = Pattern.compile("^#([a-z0-9.:_]+)(\\{.*})?$");
	/**
	 * "&lt;namespace&gt;:&lt;id&gt;{&lt;nbt&gt;}"
	 */
	private static final Pattern RGX_KEY_NBT = Pattern.compile("^([a-z0-9.:_]+)(\\{.*})?$");

	/**
	 * @see StringListBuilder#setCellErrorSupplier(Function)
	 */
	public static @NotNull Optional<Component> supplyError (String pattern) {
		try {
			parse("minecraft", pattern);
			return Optional.empty();
		} catch (IllegalArgumentException e) {
			return Optional.of(Component.literal(e.getMessage()));
		} catch (IllegalStateException e) {
			return Optional.empty();
		}
	}

	@SafeVarargs
	public static boolean anyMatches (@NotNull ItemStack itemStack, Iterable<ItemPredicate> @NotNull ... predicatesList) {
		if (itemStack.isEmpty()) {
			return false;
		}
		for (var predicates: predicatesList) {
			for (var predicate: predicates) {
				if (predicate.matches(itemStack)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int addToSet (@NotNull String defaultNamespace, @NotNull Set<ItemPredicate> predicates, @Nullable Iterable<String> patterns) {
		int count = 0;
		if (patterns != null) {
			for (var pattern: patterns) {
				try {
					predicates.add(parse(defaultNamespace, pattern));
					count++;
				} catch (IllegalArgumentException e) {
					ThirdPerson.LOGGER.error("Skip invalid item pattern: {}, because {}", pattern, e.getMessage());
				} catch (IllegalStateException e) {
					ThirdPerson.LOGGER.warn("Skip invalid item pattern: {}, because {}", pattern, e.getMessage());
				}
			}
		}
		return count;
	}

	public static ItemPredicate parse (String pattern) throws IllegalArgumentException, IllegalStateException {
		return parse("minecraft", pattern);
	}

	/**
	 * @param defaultNs 默认命名空间
	 * @param pattern   源字符串
	 * @throws IllegalArgumentException 格式不正确或存在语法错误
	 * @throws IllegalStateException    物品ID不存在或为 minecraft:air
	 */
	public static ItemPredicate parse (String defaultNs, String pattern) throws IllegalArgumentException, IllegalStateException {
		if (pattern.isEmpty()) {
			throw new IllegalArgumentException("Empty item pattern");
		} else if (pattern.startsWith("{")) {
			return parseNbtPredicate(defaultNs, pattern);
		} else if (pattern.startsWith("#")) {
			return parseTagPredicate(defaultNs, pattern);
		} else {
			return parseKeyPredicate(defaultNs, pattern);
		}
	}

	private static ItemPredicate parseNbtPredicate (String defaultNs, String pattern) throws IllegalArgumentException {
		if (!RGX_NBT.matcher(pattern).matches()) {
			throw new IllegalArgumentException(String.format("Invalid NBT: %s", pattern));
		}
		try {
			return of(defaultNs, pattern, null, null);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("Invalid NBT: %s, %s", pattern, e.getMessage()));
		}
	}

	private static ItemPredicate parseTagPredicate (String defaultNs, String pattern) throws IllegalArgumentException {
		var m = RGX_TAG_NBT.matcher(pattern);
		if (m.matches()) {
			return of(defaultNs, m.group(2), m.group(1), null);
		}
		throw new IllegalArgumentException(String.format("Invalid item tag: %s", pattern));
	}

	private static ItemPredicate parseKeyPredicate (String defaultNs, String pattern) throws IllegalArgumentException, IllegalStateException {
		var m = RGX_KEY_NBT.matcher(pattern);
		if (m.matches()) {
			return of(defaultNs, m.group(2), null, m.group(1));
		} else {
			throw new IllegalArgumentException(String.format("Invalid item pattern: %s", pattern));
		}
	}

	/**
	 * @param defaultNs      默认命名空间
	 * @param nbtPattern     NBT模板，null 表示任意，例如 "{Charged:1b}"
	 * @param tagKeyPattern  物品标签，例如 "minecraft:boats"
	 * @param itemKeyPattern 物品键，例如 "minecraft:bow"。null表示任意，空字符串表示不匹配任何物品
	 * @return {@link ItemPredicate}
	 * @throws IllegalArgumentException 格式错误
	 * @throws IllegalStateException    物品不存在或为 minecraft:air
	 */
	private static ItemPredicate of (String defaultNs, @Nullable String nbtPattern, @Nullable String tagKeyPattern, @Nullable String itemKeyPattern) throws IllegalArgumentException, IllegalStateException {
		NbtPredicate nbt;
		TagKey<Item> tagKey = null;
		Set<Item>    items;
		try {
			nbt = nbtPattern == null ? NbtPredicate.ANY: new NbtPredicate(TagParser.parseTag(nbtPattern));
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		if (tagKeyPattern != null) {
			tagKey = TagKey.create(Registries.ITEM, parseResourceLocation(defaultNs, tagKeyPattern));
		}
		if (itemKeyPattern == null) {
			items = null;
		} else if (itemKeyPattern.isEmpty()) {
			items = ImmutableSet.of();
		} else {
			var resourceLocation = parseResourceLocation(defaultNs, itemKeyPattern);
			var item             = BuiltInRegistries.ITEM.get(resourceLocation);
			if (item == Items.AIR) {
				throw new IllegalStateException(String.format("Item %s does not exist or it's minecraft:air", resourceLocation));
			}
			items = ImmutableSet.of(item);
		}
		return new ItemPredicate(tagKey, items, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, nbt);
	}

	private static ResourceLocation parseResourceLocation (String defaultNamespace, String pattern) throws IllegalArgumentException {
		try {
			return pattern.indexOf(':') < 0 ? new ResourceLocation(defaultNamespace, pattern): new ResourceLocation(pattern);
		} catch (ResourceLocationException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
