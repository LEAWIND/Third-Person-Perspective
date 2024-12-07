package com.github.leawind.thirdperson.util.math.smoothvalue;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ISmoothValue<T> {
  /**
   * 设置目标值
   *
   * <p>任何时候都可能设置此值
   */
  void setTarget(@NotNull T endValue);

  /**
   * 记录旧的平滑值，然后更新平滑值
   *
   * <p>对于 {@link ExpSmoothValue}，理论上任何时候都可以更新，
   *
   * <p>但考虑到时间的精度有限，更新间隔不应过小。
   *
   * @param period 经过的时间（s）
   */
  void update(double period);

  /**
   * 获取当前的平滑值
   *
   * <p>如果使用 {@link ISmoothValue#update(double)} 进行更新的频率小于渲染频率，
   *
   * <p>则不建议在渲染中直接采用此方法获取平滑值，因为这可能造成不平滑的效果。
   *
   * <p>应当使用 {@link ISmoothValue#get(double)}
   */
  @NotNull
  T get();

  /**
   * 旧值与当前平滑值的线性插值
   *
   * <p>每次更新时，都会记录下当时的平滑值作为旧值，然后再计算新的平滑值。
   *
   * <p>当更新频率小于渲染频率时，此方法十分有效。
   *
   * @param t 自上次更新以来经过的时间占更新间隔的比例，用于线性插值。
   */
  @NotNull
  T get(double t);

  /** 获取上次更新前的平滑值（旧值） */
  @NotNull
  T getLast();
}
