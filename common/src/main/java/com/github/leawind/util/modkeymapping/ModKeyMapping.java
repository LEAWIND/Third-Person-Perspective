package com.github.leawind.util.modkeymapping;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import java.util.HashMap;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 按键映射
 *
 * <p>使用方法：
 *
 * <p>在模组初始化时，首先使用{@link ModKeyMapping#of}实例化所有按键映射，并绑定需要的的事件处理函数。
 *
 * <p>最后调用 {@link ModKeyMapping#registerAll()}方法使用 Architectury API 注册按键。
 *
 * <p>示例：
 *
 * <pre>
 * public class ModKeys{
 *     public static final KEY_OPEN_CONFIG = ModKeyMapping.of("key.examplemod.open_config", InputConstants.UNKNOWN.getValue(), "key.categories.examplemod")
 *     public static void init(){
 *         ModKeyMapping.registerAll();
 *     }
 * }
 * </pre>
 */
public interface ModKeyMapping extends Comparable<KeyMapping> {
  HashMap<String, ModKeyMappingImpl> mappings = new HashMap<>();

  /**
   * 不设置默认按键
   *
   * <p>需要用户自行设置按键
   *
   * @param id 按键映射的标识符，用于可翻译文本
   * @param categoryKey 类别标识符，用于可翻译文本
   */
  @Contract("_,_ -> new")
  static @NotNull ModKeyMapping of(@NotNull String id, @NotNull String categoryKey) {
    return of(id, InputConstants.UNKNOWN.getValue(), categoryKey);
  }

  /**
   * @param id 按键映射的标识符，用于可翻译文本
   * @param defaultValue 默认按键
   * @param categoryKey 类别标识符，用于可翻译文本
   */
  @Contract("_,_,_ -> new")
  static @NotNull ModKeyMapping of(
      @NotNull String id, int defaultValue, @NotNull String categoryKey) {
    return new ModKeyMappingImpl(id, defaultValue, categoryKey);
  }

  /** 使用 Architectury API 注册所有已实例化的按键映射 */
  static void registerAll() {
    mappings.values().forEach(KeyMappingRegistry::register);
  }

  /** 按键是否已按下 */
  boolean isDown();

  /**
   * 长按时长
   *
   * <p>按住一个按键足够长时间后触发长按事件
   *
   * @param holdLength 长按时长，单位是 ms
   */
  @Contract("_ -> this")
  ModKeyMapping holdMs(long holdLength);

  /**
   * 短按时长
   *
   * <p>按下一个按键，并在足够短的时间内松开，就会触发短按事件。
   *
   * @param pressLength 短按时长，单位是 ms
   */
  @Contract("_ -> this")
  ModKeyMapping pressMs(long pressLength);

  /** 当按键被按下时立即触发 */
  @Contract("_ -> this")
  ModKeyMapping onDown(@NotNull Runnable handler);

  /**
   * 当按键被按下时立即触发
   *
   * @param handler 事件处理函数。若其返回true，则不会触发后续的 onPress 或 onHold 事件
   */
  @Contract("_ -> this")
  ModKeyMapping onDown(@NotNull Supplier<Boolean> handler);

  /** 当按键松开时立即触发，位于 onPress 之前 */
  @Contract("_ -> this")
  ModKeyMapping onUp(@NotNull Runnable handler);

  /**
   * 当按键松开时立即触发，位于 onPress 之前
   *
   * @param handler 事件处理函数。若其返回true，则不会触发后续的 onPress 事件
   */
  @Contract("_ -> this")
  ModKeyMapping onUp(@NotNull Supplier<Boolean> handler);

  /** 按下一个按键后经过足够短的时间后抬起时触发 */
  @Contract("_ -> this")
  ModKeyMapping onPress(@NotNull Runnable handler);

  /** 按下一个按键后经过足够短的时间后抬起时触发 */
  @Contract("_ -> this")
  ModKeyMapping onPress(@NotNull Supplier<Boolean> handler);

  /** 当按住一个按键时间足够长时触发 */
  @Contract("_ -> this")
  ModKeyMapping onHold(@NotNull Runnable handler);

  /** 当按住一个按键时间足够长时触发 */
  @Contract("_ -> this")
  ModKeyMapping onHold(@NotNull Supplier<Boolean> handler);
}
