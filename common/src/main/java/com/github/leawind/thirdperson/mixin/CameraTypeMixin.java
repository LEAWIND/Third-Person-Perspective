package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.ThirdPerson;
import net.minecraft.client.CameraType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CameraType.class, priority = 2000)
public class CameraTypeMixin {
  /**
   * 这个字段的含义是当前目标是否为第一人称，或者说玩家想要通过按键切换到的是否为第一人称
   *
   * <p>通过 {@link CameraType#cycle()} 可以更改此字段，其他模组也可以通过直接设置此字段来切换视角。
   *
   * <p>注意：即使此字段为true，即目标是第一人称，也未必以第一人称渲染。因为从第三人称过渡到第一人称可能是一个平滑连续的过程。
   */
  @Final @Shadow private boolean firstPerson;

  /**
   * 应用此 Mixin 后，此方法的语义变为：
   *
   * <p>要么当前目标为第一人称且 {@link ThirdPersonStatus#isPerspectiveInverted} 为 false，要么当前为第三人称且 {@link
   * ThirdPersonStatus#isPerspectiveInverted} 为 true。
   *
   * @see CameraTypeMixin#firstPerson
   * @see ThirdPersonStatus#isPerspectiveInverted
   */
  @Inject(method = "isFirstPerson", at = @At("RETURN"), cancellable = true)
  private void isFirstPerson(@NotNull CallbackInfoReturnable<Boolean> ci) {
    if (ThirdPerson.isAvailable()) {
      ci.setReturnValue(firstPerson ^ ThirdPersonStatus.isPerspectiveInverted);
      ci.cancel();
    }
  }

  /** 根据配置决定是否跳过原版的“盯着镜头”视角，（有人称其为第二人称视角） */
  @Inject(method = "cycle", at = @At("RETURN"), cancellable = true)
  private void modifyCycle(CallbackInfoReturnable<CameraType> ci) {
    var config = ThirdPerson.getConfig();
    if (config.is_mod_enabled && config.skip_vanilla_second_person_camera) {
      var that = (CameraType) (Object) this;
      if (that != CameraType.FIRST_PERSON) {
        ci.setReturnValue(CameraType.FIRST_PERSON);
        ci.cancel();
      }
    }
  }
}
