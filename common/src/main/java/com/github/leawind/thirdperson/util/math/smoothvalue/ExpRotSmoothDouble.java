package com.github.leawind.thirdperson.util.math.smoothvalue;

import com.github.leawind.thirdperson.util.math.LMath;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpRotSmoothDouble extends ExpSmoothDouble {
  public static @NotNull ExpRotSmoothDouble createWithHalflife(double cycle, double halflife) {
    var v = new ExpRotSmoothDouble(cycle);
    v.setHalflife(halflife);
    return v;
  }

  private double cycle;

  /**
   * @param cycle 周期
   */
  public ExpRotSmoothDouble(double cycle) {
    super();
    setCycle(cycle);
  }

  public double getCycle() {
    return cycle;
  }

  public void setCycle(double cycle) {
    this.cycle = cycle;
  }

  @Override
  public void setTarget(double d) {
    super.setTarget(LMath.floorMod(d, cycle));
  }

  @Override
  public @NotNull Double get(double t) {
    lastValue = LMath.floorMod(lastValue, cycle);
    value = LMath.floorMod(value, cycle);
    double delta = LMath.floorMod(value - lastValue, cycle);
    if (delta > cycle / 2) {
      delta -= cycle;
    }
    value = lastValue + delta;
    return LMath.lerp(lastValue, value, t);
  }

  @Override
  protected void updateWithOutSavingLastValue(double period) {
    value = LMath.floorMod(value, cycle);
    target = LMath.floorMod(target, cycle);
    double delta = LMath.floorMod(target - value, cycle);
    if (delta > cycle / 2) {
      delta -= cycle;
    }
    target = value + delta;
    value = LMath.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
  }

  @Override
  public void set(@NotNull Double d) {
    d = LMath.floorMod(d, cycle);
    super.set(d);
  }

  @Override
  public void setHalflife(double halflife) {
    super.setHalflife(halflife);
  }

  @Override
  public void setValue(double d) {
    d = LMath.floorMod(d, cycle);
    super.setValue(d);
  }
}
