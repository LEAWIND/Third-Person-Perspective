package com.github.leawind.util.math.decisionmap;


import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class DecisionMapBuilder<T> {
	private final Supplier<T> EMPTY_OPERATION = () -> null;

	private final List<@Nullable DecisionFactor> factors          = new ArrayList<>(DecisionMap.MAX_FACTOR_COUNT);
	private final List<@Nullable Rule<T>>        rules            = new LinkedList<>();
	private       Supplier<T>                    defaultOperation = EMPTY_OPERATION;

	private int lastFactorIndex = -1;

	DecisionMapBuilder () {
	}

	/**
	 * 清空所有规则并重置默认值
	 */
	public DecisionMapBuilder<T> clearRules () {
		rules.clear();
		defaultOperation = EMPTY_OPERATION;
		return this;
	}

	/**
	 * 根据名称获取因素
	 *
	 * @param name 名称
	 * @throws IllegalArgumentException 不存在给定名称的因素
	 */
	public DecisionFactor factor (String name) {
		for (var factor: factors) {
			if (factor != null && factor.getName().equals(name)) {
				return factor;
			}
		}
		throw new IllegalArgumentException("No such factor: " + name);
	}

	/**
	 * 不指定索引，则索引将是上一个定义的因素的索引+1
	 *
	 * @param name     名称
	 * @param supplier 计算因素的函数
	 */
	public DecisionMapBuilder<T> factor (String name, BooleanSupplier supplier) {
		return factor(lastFactorIndex + 1, name, supplier);
	}

	/**
	 * <p>
	 * 定义一个因素
	 *
	 * @param index    因素的索引
	 * @param name     名称
	 * @param supplier 计算因素的函数
	 * @throws IndexOutOfBoundsException 索引值超出范围
	 * @throws IllegalArgumentException  索引或名称重复
	 * @throws IllegalStateException     因素的数量已达到上限
	 */
	public DecisionMapBuilder<T> factor (int index, String name, BooleanSupplier supplier) {
		if (index < 0 || index >= DecisionMap.MAX_FACTOR_COUNT) {
			throw new IndexOutOfBoundsException(String.format("Index %d out of bounds [%d, %d)", index, 0, DecisionMap.MAX_FACTOR_COUNT));
		}
		for (var factor: factors) {
			if (factor != null) {
				if (factor.index == index) {
					throw new IllegalArgumentException("Duplicated factor index: " + index);
				}
				if (factor.getName().equals(name)) {
					throw new IllegalArgumentException("Duplicated factor name: " + name);
				}
			}
		}
		while (factors.size() <= index) {
			factors.add(null);
		}
		var factor = new DecisionFactor(name, supplier);
		factor.index = index;
		factors.set(index, factor);
		lastFactorIndex = index;
		return this;
	}

	public DecisionMapBuilder<T> clearFactors () {
		factors.clear();
		return this;
	}

	/**
	 * 定义默认操作
	 *
	 * @param operation null 表示什么也不做
	 */
	public DecisionMapBuilder<T> whenDefault (@Nullable Supplier<T> operation) {
		defaultOperation = Objects.requireNonNullElse(operation, EMPTY_OPERATION);
		return this;
	}

	/**
	 * 添加规则：当特定索引的因素等于指定值时
	 *
	 * @param name      因素的名称
	 * @param value     当索引等于何值时
	 * @param operation 操作
	 */
	public DecisionMapBuilder<T> when (String name, boolean value, Supplier<T> operation) {
		var index = factor(name).index;
		return when(1 << index, (value ? 1: 0) << index, operation);
	}

	/**
	 * 添加规则：当特定索引的因素等于指定值时
	 *
	 * @param index     因素的索引
	 * @param value     当索引等于何值时
	 * @param operation 操作
	 */
	public DecisionMapBuilder<T> when (int index, boolean value, Supplier<T> operation) {
		return when(1 << index, (value ? 1: 0) << index, operation);
	}

	/**
	 * 当所有因素都等于给定的值时
	 *
	 * @param flags     所有因素的值
	 * @param operation 操作
	 */
	public DecisionMapBuilder<T> whenAll (int flags, Supplier<T> operation) {
		return when(~0, flags, operation);
	}

	/**
	 * 添加规则：当指定的因素为指定的值的时候，执行特定的操作
	 *
	 * @param mask      掩码，表示在这个规则中要考虑哪些因素
	 * @param flags     因素的值，当被考虑的这些因素恰好为指定的这些值的时候，执行 operation。
	 *                  <p>
	 *                  注意：可能被之后定义的规则覆盖。
	 * @param operation 当满足上述因素时执行的操作
	 */
	public DecisionMapBuilder<T> when (int mask, int flags, Supplier<T> operation) {
		rules.add(new Rule<>(mask, flags, operation));
		return this;
	}

	/**
	 * 构建决策表
	 * <p>
	 * 对于每条规则，计算该规则适用的所有情况，将这些情况下的因素组合与相应的操作加入到决策表中
	 *
	 * @throws IllegalStateException 因素列表中存在null。因素的索引必须从0开始，必须连续
	 */
	public DecisionMap<T> build () {
		for (int i = 0; i < factors.size(); i++) {
			if (factors.get(i) == null) {
				throw new IllegalStateException(String.format("Factor[%d] has not been specified", i));
			}
		}

		var strategyMap = new HashMap<Integer, Supplier<T>>();
		// 有效位的掩码
		// 例如：若共有3个因素，则 filter = 0b111
		int filter = (int)((1L << factors.size()) - 1);

		for (var rule: rules) {
			if (rule != null) {
				rule.filter(filter);
				for (int flags = 0; flags <= filter; flags++) {
					if ((rule.mask & flags) == rule.flags) {
						strategyMap.put(flags, rule.operation);
					}
				}
			}
		}
		return new DecisionMap<>(factors, strategyMap, defaultOperation);
	}

	private static class Rule<T> {
		int mask;
		int flags;
		final Supplier<T> operation;

		Rule (int mask, int flags, Supplier<T> operation) {
			this.mask      = mask;
			this.flags     = flags;
			this.operation = operation;
		}

		void filter (int filter) {
			mask &= filter;
			flags &= mask;
		}
	}
}
