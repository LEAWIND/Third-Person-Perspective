package com.github.leawind.thirdperson.core.rotation;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.core.CameraAgent;
import com.github.leawind.thirdperson.util.math.LMath;
import java.util.function.Function;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

/** 旋转目标，即玩家应该转向何处 */
public enum RotateTargetEnum {
  /** 保持当前朝向，不旋转 */
  NONE(partialTick -> ThirdPerson.ENTITY_AGENT.getRawRotation(partialTick)),
  /**
   * 兴趣点
   *
   * <p>当相机位于后方时，玩家看向准星处，相机位于前方时，玩家看向相机
   */
  INTEREST_POINT(
      partialTick -> {
        var point = ThirdPerson.ENTITY_AGENT.getInterestPoint();
        if (point == null) {
          return NONE.getRotation(partialTick);
        }

        var player = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity();

        var toInterestedPoint = point.subtract(player.getEyePosition(partialTick));
        if (toInterestedPoint.length() < 1e-5) {
          return NONE.getRotation(partialTick);
        }

        var playerRot = ThirdPerson.ENTITY_AGENT.getRawRotation(1);
        ThirdPerson.FINITE_CHECKER.checkOnce(playerRot.x, playerRot.y);

        var rot = LMath.rotationDegreeFromDirection(LMath.toVector3d(toInterestedPoint));
        ThirdPerson.FINITE_CHECKER.checkOnce(rot.x, rot.y);

        double leftBound =
            player.yBodyRot - ThirdPersonConstants.VANILLA_PLAYER_HEAD_ROTATE_LIMIT_DEGREES;
        double rightBound =
            player.yBodyRot + ThirdPersonConstants.VANILLA_PLAYER_HEAD_ROTATE_LIMIT_DEGREES;

        if (LMath.isWithinDegrees(rot.y, leftBound, rightBound)) {
          playerRot.y = rot.y;
        } else {
          playerRot.y =
              LMath.subtractDegrees(rot.y, leftBound) < LMath.subtractDegrees(rot.y, rightBound)
                  ? leftBound
                  : rightBound;
        }

        playerRot.x = rot.x;
        return playerRot;
      }),
  /** 与相机朝向相同 */
  CAMERA_ROTATION(partialTick -> ThirdPerson.CAMERA_AGENT.getRotation()),
  /** 转向相机的视线落点，即准星所指的位置 */
  CAMERA_HIT_RESULT(
      partialTick -> {
        var cameraHitResult = ThirdPerson.CAMERA_AGENT.getHitResult();
        if (cameraHitResult.getType() == Type.MISS) {
          return CAMERA_ROTATION.getRotation(partialTick);
        } else {
          var cameraHitPosition = LMath.toVector3d(cameraHitResult.getLocation());
          var eyePosition = ThirdPerson.ENTITY_AGENT.getRawEyePosition(partialTick);
          return LMath.rotationDegreeFromDirection(cameraHitPosition.sub(eyePosition));
        }
      }),
  /**
   * 预测玩家想射击的目标实体
   *
   * <p>玩家将朝向的目标点为 相机位置 + 相机射线单位向量*目标实体距离
   *
   * <p>这样在射击远处的实体时，就不需要考虑玩家视线与相机视线间的偏移量了。
   *
   * <p>但是问题在于，当周围有许多实体时，对目标实体的预测可能不准确。
   *
   * @see CameraAgent#predictTargetEntity(float)
   */
  PREDICTED_TARGET_ENTITY(
      partialTick -> {
        var rotation = CAMERA_HIT_RESULT.getRotation(partialTick);

        if (!ThirdPerson.getConfig().enable_target_entity_predict
            || !ThirdPerson.ENTITY_AGENT.isControlled()) {
          return rotation;
        }

        var predicted = ThirdPerson.CAMERA_AGENT.predictTargetEntity(partialTick);

        if (predicted == null) {
          return rotation;
        }

        var camera = ThirdPerson.CAMERA_AGENT.getRawCamera();
        var playerEyePos = ThirdPerson.ENTITY_AGENT.getRawEyePosition(partialTick);
        var cameraPos = LMath.toVector3d(camera.getPosition());
        var targetPos = LMath.toVector3d(predicted.getPosition(partialTick));
        var end =
            LMath.toVector3d(camera.getLookVector())
                .normalize(cameraPos.distance(targetPos))
                .add(cameraPos);
        var eyeToEnd = end.sub(playerEyePos);

        if (eyeToEnd.length() < 1e-5) {
          return rotation;
        }

        return LMath.rotationDegreeFromDirection(eyeToEnd);
      }),
  /**
   * 使用键盘控制的移动方向
   *
   * <p>当没有使用键盘控制时保持当前朝向
   */
  IMPULSE_DIRECTION(
      partialTick ->
          ThirdPersonStatus.impulseHorizon.length() < 1e-5
              ? NONE.getRotation(partialTick)
              : LMath.rotationDegreeFromDirection(ThirdPersonStatus.impulse)),
  /**
   * 使用键盘控制的移动方向（仅水平）
   *
   * <p>当没有使用键盘控制时保持当前朝向
   */
  HORIZONTAL_IMPULSE_DIRECTION(
      partialTick -> {
        if (ThirdPersonStatus.impulseHorizon.length() < 1e-5) {
          return NONE.getRotation(partialTick);
        }
        double absoluteYRotDegree =
            LMath.rotationDegreeFromDirection(ThirdPersonStatus.impulseHorizon);
        return new Vector2d(0.1, absoluteYRotDegree);
      });

  private final Function<Float, Vector2d> rotationGetter;

  RotateTargetEnum(@NotNull Function<Float, Vector2d> rotationGetter) {
    this.rotationGetter = rotationGetter;
  }

  /** 获取玩家当前的目标朝向 */
  public @NotNull Vector2d getRotation(float partialTick) {
    var rotation = rotationGetter.apply(partialTick);
    ThirdPerson.FINITE_CHECKER.checkOnce(rotation.x, rotation.y);
    return rotation;
  }
}
