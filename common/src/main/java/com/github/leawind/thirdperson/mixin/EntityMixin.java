package com.github.leawind.thirdperson.mixin;


import com.github.leawind.api.base.GameEvents;
import com.github.leawind.api.client.event.EntityTurnStartEvent;
import com.github.leawind.api.client.event.MinecraftPickEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 */
@Mixin(value=Entity.class, priority=2000)
public class EntityMixin {
	/**
	 * 探测方块
	 *
	 * @param playerReach  探测距离，目标与玩家眼睛间的最大距离
	 * @param includeFluid 是否探测液体，如果是，则使用{@link ClipContext.Fluid#ANY}，否则使用{@link ClipContext.Fluid#NONE}
	 * @see GameRendererMixin
	 */
	@Inject(method="pick", at=@At("HEAD"), cancellable=true)
	private void prePick (double playerReach, float partialTick, boolean includeFluid, CallbackInfoReturnable<HitResult> ci) {
		if (GameEvents.minecraftPick != null) {
			var entity = (Entity)(Object)this;
			var event  = new MinecraftPickEvent(partialTick, playerReach);
			GameEvents.minecraftPick.accept(event);
			if (event.set()) {
				var result = entity.level().clip(new ClipContext(event.pickFrom(), event.pickTo(), ClipContext.Block.OUTLINE, includeFluid ? ClipContext.Fluid.ANY: ClipContext.Fluid.NONE, entity));
				if (result.getType() != HitResult.Type.MISS) {
					if (result.getLocation().distanceTo(entity.getEyePosition(partialTick)) > playerReach) {
						result = BlockHitResult.miss(result.getLocation(), result.getDirection(), result.getBlockPos());
					}
				}
				ci.setReturnValue(result);
				ci.cancel();
			}
		}
	}

	/**
	 * 鼠标移动事件处理函数会调用此方法旋转玩家
	 *
	 * @see MouseHandler#turnPlayer()
	 */
	@Inject(method="turn", at=@At("HEAD"), cancellable=true)
	private void preTurn (double yRot, double xRot, @NotNull CallbackInfo ci) {
		if (GameEvents.entityTurnStart != null) {
			var entity = (Entity)(Object)this;
			var event  = new EntityTurnStartEvent(entity, yRot * 0.15, xRot * 0.15);
			GameEvents.entityTurnStart.accept(event);
			if (event.isDefaultCancelled()) {
				ci.cancel();
			}
		}
	}
}
