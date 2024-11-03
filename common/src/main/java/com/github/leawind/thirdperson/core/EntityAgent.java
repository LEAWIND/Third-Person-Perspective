package com.github.leawind.thirdperson.core;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import com.github.leawind.thirdperson.ThirdPersonResources;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.core.rotation.RotateStrategy;
import com.github.leawind.thirdperson.core.rotation.RotateTargetEnum;
import com.github.leawind.thirdperson.core.rotation.SmoothTypeEnum;
import com.github.leawind.util.FiniteChecker;
import com.github.leawind.util.ItemPredicateUtil;
import com.github.leawind.util.annotation.VersionSensitive;
import com.github.leawind.util.math.LMath;
import com.github.leawind.util.math.decisionmap.DecisionMap;
import com.github.leawind.util.math.smoothvalue.ExpSmoothDouble;
import com.github.leawind.util.math.smoothvalue.ExpSmoothRotation;
import com.github.leawind.util.math.vector.Vector2d;
import com.github.leawind.util.math.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EntityAgent {
	public final FiniteChecker FINITE_CHECKER = new FiniteChecker(err -> ThirdPerson.LOGGER.error(err.toString()));

	private final Minecraft minecraft;

	private final ExpSmoothRotation smoothRotation = ExpSmoothRotation.createWithHalflife(0.5);
	// private final ExpRotSmoothDouble smoothYBodyRotation = ExpRotSmoothDouble.createWithHalflife(360, 0.5);

	private final ExpSmoothDouble smoothOpacity;

	private final DecisionMap<Double> rotateDecisionMap = RotateStrategy.build();

	/**
	 * 在 clientTick 中更新
	 */
	public           double           vehicleTotalSizeCached = 1D;
	private @NotNull RotateTargetEnum rotateTarget           = RotateTargetEnum.NONE;
	private @NotNull SmoothTypeEnum   smoothRotationType     = SmoothTypeEnum.EXP_LINEAR;
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean          wasAiming              = false;

	public EntityAgent (@NotNull Minecraft minecraft) {
		this.minecraft = minecraft;

		smoothOpacity = new ExpSmoothDouble();
		smoothOpacity.set(1d);

		ThirdPerson.LOGGER.debug(rotateDecisionMap.toDescription());
	}

	/**
	 * 相机实体 {@link Minecraft#cameraEntity} 是否已经存在
	 */
	public boolean isCameraEntityExist () {
		return minecraft.cameraEntity != null;
	}

	/**
	 * 重置各种属性
	 * <p>
	 * 当初始化或进入第三人称时调用
	 */
	public void reset () {
		ThirdPerson.LOGGER.debug("Reset EntityAgent");
		smoothOpacity.set(0d);
		wasAiming = false;
	}

	@NotNull
	public RotateTargetEnum getRotateTarget () {
		return rotateTarget;
	}

	/**
	 * 设置旋转目标
	 */
	public void setRotateTarget (@NotNull RotateTargetEnum rotateTarget) {
		this.rotateTarget = rotateTarget;
	}

	public @NotNull SmoothTypeEnum getRotationSmoothType () {
		return smoothRotationType;
	}

	/**
	 * 设置平滑类型
	 * <p>
	 * 在 clientTick 和 renderTick 中要根据平滑类型采用不同的处理方式
	 */
	public void setRotationSmoothType (@NotNull SmoothTypeEnum smoothType) {
		smoothRotationType = smoothType;
	}

	/**
	 * 设置平滑转向的半衰期
	 */
	@SuppressWarnings("unused")
	public void setSmoothRotationHalflife (double halflife) {
		smoothRotation.setHalflife(halflife);
	}

	/**
	 * 获取相机实体不透明度
	 */
	public float getSmoothOpacity (float partialTick) {
		return smoothOpacity.get(partialTick).floatValue();
	}

	public ExpSmoothRotation getSmoothRotation () {
		return smoothRotation;
	}

	/**
	 * @param period 相邻两次 render tick 的时间差，单位：s
	 */
	@SuppressWarnings("unused")
	@PerformanceSensitive
	public void onRenderTickStart (double now, double period, float partialTick) {
		if (ThirdPersonStatus.isRenderingInThirdPerson() && isControlled() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			var targetRotation = getRotateTarget().getRotation(partialTick);

			smoothRotation.setTarget(targetRotation);

			switch (smoothRotationType) {
				case HARD -> setRawRotation(targetRotation);
				case LINEAR, EXP_LINEAR -> setRawRotation(smoothRotation.get(partialTick));
				case EXP -> {
					smoothRotation.update(period);
					setRawRotation(smoothRotation.get());
				}
				default -> throw new IllegalStateException("Invalid smooth rotation type: " + smoothRotationType);
			}
		}
	}

	public void onClientTickStart () {
		if (ThirdPersonStatus.clientTicks % 2 == 0) {
			vehicleTotalSizeCached = getVehicleTotalSize();
		}
		ThirdPersonStatus.clientTicks++;

		wasAiming = isAiming();
		ThirdPerson.getConfig().getCameraOffsetScheme().setAiming(wasAiming());

		updateRotateStrategy();
		updateBodyRotation();
		updateSmoothOpacity(ThirdPersonConstants.VANILLA_CLIENT_TICK_TIME, 1);

		switch (smoothRotationType) {
			case HARD, EXP -> {
			}
			case LINEAR, EXP_LINEAR -> {
				smoothRotation.setTarget(getRotateTarget().getRotation(1));
				smoothRotation.update(ThirdPersonConstants.VANILLA_CLIENT_TICK_TIME);
			}
		}
	}

	/**
	 * 设置实体朝向
	 */
	public void setRawRotation (@NotNull Vector2d rot) {
		FINITE_CHECKER.checkOnce(rot.x(), rot.y());
		var entity = getRawPlayerEntity();

		entity.setYRot(entity.yRotO = (float)rot.y());
		entity.setXRot(entity.xRotO = (float)rot.x());
	}

	/**
	 * 玩家当前是否在操控这个实体
	 */
	public boolean isControlled () {
		return getRawPlayerEntity() == minecraft.cameraEntity;
	}

	/**
	 * 获取相机附着的实体
	 *
	 * @see EntityAgent#isCameraEntityExist
	 */
	public @NotNull Entity getRawCameraEntity () {
		return Objects.requireNonNull(minecraft.cameraEntity);
	}

	/**
	 * 获取玩家实体
	 */
	public @NotNull LocalPlayer getRawPlayerEntity () {
		return Objects.requireNonNull(minecraft.player);
	}

	/**
	 * 直接从相机实体获取眼睛坐标
	 */
	public @NotNull Vector3d getRawEyePosition (float partialTick) {
		return LMath.toVector3d(getRawCameraEntity().getEyePosition(partialTick));
	}

	/**
	 * 直接从实体获取朝向
	 */
	@VersionSensitive
	public @NotNull Vector2d getRawRotation (float partialTick) {
		var entity = getRawCameraEntity();
		return Vector2d.of(entity.getViewXRot(partialTick), entity.getViewYRot(partialTick));
	}

	/**
	 * 实体的眼睛是否在墙里
	 * <p>
	 * 与{@link Entity#isInWall()}不同的是，旁观者模式下此方法仍然可以返回true
	 */
	public boolean isEyeInWall (@NotNull ClipContext.ShapeGetter shapeGetter) {
		var cameraEntity = getRawCameraEntity();
		var eyePos       = cameraEntity.getEyePosition();
		var blockPos     = BlockPos.containing(eyePos.x(), eyePos.y(), eyePos.z());
		var blockState   = cameraEntity.level().getBlockState(blockPos);
		var eyeAabb      = AABB.ofSize(eyePos, 0.8, 1e-6, 0.8);
		return shapeGetter.get(blockState, cameraEntity.level(), blockPos, CollisionContext.empty()).toAabbs().stream().anyMatch(a -> a.move(blockPos).intersects(eyeAabb));
	}

	/**
	 * 实体是否在交互
	 * <p>
	 * 当控制玩家时，相当于是否按下了 使用|攻击|选取 键
	 * <p>
	 * 当附身其他实体时，另做判断
	 */
	@VersionSensitive
	public boolean isInteracting () {
		if (!isControlled()) {
			return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isUsingItem();
		}
		var options = minecraft.options;
		return options.keyUse.isDown() || options.keyAttack.isDown() || options.keyPickItem.isDown();
	}

	/**
	 * 实体是否在飞行
	 */
	@VersionSensitive
	public boolean isFallFlying () {
		return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isFallFlying();
	}

	/**
	 * 实体是否在奔跑
	 */
	public boolean isSprinting () {
		return getRawCameraEntity().isSprinting();
	}

	/**
	 * 正在吃食物
	 * <p>
	 * 使用 {@link ItemStack#isEdible()} 判断是否是食物
	 */
	public boolean isEating () {
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			return livingEntity.getUseItem().isEdible();
		}
		return false;
	}

	/**
	 * 根据以下因素判断是否在瞄准
	 * <li>是否在使用物品</li>
	 * <li>实体拿着的物品</li>
	 * <li>按键</li>
	 * <li>使用物品时正在播放的动画</li>
	 */
	public boolean isAiming () {
		var config = ThirdPerson.getConfig();
		if (ThirdPersonStatus.doesPlayerWantToAim()) {
			return true;
		}
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			if (config.determine_aim_mode_by_animation && livingEntity.isUsingItem()) {
				var anim = livingEntity.getUseItem().getUseAnimation();
				if (anim == UseAnim.BOW || anim == UseAnim.SPEAR) {
					return true;
				}
			}

			boolean shouldBeAiming = ItemPredicateUtil.anyMatches(livingEntity.getMainHandItem(), config.getHoldToAimItemPredicates(), ThirdPersonResources.itemPredicateManager.holdToAimItemPredicates) || //
									 ItemPredicateUtil.anyMatches(livingEntity.getOffhandItem(), config.getHoldToAimItemPredicates(), ThirdPersonResources.itemPredicateManager.holdToAimItemPredicates);

			if (livingEntity.isUsingItem()) {
				shouldBeAiming |= ItemPredicateUtil.anyMatches(livingEntity.getUseItem(), config.getUseToAimItemPredicates(), ThirdPersonResources.itemPredicateManager.useToAimItemPredicates);
			}
			return shouldBeAiming;
		}
		return false;
	}

	/**
	 * 在上一个 clientTick 中是否在瞄准
	 */
	public boolean wasAiming () {
		return wasAiming;
	}

	public AABB getBoundingBox (float partialTick) {
		var entity = this.getRawCameraEntity();
		return entity.getDimensions(entity.getPose()).makeBoundingBox(entity.getPosition(partialTick));
	}

	/**
	 * 计算点到碰撞箱的距离。如果点在碰撞箱内，则返回0
	 */
	public double boxDistanceTo (@NotNull Vector3d target, float partialTick) {
		var aabb = getBoundingBox(partialTick);
		var c    = Vector3d.of();

		c.x(LMath.clamp(target.x(), aabb.minX, aabb.maxX));
		c.y(LMath.clamp(target.y(), aabb.minY, aabb.maxY));
		c.z(LMath.clamp(target.z(), aabb.minZ, aabb.maxZ));

		return c.distance(target);
	}

	public double getBodyRadius () {
		return getRawCameraEntity().getBbWidth() * 0.8660254037844386; // 0.5 * sqrt(3)
	}

	public double columnDistanceTo (@NotNull Vector3d target, float partialTick) {
		var entity = getRawCameraEntity();

		var    c    = LMath.toVector3d(entity.getPosition(partialTick));
		double maxY = c.y() + entity.getEyeHeight();
		c.y(LMath.clamp(target.y(), c.y(), maxY));
		double dist = c.distance(target);
		if (maxY > target.y() && target.y() > c.y()) {
			dist = Math.max(0, dist - getBodyRadius());
		}
		return dist;
	}

	/**
	 * 更新旋转策略、平滑类型、平滑系数
	 * <p>
	 * 根据配置、游泳、飞行、瞄准等状态判断。
	 */
	private void updateRotateStrategy () {
		setSmoothRotationHalflife(rotateDecisionMap.updateAll().make());
	}

	/**
	 * TODO 接管身体的转动
	 * <p>
	 * 脖子最多左右转85度
	 *
	 * @see net.minecraft.client.renderer.entity.LivingEntityRenderer#render
	 */
	private void updateBodyRotation () {

		//  if (getRotateTarget() == RotateTargetEnum.INTEREST_POINT) {
		//  	if (ThirdPersonStatus.impulseHorizon.length() >= 1e-5) {
		//  		var rot    = RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION.getRotation(1);
		//  	}
		//  }

		// net.minecraft.client.renderer.entity.LivingEntityRenderer.render
		var config = ThirdPerson.getConfig();
		if (config.auto_turn_body_drawing_a_bow && ThirdPerson.ENTITY_AGENT.isControlled()) {
			var player = getRawPlayerEntity();
			if (player.isUsingItem() && player.getUseItem().is(Items.BOW)) {
				double k = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1: -1;
				if (minecraft.options.mainHand().get() == HumanoidArm.LEFT) {
					k = -k;
				}
				player.yBodyRot = (float)(k * 45 + player.getYRot());
			}
		}
	}

	/**
	 * 更新平滑的透明度
	 */
	private void updateSmoothOpacity (double period, float partialTick) {
		double targetOpacity = 1.0;
		var    config        = ThirdPerson.getConfig();
		if (config.player_fade_out_enabled) {
			final double C              = ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION * 2;
			var          cameraPosition = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition());
			double       distance       = getRawEyePosition(partialTick).distance(cameraPosition);

			targetOpacity = (distance - C) / (1 - C);
			FINITE_CHECKER.checkOnce(targetOpacity);

			if (targetOpacity > config.gaze_opacity && !isFallFlying() && ThirdPerson.CAMERA_AGENT.isLookingAt(getRawCameraEntity())) {
				targetOpacity = config.gaze_opacity;
			}
		}

		smoothOpacity.setTarget(LMath.clamp(targetOpacity, 0, 1));
		smoothOpacity.setHalflife(ThirdPersonConstants.OPACITY_HALFLIFE * (wasAiming() ? 0.25: 1));
		smoothOpacity.update(period);
	}

	/**
	 * 当相机在身后时，兴趣点是准星指向的目标点
	 * <p>
	 * 当相机在面前时，兴趣点是相机
	 */
	public @Nullable Vec3 getInterestPoint () {
		if (LMath.subtractDegrees(getRawPlayerEntity().yBodyRot, ThirdPerson.CAMERA_AGENT.getRelativeRotation().y()) > 90) {
			return ThirdPerson.CAMERA_AGENT.getHitResult().getLocation();
		} else {
			return LMath.toVec3(ThirdPerson.CAMERA_AGENT.getRawCameraPosition());
		}
	}

	public double getVehicleTotalSize () {
		var root = getRawCameraEntity().getRootVehicle();
		var bb   = root.getPassengersAndSelf().map(Entity::getBoundingBox).reduce(AABB::minmax).orElse(root.getBoundingBox());
		return Math.hypot(Math.hypot(bb.getXsize(), bb.getYsize()), bb.getZsize());
	}
}
