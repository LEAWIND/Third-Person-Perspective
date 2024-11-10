package com.github.leawind.util.math.decisionmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * 决策表
 *
 * <p>事先将给定因素的组合与具体决策间的映射关系存储在表中，随时可以更新因素的值并根据它们做出新的决策
 *
 * <p>和直接使用条件判断语句进行判断相比，具有以下缺点：
 *
 * <ul>
 *   <li>构造麻烦
 *   <li>需要时间阅读文档
 *   <li>最好为规则编写清晰的注释
 * </ul>
 *
 * <p>优点：
 *
 * <ul>
 *   <li>便于精细控制。你可以为某一种具体情况指定相应的规则，而几乎不会影响运行时的性能
 * </ul>
 */
public class DecisionMap<T> {
  public static final int MAX_FACTOR_COUNT = 32;

  /**
   * 创建构造器
   *
   * @param <T> 决策结果类型
   */
  public static <T> DecisionMapBuilder<T> builder() {
    return new DecisionMapBuilder<>();
  }

  /** index -> {@link DecisionFactor} */
  private final List<DecisionFactor> factors;

  /** name -> {@link DecisionFactor} */
  private final Map<String, DecisionFactor> factorMap = new HashMap<>();

  /** flags -> strategy */
  private final Map<Integer, Supplier<T>> strategies;

  private final Supplier<T> defaultOperation;
  private int factorValues = 0;

  public DecisionMap(
      List<DecisionFactor> factorList,
      Map<Integer, Supplier<T>> strategyMap,
      Supplier<T> defaultOperation) {
    if (factorList.size() > MAX_FACTOR_COUNT) {
      throw new IllegalArgumentException(
          "Too many factors. Max is " + MAX_FACTOR_COUNT + ", got " + factorList.size());
    }

    factors = new ArrayList<>(factorList);

    int i = 0;
    for (var factor : factors) {
      factorMap.put(factor.getName(), factor);
      factor.index = i;
      i++;
    }

    strategies = new HashMap<>(strategyMap);

    this.defaultOperation = defaultOperation;
  }

  public int getFactorCount() {
    return factors.size();
  }

  public int getFactorValues() {
    return factorValues;
  }

  private @Nullable DecisionFactor getFactor(String name) {
    return factorMap.get(name);
  }

  /**
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >=
   *     size()})
   */
  private DecisionFactor getFactor(int index) throws IndexOutOfBoundsException {
    return factors.get(index);
  }

  /**
   * @throws IllegalArgumentException if the given name does not exist
   */
  public void update(String name) {
    var factor = getFactor(name);
    if (factor == null) {
      throw new IllegalArgumentException("No such factor: " + name);
    }
    factor.update();
    factorValues &= ~(1 << factor.index);
    factorValues |= factor.getInt() << factor.index;
  }

  /**
   * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >=
   *     size()})
   */
  public void update(int index) throws IndexOutOfBoundsException {
    var factor = getFactor(index);
    factor.update();
    factorValues &= ~(1 << index);
    factorValues |= factor.getInt() << index;
  }

  /** 更新所有因素 */
  public DecisionMap<T> updateAll() {
    for (var i = 0; i < factors.size(); i++) {
      update(i);
    }
    return this;
  }

  /**
   * 根据当前因素值执行对应的操作
   *
   * <p>如果策略表中没有当前因素值的组合，则执行默认操作
   */
  public T make() {
    return strategies.getOrDefault(factorValues, defaultOperation).get();
  }

  public String toDescription() {
    var strategySet = new HashSet<>(strategies.values());
    StringBuilder s = new StringBuilder("DecisionMap\n");
    s.append(
        String.format("\tTotally %d factors, %d strategies\n", factors.size(), strategySet.size()));
    s.append(
        String.format(
            "\tSpecified cases: %d/%d\n", strategies.size(), (int) Math.pow(2, factors.size())));
    for (int i = factors.size() - 1; i >= 0; i--) {
      s.append(String.format("\t\t%2d. %s\n", i, factors.get(i).getName()));
    }
    return s.toString();
  }

  public String toDescriptionWithCases(boolean allCases) {
    var strategySet = new HashSet<>(strategies.values());

    StringBuilder s = new StringBuilder("DecisionMap\n");
    s.append(
        String.format("\tTotally %d factors, %d strategies\n", factors.size(), strategySet.size()));
    s.append(
        String.format(
            "\tSpecified cases: %d/%d\n", strategies.size(), (int) Math.pow(2, factors.size())));
    for (int i = factors.size() - 1; i >= 0; i--) {
      s.append(String.format("\t\t%2d. %s\n", i, factors.get(i).getName()));
    }
    int flagsMax = (1 << factors.size()) - 1;
    for (int flags = 0; flags <= flagsMax; flags++) {
      if (allCases || strategies.containsKey(flags)) {
        s.append("\t");
        var flagsStr = Integer.toBinaryString(flags);
        s.append("0".repeat(factors.size() - flagsStr.length())).append(flagsStr);
        var strategy = strategies.get(flags);
        if (strategy != null) {
          s.append(String.format(" %s\n", strategy.getClass().getSimpleName()));
        } else {
          s.append(" (Default)\n");
        }
      }
    }

    return s.toString();
  }
}
