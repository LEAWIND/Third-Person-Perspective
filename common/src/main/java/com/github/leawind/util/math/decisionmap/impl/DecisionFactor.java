package com.github.leawind.util.math.decisionmap.impl;


import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class DecisionFactor {
	private final @NotNull BooleanSupplier getter;

	protected        int     index;
	private          boolean value = false;
	private @NotNull String  name  = "unnamed";

	public DecisionFactor (@NotNull BooleanSupplier getter) {
		this.getter = getter;
	}

	/**
	 * 重新计算因素的值
	 */
	@org.jetbrains.annotations.Contract("-> this")
	public @NotNull DecisionFactor update () {
		value = getter.getAsBoolean();
		return this;
	}

	/**
	 * 获取上次的计算结果
	 */
	public boolean get () {
		return value;
	}

	public @NotNull String getName () {
		return name;
	}

	public void setName (@NotNull String name) {
		this.name = name;
	}

	/**
	 * 设置索引
	 */
	public void setIndex (int index) {
		this.index = index;
	}

	/**
	 * 在所有因素中的索引
	 */
	public int index () {
		return index;
	}

	/**
	 * 掩码，即 1<<index
	 */
	public int mask () {
		return 1 << index();
	}
}
