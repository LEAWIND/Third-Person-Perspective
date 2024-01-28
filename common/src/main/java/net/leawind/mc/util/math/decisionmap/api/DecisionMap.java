package net.leawind.mc.util.math.decisionmap.api;


import net.leawind.mc.util.math.decisionmap.impl.DecisionMapImpl;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 决策表
 */
@SuppressWarnings("unused")
public interface DecisionMap<T> {
	int MAX_FACTOR_COUNT = 32;

	static <T> DecisionMap<T> of (Class<?> clazz) {
		return new DecisionMapImpl<>(clazz);
	}

	static int toFlagBits (boolean[] flagList) {
		int flagBits = 0;
		for (int i = 0; i < flagList.length; i++) {
			if (flagList[i]) {
				flagBits |= 1 << i;
			}
		}
		return flagBits;
	}

	/**
	 * 更新因素
	 */
	DecisionMap<T> updateFactors ();

	/**
	 * 不更新因素，直接做出决策
	 */
	T make ();

	Supplier<T> getStrategy (int flagBits);

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
	DecisionMap<T> build ();

	/**
	 * 是否已经构建
	 */
	boolean isBuilt ();

	DecisionMap<T> addRule (Function<boolean[], Supplier<T>> func);

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
	DecisionMap<T> addRule (BiFunction<Integer, boolean[], Supplier<T>> func);

	/**
	 * 添加规则
	 *
	 * @param strategy 决策方法
	 * @return 原对象
	 */
	DecisionMap<T> addRule (int flagBits, Supplier<T> strategy);

	/**
	 * 添加规则
	 *
	 * @param flagBits 因素列表
	 * @param mask     掩码
	 * @param strategy 决策方法
	 * @return 原对象
	 */
	DecisionMap<T> addRule (int flagBits, int mask, Supplier<T> strategy);
}