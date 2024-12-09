package com.github.leawind.thirdperson.util.math.monolist;

/**
 * 单调列表
 *
 * <p>列表中的数据是单调递增或单调递减的
 */
@SuppressWarnings("unused")
public interface MonoList {
  /** 获取下标对应的值 */
  double get(int i);

  /**
   * 计算当前值对应的下标，将下标偏移后获取其在列表中对应的值
   *
   * <p>如果偏移后的下标超出范围，则取边缘的值（第一个或最后一个值）
   *
   * <p>例：
   *
   * <pre>{@code
   * value ≈ B
   * offset = 2
   *
   * 下标： | 0 | 1 | 2 | 3 | 4 |
   * 数值： | A | B | C | D | E |
   *             ↑       ↑
   *           value     |
   *                     |
   *            index(value)+offset
   * }</pre>
   *
   * @param value 值
   * @param offset 偏移量
   */
  double offset(double value, int offset);

  /**
   * 取最接近的一个值的下标
   *
   * <p>例：
   *
   * <pre>{@code
   * value ≈ 2.4
   *             result
   *               ↓
   * 下标： | 0   | 1   | 2   | 3   | 4   |
   * 数值： | 1.0 | 2.0 | 3.0 | 4.0 | 5.0 |
   *                  ↑
   *              value=2.4
   * }</pre>
   */
  int iadsorption(double value);

  /**
   * 找最近的一个值
   *
   * <p>例：
   *
   * <pre>{@code
   * value ≈ 2.4
   * 下标： | 0   | 1   | 2   | 3   | 4   |
   * 数值： | 1.0 | 2.0 | 3.0 | 4.0 | 5.0 |
   *               ↑  ↑
   *               | value=2.4
   *             result
   * }</pre>
   */
  double adsorption(double value);

  /**
   * 获取指定值的下一个值
   *
   * <p>如果该值已经位于最后一个区间，那么直接返回最后一个值
   */
  double getNext(double value);

  /**
   * 获取指定值的上一个值
   *
   * <p>如果该值已经位于第一个区间，那么直接返回第一个值
   */
  double getLast(double value);

  /**
   * @return 如果单调递增则为1，单调递减则为-1
   */
  int sgn();

  /**
   * @return 列表长度
   */
  int length();
}
