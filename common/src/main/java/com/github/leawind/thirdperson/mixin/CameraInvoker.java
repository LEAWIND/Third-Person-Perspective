package com.github.leawind.thirdperson.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvoker {
  @Invoker("setPosition")
  void invokeSetPosition(Vec3 pos);

  /**
   * 设置相机朝向，单位是角度制
   *
   * <p>该方法除了会设置对象的 yRot和xRot属性外，还会更新 rotation, forwards, up, left 属性
   *
   * @param yRot 偏航角，z轴正向是0，顺时针为正向
   * @param xRot 俯仰角，俯正仰负 [-90,90]
   */
  @Invoker("setRotation")
  void invokeSetRotation(float yRot, float xRot);

  /** 相对于当前位置移动相机 */
  @Invoker("move")
  void invokeMove(double forward, double up, double left);
}
