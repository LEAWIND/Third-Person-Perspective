package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.api.base.GameEvents;
import com.github.leawind.thirdperson.api.client.event.EntityTurnStartEvent;
import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** */
@Mixin(value = Entity.class, priority = 2000)
public class EntityMixin {
  /**
   * 实体探测方块
   *
   * <h2>原本</h2>
   *
   * 从实体眼睛出发。使用实体的{@link Entity#getViewVector}计算方向，从而计算探测终点
   *
   * <h2>Mixin 效果</h2>
   *
   * 如果实体是当前相机实体，从实体眼睛或相机出发，取决于配置。终点位于相机准星落点，略微延长一点。
   *
   * @param pickFrom 探测起点，原本是玩家眼睛位置
   * @param pickTo 探测终点，原本是玩家眼睛前方距离为 pickRange 的位置
   * @param blockShape 探测的方块类型
   * @param fluidShape 探测的流体类型
   * @param entity 探测者
   * @param pickRange 探测距离，即目标与玩家眼睛间的最大距离
   * @see GameRendererMixin
   */
  @WrapOperation(
      method = "pick",
      at = @At(value = "NEW", target = "Lnet/minecraft/world/level/ClipContext;"))
  private ClipContext wrapPick(
      Vec3 pickFrom,
      Vec3 pickTo,
      Block blockShape,
      Fluid fluidShape,
      Entity entity,
      Operation<ClipContext> original,
      @Local(argsOnly = true) double pickRange,
      @Local(argsOnly = true) float partialTick) {
    if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
      if (entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
        pickTo = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation();
        if (ThirdPersonStatus.shouldPickFromCamera()) {
          if (pickFrom.distanceTo(pickTo) > pickRange) {
            // 相机实体的眼睛到准星落点距离超过了限制
            pickTo = pickFrom;
          } else {
            pickFrom = ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition();
            var pickVector = pickFrom.vectorTo(pickTo).normalize();
            pickTo = pickTo.add(pickVector.scale(1e-4));
          }
        } else {
          var pickVector = pickFrom.vectorTo(pickTo).normalize().scale(pickRange);
          pickTo = pickFrom.add(pickVector);
        }
      }
    }
    return original.call(pickFrom, pickTo, blockShape, fluidShape, entity);
  }

  /**
   * 鼠标移动事件处理函数会调用此方法旋转玩家
   *
   * @see MouseHandler#turnPlayer()
   */
  @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
  private void preTurn(double yRot, double xRot, @NotNull CallbackInfo ci) {
    if (GameEvents.entityTurnStart != null) {
      var entity = (Entity) (Object) this;
      var event = new EntityTurnStartEvent(entity, yRot * 0.15, xRot * 0.15);
      GameEvents.entityTurnStart.accept(event);
      if (event.isDefaultCancelled()) {
        ci.cancel();
      }
    }
  }
}
