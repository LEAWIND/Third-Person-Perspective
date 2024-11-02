package com.github.leawind.thirdperson.mixin;


import com.github.leawind.api.base.GameEvents;
import com.github.leawind.api.client.event.RenderTickStartEvent;
import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

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
public abstract class GameRendererMixin {

	@Shadow @Final         Minecraft minecraft;
	@Final @Shadow private Camera    mainCamera;

	/**
	 * 修改探测实体的起点和终点
	 *
	 * @param pickFrom     探测起点，原本是玩家眼睛位置
	 * @param pickTo       探测终点，原本是玩家眼睛前方距离为 pickRange 的位置
	 * @param aabb         碰撞箱，只有与它有交点的实体才会被考虑
	 * @param predicate    目标实体谓词
	 * @param pickRangeSqr 探测距离上限的平方
	 */
	@WrapOperation(method="pick", at=@At(value="INVOKE",
										 target="Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;" +
												"Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
	private EntityHitResult wrapEntityHit (Entity receiver, Vec3 pickFrom, Vec3 pickTo, AABB aabb, Predicate<Entity> predicate, double pickRangeSqr, Operation<EntityHitResult> original) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			if (receiver == minecraft.cameraEntity) {
				pickTo = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation();
				if (ThirdPersonStatus.shouldPickFromCamera()) {
					// 从相机到准星处
					pickFrom = mainCamera.getPosition();
					var pickVector = pickFrom.vectorTo(pickTo).normalize().scale(1e-4);
					pickTo = pickTo.add(pickVector);
				} else {
					// 从玩家眼睛到准星处
					// 限制距离为 pickRange
					var pickRange  = Math.sqrt(pickRangeSqr);
					var pickVector = pickFrom.vectorTo(pickTo).normalize().scale(pickRange);
					pickTo = pickFrom.add(pickVector);
				}
				aabb         = new AABB(pickFrom, pickTo).inflate(1.0);
				pickRangeSqr = pickFrom.distanceToSqr(pickTo);
			}
		}
		return original.call(receiver, pickFrom, pickTo, aabb, predicate, pickRangeSqr);
	}

	@ModifyVariable(method="getFov", at=@At(value="STORE"))
	private double modifyFov (double fov) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			return fov / ThirdPerson.CAMERA_AGENT.getSmoothFovDivisor();
		}
		return fov;
	}

	/**
	 * 渲染tick前
	 */
	@Inject(method="render", at=@At("HEAD"))
	private void preRender (float partialTick, long nanoTime, boolean doRenderLevel, CallbackInfo ci) {
		if (GameEvents.renderTickStart != null) {
			GameEvents.renderTickStart.accept(new RenderTickStartEvent(partialTick));
		}
	}
}
