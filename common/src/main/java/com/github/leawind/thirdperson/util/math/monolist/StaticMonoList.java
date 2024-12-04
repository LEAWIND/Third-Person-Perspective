package com.github.leawind.thirdperson.util.math.monolist;

import com.github.leawind.thirdperson.util.math.LMath;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 静态单调列表
 *
 * <p>列表中每一项的值会在列表被实例化时创建，不可更改
 */
@SuppressWarnings("unused")
public class StaticMonoList implements MonoList {
  private final int sgn;
  private final double[] list;

  /**
   * 根据列表直接创建
   *
   * @param list 列表
   */
  public StaticMonoList(double @NotNull [] list) {
    this.list = list;
    sgn = (int) Math.signum(list[1] - list[0]);
    if (!isMono()) {
      throw new IllegalArgumentException("Invalid list");
    }
  }

  private boolean isMono() {
    double lastValue = list[0];
    for (double value : list) {
      if (value != lastValue && Math.signum(value - lastValue) != sgn) {
        return false;
      }
    }
    return true;
  }

  @Override
  public double get(int i) {
    return list[i];
  }

  @Override
  public double offset(double value, int offset) {
    int i = iadsorption(value) + offset * sgn();
    i = LMath.clamp(i, 0, length() - 1);
    return list[i];
  }

  @Override
  public int iadsorption(double value) {
    int ileft = 0;
    int iright = length() - 1;
    int icenter = length() / 2;

    while (true) {
      if (list[icenter] < value) {
        ileft = icenter;
      } else if (list[icenter] > value) {
        iright = icenter;
      } else {
        return icenter;
      }
      if (iright - ileft == 1) {
        break;
      }
      icenter = (ileft + iright) / 2;
    }
    if (value - list[ileft] <= list[iright] - value) {
      return ileft;
    } else {
      return iright;
    }
  }

  @Override
  public double adsorption(double value) {
    return list[iadsorption(value)];
  }

  @Override
  public double getNext(double value) {
    return offset(value, 1);
  }

  @Override
  public double getLast(double value) {
    return offset(value, -1);
  }

  @Override
  public int sgn() {
    return sgn;
  }

  @Override
  public int length() {
    return list.length;
  }

  public static @NotNull StaticMonoList linear(int length) {
    return of(length, d -> (double) d);
  }

  public static @NotNull StaticMonoList of(int length, @NotNull Function<Integer, Double> getter) {
    double[] list = new double[length];
    for (int i = 0; i < length; i++) {
      list[i] = getter.apply(i);
    }
    return of(list);
  }

  @Contract("_ -> new")
  public static @NotNull StaticMonoList of(double[] list) {
    return new StaticMonoList(list);
  }

  public static @NotNull StaticMonoList exp(int length) {
    return of(length, Math::exp);
  }

  public static @NotNull StaticMonoList squared(int length) {
    return of(length, i -> (double) (i * i));
  }

  public static @NotNull StaticMonoList of(
      int length,
      double min,
      double max,
      @NotNull Function<Double, Double> f,
      @NotNull Function<Double, Double> fInv) {
    double xmin = fInv.apply(min);
    double xrange = fInv.apply(max) - xmin;
    return of(length, i -> f.apply(i * xrange / length + xmin));
  }
}
