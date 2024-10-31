package com.github.leawind.api.client.event;


import com.github.leawind.api.base.ModEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MinecraftPickEvent implements ModEvent {
	public final float  partialTick;
	/**
	 * 预期的玩家选取距离
	 */
	public final double playerReach;

	private @Nullable Vec3 pickFrom = null;
	private @Nullable Vec3 pickTo   = null;

	public MinecraftPickEvent (float partialTick, double playerReach) {
		this.partialTick = partialTick;
		this.playerReach = playerReach;
	}

	public boolean set () {
		return pickFrom != null && pickTo != null;
	}

	public void pickFrom (@Nullable Vec3 pos) {
		pickFrom = pos;
	}

	public void pickTo (@Nullable Vec3 pos) {
		pickTo = pos;
	}

	public @Nullable Vec3 pickFrom () {
		return pickFrom;
	}

	public @Nullable Vec3 pickTo () {
		return pickTo;
	}

	public void setPickRange (double range) {
		assert pickFrom != null;
		pickTo = pickFrom.add(getPickVector().normalize().scale(range));
	}

	public Vec3 getPickVector () {
		assert pickFrom != null;
		assert pickTo != null;
		return pickFrom.vectorTo(pickTo);
	}
}
