package com.github.leawind.thirdperson.core;

import com.github.leawind.thirdperson.util.math.LMath;
import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

/**
 * 在预测目标实体时，判断两个实体的优先级
 *
 * @see CameraAgent#predictTargetEntity(float)
 */
public record AimingTargetComparator(Vec3 pos, Vector3d viewVector) implements Comparator<Entity> {
  @Override
  public int compare(Entity e1, Entity e2) {
    return (int) Math.signum(getCost(e1) - getCost(e2));
  }

  /** 计算一个目标实体的代价，值越低越优先 */
  public double getCost(@NotNull Entity entity) {
    var entityPos = entity.getPosition(1);
    var vectorToTarget = LMath.toVector3d(entityPos.subtract(pos));
    if (vectorToTarget.length() < 1e-5) {
      return 0;
    }
    vectorToTarget.normalize();
    double dist = pos.distanceTo(entityPos);
    double angrad = Math.acos(viewVector.dot(vectorToTarget));
    double angdeg = Math.toDegrees(angrad);
    return Math.pow(dist, 2) * Math.pow(angdeg, 2.5);
    //		return dist * 2 + angdeg * 5;
  }
}
