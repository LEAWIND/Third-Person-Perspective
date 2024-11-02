package com.github.leawind.thirdperson.core.rotation;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.util.math.decisionmap.DecisionMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

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

		static boolean shouldRotateWithCameraWhenNotAiming () {
			return ThirdPerson.getConfig().player_rotate_with_camera_when_not_aiming;
		}

		static boolean isRotateInteracting () {
			return ThirdPerson.getConfig().auto_rotate_interacting && ThirdPerson.ENTITY_AGENT.isInteracting() && !(ThirdPerson.getConfig().do_not_rotate_when_eating && ThirdPerson.ENTITY_AGENT.isEating());
		}
	}

	private static final class Do {
		static double defaultOperation () {
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
		}

		static double swimming () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.IMPULSE_DIRECTION);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
			return 0.01D;
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

		static double withCameraNotAiming () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
			return 0D;
		}

		static double interacting () {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_HIT_RESULT);
			ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
			return 0D;
		}
	}

	public static DecisionMap<Double> build () {
		var builder = DecisionMap.<Double>builder();
		builder.factor("swimming", Factor::isSwimming);
		builder.factor("aiming", Factor::isAiming);
		builder.factor("fall_flying", Factor::isFallFlying);
		builder.factor("rotate_with_camera_when_not_aiming", Factor::shouldRotateWithCameraWhenNotAiming);
		builder.factor("rotate_interacting", Factor::isRotateInteracting);

		builder.whenDefault(Do::defaultOperation);
		builder.when("rotate_interacting", true, Do::interacting);
		builder.when("rotate_with_camera_when_not_aiming", true, Do::withCameraNotAiming);
		builder.when("fall_flying", true, Do::fallFlying);
		builder.when("swimming", true, Do::swimming);
		builder.when("aiming", true, Do::aiming);

		return builder.build();
	}
}
