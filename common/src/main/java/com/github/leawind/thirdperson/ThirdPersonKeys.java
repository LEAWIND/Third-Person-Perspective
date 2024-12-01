package com.github.leawind.thirdperson;

import com.github.leawind.thirdperson.core.rotation.RotateTargetEnum;
import com.github.leawind.util.modkeymapping.ModKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ThirdPersonKeys {
  public static final ModKeyMapping ADJUST_POSITION =
      ModKeyMapping.of(
              getId("adjust_position"), InputConstants.KEY_Z, ThirdPersonConstants.KEY_CATEGORY)
          .onDown(ThirdPersonEvents::onStartAdjustingCameraOffset)
          .onUp(ThirdPersonEvents::onStopAdjustingCameraOffset);

  public static final ModKeyMapping FORCE_AIMING =
      ModKeyMapping.of(getId("force_aiming"), ThirdPersonConstants.KEY_CATEGORY);

  public static final ModKeyMapping TOGGLE_MOD_ENABLE =
      ModKeyMapping.of(getId("toggle_mod_enable"), ThirdPersonConstants.KEY_CATEGORY)
          .onDown(
              () -> {
                var config = ThirdPerson.getConfig();
                if (ThirdPersonStatus.isRenderingInThirdPerson()) {
                  if (config.is_mod_enabled) {
                    ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTargetEnum.CAMERA_ROTATION);
                  } else {
                    Minecraft.getInstance().gameRenderer.checkEntityPostEffect(null);
                    ThirdPersonEvents.resetPlayer();
                  }
                  config.is_mod_enabled = !config.is_mod_enabled;
                }
              });

  public static final ModKeyMapping OPEN_CONFIG_MENU =
      ModKeyMapping.of(getId("open_config_menu"), ThirdPersonConstants.KEY_CATEGORY)
          .onDown(
              () -> {
                var mc = Minecraft.getInstance();
                if (mc.screen == null) {
                  mc.setScreen(ThirdPerson.CONFIG_MANAGER.getConfigScreen(null));
                }
              });

  public static final ModKeyMapping TOGGLE_SIDE =
      ModKeyMapping.of(
              getId("toggle_side"), InputConstants.KEY_CAPSLOCK, ThirdPersonConstants.KEY_CATEGORY)
          .onDown(
              () -> {
                var scheme = ThirdPerson.getConfig().getCameraOffsetScheme();
                boolean wasCentered = scheme.isCentered();
                if (wasCentered) {
                  scheme.toNextSide();
                }
                return wasCentered;
              }) //
          .onHold(() -> ThirdPerson.getConfig().getCameraOffsetScheme().setCentered(true))
          .onPress(() -> ThirdPerson.getConfig().getCameraOffsetScheme().toNextSide());

  public static final ModKeyMapping TOGGLE_AIMING =
      ModKeyMapping.of(getId("toggle_aiming"), ThirdPersonConstants.KEY_CATEGORY)
          .onDown(
              () -> {
                if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
                  ThirdPersonStatus.isToggleToAiming = !ThirdPersonStatus.isToggleToAiming;
                }
              });

  public static final ModKeyMapping TOGGLE_PITCH_LOCK =
      ModKeyMapping.of(getId("toggle_pitch_lock"), ThirdPersonConstants.KEY_CATEGORY)
          .onDown(
              () -> {
                var config = ThirdPerson.getConfig();
                config.lock_camera_pitch_angle = !config.lock_camera_pitch_angle;
              });

  private static @NotNull String getId(@NotNull String name) {
    return "key." + ThirdPersonConstants.MOD_ID + "." + name;
  }

  public static void register() {
    ModKeyMapping.registerAll();
  }
}
