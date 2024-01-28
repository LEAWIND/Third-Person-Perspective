package net.leawind.mc.thirdperson.impl.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.api.core.rotation.SmoothType;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateStrategy;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateTarget;
import net.leawind.mc.util.api.ItemPattern;
import net.leawind.mc.util.api.math.LMath;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.leawind.mc.util.math.decisionmap.api.DecisionMap;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothRotation;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityAgentImpl implements EntityAgent {
	private final    Minecraft           minecraft;
	private final    ExpSmoothVector3d   smoothEyePosition;
	private final    ExpSmoothRotation   smoothRotation     = ExpSmoothRotation.createWithHalflife(0.5);
	private final    DecisionMap<Double> rotateDecisionMap  = DecisionMap.of(RotateStrategy.class);
	private @NotNull RotateTarget        rotateTarget       = RotateTarget.NONE;
	private @NotNull SmoothType          smoothRotationType = SmoothType.EXP_LINEAR;
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean             wasAiming          = false;
	/**
	 * 上一个 client tick 中的 isInterecting 的值
	 */
	private          boolean             wasInterecting     = false;

	public EntityAgentImpl (@NotNull Minecraft minecraft) {
		this.minecraft = minecraft;
		{
			smoothEyePosition = new ExpSmoothVector3d();
			smoothEyePosition.setSmoothFactorWeight(ModConstants.EYE_POSITIOIN_SMOOTH_WEIGHT);
		}
		ThirdPerson.LOGGER.debug(rotateDecisionMap.toString());
	}

	@Override
	public boolean isCameraEntityExist () {
		return minecraft.cameraEntity != null;
	}

	@Override
	public void reset () {
		ThirdPerson.lastPartialTick = minecraft.getFrameTime();
		smoothEyePosition.set(getRawEyePosition(ThirdPerson.lastPartialTick));
		wasAiming      = false;
		wasInterecting = false;
	}

	@Override
	public void setRotateTarget (@NotNull RotateTarget rotateTarget) {
		this.rotateTarget = rotateTarget;
	}

	@Override
	public void setSmoothRotationType (@NotNull SmoothType smoothType) {
		smoothRotationType = smoothType;
	}

	@Override
	public void setSmoothRotationHalflife (double halflife) {
		smoothRotation.setHalflife(halflife);
	}

	@PerformanceSensitive
	@Override
	public void onPreRender (double period, float partialTick) {
		if (!isControlled()) {
			return;
		}
		Vector2d targetRotation = rotateTarget.getRotation();
		smoothRotation.setTarget(targetRotation);
		switch (smoothRotationType) {
			case HARD -> setRawRotation(targetRotation);
			case LINEAR, EXP_LINEAR -> setRawRotation(smoothRotation.get(partialTick));
			case EXP -> {
				smoothRotation.update(period);
				setRawRotation(smoothRotation.get());
			}
		}
	}

	@Override
	public void onClientTickPre () {
		final double period = 0.05;
		wasAiming      = isAiming();
		wasInterecting = isInterecting();
		updateRotateStrategy();
		smoothRotation.update(period);
		{
			// NOW 更新smoothEyePositon
			Config   config      = ThirdPerson.getConfig();
			Vector3d eyePosition = getRawEyePosition(1);
			if (isFallFlying()) {
				smoothEyePosition.setSmoothFactor(config.flying_smooth_factor);
			} else {
				config.cameraOffsetScheme.getMode().getEyeSmoothFactor(smoothEyePosition.smoothFactor);
			}
			smoothEyePosition.setTarget(eyePosition);
			smoothEyePosition.update(period);
		}
		switch (smoothRotationType) {
			case HARD, EXP -> {
			}
			case LINEAR, EXP_LINEAR -> {
				smoothRotation.setTarget(rotateTarget.getRotation());
				smoothRotation.update(period);
			}
		}
	}

	/**
	 * 更新旋转策略、平滑类型、平滑系数
	 * <p>
	 * 根据配置、游泳、飞行、瞄准等状态判断。
	 */
	private void updateRotateStrategy () {
		setSmoothRotationHalflife(rotateDecisionMap.remake());
		updateBodyRotation();
	}

	private void updateBodyRotation () {
		// TODO 侧身拉弓
		Config config = ThirdPerson.getConfig();
		if (config.auto_turn_body_drawing_a_bow && CameraAgent.isControlledCamera()) {
			assert minecraft.player != null;
			if (minecraft.player.isUsingItem() && minecraft.player.getUseItem().is(Items.BOW)) {
				double k = minecraft.player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1: -1;
				if (minecraft.options.mainHand().get() == HumanoidArm.LEFT) {
					k = -k;
				}
				minecraft.player.yBodyRot = (float)(k * 45 + minecraft.player.getYRot());
			}
		}
	}

	@Override
	public boolean isControlled () {
		return getRawPlayerEntity() == getRawCameraEntity();
	}

	@Override
	public @NotNull Entity getRawCameraEntity () {
		return Objects.requireNonNull(minecraft.cameraEntity);
	}

	@Override
	public @NotNull LocalPlayer getRawPlayerEntity () {
		return Objects.requireNonNull(minecraft.player);
	}

	@Override
	public @NotNull Vector3d getRawEyePosition (float partialTick) {
		return LMath.toVector3d(getRawCameraEntity().getEyePosition(partialTick));
	}

	@Override
	public @NotNull Vector3d getRawPosition (float partialTick) {
		return LMath.toVector3d(Objects.requireNonNull(getRawCameraEntity()).getPosition(partialTick));
	}

	@Override
	public @NotNull Vector2d getRawRotation (float partialTick) {
		Entity entity = getRawCameraEntity();
		return Vector2d.of(entity.getViewXRot(partialTick), entity.getViewYRot(partialTick));
	}

	@Override
	public @NotNull Vector3d getSmoothEyePosition (float partialTick) {
		return smoothEyePosition.get(partialTick);
	}

	/**
	 * 如果平滑系数为0，则返回完全不平滑的值
	 * <p>
	 * 如果平滑系数不为0，则采用 EXP_LINEAR 平滑
	 */
	@Override
	public @NotNull Vector3d getSmoothEyePositionDoNotKnowHowToName (float partialTick) {
		Vector3d smoothEyePositionValue = smoothEyePosition.get(partialTick);
		Vector3d smoothFactor           = smoothEyePosition.smoothFactor.copy();
		boolean  isHorizontalZero       = smoothFactor.x() * smoothFactor.z() == 0;
		boolean  isVerticalZero         = smoothFactor.y() == 0;
		if (isHorizontalZero || isVerticalZero) {
			Vector3d rawEyePosition = LMath.toVector3d(getRawCameraEntity().getEyePosition(partialTick));
			smoothEyePositionValue = Vector3d.of(isHorizontalZero ? rawEyePosition.x(): smoothEyePositionValue.x(),//
												 isVerticalZero ? rawEyePosition.y(): smoothEyePositionValue.y(),//
												 isHorizontalZero ? rawEyePosition.z(): smoothEyePositionValue.z());
		}
		return smoothEyePositionValue;
	}

	@Override
	public boolean isInterecting () {
		if (isControlled()) {
			Options options = minecraft.options;
			return options.keyUse.isDown() || options.keyAttack.isDown() || options.keyPickItem.isDown();
		} else {
			return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isUsingItem();
		}
	}

	@Override
	public boolean isFallFlying () {
		return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isFallFlying();
	}

	@Override
	public boolean isAiming () {
		Config config = ThirdPerson.getConfig();
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			if (livingEntity.isUsingItem()) {
				return ItemPattern.anyMatch(config.use_aim_item_patterns, livingEntity.getUseItem());
			} else {
				return ItemPattern.anyMatch(config.aim_item_patterns, livingEntity.getMainHandItem()) || //
					   ItemPattern.anyMatch(config.aim_item_patterns, livingEntity.getOffhandItem());
			}
		}
		return false;
	}

	@Override
	public boolean wasAiming () {
		return wasAiming;
	}

	@Override
	public boolean wasInterecting () {
		return wasInterecting;
	}

	/**
	 * 立即设置玩家朝向
	 * <p>
	 * 同时修改原始玩家实体的朝向和旧朝向
	 */
	private void setRawRotation (Vector2d rot) {
		Entity entity = getRawPlayerEntity();
		entity.setYRot(entity.yRotO = (float)rot.y());
		entity.setXRot(entity.xRotO = (float)rot.x());
	}
}