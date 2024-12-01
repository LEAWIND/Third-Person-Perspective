package com.github.leawind.thirdperson.api.client.event;

import com.github.leawind.thirdperson.api.base.ModEvent;
import net.minecraft.client.player.KeyboardInput;

public class CalculateMoveImpulseEvent implements ModEvent {
  public final KeyboardInput input;
  public final float impulseMultiplier;

  public float forwardImpulse = 0;
  public float leftImpulse = 0;

  public CalculateMoveImpulseEvent(KeyboardInput input, float impulseMultiplier) {
    this.input = input;
    this.impulseMultiplier = impulseMultiplier;
  }

  @Override
  public boolean set() {
    return true;
  }
}
