package com.github.leawind.util.math.decisionmap;


import java.util.function.BooleanSupplier;

public class DecisionFactor {
	private final String          name;
	private final BooleanSupplier supplier;

	int index;

	private boolean value = false;

	/**
	 * @param name     因素的名称
	 * @param supplier 用于计算因素的值的函数
	 */
	public DecisionFactor (String name, BooleanSupplier supplier) {
		this.name     = name;
		this.supplier = supplier;
	}

	public String getName () {
		return name;
	}

	/**
	 * 重新计算因素的值
	 */
	public boolean update () {
		return value = supplier.getAsBoolean();
	}

	public boolean get () {
		return value;
	}

	public int getInt () {
		return value ? 1: 0;
	}
}
