package com.github.leawind.thirdperson;

import com.github.leawind.thirdperson.core.rotation.RotateTargetEnum;
import com.github.leawind.thirdperson.core.rotation.SmoothTypeEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;

public final class ThirdPersonStatus {
  /** 移动脉冲 */
  public static final @NotNull Vector3d impulse = new Vector3d(0);

  /** 移动脉冲的水平分量 */
  public static final @NotNull Vector2d impulseHorizon = new Vector2d(0);

  public static int clientTicks = 0;

  /**
   * @see ThirdPersonKeys#TOGGLE_AIMING
   */
  public static boolean isToggleToAiming = false;

  public static double lastRenderTickTimeStamp = 0;

  /** 上一tick中是否以第三人称视角渲染 mc.options.cameraType.isThirdPerson() */
  public static boolean wasRenderInThirdPersonLastRenderTick = false;

  /**
   * 在 ThirdPersonEvents#onPreRender 中更新
   *
   * @see ThirdPersonStatus#shouldCameraTurnWithEntity
   */
  public static boolean wasShouldCameraTurnWithEntity = false;

  /** 自上次离开狭窄空间以来经过的 tick 数 */
  public static int ticksSinceLeaveNarrowSpace =
      ThirdPersonConstants.LEAVE_NARROW_SPACE_DELAY_TICKS;

  /** 是否正在调整摄像机偏移量 */
  public static boolean isAdjustingCameraOffset() {
    return isAdjustingCameraDistance();
  }

  /** 检查相机距离是否正在调整。 */
  public static boolean isAdjustingCameraDistance() {
    return ThirdPerson.isAvailable()
        && isRenderingInThirdPerson()
        && ThirdPersonKeys.ADJUST_POSITION.isDown();
  }

  /**
   * 当前是否以第三人称渲染
   *
   * <p>当前相机模式既不是第一人称，也不是镜像的
   */
  public static boolean isRenderingInThirdPerson() {
    var cameraType = Minecraft.getInstance().options.getCameraType();
    return !(cameraType.isFirstPerson() || cameraType.isMirrored());
  }

  /** 当前是否显示准星 */
  public static boolean shouldRenderThirdPersonCrosshair() {
    var config = ThirdPerson.getConfig();
    return ThirdPerson.isAvailable()
        && isRenderingInThirdPerson()
        && (ThirdPerson.ENTITY_AGENT.wasAiming()
            ? config.render_crosshair_when_aiming
            : config.render_crosshair_when_not_aiming
                && (!(ThirdPerson.ENTITY_AGENT.isFallFlying()
                    && config.hide_crosshair_when_flying)));
  }

  /** 根据玩家的按键判断玩家是否想瞄准 */
  public static boolean doesPlayerWantToAim() {
    return isToggleToAiming || ThirdPersonKeys.FORCE_AIMING.isDown();
  }

  /** 探测射线是否应当起始于相机处，而非玩家眼睛处 */
  public static boolean shouldPickFromCamera() {
    if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
      return false;
    } else if (!ThirdPerson.getConfig().use_camera_pick_in_creative) {
      return false;
    }
    return ThirdPerson.ENTITY_AGENT.getRawCameraEntity() instanceof Player player
        && player.isCreative();
  }

  /**
   * 是否渲染相机实体
   *
   * <p>当透明度小于阈值，或相机距离实体太近时，不渲染相机实体
   *
   * @return 是否渲染相机实体
   */
  public static boolean shouldRenderCameraEntity(float partialTick) {
    return ThirdPerson.ENTITY_AGENT.getSmoothOpacity(partialTick)
            > ThirdPersonConstants.RENDERED_OPACITY_THRESHOLD_MIN
        && ThirdPerson.ENTITY_AGENT.columnDistanceTo(
                ThirdPerson.CAMERA_AGENT.getRawCameraPosition(), partialTick)
            > ThirdPerson.getConfig().player_invisible_threshold;
  }

  /**
   * 是否按照透明度渲染相机实体
   *
   * <p>当透明度大于 {@link ThirdPersonConstants#RENDERED_OPACITY_THRESHOLD_MAX} 时，返回false
   *
   * @return 是否按照透明度渲染相机实体
   */
  public static boolean useCameraEntityOpacity(float partialTick) {
    return ThirdPerson.ENTITY_AGENT.getSmoothOpacity(partialTick)
        < ThirdPersonConstants.RENDERED_OPACITY_THRESHOLD_MAX;
  }

  /**
   * 第三人称下，通常是直接用鼠标控制相机的朝向 CameraAgentImpl#relativeRotation，再根据一些因素决定玩家的朝向。
   *
   * <p>当飞行时，实体的旋转目标是相机朝向，且平滑类型是 {@link SmoothTypeEnum#HARD}，相当于鼠标直接控制玩家朝向，而相机跟随玩家旋转。这样就可以兼容 Do a
   * Barrel Roll
   */
  public static boolean shouldCameraTurnWithEntity() {
    return ThirdPerson.ENTITY_AGENT.getRotateTarget() == RotateTargetEnum.CAMERA_ROTATION
        && ThirdPerson.ENTITY_AGENT.getRotationSmoothType() == SmoothTypeEnum.HARD;
  }

  /** 实体是否位于狭窄空间内 */
  public static boolean calcIsInNarrowSpace(Entity entity) {
    boolean isInNarrowSpace = true;
    var center = BlockPos.containing(entity.getEyePosition(1));
    ThirdPersonConstants.SURROUNDINGS_MATCHING.rematch(
        center, entity.level(), s -> s.isViewBlocking(entity.level(), center));

    int countT = ThirdPersonConstants.SURROUNDINGS_MATCHING.getMatches("T").count();
    int countM = ThirdPersonConstants.SURROUNDINGS_MATCHING.getMatches("M").count();

    isInNarrowSpace &= countT >= 3;
    isInNarrowSpace &= countM >= 1;
    return isInNarrowSpace;
  }
}
