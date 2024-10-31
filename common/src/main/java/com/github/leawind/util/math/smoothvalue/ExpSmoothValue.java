package com.github.leawind.util.math.smoothvalue;


import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ExpSmoothValue<T> implements ISmoothValue<T> {
	/**
	 * 平滑系数，越大越平滑
	 */
	public @NotNull    T smoothFactor;
	/**
	 * 平滑系数乘数，默认应为1
	 */
	public @NotNull    T smoothFactorWeight;// factor ^ weight
	/**
	 * 目标值
	 */
	public @NotNull    T target;
	/**
	 * 当前平滑的值
	 */
	protected @NotNull T value;
	/**
	 * 上次更新时的目标值
	 */
	protected @NotNull T lastValue;

	protected ExpSmoothValue (@NotNull T smoothFactor, @NotNull T smoothFactorWeight, @NotNull T value, @NotNull T lastValue, @NotNull T target) {
		this.smoothFactor       = smoothFactor;
		this.smoothFactorWeight = smoothFactorWeight;

		this.value     = value;
		this.lastValue = lastValue;
		this.target    = target;
	}

	public T getRawValue () {
		return value;
	}

	public T getRawTarget () {
		return target;
	}

	public T getRawLastValue () {
		return lastValue;
	}

	final public void update (double period) {
		saveLastValue();
		updateWithOutSavingLastValue(period);
	}

	@Override
	final public @NotNull T get () {
		return value;
	}

	@Override
	abstract public @NotNull T get (double t);

	@Override
	final public @NotNull T getLast () {
		return lastValue;
	}

	/**
	 * 记录下更新前的平滑值（旧值），存储在 lastValue 中。
	 * <p>
	 * 应当在 update 方法中写入新值前调用
	 */
	final protected void saveLastValue () {
		lastValue = value;
	}

	abstract protected void updateWithOutSavingLastValue (double period);

	abstract public void setValue (@NotNull T value);

	/**
	 * 同时设置目标值和当前平滑值
	 * <p>
	 * 不改变旧值
	 */
	abstract public void set (@NotNull T value);

	abstract public void setSmoothFactor (@NotNull T smoothFactor);

	abstract void setSmoothFactor (double smoothFactor);

	/**
	 * 根据以下规则设置平滑系数：
	 * <p>
	 * 每隔 time 秒，value 变为原来的 multiplier 倍。
	 */
	abstract void setMT (@NotNull T multiplier, @NotNull T time);

	/**
	 * 根据半衰期设置平滑系数
	 */
	abstract void setHalflife (@NotNull T halflife);

	/**
	 * 根据半衰期设置平滑系数
	 */
	abstract void setHalflife (double halflife);
}
