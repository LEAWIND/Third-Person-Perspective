package com.github.leawind.thirdperson.mixin;


import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererInvoker {
	@Invoker("getFov")
	double invokeGetFov (Camera camera, float partialTick, boolean considerUserOption);
}
