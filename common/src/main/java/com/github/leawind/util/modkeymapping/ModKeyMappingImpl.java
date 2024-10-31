package com.github.leawind.util.modkeymapping;


import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModKeyMappingImpl extends KeyMapping implements ModKeyMapping {
	private long holdLength  = 300;
	private long pressLength = 300;
	private long keyDownTime = 0;

	private @Nullable Timer timer = null;

	private @Nullable Supplier<Boolean> ondown  = null;
	private @Nullable Supplier<Boolean> onup    = null;
	private @Nullable Supplier<Boolean> onhold  = null;
	private @Nullable Supplier<Boolean> onpress = null;

	/**
	 * @param id           按键映射的标识符，用于可翻译文本
	 * @param defaultValue 默认按键
	 * @param categoryKey  类别标识符，用于可翻译文本
	 */
	public ModKeyMappingImpl (String id, int defaultValue, String categoryKey) {
		super(id, defaultValue, categoryKey);
		mappings.put(id, this);
	}

	@Override
	public boolean isDown () {
		return super.isDown();
	}

	@Override
	public void setDown (boolean down) {
		boolean wasDown = isDown();
		super.setDown(down);
		long now = System.currentTimeMillis();
		if (!wasDown && down) {
			// key down
			if (handle(ondown)) {
				return;
			}
			keyDownTime = now;
			if (onhold != null) {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run () {
						handle(onhold);
						timer = null;
					}
				}, holdLength);
			}
		} else if (wasDown && !down) {
			// key up
			long sinceKeydown = now - keyDownTime;
			if (handle(onup)) {
				return;
			}
			if (sinceKeydown < pressLength) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				handle(onpress);
			}
		}
	}

	@Override
	public ModKeyMappingImpl holdLength (long holdLength) {
		this.holdLength = holdLength;
		return this;
	}

	@Override
	public ModKeyMappingImpl pressLength (long pressLength) {
		this.pressLength = pressLength;
		return this;
	}

	@Override
	public ModKeyMappingImpl onDown (@NotNull Runnable handler) {
		return onDown(() -> {
			handler.run();
			return false;
		});
	}

	@Override
	public ModKeyMappingImpl onDown (@NotNull Supplier<Boolean> handler) {
		ondown = handler;
		return this;
	}

	@Override
	public ModKeyMappingImpl onUp (@NotNull Runnable handler) {
		return onUp(() -> {
			handler.run();
			return false;
		});
	}

	@Override
	public ModKeyMappingImpl onUp (@NotNull Supplier<Boolean> handler) {
		onup = handler;
		return this;
	}

	@Override
	public ModKeyMappingImpl onPress (@NotNull Runnable handler) {
		return onPress(() -> {
			handler.run();
			return false;
		});
	}

	@Override
	public ModKeyMappingImpl onPress (@NotNull Supplier<Boolean> handler) {
		onpress = handler;
		return this;
	}

	@Override
	public ModKeyMappingImpl onHold (@NotNull Runnable handler) {
		return onHold(() -> {
			handler.run();
			return false;
		});
	}

	@Override
	public ModKeyMappingImpl onHold (@NotNull Supplier<Boolean> handler) {
		onhold = handler;
		return this;
	}

	private static boolean handle (@Nullable Supplier<Boolean> handler) {
		return handler != null && handler.get();
	}
}
