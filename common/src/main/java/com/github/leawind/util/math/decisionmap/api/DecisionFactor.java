package com.github.leawind.util.math.decisionmap.api;


import com.github.leawind.util.math.decisionmap.impl.DecisionFactorImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * 决策因素
 */
public interface DecisionFactor {
	/**
	 * 重新计算因素的值
	 */
	@Contract("-> this")
	DecisionFactor update ();

	/**
	 * 获取上次的计算结果
	 */
	boolean get ();

	@NotNull String getName ();

	void setName (@NotNull String name);

	/**
	 * 设置索引
	 */
	void setIndex (int index);

	/**
	 * 在所有因素中的索引
	 */
	int index ();

	/**
	 * 掩码，即 1<<index
	 */
	default int mask () {
		return 1 << index();
	}

	@Contract("_ -> new")
	static @NotNull DecisionFactor of (BooleanSupplier getter) {
		return new DecisionFactorImpl(getter);
	}
}
