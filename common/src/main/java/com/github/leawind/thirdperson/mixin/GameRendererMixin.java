package com.github.leawind.thirdperson.mixin;


import com.github.leawind.api.base.GameEvents;
import com.github.leawind.api.client.event.MinecraftPickEvent;
import com.github.leawind.api.client.event.RenderTickStartEvent;
import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.util.annotation.VersionSensitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link GameRenderer#pick(float)} 的作用是更新{@link Minecraft#hitResult}和{@link Minecraft#crosshairPickEntity}
 * <p>
 * 在原版中它有两处调用：
 * <p>
 * 1. 在{@link Minecraft#tick()}开头，tick 完 chatListener 和 gui 之后调用
 * <p>
 * 2. 在{@link GameRenderer#renderLevel}的开头，更新完相机实体后调用
 * <p>
 * {@link GameRenderer#pick}会先调用{@link Entity#pick}探测方块，再通过{@link ProjectileUtil#getEntityHitResult}探测实体，然后计算最终探测结果
 * <p>
 * 当探测结果为空时，它会通过 {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} 创建一个表示结果为空的 BlockHitResult 对象，此时会根据玩家的朝向计算 Direction 参数。
 */
@Mixin(value=GameRenderer.class, priority=2000)
public class GameRendererMixin {
	/**
	 * 渲染tick前
	 */
	@Inject(method="render", at=@At("HEAD"))
	private void pre_render (float partialTick, long nanoTime, boolean doRenderLevel, CallbackInfo ci) {
		if (GameEvents.renderTickStart != null) {
			GameEvents.renderTickStart.accept(new RenderTickStartEvent(partialTick));
		}
	}

	/**
	 * 更新{@link Minecraft#hitResult}和{@link Minecraft#crosshairPickEntity}
	 */
	@VersionSensitive("Entity predicate")
	@Inject(method="pick", at=@At("HEAD"), cancellable=true)
	private void pick_storeViewVector (float partialTick, CallbackInfo ci) {
		if (GameEvents.minecraftPick != null) {
			var event = new MinecraftPickEvent(partialTick, 4.5);
			GameEvents.minecraftPick.accept(event);
			if (event.set()) {
				var that      = (GameRenderer)(Object)this;
				var minecraft = that.getMinecraft();
				assert minecraft.gameMode != null;
				var cameraEntity = minecraft.getCameraEntity();
				assert cameraEntity != null;
				minecraft.getProfiler().push("pick");
				minecraft.crosshairPickEntity = null;
				// pick距离，创造模式为5，否则为4.5
				double playerReach = minecraft.gameMode.getPickRange();
				// 选取方块
				minecraft.hitResult = cameraEntity.pick(playerReach, partialTick, false);
				var pickFrom = event.pickFrom();
				assert pickFrom != null;
				// 选取实体
				boolean notCreativeMode = false;
				double  dist            = playerReach;
				if (minecraft.gameMode.hasFarPickRange()) {
					// 如果当前是创造模式，则距离为6
					dist = 6.0D;
				} else if (playerReach > 3.0D) {
					// 实际上一定大于3
					notCreativeMode = true;
				}
				// dist 变成了 dist的平方
				dist *= dist;
				if (minecraft.hitResult != null) {
					// 如果pick到了方块，则更新 dist 为玩家眼睛到目标的距离平方
					dist = minecraft.hitResult.getLocation().distanceToSqr(pickFrom);
				}
				var viewVector = event.getPickVector();
				var pickTo     = event.pickTo();
				assert pickTo != null;
				// 计算可能和目标实体发生碰撞的碰撞盒
				var aabb = new AABB(pickFrom, pickTo);
				// 探测实体
				var entityHitResult = ProjectileUtil.getEntityHitResult(cameraEntity, pickFrom, pickTo, aabb, entity -> !entity.isSpectator() && entity.isPickable(), dist);
				if (entityHitResult != null) {
					var    targetEntity   = entityHitResult.getEntity();
					var    targetLocation = entityHitResult.getLocation();
					double entityDistSqr  = pickFrom.distanceToSqr(targetLocation);
					if (notCreativeMode && entityDistSqr > 9.0D) {
						// 如果不是创造模式且目标实体距离超过3
						minecraft.hitResult = BlockHitResult.miss(targetLocation, Direction.getNearest(viewVector.x, viewVector.y, viewVector.z), BlockPos.containing(targetLocation));
					} else if (entityDistSqr < dist || minecraft.hitResult == null) {
						// 如果目标实体距离小于目标方块距离，或者没探测到目标方块，则采用目标实体作为结果
						minecraft.hitResult = entityHitResult;
						if (targetEntity instanceof LivingEntity || targetEntity instanceof ItemFrame) {
							minecraft.crosshairPickEntity = targetEntity;
						}
					}
				}
				ci.cancel();
				minecraft.getProfiler().pop();
			}
		}
	}

	@ModifyVariable(method="getFov", at=@At(value="STORE"))
	private double getFov (double fov) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			return fov / ThirdPerson.CAMERA_AGENT.getSmoothFovDivisor();
		}
		return fov;
	}
}
