package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.api.base.GameEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** handleKeybinds 方法中会处理各种按键事件， 其中包括鼠标使用、攻击、选取按键 */
@Mixin(value = Minecraft.class, priority = 2000)
public class MinecraftMixin {
  /** 注入到 handleKeybinds 头部，触发相应事件 */
  @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
  private void preHandleKeybinds(CallbackInfo ci) {
    if (GameEvents.handleKeybindsStart != null) {
      GameEvents.handleKeybindsStart.run();
    }
  }
}
