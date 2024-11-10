package com.github.leawind.util.math.smoothvalue;

import com.github.leawind.util.math.LMath;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

@SuppressWarnings("unused")
public class ExpSmoothVector2d extends ExpSmoothValue<Vector2d> {
  public ExpSmoothVector2d() {
    super(new Vector2d(0), new Vector2d(1), new Vector2d(0), new Vector2d(0), new Vector2d(0));
  }

  public void setTarget(double x, double y) {
    this.target.set(x, y);
  }

  @Override
  public void setTarget(@NotNull Vector2d target) {
    this.target.set(target);
  }

  @Override
  public @NotNull Vector2d get(double t) {
    return new Vector2d(lastValue).lerp(value, t);
  }

  @Override
  protected void saveLastValue() {
    lastValue.set(value);
  }

  @Override
  protected void updateWithOutSavingLastValue(double period) {
    var t =
        LMath.pow(new Vector2d(smoothFactor), new Vector2d(smoothFactorWeight).mul(period))
            .negate()
            .add(1, 1);
    LMath.lerp(value, target, t);
  }

  @Override
  public void setValue(@NotNull Vector2d v) {
    value.set(v);
  }

  @Override
  public void set(@NotNull Vector2d v) {
    value.set(v);
    target.set(v);
  }

  @Override
  public void setSmoothFactor(@NotNull Vector2d s) {
    this.smoothFactor.set(s);
  }

  @Override
  public void setSmoothFactor(double smoothFactor) {
    setSmoothFactor(smoothFactor, smoothFactor);
  }

  @Override
  public void setMT(@NotNull Vector2d multiplier, @NotNull Vector2d time) {
    if (multiplier.x < 0 || multiplier.x > 1) {
      throw new IllegalArgumentException("Multiplier.x should in [0,1]: " + multiplier.x);
    } else if (multiplier.y < 0 || multiplier.y > 1) {
      throw new IllegalArgumentException("Multiplier.y should in [0,1]: " + multiplier.y);
    } else if (time.x < 0 || time.y < 0) {
      throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
    }
    this.smoothFactor.set(
        time.x == 0 ? 0 : Math.pow(multiplier.x, 1 / time.x), //
        time.y == 0 ? 0 : Math.pow(multiplier.y, 1 / time.y));
  }

  @Override
  public void setHalflife(@NotNull Vector2d halflife) {
    setMT(new Vector2d(0.5), halflife);
  }

  @Override
  public void setHalflife(double halflife) {
    setMT(new Vector2d(0.5), new Vector2d(halflife));
  }

  public void setSmoothFactor(double x, double y) {
    this.smoothFactor.set(x, y);
  }

  public void setSmoothFactorWeight(double x, double y) {
    this.smoothFactorWeight.set(x, y);
  }

  public void setValue(double x, double y) {
    this.value.set(x, y);
  }
}
