package com.github.leawind.api.client.event;

import com.github.leawind.api.base.ModEvent;

public class MouseTurnPlayerStartEvent implements ModEvent {
  /** 累积变化量 */
  public final double accumulatedDX;

  public final double accumulatedDY;

  private boolean isDefaultCancelled = false;

  public MouseTurnPlayerStartEvent(double accumulatedDX, double accumulatedDY) {
    this.accumulatedDX = accumulatedDX;
    this.accumulatedDY = accumulatedDY;
  }

  /** 取消默认操作 */
  public void cancelDefault() {
    isDefaultCancelled = true;
  }

  public boolean isDefaultCancelled() {
    return isDefaultCancelled;
  }

  @Override
  public boolean set() {
    return false;
  }
}
