package com.github.leawind.thirdperson.config;


import com.google.gson.annotations.Expose;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置的抽象类
 * <p>
 * 定义了所有配置选项及其默认值，可以依照此类编辑配置屏幕
 */
public abstract class AbstractConfig {
	// ================================================================================常用 //
	@Expose public boolean            is_mod_enabled                          = true;
	@Expose public boolean            center_offset_when_flying               = true;
	@Expose public boolean            temp_first_person_in_narrow_space       = true;
	//------------------------------玩家旋转
	@Expose public PlayerRotateMode   normal_rotate_mode                      = PlayerRotateMode.INTEREST_POINT;
	@Expose public boolean            auto_rotate_interacting                 = true;
	@Expose public boolean            do_not_rotate_when_eating               = true;
	@Expose public boolean            auto_turn_body_drawing_a_bow            = false;
	//------------------------------玩家实体虚化
	@Expose public boolean            player_fade_out_enabled                 = false;
	@Expose public double             gaze_opacity                            = 0.28;
	@Expose public double             player_invisible_threshold              = 0.55;
	//------------------------------相机到玩家距离调节
	@Expose public int                available_distance_count                = 16;
	@Expose public double             camera_distance_min                     = 0;
	@Expose public double             camera_distance_max                     = 4;
	// ================================================================================平滑系数 //
	@Expose public double             flying_smooth_halflife                  = 0.45;
	@Expose public double             t2f_transition_halflife                 = 0.1;
	//------------------------------调节相机
	@Expose public double             adjusting_camera_offset_smooth_halflife = 0.04;
	@Expose public double             adjusting_distance_smooth_halflife      = 0.08;
	//------------------------------正常模式
	@Expose public double             normal_smooth_halflife_horizon          = 0.25;
	@Expose public double             normal_smooth_halflife_vertical         = 0.20;
	@Expose public double             normal_camera_offset_smooth_halflife    = 0.08;
	@Expose public double             normal_distance_smooth_halflife         = 0.72;
	//------------------------------瞄准模式
	@Expose public double             aiming_smooth_halflife_horizon          = 0.05;
	@Expose public double             aiming_smooth_halflife_vertical         = 0.05;
	@Expose public double             aiming_camera_offset_smooth_halflife    = 0.03;
	@Expose public double             aiming_distance_smooth_halflife         = 0.04;
	// ================================================================================相机偏移 //
	@Expose public double             aiming_fov_divisor                      = 1.125;
	//------------------------------正常模式
	@Expose public double             normal_max_distance                     = 1.5625;
	@Expose public double             normal_offset_x                         = -0.145;
	@Expose public double             normal_offset_y                         = 0.12;
	@Expose public boolean            normal_is_centered                      = false;
	@Expose public double             normal_offset_center                    = 0.24;
	//------------------------------瞄准模式
	@Expose public double             aiming_max_distance                     = 0.56;
	@Expose public double             aiming_offset_x                         = -0.29;
	@Expose public double             aiming_offset_y                         = 0.19;
	@Expose public boolean            aiming_is_centered                      = false;
	@Expose public double             aiming_offset_center                    = 0.48;
	// ================================================================================物品谓词 //
	@Expose public boolean            determine_aim_mode_by_animation         = true;
	@Expose public List<String>       hold_to_aim_item_patterns               = new ArrayList<>();
	@Expose public List<String>       use_to_aim_item_patterns                = new ArrayList<>();
	@Expose public List<String>       use_to_first_person_patterns            = new ArrayList<>();
	// ================================================================================其他 //
	@Expose public String             config_screen_api                       = "YACL";
	@Expose public CameraDistanceMode camera_distance_mode                    = CameraDistanceMode.STRAIGHT;
	@Expose public double             rotate_center_height_offset             = 0.3;
	@Expose public boolean            enable_target_entity_predict            = true;
	@Expose public boolean            skip_vanilla_second_person_camera       = true;
	@Expose public boolean            allow_double_tap_sprint                 = false;
	@Expose public boolean            lock_camera_pitch_angle                 = false;
	@Expose public boolean            use_camera_pick_in_creative             = false;
	@Expose public double             camera_ray_trace_length                 = 512;
	//------------------------------准星
	@Expose public boolean            render_crosshair_when_not_aiming        = true;
	@Expose public boolean            render_crosshair_when_aiming            = true;
	@Expose public boolean            hide_crosshair_when_flying              = true;

	public enum PlayerRotateMode {
		INTEREST_POINT("interest_point"),
		CAMERA_CROSSHAIR("camera_crosshair"),
		PARALLEL_WITH_CAMERA("parallel_with_camera"),
		NONE("none"),
		;
		public static final String KEY = "option.normal_rotate_mode";
		private final String key;

		PlayerRotateMode (String key) {
			this.key = key;
		}

		public static Component formatter (Enum<PlayerRotateMode> value) {
			return formatter((PlayerRotateMode)value);
		}

		public static Component formatter (PlayerRotateMode value) {
			return ConfigManager.getText(KEY + "." + value.key);
		}
	}

	public enum CameraDistanceMode {
		PLANE(true, "plane"),
		STRAIGHT(false, "straight");

		public static final String KEY = "option.camera_distance_mode";
		private final boolean bool;
		private final String  key;

		CameraDistanceMode (boolean bool, String key) {
			this.bool = bool;
			this.key  = key;
		}

		public boolean bool () {
			return bool;
		}

		public static CameraDistanceMode of (boolean b) {
			return b ? PLANE: STRAIGHT;
		}

		public static Component formatter (boolean v) {
			return ConfigManager.getText(KEY + "." + of(v).key);
		}

		public static Component formatter (Enum<CameraDistanceMode> value) {
			return formatter((CameraDistanceMode)value);
		}

		public static Component formatter (CameraDistanceMode value) {
			return ConfigManager.getText(KEY + "." + value.key);
		}
	}
}
