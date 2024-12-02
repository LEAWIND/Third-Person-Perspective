package com.github.leawind.thirdperson.util;

import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * PossibleSupplier 接口为可能不可用的供应操作提供了一种表示方法。
 *
 * <p>它允许在调用时检查供应操作是否可用。
 *
 * @param <T> 供应操作返回的类型
 */
public interface PossibleSupplier<T> {
  /**
   * 创建并返回一个新的PossibleSupplier实例。
   *
   * <p>该方法通过合并一个供应商和一个可用性谓词，提供了一个方便的方式来处理可能不可用的供应操作。
   *
   * @param supplier 提供值的供应操作
   * @param availablePredicate 用于判断供应操作是否可用的供应操作
   * @param <E> 供应操作返回的类型
   * @return 一个新的PossibleSupplier实例，封装了提供的供应操作和可用性谓词
   */
  @Contract(value = "_, _ -> new", pure = true)
  static <E> @NotNull PossibleSupplier<E> of(
      Supplier<E> supplier, Supplier<Boolean> availablePredicate) {
    return new PossibleSupplier<>() {
      @Override
      public E get() {
        return supplier.get();
      }

      @Override
      public boolean available() {
        return availablePredicate.get();
      }
    };
  }

  /**
   * 执行供应操作并获取其结果。
   *
   * @return 供应操作的结果
   */
  T get();

  /**
   * 检查此供应操作是否可用。
   *
   * @return 如果供应操作可用，则返回true；否则返回false
   */
  boolean available();
}
