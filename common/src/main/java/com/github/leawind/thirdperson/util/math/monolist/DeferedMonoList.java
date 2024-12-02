package com.github.leawind.thirdperson.util.math.monolist;

import com.github.leawind.thirdperson.util.math.LMath;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * 延迟计算的单调列表
 *
 * <p>列表项是下标的单调函数
 *
 * <p>数列中每一项的值只有在被访问时才计算
 */
@SuppressWarnings("unused")
public class DeferedMonoList implements MonoList {
  public static @NotNull DeferedMonoList exp(int length) {
    return new DeferedMonoList(length, Math::exp);
  }

  public static @NotNull DeferedMonoList squared(int length) {
    return new DeferedMonoList(length, i -> (double) (i * i));
  }

  public static @NotNull DeferedMonoList of(int length, @NotNull Function<Integer, Double> getter) {
    return new DeferedMonoList(length, getter);
  }

  private final int length;
  private final @NotNull Function<Integer, Double> getter;
  private final int sgn;

  /**
   * @param length 列表长度
   * @param getter 值与下标的对应关系
   */
  protected DeferedMonoList(int length, @NotNull Function<Integer, Double> getter) {
    this.length = length;
    this.getter = getter;
    this.sgn = getter.apply(1) > getter.apply(0) ? 1 : -1;
  }

  @Override
  public double get(int i) {
    return getter.apply(i);
  }

  @Override
  public double offset(double value, int offset) {
    int i = iadsorption(value) + offset;
    i = LMath.clamp(i, 0, length() - 1);
    return get(i);
  }

  @Override
  public int iadsorption(double value) {
    int iLeft = 0;
    int iRight = length() - 1;
    int iCenter = length() / 2;
    while (true) {
      double vi = get(iCenter);
      if (vi < value) {
        iLeft = iCenter;
      } else if (vi > value) {
        iRight = iCenter;
      } else {
        return iCenter;
      }
      if (iRight - iLeft == 1) {
        break;
      }
      iCenter = (iLeft + iRight) / 2;
    }
    double min = value - get(iLeft);
    double max = get(iRight) - value;
    if (min <= max) {
      return iLeft;
    } else {
      return iRight;
    }
  }

  @Override
  public double adsorption(double value) {
    return get(iadsorption(value));
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
    return length;
  }
}
