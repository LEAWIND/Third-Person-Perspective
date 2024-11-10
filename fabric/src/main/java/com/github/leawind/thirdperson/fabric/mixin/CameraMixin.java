package com.github.leawind.thirdperson.fabric.mixin;

import com.github.leawind.api.base.GameEvents;
import com.github.leawind.api.client.event.ThirdPersonCameraSetupEvent;
import com.github.leawind.thirdperson.mixin.CameraInvoker;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Camera.class, priority = 2000)
public class CameraMixin {
  /**
   * setup 方法中第三人称下移动相机之前
   *
   * <p>setup 方法位于真正渲染画面之前。
   *
   * <p>GameRender#render -> GameRender#renderLevel -> Camera#setup
   */
  @Inject(
      method = "setup",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/Camera;move(DDD)V",
              shift = At.Shift.BEFORE),
      cancellable = true)
  private void preMoveCamera(
      BlockGetter level,
      Entity attachedEntity,
      boolean detached,
      boolean reversedView,
      float partialTick,
      CallbackInfo ci) {
    if (GameEvents.thirdPersonCameraSetup != null) {
      var event = new ThirdPersonCameraSetupEvent(partialTick);
      GameEvents.thirdPersonCameraSetup.accept(event);
      if (event.set()) {
        var camera = (Camera) (Object) this;
        ((CameraInvoker) camera).invokeSetPosition(event.pos);
        ((CameraInvoker) camera).invokeSetRotation(event.yRot, event.xRot);
        ci.cancel();
      }
    }
  }
}
