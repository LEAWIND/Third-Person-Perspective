package com.github.leawind.util.math.decisionmap.api;


import com.github.leawind.util.math.decisionmap.impl.DecisionMapImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 决策表
 * <p>
 * 根据特定布尔类型因素做出决策
 */
@SuppressWarnings("unused")
public interface DecisionMap<T> {
	int MAX_FACTOR_COUNT = 32;

	void reset ();

	/**
	 * 更新因素
	 */
	@Contract("-> this")
	DecisionMap<T> updateFactors ();

	/**
	 * 不更新因素，直接做出决策
	 */
	T make ();

	/**
	 * 获取因素对应的决策
	 *
	 * @param flagBits 因素位图
	 */
	@NotNull Optional<Supplier<T>> getStrategy (int flagBits);

	/**
	 * 更新因素并做出决策
	 */
	T remake ();

	/**
	 * 获取因素数量
	 */
	int getFactorCount ();

	/**
	 * 2^因素数量
	 * <p>
	 * 每个因素有2中可能性，返回所有可能性总数
	 */
	int getMapSize ();

	/**
	 * 构建
	 */
	@Contract("-> this")
	@NotNull DecisionMap<T> build ();

	/**
	 * 是否已经构建
	 */
	boolean isBuilt ();

	@Contract("_-> this")
	@NotNull DecisionMap<T> addRule (@NotNull Function<boolean[], Supplier<T>> func);

	/**
	 * 添加规则
	 * <p>
	 * 此方法会立即使之前在此对象上调用的所有{@link DecisionMap#addRule}方法失效
	 *
	 * @param func 根据输入的各个因素返回相应的决策函数
	 *             <p>
	 *             <li>Integer flag bits</li>
	 *             <li>boolean[] flag list</li>
	 * @return 原对象
	 */
	@Contract("_-> this")
	@NotNull DecisionMap<T> addRule (@NotNull BiFunction<Integer, boolean[], Supplier<T>> func);

	/**
	 * 添加规则
	 *
	 * @param strategy 决策方法
	 * @return 原对象
	 */
	@Contract("_,_ -> this")
	@NotNull DecisionMap<T> addRule (int flagBits, @NotNull Supplier<T> strategy);

	/**
	 * 添加规则
	 *
	 * @param flagBits 因素列表
	 * @param mask     掩码
	 * @param strategy 决策方法
	 * @return 原对象
	 */
	@Contract("_,_,_ -> this")
	@NotNull DecisionMap<T> addRule (int flagBits, int mask, @NotNull Supplier<T> strategy);

	@Contract("_ -> new")
	static <T> @NotNull DecisionMap<T> of (@NotNull Class<?> clazz) {
		return new DecisionMapImpl<>(clazz);
	}

	static int toFlagBits (boolean @NotNull [] flagList) {
		int flagBits = 0;
		for (int i = 0; i < flagList.length; i++) {
			if (flagList[i]) {
				flagBits |= 1 << i;
			}
		}
		return flagBits;
	}
}
