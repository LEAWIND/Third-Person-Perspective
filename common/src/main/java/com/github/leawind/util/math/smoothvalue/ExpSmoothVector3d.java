package com.github.leawind.util.math.smoothvalue;


import com.github.leawind.util.math.LMath;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

@SuppressWarnings("unused")
public class ExpSmoothVector3d extends ExpSmoothValue<Vector3d> {
	public ExpSmoothVector3d () {
		super(new Vector3d(0), new Vector3d(1), new Vector3d(0), new Vector3d(0), new Vector3d(0));
	}

	public void setTarget (double x, double y, double z) {
		this.target.set(x, y, z);
	}

	public void setValue (double x, double y, double z) {
		this.value.set(x, y, z);
	}

	@Override
	public void setTarget (@NotNull Vector3d target) {
		this.target.set(target);
	}

	@Override
	public @NotNull Vector3d get (double t) {
		return new Vector3d(lastValue).lerp(value, t);
	}

	@Override
	protected void saveLastValue () {
		lastValue.set(value);
	}

	@Override
	protected void updateWithOutSavingLastValue (double period) {
		var t = LMath.pow(new Vector3d(smoothFactor), new Vector3d(smoothFactorWeight).mul(period)).negate().add(1, 1, 1);
		LMath.lerp(value, target, t);
	}

	@Override
	public void setValue (@NotNull Vector3d v) {
		value.set(v);
	}

	@Override
	public void set (@NotNull Vector3d v) {
		value.set(v);
		target.set(v);
	}

	@Override
	public void setSmoothFactor (@NotNull Vector3d smoothFactor) {
		this.smoothFactor.set(smoothFactor);
	}

	@Override
	public void setSmoothFactor (double d) {
		setSmoothFactor(d, d, d);
	}

	@Override
	public void setMT (@NotNull Vector3d multiplier, @NotNull Vector3d time) {
		if (multiplier.x < 0 || multiplier.x > 1) {
			throw new IllegalArgumentException("Multiplier.x should in [0,1]: " + multiplier.x);
		} else if (multiplier.y < 0 || multiplier.y > 1) {
			throw new IllegalArgumentException("Multiplier.y should in [0,1]: " + multiplier.y);
		} else if (multiplier.z < 0 || multiplier.z > 1) {
			throw new IllegalArgumentException("Multiplier.z should in [0,1]: " + multiplier.z);
		} else if (time.x < 0 || time.y < 0 || time.z < 0) {
			throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
		}
		this.smoothFactor.set(time.x == 0 ? 0: Math.pow(multiplier.x, 1 / time.x),//
							  time.y == 0 ? 0: Math.pow(multiplier.y, 1 / time.y),//
							  time.z == 0 ? 0: Math.pow(multiplier.z, 1 / time.z));
	}

	@Override
	public void setHalflife (@NotNull Vector3d halflife) {
		setMT(new Vector3d(0.5), halflife);
	}

	@Override
	public void setHalflife (double halflife) {
		setMT(new Vector3d(0.5), new Vector3d(halflife));
	}

	private void setSmoothFactor (double x, double y, double z) {
		this.smoothFactor.set(x, y, z);
	}

	public void setSmoothFactorWeight (double x, double y, double z) {
		this.smoothFactorWeight.set(x, y, z);
	}
}
