package com.github.leawind.util;

import org.jetbrains.annotations.NotNull;

/**
 * 区间
 *
 * <pre>
 *   min    center    max
 *    |________|_______|
 * </pre>
 */
@SuppressWarnings("unused")
public class Zone {
  /**
   * @throws IllegalArgumentException min > max
   */
  public static @NotNull Zone of(double min, double max) throws IllegalArgumentException {
    return new Zone(min, max);
  }

  /**
   * 由两个端点创建区间，允许 a > b
   *
   * @param a 端点
   * @param b 另一个端点
   */
  public static @NotNull Zone ofAuto(double a, double b) {
    return a < b ? new Zone(a, b) : new Zone(b, a);
  }

  /**
   * @throws IllegalArgumentException radius < 0
   */
  public static @NotNull Zone ofRadius(double center, double radius)
      throws IllegalArgumentException {
    return new Zone(center - radius, center + radius);
  }

  /**
   * @throws IllegalArgumentException length < 0
   */
  public static @NotNull Zone ofLength(double center, double length)
      throws IllegalArgumentException {
    return new Zone(center - length / 2, center + length / 2);
  }

  public final double min;
  public final double max;

  /**
   * @throws IllegalArgumentException min > max
   */
  public Zone(double min, double max) throws IllegalArgumentException {
    if (min > max) {
      throw new IllegalArgumentException(
          "Minimum cannot be greater than maximum: " + min + " > " + max);
    }
    this.min = min;
    this.max = max;
  }

  /** 区间长度的一半 */
  public double radius() {
    return (max - min) / 2;
  }

  @Override
  public String toString() {
    return "Zone[" + min + ", " + max + "]";
  }

  /**
   * 保持区间中点不变，放缩长度
   *
   * @param scale 比例
   */
  public @NotNull Zone scale(double scale) {
    return Zone.ofLength(center(), length() * scale);
  }

  /**
   * @return 区间中点
   */
  public double center() {
    return (min + max) / 2;
  }

  /** 区间长度 */
  public double length() {
    return max - min;
  }

  /**
   * 求两个区间的相交区域
   *
   * <pre>
   * 	this:   |--------|
   * 	that:         |---------|
   * 	union:        |--|
   * </pre>
   *
   * @throws IllegalArgumentException 没有相交区域
   */
  public @NotNull Zone intersection(@NotNull Zone zone) throws IllegalArgumentException {
    return new Zone(Math.max(min, zone.min), Math.min(max, zone.max));
  }

  /**
   * 求两个区间的并集
   *
   * <pre>
   * 	this:   |--------|
   * 	that:         |---------|
   * 	union:  |---------------|
   * </pre>
   *
   * @throws IllegalArgumentException 没有相交区域
   */
  public @NotNull Zone union(@NotNull Zone zone) {
    if (!hasIntersection(zone)) {
      throw new IllegalArgumentException("Zones do not intersect");
    }
    return new Zone(Math.min(min, zone.min), Math.max(max, zone.max));
  }

  /**
   * 增加半径
   *
   * @throws IllegalArgumentException d < 0
   */
  public @NotNull Zone expendRadius(double d) throws IllegalArgumentException {
    if (d < 0) {
      throw new IllegalArgumentException("Expected non-negative, got " + d);
    }
    return new Zone(min - d, max + d);
  }

  /**
   * 两端分别延长
   *
   * @throws IllegalArgumentException a < 0 || b < 0
   */
  public @NotNull Zone expend(double a, double b) {
    if (a < 0 || b < 0) {
      throw new IllegalArgumentException("Expected non-negative, got (" + a + ", " + b + ")");
    }
    return new Zone(min - a, max + b);
  }

  /**
   * 减小半径
   *
   * @throws IllegalArgumentException d > radius
   */
  public @NotNull Zone squeeze(double d) throws IllegalArgumentException {
    if (d > radius()) {
      throw new IllegalArgumentException("Squeeze too much!");
    }
    return new Zone(min + d, max - d);
  }

  /**
   * 将半径减小
   *
   * <p>当减小量超过当前半径时，将视为将半径设为 0
   */
  public @NotNull Zone squeezeSafely(double d) {
    double r = Math.min(d, radius());
    return Zone.ofAuto(min + r, max - r);
  }

  /** 检查当前区间是否与给定的区间有交集。 */
  public boolean hasIntersection(@NotNull Zone zone) {
    return min <= zone.max && zone.min <= max;
  }

  /**
   * 将当前区间沿着数轴平移指定的距离。
   *
   * @param offset 偏移量
   */
  public @NotNull Zone move(double offset) {
    return new Zone(min + offset, max + offset);
  }

  /** 检查当前区间是否完全位于给定区间的左侧。 */
  public boolean lessThan(@NotNull Zone zone) {
    return max <= zone.min;
  }

  /** 检查当前区间是否完全位于给定区间的右侧。 */
  public boolean greaterThan(@NotNull Zone zone) {
    return min >= zone.max;
  }

  /**
   * 在左侧创建一个紧邻当前区间的新区间
   *
   * <pre>
   * this:          |----|
   * neighbor:  |---|
   * </pre>
   *
   * @param length 新区间的长度
   * @throws IllegalArgumentException length < 0
   */
  public @NotNull Zone lessNeighbor(double length) throws IllegalArgumentException {
    if (length < 0) {
      throw new IllegalArgumentException("Length must be non-negative, not " + length);
    }
    return new Zone(min - length, min);
  }

  /**
   * 在右侧创建一个紧邻当前区间的新区间
   *
   * <pre>
   * this:     |----|
   * neighbor:      |---|
   * </pre>
   *
   * @param length 新区间的长度
   * @throws IllegalArgumentException length < 0
   */
  public @NotNull Zone greaterNeighbor(double length) throws IllegalArgumentException {
    if (length < 0) {
      throw new IllegalArgumentException("Length must be non-negative, not " + length);
    }
    return new Zone(max, max + length);
  }

  /** 返回区间中与给定数值最近的值 */
  public double nearest(double value) {
    return Math.min(Math.max(value, min), max);
  }

  /** 返回区间中与给定数值最远的值 */
  public double furthest(double value) {
    return value <= center() ? max : min;
  }

  /** 返回与给定数值的距离 */
  public double distance(double value) {
    return (value < min) ? (min - value) : (value > max) ? (value - max) : 0;
  }

  /** 检查当前区间是否完全包含于给定区间中。 */
  public boolean in(@NotNull Zone zone) {
    return zone.min <= min && max <= zone.max;
  }

  /** 检查当前区间是否完全包含给定区间。 */
  public boolean contains(@NotNull Zone zone) {
    return zone.min >= min && zone.max <= max;
  }

  /** 检查当前区间是否包含给定数值。 */
  public boolean contains(double value) {
    return min <= value && value <= max;
  }

  /**
   * 创建一个新的区间，具有相同的最小值和新的最大值。
   *
   * @param max 新的最大值
   * @return 具有相同最小值和新最大值的新区间
   * @throws IllegalArgumentException min < max
   */
  public @NotNull Zone withMax(double max) throws IllegalArgumentException {
    return new Zone(min, max);
  }

  /**
   * 创建一个新的区间，具有新的最小值和相同的最大值。
   *
   * @param min 新的最小值
   * @return 具有新最小值和相同最大值的新区间
   * @throws IllegalArgumentException min < max
   */
  public @NotNull Zone withMin(double min) throws IllegalArgumentException {
    return new Zone(min, max);
  }
}
