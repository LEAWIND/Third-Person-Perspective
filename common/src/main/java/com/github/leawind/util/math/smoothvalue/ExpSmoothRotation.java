package com.github.leawind.util.math.smoothvalue;


import com.github.leawind.util.math.LMath;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

/**
 * 平滑旋转
 * <p>
 * 实体朝向通常用二维向量来描述，但要实现其朝向的平滑变化， 不能直接使用 {@link ExpSmoothVector2d}
 * <p>
 * 因为其存在一些约束条件和特殊性质
 * <p>
 * <li>y：偏航角，范围：[0， 360]</li>
 * <li>x：俯仰角，范围：[-90， 90]</li>
 */
@SuppressWarnings("unused")
public class ExpSmoothRotation {
	private final ExpRotSmoothDouble y;
	private final ExpSmoothDouble    x;

	private ExpSmoothRotation () {
		y = new ExpRotSmoothDouble(360);
		x = new ExpSmoothDouble();
	}

	public void setHalflife (double halflife) {
		y.setHalflife(halflife);
		x.setHalflife(halflife);
	}

	public void setMT (double multiplier, double time) {
		y.setMT(multiplier, time);
		x.setMT(multiplier, time);
	}

	public void setSmoothFactor (double smoothFactor) {
		y.setSmoothFactor(smoothFactor);
		x.setSmoothFactor(smoothFactor);
	}

	public void setTarget (@NotNull Vector2d rot) {
		y.setTarget(rot.y);
		x.setTarget(LMath.clamp(rot.x, -90, 90));
	}

	/**
	 * 记录旧的平滑值，然后更新平滑值
	 * <p>
	 * 对于 {@link ExpSmoothValue}，理论上任何时候都可以更新，
	 * <p>
	 * 但考虑到时间的精度有限，更新间隔不应过小。
	 *
	 * @param period 经过的时间（s）
	 */
	public void update (double period) {
		y.update(period);
		x.update(period);
	}

	/**
	 * 获取当前的平滑值
	 * <p>
	 * 如果使用 {@link ISmoothValue#update(double)} 进行更新的频率小于渲染频率，
	 * <p>
	 * 则不建议在渲染中直接采用此方法获取平滑值，因为这可能造成不平滑的效果。
	 * <p>
	 * 应当使用 {@link ISmoothValue#get(double)}
	 */
	public @NotNull Vector2d get () {
		return new Vector2d(x.get(), y.get());
	}

	/**
	 * 旧值与当前平滑值的线性插值
	 * <p>
	 * 每次更新时，都会记录下当时的平滑值作为旧值，然后再计算新的平滑值。
	 * <p>
	 * 当更新频率小于渲染频率时，此方法十分有效。
	 *
	 * @param t 自上次更新以来经过的时间占更新间隔的比例，用于线性插值。
	 */
	public @NotNull Vector2d get (double t) {
		return new Vector2d(x.get(t), y.get(t));
	}

	public void set (@NotNull Vector2d v) {
		y.set(v.y);
		x.set(LMath.clamp(v.x, -90, 90));
	}

	public void setSmoothFactorWeight (double weight) {
		y.setSmoothFactorWeight(weight);
		x.setSmoothFactorWeight(weight);
	}

	public void setValue (@NotNull Vector2d v) {
		y.setValue(v.y);
		x.setValue(LMath.clamp(v.x, -90, 90));
	}

	/**
	 * 获取上次更新前的平滑值（旧值）
	 */
	public @NotNull Vector2d getLast () {
		return new Vector2d(x.getLast(), y.getLast());
	}

	public static @NotNull ExpSmoothRotation createWithHalflife (double halflife) {
		var v = new ExpSmoothRotation();
		v.setHalflife(halflife);
		return v;
	}
}
