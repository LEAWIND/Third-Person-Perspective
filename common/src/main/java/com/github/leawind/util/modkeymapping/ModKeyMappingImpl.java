package com.github.leawind.util.modkeymapping;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ModKeyMappingImpl extends KeyMapping implements ModKeyMapping {
  private long holdMs = 300;
  private long pressMs = 300;
  private long keyDownTime = 0;
  private @Nullable Timer timer = null;
  private @Nullable Supplier<Boolean> onDown = null;
  private @Nullable Supplier<Boolean> onUp = null;
  private @Nullable Supplier<Boolean> onHold = null;
  private @Nullable Supplier<Boolean> onPress = null;

  /**
   * @param id 按键映射的标识符，用于可翻译文本
   * @param defaultValue 默认按键
   * @param categoryKey 类别标识符，用于可翻译文本
   */
  public ModKeyMappingImpl(String id, int defaultValue, String categoryKey) {
    super(id, defaultValue, categoryKey);
    mappings.put(id, this);
  }

  @Override
  public boolean isDown() {
    return super.isDown();
  }

  @Override
  public void setDown(boolean down) {
    boolean wasDown = isDown();
    super.setDown(down);
    long now = System.currentTimeMillis();
    if (!wasDown && down) {
      // key down
      if (runIfNonNull(onDown)) {
        return;
      }
      keyDownTime = now;
      if (onHold != null) {
        timer = new Timer();
        timer.schedule(
            new TimerTask() {
              @Override
              public void run() {
                runIfNonNull(onHold);
                timer = null;
              }
            },
            holdMs);
      }
    } else if (wasDown && !down) {
      // key up
      long sinceKeydown = now - keyDownTime;
      if (runIfNonNull(onUp)) {
        return;
      }
      if (sinceKeydown < pressMs) {
        if (timer != null) {
          timer.cancel();
          timer = null;
        }
        runIfNonNull(onPress);
      }
    }
  }

  @Override
  public ModKeyMappingImpl holdMs(long holdLength) {
    this.holdMs = holdLength;
    return this;
  }

  @Override
  public ModKeyMappingImpl pressMs(long pressLength) {
    this.pressMs = pressLength;
    return this;
  }

  @Override
  public ModKeyMappingImpl onDown(@NotNull Runnable handler) {
    return onDown(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onDown(@NotNull Supplier<Boolean> handler) {
    onDown = handler;
    return this;
  }

  @Override
  public ModKeyMappingImpl onUp(@NotNull Runnable handler) {
    return onUp(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onUp(@NotNull Supplier<Boolean> handler) {
    onUp = handler;
    return this;
  }

  @Override
  public ModKeyMappingImpl onPress(@NotNull Runnable handler) {
    return onPress(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onPress(@NotNull Supplier<Boolean> handler) {
    onPress = handler;
    return this;
  }

  @Override
  public ModKeyMappingImpl onHold(@NotNull Runnable handler) {
    return onHold(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onHold(@NotNull Supplier<Boolean> handler) {
    onHold = handler;
    return this;
  }

  private static boolean runIfNonNull(@Nullable Supplier<Boolean> handler) {
    return handler != null && handler.get();
  }
}
