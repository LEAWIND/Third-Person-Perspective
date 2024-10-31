package com.github.leawind.util.math.smoothvalue;


import com.github.leawind.util.math.LMath;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpSmoothDouble extends ExpSmoothValue<Double> {
	public ExpSmoothDouble () {
		super(0D, 1D, 0D, 0D, 0D);
	}

	public void setTarget (double target) {
		this.target = target;
	}

	@Override
	public void setTarget (@NotNull Double target) {
		this.target = target;
	}

	@Override
	public @NotNull Double get (double t) {
		return LMath.lerp(lastValue, value, t);
	}

	@Override
	protected void updateWithOutSavingLastValue (double period) {
		value = LMath.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
	}

	@Override
	public void setValue (@NotNull Double d) {
		value = d;
	}

	@Override
	public void set (@NotNull Double d) {
		value = target = d;
	}

	@Override
	public void setSmoothFactor (@NotNull Double smoothFactor) {
		this.smoothFactor = smoothFactor;
	}

	@Override
	public void setSmoothFactor (double smoothFactor) {
		this.smoothFactor = smoothFactor;
	}

	@Override
	public void setMT (@NotNull Double multiplier, @NotNull Double time) {
		if (multiplier < 0 || multiplier > 1) {
			throw new IllegalArgumentException("Multiplier should in [0,1]: " + multiplier);
		} else if (time < 0) {
			throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
		}
		setSmoothFactor(time == 0 ? 0: Math.pow(multiplier, 1 / time));
	}

	@Override
	public void setHalflife (@NotNull Double halflife) {
		setMT(0.5, halflife);
	}

	@Override
	public void setHalflife (double halflife) {
		setMT(0.5, halflife);
	}

	public void setSmoothFactorWeight (double weight) {
		this.smoothFactorWeight = weight;
	}

	public void setValue (double d) {
		value = d;
	}

	public static @NotNull ExpSmoothDouble createWithHalflife (double halflife) {
		var v = new ExpSmoothDouble();
		v.setHalflife(halflife);
		return v;
	}
}
