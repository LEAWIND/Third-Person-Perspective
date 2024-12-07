package com.github.leawind.thirdperson.api.client.event;

import com.github.leawind.thirdperson.api.base.ModEvent;
import net.minecraft.world.entity.Entity;

public class EntityTurnStartEvent implements ModEvent {
  public final Entity entity;

  public final double dXRot;
  public final double dYRot;

  private boolean isDefaultCancelled = false;

  public EntityTurnStartEvent(Entity entity, double dYRot, double dXRot) {
    this.entity = entity;
    this.dXRot = dXRot;
    this.dYRot = dYRot;
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
