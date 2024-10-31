package com.github.leawind.thirdperson.config;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.cameraoffset.CameraOffsetScheme;
import com.github.leawind.thirdperson.resources.ItemPredicateManager;
import com.github.leawind.util.ItemPredicateUtil;
import com.github.leawind.util.math.monolist.MonoList;
import com.github.leawind.util.math.monolist.StaticMonoList;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * AbstractConfig 中包含了用户可以直接修改的配置项及默认值。
 * <p>
 * 但要在模组中使用这些配置项，还需要进行进一步的处理。
 */
public class Config extends AbstractConfig {
	public static final @NotNull Config DEFAULTS = new Config();

	private final @NotNull CameraOffsetScheme cameraOffsetScheme = new CameraOffsetScheme(this);

	private final @NotNull Set<ItemPredicate> holdToAimItemPredicates        = new HashSet<>();
	private final @NotNull Set<ItemPredicate> useToAimItemPredicates         = new HashSet<>();
	private final @NotNull Set<ItemPredicate> useToFirstPersonItemPredicates = new HashSet<>();

	private @Nullable MonoList distanceMonoList;

	public Config () {
		update();
	}

	/**
	 * 在配置项发生变化时更新
	 */
	public void update () {
		updateDistancesMonoList();
		updateItemPredicates();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateDistancesMonoList () {
		ThirdPerson.LOGGER.debug("Updating distances mono list");
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	/**
	 * @see ItemPredicateManager#apply
	 */
	public void updateItemPredicates () {
		holdToAimItemPredicates.clear();
		useToAimItemPredicates.clear();
		useToFirstPersonItemPredicates.clear();

		int count;
		count = ItemPredicateUtil.addToSet("minecraft", holdToAimItemPredicates, hold_to_aim_item_patterns);
		if (count > 0) {
			ThirdPerson.LOGGER.info("Loaded {} hold_to_aim item patterns from configuration", count);
		}
		count = ItemPredicateUtil.addToSet("minecraft", useToAimItemPredicates, use_to_aim_item_patterns);
		if (count > 0) {
			ThirdPerson.LOGGER.info("Loaded {}  use_to_aim item patterns from configuration", count);
		}
		count = ItemPredicateUtil.addToSet("minecraft", useToFirstPersonItemPredicates, use_to_first_person_patterns);
		if (count > 0) {
			ThirdPerson.LOGGER.info("Loaded {} use_to_first_person item patterns from configuration", count);
		}
	}

	public @NotNull Set<ItemPredicate> getHoldToAimItemPredicates () {
		return holdToAimItemPredicates;
	}

	public @NotNull Set<ItemPredicate> getUseToAimItemPredicates () {
		return useToAimItemPredicates;
	}

	public @NotNull Set<ItemPredicate> getUseToFirstPersonItemPredicates () {
		return useToFirstPersonItemPredicates;
	}

	public @NotNull CameraOffsetScheme getCameraOffsetScheme () {
		return cameraOffsetScheme;
	}

	public @NotNull MonoList getDistanceMonoList () {
		return Objects.requireNonNull(distanceMonoList);
	}
}
