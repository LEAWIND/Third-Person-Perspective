package com.github.leawind.thirdperson.core;


import com.github.leawind.api.base.GameStatus;
import com.github.leawind.api.client.event.ThirdPersonCameraSetupEvent;
import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.mixin.CameraInvoker;
import com.github.leawind.thirdperson.mixin.ClientLevelInvoker;
import com.github.leawind.util.annotation.VersionSensitive;
import com.github.leawind.util.math.LMath;
import com.github.leawind.util.math.smoothvalue.ExpSmoothDouble;
import com.github.leawind.util.math.smoothvalue.ExpSmoothVector2d;
import com.github.leawind.util.math.smoothvalue.ExpSmoothVector3d;
import com.github.leawind.util.math.vector.Vector2d;
import com.github.leawind.util.math.vector.Vector3d;
import com.google.common.collect.Lists;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CameraAgent {
	private final @NotNull Minecraft         minecraft;
	private final          ExpSmoothVector3d smoothRotateCenter;
	private final @NotNull Camera            fakeCamera       = new Camera();
	private final @NotNull Vector2d          relativeRotation = Vector2d.of(0);
	/**
	 * 相机偏移量
	 */
	private final @NotNull ExpSmoothVector2d smoothOffsetRatio;
	/**
	 * 虚相机到平滑眼睛外壳的距离系数
	 * <p>
	 * 外壳的半径是 {@link EntityAgent#getBodyRadius()}
	 */
	private final @NotNull ExpSmoothDouble   smoothDistanceMultiplier;
	/**
	 * 在 {@link CameraAgent#onRenderTickStart} 中更新
	 */
	private @NotNull       HitResult         hitResult        = BlockHitResult.miss(Vec3.ZERO, Direction.EAST, BlockPos.ZERO);

	public CameraAgent (@NotNull Minecraft minecraft) {
		this.minecraft           = minecraft;
		smoothRotateCenter       = new ExpSmoothVector3d();
		smoothOffsetRatio        = new ExpSmoothVector2d();
		smoothDistanceMultiplier = new ExpSmoothDouble();
	}

	/**
	 * 重置各种属性
	 */
	public void reset () {
		ThirdPerson.LOGGER.debug("Reset CameraAgent");
		smoothOffsetRatio.setValue(0, 0);
		smoothDistanceMultiplier.set(0D);
		if (ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			smoothRotateCenter.set(ThirdPerson.ENTITY_AGENT.getRotateCenter(ThirdPersonStatus.lastPartialTick));
		}
		if (ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			var entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			relativeRotation.set(-entity.getViewXRot(ThirdPersonStatus.lastPartialTick), entity.getViewYRot(ThirdPersonStatus.lastPartialTick) - 180);
		}
	}

	public void checkGameStatus () {
		if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
			GameStatus.isPerspectiveInverted = smoothDistanceMultiplier.get() > ThirdPersonConstants.FIRST_PERSON_TRANSITION_END_THRESHOLD;
		}
	}

	/**
	 * 渲染前
	 */
	public void onRenderTickStart (double now, double period, float partialTick) {
		if (!minecraft.isPaused() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			// mc 没有暂停，且正在以第三人称渲染
			// 更新探测结果
			hitResult = pick(getPickRange());
			// 平滑更新距离
			updateSmoothVirtualDistance(period);
			// 平滑更新相机偏移量
			updateSmoothOffsetRatio(period);
			//
			if (ThirdPersonStatus.shouldCameraTurnWithEntity()) {
				// 将相机朝向与相机实体朝向同步
				var rot = ThirdPerson.ENTITY_AGENT.getRawRotation(partialTick);
				relativeRotation.set(-rot.x(), rot.y() - 180);
			}
		}
	}

	/**
	 * 渲染过程中放置相机
	 */
	public void onCameraSetup (@NotNull ThirdPersonCameraSetupEvent event) {
		updateFakeCameraRotationPosition();
		preventThroughWall();
		updateFakeCameraRotationPosition();
		event.setPosition(fakeCamera.getPosition());
		float yRot = fakeCamera.getYRot();
		float xRot = fakeCamera.getXRot();
		assert !Float.isNaN(xRot + yRot);
		event.setRotation(xRot, yRot);
	}

	/**
	 * 获取平滑的相机旋转中心
	 */
	public @NotNull Vector3d getSmoothRotateCenter (float partialTick) {
		return smoothRotateCenter.get(partialTick);
	}

	public void onClientTickStart () {
		var config = ThirdPerson.getConfig();
		{
			final Vector3d halflife;
			if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
				halflife = Vector3d.of(0);
			} else if (ThirdPerson.ENTITY_AGENT.isFallFlying()) {
				halflife = Vector3d.of(config.flying_smooth_halflife);
			} else {
				halflife = config.getCameraOffsetScheme().getMode().getEyeSmoothHalflife();
			}
			final double dist = getSmoothRotateCenter(1).distance(ThirdPerson.CAMERA_AGENT.getRawCameraPosition());
			halflife.mul(Math.pow(dist, 0.5) * ThirdPersonConstants.EYE_HALFLIFE_MULTIPLIER);
			smoothRotateCenter.setHalflife(halflife);
		}
		smoothRotateCenter.setTarget(ThirdPerson.ENTITY_AGENT.getRotateCenter(1));
		smoothRotateCenter.update(ThirdPersonConstants.VANILLA_CLIENT_TICK_TIME);
	}

	/**
	 * 获取原版相机对象
	 */
	public @NotNull Camera getRawCamera () {
		return Objects.requireNonNull(Minecraft.getInstance().gameRenderer.getMainCamera());
	}

	/**
	 * 获取原始相机位置
	 */
	public @NotNull Vector3d getRawCameraPosition () {
		return LMath.toVector3d(getRawCamera().getPosition());
	}

	/**
	 * 第三人称相机朝向
	 */
	public @NotNull Vector2d getRotation () {
		return Vector2d.of(-relativeRotation.x(), relativeRotation.y() + 180);
	}

	/**
	 * 玩家控制的相机旋转
	 *
	 * @param dYRot 方向角变化量
	 * @param dXRot 俯仰角变化量
	 */
	public void turnCamera (double dYRot, double dXRot) {
		assert Double.isFinite(dYRot) && Double.isFinite(dXRot);
		var config = ThirdPerson.getConfig();
		if (config.is_mod_enabled && !ThirdPersonStatus.isAdjustingCameraOffset()) {
			if (config.lock_camera_pitch_angle) {
				dXRot = 0;
			}
			if (dYRot != 0 || dXRot != 0) {
				double yRot = getRelativeRotation().y() + dYRot;
				double xRot = getRelativeRotation().x() - dXRot;
				yRot %= 360f;
				xRot = LMath.clamp(xRot, -ThirdPersonConstants.CAMERA_PITCH_DEGREE_LIMIT, ThirdPersonConstants.CAMERA_PITCH_DEGREE_LIMIT);
				relativeRotation.set(xRot, yRot);
			}
		}
	}

	/**
	 * 获取相对旋转角度
	 */
	public @NotNull Vector2d getRelativeRotation () {
		return relativeRotation;
	}

	/**
	 * render tick 开始时，相机的 hitResult
	 */
	public @NotNull HitResult getHitResult () {
		return hitResult;
	}

	public double getPickRange () {
		return ThirdPerson.ENTITY_AGENT.getBodyRadius() + smoothDistanceMultiplier.get() + ThirdPerson.getConfig().camera_ray_trace_length;
	}

	/**
	 * 获取pick结果坐标
	 * <p>
	 * 使用默认距离
	 */
	public @NotNull Optional<Vector3d> getPickPosition () {
		return getPickPosition(getPickRange());
	}

	/**
	 * 获取相机视线落点坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	public @NotNull Optional<Vector3d> getPickPosition (double pickRange) {
		var hitResult = pick(pickRange);
		return Optional.ofNullable(hitResult.getType() == HitResult.Type.MISS ? null: LMath.toVector3d(hitResult.getLocation()));
	}

	/**
	 * 从相机出发探测所选方块或实体。
	 * <p>
	 * 当探测不到时，返回的是{@link HitResult.Type#MISS}类型。坐标将为探测终点
	 *
	 * @param pickRange 探测距离限制
	 */
	@VersionSensitive
	public @NotNull HitResult pick (double pickRange) {
		var                 camera                 = getRawCamera();
		var                 cameraPos              = camera.getPosition();
		Optional<HitResult> entityHitResult        = pickEntity(pickRange).map(hr -> hr);
		HitResult           blockHitResult         = pickBlock(pickRange);
		double              blockHitResultDistance = blockHitResult.getLocation().distanceTo(cameraPos);
		return entityHitResult.filter(hitResult -> !(blockHitResultDistance < hitResult.getLocation().distanceTo(cameraPos))).orElse(blockHitResult);
	}

	/**
	 * 根据相机的视线确定所选实体
	 * <p>
	 * 如果探测不到就返回空值
	 *
	 * @param pickRange 探测距离
	 */
	@VersionSensitive
	public @NotNull Optional<EntityHitResult> pickEntity (double pickRange) {
		if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			return Optional.empty();
		}
		var cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		var camera       = getRawCamera();
		var viewVector   = new Vec3(camera.getLookVector());
		var pickFrom     = camera.getPosition();
		var pickTo       = viewVector.scale(pickRange).add(pickFrom);
		var aabb         = new AABB(pickFrom, pickTo);
		return Optional.ofNullable(ProjectileUtil.getEntityHitResult(cameraEntity, pickFrom, pickTo, aabb, target -> !target.isSpectator() && target.isPickable(), pickRange));
	}

	/**
	 * 根据相机的视线探测方块
	 *
	 * @param pickRange  从相机出发的探测距离
	 * @param blockShape 方块形状获取器
	 * @param fluidShape 液体形状获取器
	 */
	public @NotNull BlockHitResult pickBlock (double pickRange, @NotNull ClipContext.Block blockShape, @NotNull ClipContext.Fluid fluidShape) {
		var camera       = getRawCamera();
		var pickFrom     = camera.getPosition();
		var viewVector   = new Vec3(camera.getLookVector());
		var pickTo       = viewVector.scale(pickRange).add(pickFrom);
		var cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		return cameraEntity.level().clip(new ClipContext(pickFrom, pickTo, blockShape, fluidShape, cameraEntity));
	}

	/**
	 * 瞄准时使用的方块形状获取器是 {@link ClipContext.Block#COLLIDER}，不包含草
	 * <p>
	 * 非瞄准时使用的方块形状获取器是 {@link ClipContext.Block#OUTLINE}
	 * <p>
	 * 当探测不到方块时，返回的是 {@link HitResult.Type#MISS} 类型，坐标将为探测终点，即 相机位置 + 视线向量.normalize(探测距离)
	 *
	 * @param pickRange 从相机出发的探测距离
	 */
	@VersionSensitive
	public @NotNull BlockHitResult pickBlock (double pickRange) {
		return pickBlock(pickRange, ThirdPerson.ENTITY_AGENT.wasAiming() ? ClipContext.Block.COLLIDER: ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
	}

	/**
	 * 相机是否正在注视某个实体（无视其他实体或方块）
	 */
	@VersionSensitive
	public boolean isLookingAt (@NotNull Entity entity) {
		var from = getRawCamera().getPosition();
		var to   = from.add(new Vec3(getRawCamera().getLookVector()).scale(getPickRange()));
		var aabb = entity.getBoundingBox();
		return aabb.contains(from) || aabb.clip(from, to).isPresent();
	}

	/**
	 * 预测玩家可能想要射击的目标实体
	 */
	public @NotNull Optional<Entity> predictTargetEntity () {
		var config = ThirdPerson.getConfig();
		// 候选目标实体
		List<Entity> candidateTargets = Lists.newArrayList();
		var          cameraPos        = getRawCamera().getPosition();
		var          cameraRot        = getRotation();
		var          cameraViewVector = LMath.directionFromRotationDegree(cameraRot).normalize();
		if (ThirdPerson.ENTITY_AGENT.isControlled()) {
			var playerEntity = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity();
			var clientLevel  = (ClientLevel)playerEntity.level();
			var entityGetter = ((ClientLevelInvoker)clientLevel).invokeGetEntityGetter();
			for (var target: entityGetter.getAll()) {
				if (!(target instanceof LivingEntity)) {
					continue;
				}
				double distance = target.distanceTo(playerEntity);
				// 排除距离太近和太远的
				if (distance < 2 || distance > config.camera_ray_trace_length) {
					continue;
				}
				if (!target.is(playerEntity)) {
					var targetPos      = target.getPosition(ThirdPersonStatus.lastPartialTick);
					var bottomY        = LMath.toVector3d(targetPos.with(Direction.Axis.Y, target.getBoundingBox().minY));
					var vectorToBottom = bottomY.copy().sub(ThirdPerson.ENTITY_AGENT.getRawEyePosition(ThirdPersonStatus.lastPartialTick));
					if (LMath.rotationDegreeFromDirection(vectorToBottom).x() < cameraRot.x()) {
						continue;
					}
					var    vectorToTarget = LMath.toVector3d(targetPos.subtract(cameraPos)).normalizeSafely();
					double angleRadian    = Math.acos(cameraViewVector.dot(vectorToTarget));
					if (Math.toDegrees(angleRadian) < ThirdPersonConstants.TARGET_PREDICTION_DEGREES_LIMIT) {
						candidateTargets.add(target);
					}
				}
			}
		}
		if (!candidateTargets.isEmpty()) {
			candidateTargets.sort(new AimingTargetComparator(cameraPos, cameraViewVector));
			return Optional.of(candidateTargets.get(0));
		}
		return Optional.empty();
	}

	/**
	 * 根据角度、距离、偏移量计算假相机实际朝向和位置
	 */
	private void updateFakeCameraRotationPosition () {
		// 设置假相机的位置，不偏移
		((CameraInvoker)fakeCamera).invokeSetRotation((float)(relativeRotation.y() + 180), (float)-relativeRotation.x());
		((CameraInvoker)fakeCamera).invokeSetPosition(LMath.toVec3(calculatePositionWithoutOffset()));
		var mc = ThirdPerson.mc;
		// 宽高比
		double aspectRatio = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		// 垂直视野角度一半(弧度制）
		double verticalRadianHalf = Math.toRadians(mc.options.fov().get()) / 2;
		// 成像平面宽高
		double heightHalf = Math.tan(verticalRadianHalf) * ThirdPersonConstants.VANILLA_NEAR_PLANE_DISTANCE;
		double widthHalf  = aspectRatio * heightHalf;
		// // 水平视野角度一半(弧度制）
		// double horizontalRadianHalf = Math.atan(widthHalf / NEAR_PLANE_DISTANCE);
		// 添加偏移量
		double minDist = ThirdPerson.ENTITY_AGENT.getBodyRadius();
		// 平滑值
		var    smoothOffsetRatioValue     = smoothOffsetRatio.get();
		double smoothVirtualDistanceValue = smoothDistanceMultiplier.get();
		double upOffset                   = (smoothVirtualDistanceValue + minDist) * smoothOffsetRatioValue.y() * Math.tan(verticalRadianHalf);
		double leftOffset                 = (smoothVirtualDistanceValue + minDist) * smoothOffsetRatioValue.x() * widthHalf / ThirdPersonConstants.VANILLA_NEAR_PLANE_DISTANCE;
		((CameraInvoker)fakeCamera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * NOW
	 * <p>
	 * 为防止穿墙，重新计算 {@link CameraAgent#smoothDistanceMultiplier} 的值
	 * <p>
	 * 当相机实体的眼睛在墙里时，直接把相机放在眼睛上。
	 * <p>
	 * 参考 Camera#getMaxZoom(double)
	 */
	private void preventThroughWall () {
		var entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		if (entity.isSpectator() && ThirdPerson.ENTITY_AGENT.isEyeInWall(ClipContext.Block.VISUAL)) {
			return;
		}
		var    cameraPosition    = fakeCamera.getPosition();
		var    smoothEyePosition = LMath.toVec3(getSmoothRotateCenter(ThirdPersonStatus.lastPartialTick));
		var    smoothEyeToCamera = smoothEyePosition.vectorTo(cameraPosition);
		double initDistance      = smoothEyeToCamera.length();
		if (initDistance < 1e-5) {
			return;
		}
		double minDistance = initDistance;
		for (int i = 0; i < 8; ++i) {
			double offsetX = (i & 1) * 2 - 1;
			double offsetY = (i >> 1 & 1) * 2 - 1;
			double offsetZ = (i >> 2 & 1) * 2 - 1;
			offsetX *= ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION;
			offsetY *= ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION;
			offsetZ *= ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION;
			var pickFrom  = smoothEyePosition.add(offsetX, offsetY, offsetZ);
			var pickTo    = pickFrom.add(smoothEyeToCamera);
			var hitResult = entity.level().clip(new ClipContext(pickFrom, pickTo, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, ThirdPerson.ENTITY_AGENT.getRawCameraEntity()));
			if (hitResult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitResult.getLocation().distanceTo(pickFrom));
			}
		}
		smoothDistanceMultiplier.setValue(Math.max(0, smoothDistanceMultiplier.get() - (initDistance - minDistance)));
	}

	private @NotNull Vector3d calculatePositionWithoutOffset () {
		var     smoothFactor     = smoothRotateCenter.smoothFactor.copy();
		boolean isHorizontalZero = smoothFactor.x() * smoothFactor.z() == 0;
		boolean isVerticalZero   = smoothFactor.y() == 0;
		var     rotateCenter     = smoothRotateCenter.get(ThirdPersonStatus.lastPartialTick);
		if (isHorizontalZero || isVerticalZero) {
			var rawRotateCenter = ThirdPerson.ENTITY_AGENT.getRotateCenter(ThirdPersonStatus.lastPartialTick);
			rotateCenter = Vector3d.of(isHorizontalZero ? rawRotateCenter.x(): rotateCenter.x(),//
									   isVerticalZero ? rawRotateCenter.y(): rotateCenter.y(),//
									   isHorizontalZero ? rawRotateCenter.z(): rotateCenter.z());
		}
		return rotateCenter.add(LMath.directionFromRotationDegree(relativeRotation).mul(ThirdPerson.ENTITY_AGENT.getBodyRadius() + smoothDistanceMultiplier.get()));
	}

	private void updateSmoothVirtualDistance (double period) {
		var     config      = ThirdPerson.getConfig();
		boolean isAdjusting = ThirdPersonStatus.isAdjustingCameraDistance();
		var     mode        = config.getCameraOffsetScheme().getMode();
		if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
			// 当前的目标是第一人称
			smoothDistanceMultiplier.setHalflife(config.t2f_transition_halflife);
			smoothDistanceMultiplier.setTarget(-ThirdPerson.ENTITY_AGENT.getBodyRadius() * 0.5);
		} else {
			// 当前的目标不是第一人称
			smoothDistanceMultiplier.setHalflife(isAdjusting ? config.adjusting_distance_smooth_halflife: mode.getDistanceSmoothHalflife());
			smoothDistanceMultiplier.setTarget(mode.getMaxDistance() * ThirdPerson.ENTITY_AGENT.vehicleTotalSizeCached);
		}
		smoothDistanceMultiplier.update(period);
		assert Double.isFinite(smoothDistanceMultiplier.get());
	}

	private void updateSmoothOffsetRatio (double period) {
		var config = ThirdPerson.getConfig();
		var mode   = config.getCameraOffsetScheme().getMode();
		if (ThirdPersonStatus.isAdjustingCameraOffset()) {
			smoothOffsetRatio.setHalflife(config.adjusting_camera_offset_smooth_halflife);
		} else {
			smoothOffsetRatio.setHalflife(mode.getOffsetSmoothHalflife());
		}
		if (config.center_offset_when_flying && ThirdPerson.ENTITY_AGENT.isFallFlying()) {
			smoothOffsetRatio.setTarget(0, 0);
		} else {
			mode.getOffsetRatio(smoothOffsetRatio.target);
		}
		smoothOffsetRatio.update(period);
	}
}
