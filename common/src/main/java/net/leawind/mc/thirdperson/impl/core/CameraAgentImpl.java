package net.leawind.mc.thirdperson.impl.core;


import com.google.common.collect.Lists;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.core.CameraAgent;
import net.leawind.mc.thirdperson.mixin.CameraInvoker;
import net.leawind.mc.thirdperson.mixin.ClientLevelInvoker;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector2d;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CameraAgentImpl implements CameraAgent {
	private final @NotNull Minecraft         minecraft;
	private final @NotNull Camera            fakeCamera       = new Camera();
	private final @NotNull Vector2d          relativeRotation = Vector2d.of(0);
	/**
	 * 相机偏移量
	 */
	private final @NotNull ExpSmoothVector2d smoothOffsetRatio;
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	private final @NotNull ExpSmoothDouble   smoothDistanceToEye;
	private @Nullable      BlockGetter       blockGetter;
	/**
	 * 在 {@link CameraAgent#onPreRender} 中更新
	 */
	private @NotNull       HitResult         hitResult        = BlockHitResult.miss(Vec3.ZERO, Direction.EAST, BlockPos.ZERO);

	public CameraAgentImpl (@NotNull Minecraft minecraft) {
		this.minecraft      = minecraft;
		smoothOffsetRatio   = new ExpSmoothVector2d();
		smoothDistanceToEye = new ExpSmoothDouble();
	}

	@Override
	public void reset () {
		ThirdPerson.LOGGER.debug("Reset CameraAgent");
		smoothOffsetRatio.setValue(0, 0);
		smoothDistanceToEye.set(0D);
		if (ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			Entity entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			relativeRotation.set(-entity.getViewXRot(ThirdPersonStatus.lastPartialTick), entity.getViewYRot(ThirdPersonStatus.lastPartialTick) - 180);
		}
	}

	@Override
	public void setBlockGetter (@NotNull BlockGetter blockGetter) {
		this.blockGetter = blockGetter;
	}

	@Override
	public void onPreRender (double now, double period, float partialTick) {
		if (!minecraft.isPaused()) {
			// 更新探测结果
			hitResult = pick(getPickRange());
			// 平滑更新距离
			updateSmoothVirtualDistance(period);
			// 平滑更新相机偏移量
			updateSmoothOffsetRatio(period);
			//
			if (ThirdPersonStatus.shouldCameraTurnWithEntity()) {
				// 将相机朝向与相机实体朝向同步
				Vector2d rot = ThirdPerson.ENTITY_AGENT.getRawRotation(partialTick);
				relativeRotation.set(-rot.x(), rot.y() - 180);
			}
			if (ThirdPersonStatus.isTransitioningToFirstPerson) {
				// 正在从第三人称过渡到第一人称
				if (smoothDistanceToEye.get() < ThirdPersonConstants.FIRST_PERSON_TRANSITION_END_THRESHOLD) {
					// 距离足够近，结束过渡
					ThirdPersonStatus.isTransitioningToFirstPerson = false;
					ThirdPerson.mc.options.setCameraType(CameraType.FIRST_PERSON);
					ThirdPerson.mc.gameRenderer.checkEntityPostEffect(ThirdPerson.mc.getCameraEntity());
					// 将玩家转到相机朝向
					ThirdPerson.ENTITY_AGENT.setRawRotation(getRotation());
				}
			}
		}
	}

	@Override
	public void onCameraSetup () {
		updateFakeCameraRotationPosition();
		preventThroughWall();
		updateFakeCameraRotationPosition();
		applyCamera();
	}

	@Override
	public void onClientTickPre () {
		ThirdPersonStatus.isTransitioningToFirstPerson = false;
		boolean isTargetThirdPerson = ThirdPerson.getConfig().is_third_person_mode && !ThirdPersonStatus.isTemporaryFirstPerson;
		if (isTargetThirdPerson) {
			// 目标是第三人称，那就直接以第三人称渲染
			ThirdPerson.mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			ThirdPerson.mc.gameRenderer.checkEntityPostEffect(null);
		} else if (!ThirdPerson.mc.options.getCameraType().isFirstPerson()) {
			// 目标是第一人称，但是相机当前以第三人称渲染，那么开始过渡
			ThirdPersonStatus.isTransitioningToFirstPerson = true;
		}
	}

	public @NotNull Camera getRawCamera () {
		return Objects.requireNonNull(Minecraft.getInstance().gameRenderer.getMainCamera());
	}

	@Override
	public @NotNull Vector3d getRawCameraPosition () {
		return LMath.toVector3d(getRawCamera().getPosition());
	}

	@Override
	public @NotNull Vector2d getRotation () {
		return Vector2d.of(-relativeRotation.x(), relativeRotation.y() + 180);
	}

	@Override
	public @NotNull Vector2d getRawRotation () {
		Camera cam = getRawCamera();
		return Vector2d.of(-cam.getXRot(), cam.getYRot());
	}

	@Override
	public @NotNull Camera getFakeCamera () {
		return fakeCamera;
	}

	@Override
	public void onCameraTurn (double dy, double dx) {
		Config config = ThirdPerson.getConfig();
		if (config.is_mod_enable && !ThirdPersonStatus.isAdjustingCameraOffset()) {
			dy *= 0.15;
			dx *= config.lock_camera_pitch_angle ? 0: -0.15;
			if (dy != 0 || dx != 0) {
				double rx = getRelativeRotation().x() + dx;
				double ry = getRelativeRotation().y() + dy;
				rx = LMath.clamp(rx, -ThirdPersonConstants.CAMERA_PITCH_DEGREE_LIMIT, ThirdPersonConstants.CAMERA_PITCH_DEGREE_LIMIT);
				relativeRotation.set(rx, ry % 360f);
			}
		}
	}

	@Override
	public @NotNull Vector2d getRelativeRotation () {
		return relativeRotation;
	}

	@Override
	public @NotNull HitResult getHitResult () {
		return hitResult;
	}

	@Override
	public double getPickRange () {
		return smoothDistanceToEye.get() + ThirdPerson.getConfig().camera_ray_trace_length;
	}

	@Override
	public @NotNull Optional<Vector3d> getPickPosition () {
		return getPickPosition(getPickRange());
	}

	@Override
	public @NotNull Optional<Vector3d> getPickPosition (double pickRange) {
		HitResult hitResult = pick(pickRange);
		return Optional.ofNullable(hitResult.getType() == HitResult.Type.MISS ? null: LMath.toVector3d(hitResult.getLocation()));
	}

	@Override
	@VersionSensitive
	public @NotNull HitResult pick (double pickRange) {
		Camera              camera                 = getRawCamera();
		Vec3                cameraPos              = camera.getPosition();
		Optional<HitResult> entityHitResult        = pickEntity(pickRange).map(hr -> hr);
		HitResult           blockHitResult         = pickBlock(pickRange);
		double              blockHitResultDistance = blockHitResult.getLocation().distanceTo(cameraPos);
		return entityHitResult.filter(hitResult -> !(blockHitResultDistance < hitResult.getLocation().distanceTo(cameraPos))).orElse(blockHitResult);
	}

	/**
	 * @see GameRenderer#pick(float)
	 */
	@Override
	@VersionSensitive
	public @NotNull Optional<EntityHitResult> pickEntity (double pickRange) {
		if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			return Optional.empty();
		}
		Entity cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		Camera camera       = getRawCamera();
		Vec3   viewVector   = new Vec3(camera.getLookVector());
		Vec3   pickFrom     = camera.getPosition();
		Vec3   pickTo       = viewVector.scale(pickRange).add(pickFrom);
		AABB   aabb         = new AABB(pickFrom, pickTo);
		return Optional.ofNullable(ProjectileUtil.getEntityHitResult(cameraEntity, pickFrom, pickTo, aabb, target -> !target.isSpectator() && target.isPickable(), pickRange));
	}

	@Override
	public @NotNull Optional<EntityHitResult> pickEntity () {
		return pickEntity(getPickRange());
	}

	@Override
	public @NotNull BlockHitResult pickBlock (double pickRange, @NotNull ClipContext.Block blockShape, @NotNull ClipContext.Fluid fluidShape) {
		Camera camera       = getRawCamera();
		Vec3   pickFrom     = camera.getPosition();
		Vec3   viewVector   = new Vec3(camera.getLookVector());
		Vec3   pickTo       = viewVector.scale(pickRange).add(pickFrom);
		Entity cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		return cameraEntity.level.clip(new ClipContext(pickFrom, pickTo, blockShape, fluidShape, cameraEntity));
	}

	@Override
	public @NotNull BlockHitResult pickBlock (@NotNull ClipContext.Block blockShape, @NotNull ClipContext.Fluid fluidShape) {
		return pickBlock(getPickRange(), blockShape, fluidShape);
	}

	@Override
	@VersionSensitive
	public @NotNull BlockHitResult pickBlock (double pickRange) {
		return pickBlock(pickRange, ThirdPerson.ENTITY_AGENT.wasAiming() ? ClipContext.Block.COLLIDER: ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
	}

	@VersionSensitive
	@Override
	public boolean isLookingAt (@NotNull Entity entity) {
		Vec3 from = getRawCamera().getPosition();
		Vec3 to   = from.add(new Vec3(getRawCamera().getLookVector()).scale(getPickRange()));
		AABB aabb = entity.getBoundingBox();
		return aabb.contains(from) || aabb.clip(from, to).isPresent();
	}

	/**
	 * @see AimingTargetComparator#getCost(Entity)
	 */
	@Override
	public @NotNull Optional<Entity> predictTargetEntity () {
		Config config = ThirdPerson.getConfig();
		// 候选目标实体
		List<Entity> candidateTargets = Lists.newArrayList();
		Vec3         cameraPos        = getRawCamera().getPosition();
		Vector2d     cameraRot        = getRotation();
		Vector3d     cameraViewVector = LMath.directionFromRotationDegree(cameraRot).normalize();
		if (ThirdPerson.ENTITY_AGENT.isControlled()) {
			Entity                    playerEntity = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity();
			ClientLevel               clientLevel  = (ClientLevel)playerEntity.level;
			LevelEntityGetter<Entity> entityGetter = ((ClientLevelInvoker)clientLevel).invokeGetEntityGetter();
			for (Entity target: entityGetter.getAll()) {
				if (!(target instanceof LivingEntity)) {
					continue;
				}
				double distance = target.distanceTo(playerEntity);
				// 排除距离太近和太远的
				if (distance < 2 || distance > config.camera_ray_trace_length) {
					continue;
				}
				if (!target.is(playerEntity)) {
					Vec3     targetPos      = target.getPosition(ThirdPersonStatus.lastPartialTick);
					Vector3d bottomY        = LMath.toVector3d(targetPos.with(Direction.Axis.Y, target.getBoundingBox().minY));
					Vector3d vectorToBottom = bottomY.copy().sub(ThirdPerson.ENTITY_AGENT.getRawEyePosition(ThirdPersonStatus.lastPartialTick));
					if (LMath.rotationDegreeFromDirection(vectorToBottom).x() < cameraRot.x()) {
						continue;
					}
					Vector3d vectorToTarget = LMath.toVector3d(targetPos.subtract(cameraPos)).normalize();
					double   angleRadian    = Math.acos(cameraViewVector.dot(vectorToTarget));
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
		Minecraft mc = ThirdPerson.mc;
		// 宽高比
		double aspectRatio = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		// 垂直视野角度一半(弧度制）
		double verticalRadianHalf = Math.toRadians(mc.options.fov) / 2;
		// 成像平面宽高
		double heightHalf = Math.tan(verticalRadianHalf) * ThirdPersonConstants.NEAR_PLANE_DISTANCE;
		double widthHalf  = aspectRatio * heightHalf;
		//		// 水平视野角度一半(弧度制）
		//		double horizonalRadianHalf = Math.atan(widthHalf / NEAR_PLANE_DISTANCE);
		// 平滑值
		Vector2d smoothOffsetRatioValue     = smoothOffsetRatio.get();
		double   smoothVirtualDistanceValue = smoothDistanceToEye.get();
		// 偏移量
		double upOffset   = smoothOffsetRatioValue.y() * smoothVirtualDistanceValue * Math.tan(verticalRadianHalf);
		double leftOffset = smoothOffsetRatioValue.x() * smoothVirtualDistanceValue * widthHalf / ThirdPersonConstants.NEAR_PLANE_DISTANCE;
		// 没有偏移的情况下相机位置
		Vector3d positionWithoutOffset = calculatePositionWithoutOffset();
		// 应用到假相机
		((CameraInvoker)fakeCamera).invokeSetRotation((float)(relativeRotation.y() + 180), (float)-relativeRotation.x());
		((CameraInvoker)fakeCamera).invokeSetPosition(LMath.toVec3(positionWithoutOffset));
		((CameraInvoker)fakeCamera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * 为防止穿墙，重新计算 {@link CameraAgentImpl#smoothDistanceToEye} 的值
	 * <p>
	 * 当相机实体的眼睛在墙里时，直接把相机放在眼睛上。
	 * <p>
	 * 参考 Camera#getMaxZoom(double)
	 */
	private void preventThroughWall () {
		Entity entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		if (entity.isSpectator() && ThirdPerson.ENTITY_AGENT.isEyeInWall(ClipContext.Block.VISUAL)) {
			return;
		}
		Vec3   cameraPosition    = fakeCamera.getPosition();
		Vec3   smoothEyePosition = LMath.toVec3(ThirdPerson.ENTITY_AGENT.getSmoothEyePosition(ThirdPersonStatus.lastPartialTick));
		Vec3   smoothEyeToCamera = smoothEyePosition.vectorTo(cameraPosition);
		double initDistance      = smoothEyeToCamera.length();
		if (initDistance < 1e-5) {
			return;
		}
		double minDistance = initDistance;
		assert blockGetter != null;
		for (int i = 0; i < 8; ++i) {
			double offsetX = (i & 1) * 2 - 1;
			double offsetY = (i >> 1 & 1) * 2 - 1;
			double offsetZ = (i >> 2 & 1) * 2 - 1;
			offsetX *= ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION;
			offsetY *= ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION;
			offsetZ *= ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION;
			Vec3      pickFrom  = smoothEyePosition.add(offsetX, offsetY, offsetZ);
			Vec3      pickTo    = pickFrom.add(smoothEyeToCamera);
			HitResult hitResult = blockGetter.clip(new ClipContext(pickFrom, pickTo, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, ThirdPerson.ENTITY_AGENT.getRawCameraEntity()));
			if (hitResult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitResult.getLocation().distanceTo(pickFrom));
			}
		}
		smoothDistanceToEye.setValue(smoothDistanceToEye.get() * minDistance / initDistance);
	}

	/**
	 * 将假相机的朝向和位置应用到真相机上
	 */
	private void applyCamera () {
		Camera camera = getRawCamera();
		float  yRot   = fakeCamera.getYRot();
		float  xRot   = fakeCamera.getXRot();
		assert !Float.isNaN(xRot + yRot);
		((CameraInvoker)camera).invokeSetRotation(yRot, xRot);
		((CameraInvoker)camera).invokeSetPosition(fakeCamera.getPosition());
	}

	private @NotNull Vector3d calculatePositionWithoutOffset () {
		return ThirdPerson.ENTITY_AGENT.getPossibleSmoothEyePosition(ThirdPersonStatus.lastPartialTick).add(LMath.directionFromRotationDegree(relativeRotation).mul(smoothDistanceToEye.get()));
	}

	private void updateSmoothVirtualDistance (double period) {
		Config           config      = ThirdPerson.getConfig();
		boolean          isAdjusting = ThirdPersonStatus.isAdjustingCameraDistance();
		CameraOffsetMode mode        = config.getCameraOffsetScheme().getMode();
		if (ThirdPersonStatus.isTransitioningToFirstPerson) {
			smoothDistanceToEye.setHalflife(config.t2f_transition_halflife);
			smoothDistanceToEye.setTarget(0);
		} else {
			smoothDistanceToEye.setHalflife(isAdjusting ? config.adjusting_distance_smooth_halflife: mode.getDistanceSmoothHalflife());
			smoothDistanceToEye.setTarget(mode.getMaxDistance());
		}
		smoothDistanceToEye.update(period);
		// 如果是非瞄准模式下，且距离过远则强行放回去
		if (!config.getCameraOffsetScheme().isAiming() && !isAdjusting) {
			smoothDistanceToEye.set(Math.min(mode.getMaxDistance(), smoothDistanceToEye.get()));
		}
		assert !Double.isNaN(smoothDistanceToEye.get());
	}

	private void updateSmoothOffsetRatio (double period) {
		Config           config = ThirdPerson.getConfig();
		CameraOffsetMode mode   = config.getCameraOffsetScheme().getMode();
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
