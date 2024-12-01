package com.github.leawind.thirdperson.core.rotation;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.util.math.decisionmap.DecisionMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

/** 玩家旋转策略 */
public final class RotateStrategy {
  public static DecisionMap<Double> build() {
    var builder = DecisionMap.<Double>builder();
    builder.factor("aiming", Factor::isAiming);
    builder.factor("swimming", Factor::isSwimming);
    builder.factor("sprint", Factor::wantToSprint);
    builder.factor("fall_flying", Factor::isFallFlying);
    builder.factor("interacting", Factor::shouldTurnToInteractPoint);
    builder.factor("force_rotate", Factor::forceRotate);
    builder.factor("is_passenger", Factor::isPassenger);
    builder.factor("is_vehicle_living_entity", Factor::isVehicleLivingEntity);

    builder.whenDefault(Do::defaultOperation);
    builder.when(List.of("is_passenger", "~is_vehicle_living_entity"), Do::ridingNonLivingEntity);
    builder.when(List.of("is_passenger", "is_vehicle_living_entity"), Do::ridingLivingEntity);
    builder.when("sprint", Do::sprint);
    builder.when("swimming", Do::swimming);
    builder.when("interacting", Do::interacting);
    builder.when("fall_flying", Do::fallFlying);
    builder.when("force_rotate", Do::defaultOperation);
    builder.when("aiming", Do::aiming);
    return builder.build();
  }

  private static final class Factor {
    static boolean isSwimming() {
      return ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isSwimming();
    }

    static boolean isAiming() {
      return ThirdPerson.ENTITY_AGENT.isAiming();
    }

    static boolean isFallFlying() {
      return ThirdPerson.ENTITY_AGENT.isFallFlying();
    }

    static boolean shouldTurnToInteractPoint() {
      return ThirdPerson.getConfig().auto_rotate_interacting
          && ThirdPerson.ENTITY_AGENT.isInteracting()
          && !(ThirdPerson.getConfig().do_not_rotate_when_eating
              && ThirdPerson.ENTITY_AGENT.isEating());
    }

    static boolean wantToSprint() {
      return Minecraft.getInstance().options.keySprint.isDown()
          || ThirdPerson.ENTITY_AGENT.isSprinting();
    }

    static boolean isPassenger() {
      return ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isPassenger();
    }

    static boolean isVehicleLivingEntity() {
      return ThirdPerson.ENTITY_AGENT.getRawCameraEntity().getVehicle() instanceof LivingEntity;
    }

    static boolean forceRotate() {
      return switch (ThirdPerson.getConfig().normal_rotate_mode) {
        case INTEREST_POINT, MOVING_DIRECTION -> false;
        default -> true;
      };
    }
  }

  private static final class Do {

    static double defaultOperation() {
      double rotateHalflife = 0;
      switch (ThirdPerson.getConfig().normal_rotate_mode) {
        case INTEREST_POINT -> {
          if (ThirdPersonStatus.impulseHorizon.length() < 1e-5) {
            ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.INTEREST_POINT);
          } else {
            ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);
          }
          ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.EXP_LINEAR);
          rotateHalflife = 0.03;
        }
        case CAMERA_CROSSHAIR -> {
          ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_HIT_RESULT);
          ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
        }
        case MOVING_DIRECTION -> {
          ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);
          ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
          rotateHalflife = 0.06;
        }
        case PARALLEL_WITH_CAMERA -> {
          ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
          ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
        }
        case NONE -> {
          ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.NONE);
          ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
        }
      }
      return rotateHalflife;
    }

    static double ridingNonLivingEntity() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.INTEREST_POINT);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.EXP_LINEAR);
      return 0.15;
    }

    static double ridingLivingEntity() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.EXP);
      return 0.1;
    }

    static double sprint() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.HORIZONTAL_IMPULSE_DIRECTION);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
      return 0.025;
    }

    static double swimming() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.IMPULSE_DIRECTION);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
      return 0.01;
    }

    static double aiming() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.PREDICTED_TARGET_ENTITY);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
      return 0D;
    }

    static double fallFlying() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.HARD);
      return 0D;
    }

    static double interacting() {
      ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_HIT_RESULT);
      ThirdPerson.ENTITY_AGENT.setRotationSmoothType(SmoothTypeEnum.LINEAR);
      return 0D;
    }
  }
}
