package com.github.leawind.api.base;


import com.github.leawind.api.client.event.CalculateMoveImpulseEvent;
import com.github.leawind.api.client.event.EntityTurnStartEvent;
import com.github.leawind.api.client.event.MouseTurnPlayerStartEvent;
import com.github.leawind.api.client.event.RenderEntityEvent;
import com.github.leawind.api.client.event.RenderTickStartEvent;
import com.github.leawind.api.client.event.ThirdPersonCameraSetupEvent;

import java.util.function.Consumer;
import java.util.function.Function;

public final class GameEvents {
	public static Consumer<ThirdPersonCameraSetupEvent> thirdPersonCameraSetup = null;
	public static Consumer<RenderTickStartEvent>        renderTickStart        = null;
	public static Consumer<CalculateMoveImpulseEvent>   calculateMoveImpulse   = null;
	public static Function<RenderEntityEvent, Boolean>  renderEntity           = null;
	public static Runnable                              handleKeybindsStart    = null;
	public static Consumer<MouseTurnPlayerStartEvent>   mouseTurnPlayerStart   = null;
	public static Consumer<EntityTurnStartEvent>        entityTurnStart        = null;
}
