package com.github.leawind.thirdperson;


import com.github.leawind.thirdperson.core.EntityAgent;
import com.github.leawind.util.surroundings.SurroundingsPattern;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;

import java.io.File;

@SuppressWarnings("unused")
public final class ThirdPersonConstants {
	public static final String              MOD_ID                                   = "leawind_third_person";
	public static final String              MOD_NAME                                 = "Leawind's Third Person";
	public static final String              KEY_CATEGORY                             = "key.categories." + MOD_ID;
	public static final File                CONFIG_FILE                              = Minecraft.getInstance().gameDirectory.toPath().resolve("config/" + MOD_ID + ".json").toFile();
	public static final long                CONFIG_LAZY_SAVE_DELAY                   = 60000L;
	public static final double              CAMERA_PITCH_DEGREE_LIMIT                = 89.800;
	public static final double              CAMERA_THROUGH_WALL_DETECTION            = 0.180;
	public static final double              ROTATE_CENTER_RADIUS                     = 0.5;
	/**
	 * true: 相机距离为从相机平面到旋转中心的距离
	 * <p>
	 * false: 相机距离为从相机位置到旋转中心的距离
	 */
	public static final boolean             USE_CAMERA_PLAIN_DISTANCE                = true;
	public static final double              OPACITY_HALFLIFE                         = 0.0625;
	/**
	 * 渲染相机实体的不透明度下限阈值，当不透明度低于这个值时，将不渲染实体。
	 *
	 * @see EntityAgent#getSmoothOpacity(float)
	 */
	public static final float               RENDERED_OPACITY_THRESHOLD_MIN           = 0.01F;
	/**
	 * 渲染相机实体的不透明度上限阈值，当不透明度高于这个值时，将以原版方式渲染实体。
	 */
	public static final float               RENDERED_OPACITY_THRESHOLD_MAX           = 0.99F;
	/**
	 * 预测目标实体时仅考虑视锥角内的实体
	 */
	public static final double              TARGET_PREDICTION_DEGREES_LIMIT          = 30;
	public static final double              FIRST_PERSON_TRANSITION_END_THRESHOLD    = 0.05;
	/**
	 * 平滑眼睛的半衰期乘数
	 */
	public static final double              EYE_HALFLIFE_MULTIPLIER                  = 0.1;
	/**
	 * 阻挡相机的方块外形获取器
	 */
	public static final ClipContext.Block   CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER       = ClipContext.Block.VISUAL;
	public static final SurroundingsPattern SURROUNDING_PATTERN                      = SurroundingsPattern.of("""
																												  B B B  C M C  T S T
																												  B B B  M C M  S T S
																												  B B B  C M C  T S T
																												  """);
	/**
	 * 成像平面到相机的距离
	 *
	 * @see Camera#getNearPlane()
	 */
	public static final double              VANILLA_NEAR_PLANE_DISTANCE              = 0.050;
	/**
	 * 玩家转头角度限制
	 */
	public static final float               VANILLA_PLAYER_HEAD_ROTATE_LIMIT_DEGREES = 50;
	/**
	 * LocalPlayer#hasEnoughImpulseToStartSprinting
	 */
	public static final double              VANILLA_SPRINT_IMPULSE_THRESHOLD         = 0.8;
	/**
	 * getPlayerPOVHitResult 方法中的探测距离
	 * <p>
	 * Item#getPlayerPOVHitResult(Level, Player, ClipContext.Fluid)
	 */
	public static final double              VANILLA_POV_REACH                        = 5.0;
	/**
	 * Client tick 时间
	 */
	public static final double              VANILLA_CLIENT_TICK_TIME                 = 0.05;
}
