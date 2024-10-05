package com.github.leawind.thirdperson;


import com.github.leawind.api.base.GameEvents;
import com.github.leawind.api.base.GameStatus;
import com.github.leawind.api.client.event.CalculateMoveImpulseEvent;
import com.github.leawind.api.client.event.EntityTurnStartEvent;
import com.github.leawind.api.client.event.MinecraftPickEvent;
import com.github.leawind.api.client.event.MouseTurnPlayerStartEvent;
import com.github.leawind.api.client.event.RenderEntityEvent;
import com.github.leawind.api.client.event.RenderTickStartEvent;
import com.github.leawind.api.client.event.ThirdPersonCameraSetupEvent;
import com.github.leawind.util.ItemPredicateUtil;
import com.github.leawind.util.annotation.VersionSensitive;
import com.github.leawind.util.math.LMath;
import com.github.leawind.util.math.vector.Vector2d;
import com.github.leawind.util.surroundings.Surroundings;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ThirdPersonEvents {
	public static void register () {
		ClientTickEvent.CLIENT_PRE.register(ThirdPersonEvents::onClientTickPre);
		ClientLifecycleEvent.CLIENT_STOPPING.register(ThirdPersonEvents::onClientStopping);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ThirdPersonEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ThirdPersonEvents::onClientPlayerJoin);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ThirdPersonEvents::onMouseScrolled);
		{
			GameEvents.thirdPersonCameraSetup = ThirdPersonEvents::onThirdPersonCameraSetup;
			GameEvents.minecraftPick          = ThirdPersonEvents::onMinecraftPickEvent;
			GameEvents.renderTickStart        = ThirdPersonEvents::onRenderTickStart;
			GameEvents.calculateMoveImpulse   = ThirdPersonEvents::onCalculateMoveImpulse;
			GameEvents.renderEntity           = ThirdPersonEvents::onRenderEntity;
			GameEvents.handleKeybindsStart    = ThirdPersonEvents::onHandleKeybindsStart;
			GameEvents.mouseTurnPlayerStart   = ThirdPersonEvents::onMouseTurnPlayerStart;
			GameEvents.entityTurnStart        = ThirdPersonEvents::onEntityTurnStart;
		}
	}

	/**
	 * Client tick 前
	 *
	 * @see ClientTickEvent#CLIENT_PRE
	 */
	private static void onClientTickPre (@NotNull Minecraft minecraft) {
		if (minecraft.isPaused() || !ThirdPerson.isAvailable()) {
			return;
		}
		var config = ThirdPerson.getConfig();
		if (minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
			// 目标是第三人称
			var cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			// 如果非旁观者模式的玩家在墙里边，就暂时切换到第一人称
			GameStatus.isPerspectiveInverted = !cameraEntity.isSpectator() && cameraEntity.isInWall();
			// 如果正在使用的物品符合相关配置，就暂时切换到第一人称
			if (cameraEntity instanceof LivingEntity livingEntity && livingEntity.isUsingItem()) {
				if (ItemPredicateUtil.anyMatches(livingEntity.getUseItem(), config.getUseToFirstPersonItemPredicates(), ThirdPersonResources.itemPredicateManager.useToFirstPersonItemPredicates)) {
					GameStatus.isPerspectiveInverted = true;
				}
			}
			// 如果位于狭窄通道内，暂时进入第一人称
			{
				final int leave_narrow_delay_ticks = 16;
				boolean   isInNarrowSpace          = true;
				var       center                   = BlockPos.containing(cameraEntity.getEyePosition(1));
				var       surroundings             = new Surroundings(minecraft.level, center);
				surroundings.apply(ThirdPersonConstants.SURROUNDING_PATTERN, s -> s.isViewBlocking(minecraft.level, center));
				int countT = surroundings.get("T").count();
				int countM = surroundings.get("M").count();
				isInNarrowSpace &= countT >= 3;
				isInNarrowSpace &= countM >= 1;
				if (isInNarrowSpace) {
					ThirdPersonStatus.ticksSinceLeaveNarrowSpace = 0;
					GameStatus.isPerspectiveInverted             = true;
				} else if (ThirdPersonStatus.ticksSinceLeaveNarrowSpace < leave_narrow_delay_ticks) {
					GameStatus.isPerspectiveInverted = true;
					ThirdPersonStatus.ticksSinceLeaveNarrowSpace++;
				}
			}
		}
		ThirdPerson.ENTITY_AGENT.onClientTickStart();
		ThirdPerson.CAMERA_AGENT.onClientTickStart();
	}

	private static void onClientStopping (Minecraft minecraft) {
		ThirdPerson.CONFIG_MANAGER.trySave();
	}

	/**
	 * 当玩家死亡后重生或加入新的维度时触发
	 *
	 * @see ClientPlayerEvent#CLIENT_PLAYER_RESPAWN
	 */
	private static void onClientPlayerRespawn (@NotNull LocalPlayer oldPlayer, @NotNull LocalPlayer newPlayer) {
		if (ThirdPerson.getConfig().is_mod_enabled) {
			resetPlayer();
			ThirdPerson.LOGGER.info("on Client player respawn");
		}
	}

	/**
	 * 当玩家加入时触发
	 *
	 * @see ClientPlayerEvent#CLIENT_PLAYER_JOIN
	 */
	private static void onClientPlayerJoin (@NotNull LocalPlayer player) {
		var config = ThirdPerson.getConfig();
		if (config.is_mod_enabled) {
			resetPlayer();
			ThirdPerson.LOGGER.info("on Client player join");
		}
		config.updateItemPredicates();
		ThirdPersonResources.itemPredicateManager.reparse();
	}

	@VersionSensitive(value="At latest architectury-api 9", until="1.20.2")
	private static @NotNull EventResult onMouseScrolled (@NotNull Minecraft minecraft, double amount) {
		return onMouseScrolled(minecraft, 0, amount);
	}

	/**
	 * 设置相机位置和朝向
	 */
	private static void onThirdPersonCameraSetup (ThirdPersonCameraSetupEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPerson.ENTITY_AGENT.isCameraEntityExist() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			ThirdPerson.CAMERA_AGENT.onCameraSetup(event);
		}
	}

	private static void onMinecraftPickEvent (MinecraftPickEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			var cameraEntity      = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			var cameraPosition    = ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition();
			var eyePosition       = cameraEntity.getEyePosition(event.partialTick);
			var cameraHitPosition = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation();
			event.pickTo(cameraHitPosition);
			double pickRange;
			if (ThirdPersonStatus.shouldPickFromCamera()) {
				event.pickFrom(cameraPosition);
				event.setPickRange(cameraHitPosition.distanceTo(cameraPosition) + 1e-5);
			} else {
				event.pickFrom(eyePosition);
				event.setPickRange(event.playerReach);
			}
		}
	}

	/**
	 * @see GameRenderer#render(float, long, boolean)
	 */
	private static void onRenderTickStart (RenderTickStartEvent event) {
		GameStatus.forceThirdPersonCrosshair = ThirdPersonStatus.shouldRenderThirdPersonCrosshair();
		if (!ThirdPerson.getConfig().is_mod_enabled) {
			return;
		}
		ThirdPerson.CAMERA_AGENT.checkGameStatus();
		// in seconds
		double now    = System.currentTimeMillis() / 1000D;
		double period = now - ThirdPersonStatus.lastRenderTickTimeStamp;
		ThirdPersonStatus.lastRenderTickTimeStamp = now;
		final boolean isRenderingInThirdPerson = ThirdPersonStatus.isRenderingInThirdPerson();
		if (isRenderingInThirdPerson != ThirdPersonStatus.wasRenderInThirdPersonLastRenderTick) {
			if (isRenderingInThirdPerson) {
				// 进入第三人称
				resetPlayer();
			} else {
				// 退出第三人称
				ThirdPerson.ENTITY_AGENT.setRawRotation(ThirdPerson.CAMERA_AGENT.getRotation());
			}
			var minecraft = Minecraft.getInstance();
			minecraft.gameRenderer.checkEntityPostEffect(minecraft.getCameraEntity());
			minecraft.levelRenderer.needsUpdate();
			ThirdPersonStatus.wasRenderInThirdPersonLastRenderTick = isRenderingInThirdPerson;
		}
		if (isRenderingInThirdPerson) {
			boolean shouldCameraTurnWithEntity = ThirdPersonStatus.shouldCameraTurnWithEntity();
			if (shouldCameraTurnWithEntity && !ThirdPersonStatus.wasShouldCameraTurnWithEntity) {
				// 将玩家朝向设为与相机一致
				if (ThirdPersonStatus.isRenderingInThirdPerson()) {
					ThirdPerson.ENTITY_AGENT.setRawRotation(ThirdPerson.CAMERA_AGENT.getRotation());
				}
			}
			ThirdPersonStatus.wasShouldCameraTurnWithEntity = shouldCameraTurnWithEntity;
		}
		if (ThirdPerson.isAvailable() && ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			ThirdPerson.ENTITY_AGENT.onRenderTickStart(now, period, event.partialTick);
			ThirdPerson.CAMERA_AGENT.onRenderTickStart(now, period, event.partialTick);
		}
	}

	/**
	 * 当前的 impulse 代表玩家希望前进的方向（世界坐标）
	 * <p>
	 * 结合当前玩家实体的朝向重新计算 impulse
	 */
	private static void onCalculateMoveImpulse (CalculateMoveImpulseEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPerson.ENTITY_AGENT.isControlled()) {
			var camera = ThirdPerson.CAMERA_AGENT.getRawCamera();
			// 计算世界坐标系下的向前和向左 impulse
			// 视线向量
			var lookImpulse = LMath.toVector3d(camera.getLookVector()).normalize();
			var leftImpulse = LMath.toVector3d(camera.getLeftVector()).normalize();
			// 水平方向上的视线向量
			var lookImpulseHorizon = Vector2d.of(lookImpulse.x(), lookImpulse.z()).normalize(event.forwardImpulse);
			var leftImpulseHorizon = Vector2d.of(leftImpulse.x(), leftImpulse.z()).normalize(event.leftImpulse);
			lookImpulseHorizon.add(leftImpulseHorizon, ThirdPersonStatus.impulseHorizon);
			// 世界坐标系下的 impulse
			lookImpulse.mul(event.forwardImpulse);    // 这才是 impulse
			leftImpulse.mul(event.leftImpulse);
			lookImpulse.add(leftImpulse, ThirdPersonStatus.impulse);
			// impulse 不为0，
			final double length = ThirdPersonStatus.impulseHorizon.length();
			if (length > 1E-5) {
				if (length > 1.0D) {
					ThirdPersonStatus.impulseHorizon.div(length, length);
				}
				float playerYRot        = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity().getViewYRot(Minecraft.getInstance().getFrameTime());
				var   playerLookHorizon = LMath.directionFromRotationDegree(playerYRot).normalize();
				var   playerLeftHorizon = LMath.directionFromRotationDegree(playerYRot - 90).normalize();
				event.forwardImpulse = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLookHorizon));
				event.leftImpulse    = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLeftHorizon));
			}
		}
	}

	private static boolean onRenderEntity (RenderEntityEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && event.entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
			return ThirdPersonStatus.shouldRenderCameraEntity(event.partialTick);
		}
		return true;
	}

	private static void onHandleKeybindsStart () {
		if (ThirdPerson.isAvailable()) {
			var config = ThirdPerson.getConfig();
			if (ThirdPersonStatus.isRenderingInThirdPerson()) {
				if (ThirdPerson.ENTITY_AGENT.isInteracting()) {
					// 立即更新玩家注视着的目标 Minecraft#hitResult
					Minecraft.getInstance().gameRenderer.pick(1f);
				}
			}
		}
	}

	/**
	 * @see MouseHandler#turnPlayer()
	 */
	private static void onMouseTurnPlayerStart (MouseTurnPlayerStartEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isAdjustingCameraOffset() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			if (event.accumulatedDX == 0 && event.accumulatedDY == 0) {
				return;
			}
			var config     = ThirdPerson.getConfig();
			var window     = Minecraft.getInstance().getWindow();
			var screenSize = Vector2d.of(window.getScreenWidth(), window.getScreenHeight());
			var scheme     = config.getCameraOffsetScheme();
			var mode       = scheme.getMode();
			if (mode.isCentered()) {
				// 相机在头顶，只能上下调整
				double topOffset = mode.getCenterOffsetRatio();
				topOffset += -event.accumulatedDY / screenSize.y();
				topOffset = LMath.clamp(topOffset, -1, 1);
				mode.setCenterOffsetRatio(topOffset);
			} else {
				// 相机没固定在头顶，可以上下左右调整
				var offset = mode.getSideOffsetRatio(Vector2d.of());
				offset.sub(Vector2d.of(event.accumulatedDX, event.accumulatedDY).div(screenSize));
				offset.clamp(-1, 1);
				scheme.setSide(Math.signum(offset.x()));
				mode.setSideOffsetRatio(offset);
			}
			event.cancelDefault();
		}
	}

	private static void onEntityTurnStart (EntityTurnStartEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			ThirdPerson.CAMERA_AGENT.turnCamera(event.dYRot, event.dXRot);
			event.cancelDefault();
		}
	}

	/**
	 * 重置玩家
	 *
	 * @see ThirdPersonEvents#onClientPlayerRespawn(LocalPlayer, LocalPlayer)
	 * @see ThirdPersonEvents#onClientPlayerJoin(LocalPlayer)
	 */
	public static void resetPlayer () {
		ThirdPerson.resetFiniteCheckers();
		ThirdPerson.ENTITY_AGENT.reset();
		ThirdPerson.CAMERA_AGENT.reset();
	}

	/**
	 * 使用滚轮调整距离
	 *
	 * @see ClientRawInputEvent.MouseScrolled
	 */
	@VersionSensitive(value="Since architectury-api 10", since="1.20.2")
	private static @NotNull EventResult onMouseScrolled (@NotNull Minecraft minecraft, double amountX, double amountY) {
		int offset = (int)-Math.signum(amountY);
		if (offset == 0 || !ThirdPersonStatus.isAdjustingCameraDistance()) {
			return EventResult.pass();
		}
		var    config = ThirdPerson.getConfig();
		double dist   = config.getCameraOffsetScheme().getMode().getDistanceLimit();
		dist = config.getDistanceMonoList().offset(dist, offset);
		config.getCameraOffsetScheme().getMode().setDistanceLimit(dist);
		return EventResult.interruptFalse();
	}

	/**
	 * @see ThirdPersonKeys#ADJUST_POSITION
	 */
	@SuppressWarnings("EmptyMethod")
	public static void onStartAdjustingCameraOffset () {
	}

	/**
	 * @see ThirdPersonKeys#ADJUST_POSITION
	 */
	public static void onStopAdjustingCameraOffset () {
		ThirdPerson.CONFIG_MANAGER.lazySave();
	}
}
