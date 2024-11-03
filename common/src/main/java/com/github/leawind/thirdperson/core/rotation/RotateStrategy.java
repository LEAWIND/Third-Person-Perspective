package com.github.leawind.thirdperson.core.rotation;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.util.math.decisionmap.DecisionMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * 玩家旋转策略
 */
public final class RotateStrategy {
	private static final class Factor {
		static boolean isSwimming () {
			return ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isSwimming();
		}

		static boolean isAiming () {
			return ThirdPerson.ENTITY_AGENT.isAiming();
		}

		static boolean isFallFlying () {
			return ThirdPerson.ENTITY_AGENT.isFallFlying();
		}

		static boolean shouldTurnToInteractPoint () {
			return ThirdPerson.getConfig().auto_rotate_interacting && ThirdPerson.ENTITY_AGENT.isInteracting() && !(ThirdPerson.getConfig().do_not_rotate_when_eating && ThirdPerson.ENTITY_AGENT.isEating());
		}

		static boolean wantToSprint () {
			return Minecraft.getInstance().options.keySprint.isDown() || ThirdPerson.ENTITY_AGENT.isSprinting();
		}

		static boolean isPassenger () {
			return ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isPassenger();
		}

		static boolean isVehicleLivingEntity () {
			return ThirdPerson.ENTITY_AGENT.getRawCameraEntity().getVehicle() instanceof LivingEntity;
		}

		static boolean isRidingNonLivingEntity () {
			var entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			return entity.isPassenger() && !(entity.getVehicle() instanceof LivingEntity);
		}
	}

	public static DecisionMap<Double> build () {
		var builder = DecisionMap.<Double>builder();
		builder.factor(0, "swimming", Factor::isSwimming);
		builder.factor(1, "aiming", Factor::isAiming);
		builder.factor(3, "interacting", Factor::shouldTurnToInteractPoint);
		builder.factor(4, "sprint", Factor::wantToSprint);
		builder.factor(2, "fall_flying", Factor::isFallFlying);
		builder.factor(5, "is_passenger", Factor::isPassenger);
		builder.factor(6, "is_vehicle_living_entity", Factor::isVehicleLivingEntity);

		// Define rules
		builder.whenDefault(Do::defaultOperation);
		builder.when("interacting", Do::interacting);
		builder.when(List.of("is_passenger", "~is_vehicle_living_entity"), Do::ridingNonLivingEntity);
		builder.when(List.of("is_passenger", "is_vehicle_living_entity"), Do::ridingLivingEntity);
		builder.when("sprint", Do::sprint);
		builder.when("fall_flying", Do::fallFlying);
		builder.when("swimming", Do::swimming);
		builder.when("aiming", Do::aiming);
		return builder.build();
	}

	private static final class Do {

		static double defaultOperation () {
			double rotateHalflife = 0;
			switch (ThirdPerson.getConfig().normal_rotate_mode) {
				case INTEREST_POINT -> {
					ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.INTEREST_POINT);
					ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.EXP_LINEAR);
					rotateHalflife = 0.1;
				}
				case CAMERA_CROSSHAIR -> {
					ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_HIT_RESULT);
					ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
				}
				case PARALLEL_WITH_CAMERA -> {
					ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
					ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
				}
				case STAY -> {
				}
			}
			if (ThirdPersonStatus.impulseHorizon.length() >= 1e-5) {
				ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);
			}
			return rotateHalflife;
		}

		static double ridingNonLivingEntity () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.INTEREST_POINT);//TODO
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.EXP_LINEAR);
			return 0.15;
		}

		static double ridingLivingEntity () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);//TODO
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.EXP);
			return 0.1;
		}

		static double sprint () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
			return 0.04;
		}

		static double swimming () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.IMPULSE_DIRECTION);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
			return 0.01;
		}

		static double aiming () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.PREDICTED_TARGET_ENTITY);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
			return 0D;
		}

		static double fallFlying () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
			return 0D;
		}

		static double interacting () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_HIT_RESULT);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
			return 0D;
		}
	}
}
