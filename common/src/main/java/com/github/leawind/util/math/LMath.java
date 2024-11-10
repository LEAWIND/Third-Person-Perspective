package com.github.leawind.util.math;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public interface LMath {

  /**
   * 将方向转换成角度（弧度制）
   *
   * @param r 方向
   * @return [x=俯仰角, y=偏航角] 当 d 的模为 0 时，返回值将包含 NaN
   */
  @Contract(pure = true)
  static Vector3d directionFromRotationDegree(Vector2d r) {
    return directionFromRotationDegree(r.x, r.y);
  }

  /**
   * 将方向转换成角度（弧度制）
   *
   * @param d 方向
   * @return [x=俯仰角, y=偏航角] 当 d 的模为 0 时，返回值将包含 NaN
   */
  @Contract(pure = true)
  static Vector2d rotationRadianFromDirection(Vector3d d) {
    d.normalize();
    return new Vector2d(-Math.asin(d.y), Math.atan2(-d.x, d.z));
  }

  @Contract(pure = true)
  static Vector3d directionFromRotationDegree(double x, double y) {
    double h = Math.cos(-y * 0.017453292519943295 - Math.PI);
    double i = Math.sin(-y * 0.017453292519943295 - Math.PI);

    double j = -Math.cos(-x * 0.017453292519943295);
    double k = Math.sin(-x * 0.017453292519943295);

    return new Vector3d(i * j, k, h * j);
  }

  /**
   * 将一个向量相对原本方向旋转一定弧度
   *
   * @param vec 原向量
   * @param dy 偏航角变化量（弧度制）
   * @param dx 俯仰角变化量（弧度制）
   */
  @Contract(pure = true)
  static Vector3d rotateRadian(Vector3d vec, float dy, float dx) {
    return directionFromRotationDegree(rotationRadianFromDirection(vec).add(new Vector2d(dx, dy)))
        .mul(vec.length());
  }

  /**
   * 将一个向量相对原本方向旋转一定弧度
   *
   * @param vec 原向量
   * @param rotation 弧度变化量（弧度制）
   */
  @Contract(pure = true)
  static Vector3d rotateRadian(Vector3d vec, Vector2d rotation) {
    return directionFromRotationDegree(rotationRadianFromDirection(vec).add(rotation))
        .mul(vec.length());
  }

  /**
   * 将一个向量相对原本方向旋转一定角度
   *
   * @param vec 原向量
   * @param dy 偏航角变化量（角度制）
   * @param dx 俯仰角变化量（角度制）
   */
  @Contract(pure = true)
  static Vector3d rotateDegree(Vector3d vec, double dy, double dx) {
    return directionFromRotationDegree(rotationDegreeFromDirection(vec).add(new Vector2d(dx, dy)))
        .mul(vec.length());
  }

  /**
   * 将方向转换成角度（角度制）
   *
   * @param d 方向
   * @return [x=俯仰角, y=偏航角] 当 d 的模为 0 时，返回值将包含 NaN
   */
  @Contract(pure = true)
  static Vector2d rotationDegreeFromDirection(Vector3d d) {
    var nd = new Vector3d(d).normalize();
    return new Vector2d(
        (-Math.toDegrees(Math.asin(nd.y))), Math.toDegrees(Math.atan2(-nd.x, nd.z)));
  }

  /**
   * 将一个向量相对原本方向旋转一定角度
   *
   * @param vec 原向量
   * @param rotationAngle 角度变化量（角度制）
   */
  @Contract(pure = true)
  static Vector3d rotateDegree(Vector3d vec, Vector2d rotationAngle) {
    return directionFromRotationDegree(rotationDegreeFromDirection(vec).add(rotationAngle))
        .mul(vec.length());
  }

  /**
   * 将方向转换成角度（角度制）
   *
   * @param d 方向
   * @return [x=俯仰角, y=偏航角] 当 d 的模为 0 时，返回值将包含 NaN
   */
  @Contract(pure = true)
  static double rotationDegreeFromDirection(Vector2d d) {
    return -Math.toDegrees(Math.atan2(d.x, d.y));
  }

  @Contract(pure = true)
  static Vector2d directionFromRotationDegree(double yRot) {
    double x = Math.sin(yRot * 0.017453292519943295 + Math.PI);
    double z = -Math.cos(yRot * 0.017453292519943295 + Math.PI);
    return new Vector2d(x, z);
  }

  @Contract(pure = true)
  static Vector3d toVector3d(Vec3 v) {
    return new Vector3d(v.x, v.y, v.z);
  }

  @Contract(pure = true)
  static Vector3d toVector3d(Vector3f v) {
    return new Vector3d(v.x, v.y, v.z);
  }

  @Contract(pure = true)
  static Vec3i toVec3i(Vec3 v) {
    return new Vec3i((int) v.x, (int) v.y, (int) v.z);
  }

  @Contract(pure = true)
  static Vec3i toVec3i(Vector3i v) {
    return new Vec3i(v.x, v.y, v.z);
  }

  @Contract(pure = true)
  static Vec3 toVec3(Vector3d v) {
    return new Vec3(v.x, v.y, v.z);
  }

  @Contract("_, _ -> param1")
  static @NotNull Vector2d pow(Vector2d v, Vector2d p) {
    v.x = Math.pow(v.x, p.x);
    v.y = Math.pow(v.y, p.y);
    return v;
  }

  @Contract("_, _ -> param1")
  static @NotNull Vector3d pow(Vector3d v, Vector3d p) {
    v.x = Math.pow(v.x, p.x);
    v.y = Math.pow(v.y, p.y);
    v.z = Math.pow(v.z, p.z);
    return v;
  }

  @Contract(pure = true)
  static int clamp(int d, int min, int max) {
    return d < min ? min : Math.min(d, max);
  }

  @Contract(pure = true)
  static long clamp(long d, long min, long max) {
    return d < min ? min : Math.min(d, max);
  }

  @Contract(pure = true)
  static float clamp(float d, float min, float max) {
    return d < min ? min : Math.min(d, max);
  }

  @Contract(pure = true)
  static double clamp(double d, double min, double max) {
    return d < min ? min : Math.min(d, max);
  }

  @Contract(pure = true)
  static void clamp(Vector2d v, double min, double max) {
    v.x = clamp(v.x, min, max);
    v.y = clamp(v.y, min, max);
  }

  /** start = start + (end - start) * t */
  @Contract(pure = true)
  static double lerp(double start, double end, double t) {
    return start + t * (end - start);
  }

  /** start = start + (end - start) * t */
  static void lerp(Vector2d start, Vector2d end, Vector2d t) {
    start.x = lerp(start.x, end.x, t.x);
    start.y = lerp(start.y, end.y, t.y);
  }

  /** start = start + (end - start) * t */
  static void lerp(Vector3d start, Vector3d end, Vector3d t) {
    start.x = lerp(start.x, end.x, t.x);
    start.y = lerp(start.y, end.y, t.y);
    start.z = lerp(start.z, end.z, t.z);
  }

  @Contract(pure = true)
  static double floorMod(double x, double y) {
    return ((x % y) + y) % y;
  }

  @Contract(pure = true)
  static float floorMod(float x, float y) {
    return ((x % y) + y) % y;
  }

  @Contract(pure = true)
  static int floorMod(int x, int y) {
    return Math.floorMod(x, y);
  }

  @Contract(pure = true)
  static long floorMod(long x, long y) {
    return Math.floorMod(x, y);
  }

  /** 角度a是否在角度x与y的夹角中 */
  static boolean isWithinRadian(double x, double a, double b) {
    double tp = Math.sin(b - a);
    return Math.sin(b - x) * tp > 0 && Math.sin(x - a) * tp > 0;
  }

  /** 两角度的夹角大小 */
  static double subtractRadian(double a, double b) {
    double x = Math.abs(Math.IEEEremainder(a - b, 6.283185307179586));
    return x > 3.141592653589793 ? 6.283185307179586 - x : x;
  }

  static boolean isWithinDegrees(double x, double a, double b) {
    return isWithinRadian(
        x * 0.017453292519943295, a * 0.017453292519943295, b * 0.017453292519943295);
  }

  static double subtractDegrees(double a, double b) {
    double x = Math.abs(Math.IEEEremainder(a - b, 360));
    return x > 180 ? 360 - x : x;
  }
}
