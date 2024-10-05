package com.github.leawind.api.client.event;


import com.github.leawind.api.base.ModEvent;
import net.minecraft.client.player.KeyboardInput;

public class CalculateMoveImpulseEvent implements ModEvent {
	public final KeyboardInput input;
	public final float         impulseMultiplier;


	public CalculateMoveImpulseEvent (KeyboardInput input, float impulseMultiplier) {
		this.input             = input;
		this.impulseMultiplier = impulseMultiplier;
	}

	public float forwardImpulse = 0;
	public float leftImpulse    = 0;

	@Override
	public boolean set () {
		return true;
	}
}
