package com.github.leawind.thirdperson.api.client.event;

import com.github.leawind.thirdperson.api.base.ModEvent;

public final class RenderTickStartEvent implements ModEvent {
  public final float partialTick;

  public RenderTickStartEvent(float partialTick) {
    this.partialTick = partialTick;
  }

  @Override
  public boolean set() {
    return false;
  }
}
