package com.github.leawind.thirdperson.core.rotation;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.util.math.decisionmap.api.DecisionFactor;
import com.github.leawind.util.math.decisionmap.api.DecisionMap;
import com.github.leawind.util.math.decisionmap.api.anno.ADecisionFactor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * 玩家旋转策略
 */
public interface RotateStrategy {
	@ADecisionFactor DecisionFactor is_swimming                               = DecisionFactor.of(() -> ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isSwimming());
	@ADecisionFactor DecisionFactor is_aiming                                 = DecisionFactor.of(() -> ThirdPerson.ENTITY_AGENT.isAiming());
	@ADecisionFactor DecisionFactor is_fall_flying                            = DecisionFactor.of(() -> ThirdPerson.ENTITY_AGENT.isFallFlying());
	@ADecisionFactor DecisionFactor should_rotate_with_camera_when_not_aiming = DecisionFactor.of(() -> ThirdPerson.getConfig().player_rotate_with_camera_when_not_aiming);
	@ADecisionFactor DecisionFactor rotate_interacting                        = DecisionFactor.of(() -> ThirdPerson.getConfig().auto_rotate_interacting && ThirdPerson.ENTITY_AGENT.isInteracting() &&
																										!(ThirdPerson.getConfig().do_not_rotate_when_eating && ThirdPerson.ENTITY_AGENT.isEating()));

	/**
	 * 默认策略：移动时转向前进方向，静止时不旋转
	 */
	Supplier<Double> DEFAULT                = () -> {
		var entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();

		var rotateTarget = ThirdPerson.getConfig().rotate_to_moving_direction && (!entity.isPassenger() || entity.getVehicle() instanceof LivingEntity)   //
						   ? RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION    //
						   : RotateTargetEnum.DEFAULT;
		ThirdPerson.ENTITY_AGENT.setRotateTarget(rotateTarget);

		var smoothType = Minecraft.getInstance().options.keySprint.isDown() || ThirdPerson.ENTITY_AGENT.isSprinting()    //
						 ? SmoothTypeEnum.HARD    //
						 : SmoothTypeEnum.EXP_LINEAR;
		ThirdPerson.ENTITY_AGENT.setRotationSmoothType(smoothType);
		return 0.1D;
	};
	Supplier<Double> SWIMMING               = () -> {
		ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.IMPULSE_DIRECTION);
		ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
		return 0.01D;
	};
	Supplier<Double> AIMING                 = () -> {
		ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.PREDICTED_TARGET_ENTITY);
		ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
		return 0D;
	};
	Supplier<Double> FALL_FLYING            = () -> {
		ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
		ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
		return 0D;
	};
	Supplier<Double> WITH_CAMERA_NOT_AIMING = () -> {
		ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
		ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
		return 0D;
	};
	Supplier<Double> INTERACTING            = () -> {
		ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_HIT_RESULT);
		ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
		return 0D;
	};

	/**
	 * 将通过反射调用此方法
	 * <p>
	 * {@link DecisionMap#of(Class)}
	 */
	@SuppressWarnings("unused")
	static void build (@NotNull DecisionMap<Double> map) {
		ThirdPerson.LOGGER.debug("Building Rotate Strategy");
		map.addRule(0, 0, DEFAULT)    //
		   .addRule(~0, rotate_interacting.mask(), INTERACTING)    //
		   .addRule(~0, should_rotate_with_camera_when_not_aiming.mask(), WITH_CAMERA_NOT_AIMING)    //
		   .addRule(~0, is_fall_flying.mask(), FALL_FLYING)    //
		   .addRule(~0, is_swimming.mask(), SWIMMING)    //
		   .addRule(~0, is_aiming.mask(), AIMING)    //
		;
	}
}
