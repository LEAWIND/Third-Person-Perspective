package com.github.leawind.thirdperson.mixin;

import com.github.leawind.api.base.GameEvents;
import com.github.leawind.api.client.event.MouseTurnPlayerStartEvent;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class, priority = 2000)
public class MouseHandlerMixin {
  @Shadow private double accumulatedDX;
  @Shadow private double accumulatedDY;

  /**
   * 在根据鼠标位移转动玩家前触发
   *
   * <p>如果在事件处理函数中调用了{@link MouseTurnPlayerStartEvent#cancelDefault()}，则后续处理将会取消，好像鼠标没有移动一样。
   */
  @Inject(method = "turnPlayer()V", at = @At(value = "HEAD"), cancellable = true)
  private void preTurnPlayer(CallbackInfo ci) {
    if (GameEvents.mouseTurnPlayerStart != null) {
      var event = new MouseTurnPlayerStartEvent(accumulatedDX, accumulatedDY);
      GameEvents.mouseTurnPlayerStart.accept(event);
      if (event.isDefaultCancelled()) {
        // 重置累积变化量
        accumulatedDX = 0;
        accumulatedDY = 0;
        ci.cancel();
      }
    }
  }
}
