package com.github.leawind.thirdperson.api.client.event;

import net.minecraft.world.entity.Entity;

public class RenderEntityEvent {
  public final Entity entity;
  public final float partialTick;

  public RenderEntityEvent(Entity entity, float partialTick) {
    this.entity = entity;
    this.partialTick = partialTick;
  }
}
