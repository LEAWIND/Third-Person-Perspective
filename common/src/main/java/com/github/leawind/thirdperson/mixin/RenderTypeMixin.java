package com.github.leawind.thirdperson.mixin;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.core.EntityAgent;
import com.github.leawind.util.annotation.VersionSensitive;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(value=RenderType.class, priority=2000)
public class RenderTypeMixin extends RenderStateShard {
	/**
	 * 修改自 RenderType#ARMOR_CUTOUT_NO_CULL
	 * <p>
	 * 将 NO_TRANSPARENCY 改成了 TRANSLUCENT_TRANSPARENCY
	 */
	@Unique private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL_TRANSLUCENT = Util.memoize((resourceLocation) -> {
		var compositeState = RenderType.CompositeState.builder()
													  .setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
													  .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
													  .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
													  .setCullState(NO_CULL)
													  .setLightmapState(LIGHTMAP)
													  .setOverlayState(OVERLAY)
													  .setLayeringState(VIEW_OFFSET_Z_LAYERING)
													  .createCompositeState(true);
		return RenderType.create("armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
	});

	@SuppressWarnings("unused")
	public RenderTypeMixin (String name, Runnable setupState, Runnable clearState) {
		super(name, setupState, clearState);
	}

	/**
	 * 对盔甲和鞘翅使用自定义的 RenderType 提供器，实现半透明效果
	 * <p>
	 * see ModelPartCubeMixin#compile(float)
	 * <p>
	 *
	 * @see EntityAgent#getSmoothOpacity(float)
	 */
	@VersionSensitive
	@Inject(method="armorCutoutNoCull", at=@At(value="HEAD", target="Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable=true)
	private static void setTransparencyState (ResourceLocation resourceLocation, @NotNull CallbackInfoReturnable<RenderType> ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPersonStatus.useCameraEntityOpacity(Minecraft.getInstance().getFrameTime())) {
			ci.setReturnValue(ARMOR_CUTOUT_NO_CULL_TRANSLUCENT.apply(resourceLocation));
			ci.cancel();
		}
	}
}
