package com.github.leawind.thirdperson.api.client.event;

import com.github.leawind.thirdperson.api.base.ModEvent;
import net.minecraft.world.phys.Vec3;

public final class ThirdPersonCameraSetupEvent implements ModEvent {
  public final float partialTick;
  public Vec3 pos;

  public float xRot = 0;
  public float yRot = 0;

  public ThirdPersonCameraSetupEvent(float partialTick) {
    this.partialTick = partialTick;
  }

  /** Set camera position */
  public void setPosition(Vec3 pos) {
    this.pos = pos;
  }

  /** Set camera rotation */
  public void setRotation(float xRot, float yRot) {
    this.xRot = xRot;
    this.yRot = yRot;
  }

  public boolean set() {
    return pos != null;
  }
}
