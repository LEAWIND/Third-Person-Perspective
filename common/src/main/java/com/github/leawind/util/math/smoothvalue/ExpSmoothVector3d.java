package com.github.leawind.util.math.smoothvalue;


import com.github.leawind.util.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpSmoothVector3d extends ExpSmoothValue<Vector3d> {
	public ExpSmoothVector3d () {
		super(Vector3d.of(0), Vector3d.of(1), Vector3d.of(0), Vector3d.of(0), Vector3d.of(0));
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
		return lastValue.copy().lerp(value, t);
	}

	@Override
	protected void udpateWithOutSavingLastValue (double period) {
		var t = smoothFactor.copy().pow(smoothFactorWeight.copy().mul(period)).negate().add(1);
		value = value.copy().lerp(target, t);
	}

	@Override
	public void setValue (@NotNull Vector3d v) {
		value = v;
	}

	@Override
	public void set (@NotNull Vector3d v) {
		value = target = v;
	}

	@Override
	public void setSmoothFactor (@NotNull Vector3d smoothFactor) {
		this.smoothFactor.set(smoothFactor);
	}

	@Override
	public void setSmoothFactor (double d) {
		setSmoothFactor(d, d, d);
	}

	private void setSmoothFactor (double x, double y, double z) {
		this.smoothFactor.set(x, y, z);
	}

	@Override
	public void setMT (@NotNull Vector3d multiplier, @NotNull Vector3d time) {
		if (multiplier.x() < 0 || multiplier.x() > 1) {
			throw new IllegalArgumentException("Multiplier.x should in [0,1]: " + multiplier.x());
		} else if (multiplier.y() < 0 || multiplier.y() > 1) {
			throw new IllegalArgumentException("Multiplier.y should in [0,1]: " + multiplier.y());
		} else if (multiplier.z() < 0 || multiplier.z() > 1) {
			throw new IllegalArgumentException("Multiplier.z should in [0,1]: " + multiplier.z());
		} else if (time.x() < 0 || time.y() < 0 || time.z() < 0) {
			throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
		}
		this.smoothFactor.set(time.x() == 0 ? 0: Math.pow(multiplier.x(), 1 / time.x()),//
							  time.y() == 0 ? 0: Math.pow(multiplier.y(), 1 / time.y()),//
							  time.z() == 0 ? 0: Math.pow(multiplier.z(), 1 / time.z()));
	}

	@Override
	public void setHalflife (@NotNull Vector3d halflife) {
		setMT(Vector3d.of(0.5), halflife);
	}

	@Override
	public void setHalflife (double halflife) {
		setMT(Vector3d.of(0.5), Vector3d.of(halflife));
	}

	public void setSmoothFactorWeight (double x, double y, double z) {
		this.smoothFactorWeight.set(x, y, z);
	}
}
