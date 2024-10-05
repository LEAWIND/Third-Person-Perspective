package com.github.leawind.thirdperson.config;


import com.google.gson.annotations.Expose;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置的抽象类
 * <p>
 * 定义了所有配置选项及其默认值，可以依照此类编辑配置屏幕
 */
public abstract class AbstractConfig {
	// ================================================================================常用 //
	@Expose public          boolean            is_mod_enabled                            = true;
	@Expose public          boolean            center_offset_when_flying                 = true;
	//------------------------------玩家旋转
	@Expose public          boolean            player_rotate_with_camera_when_not_aiming = false;
	@Expose public          boolean            player_rotate_to_interest_point           = true;
	@Expose public          boolean            rotate_to_moving_direction                = true;
	@Expose public          boolean            auto_rotate_interacting                   = true;
	@Expose public          boolean            do_not_rotate_when_eating                 = true;
	@Expose public          boolean            auto_turn_body_drawing_a_bow              = false;
	//------------------------------玩家实体虚化
	@Expose public          boolean            player_fade_out_enabled                   = false;
	@Expose public          double             gaze_opacity                              = 0.28;
	@Expose public          double             player_invisible_threshold                = 0.55;
	//------------------------------相机到玩家距离调节
	@Expose public          int                available_distance_count                  = 16;
	@Expose public          double             camera_distance_min                       = 0;
	@Expose public          double             camera_distance_max                       = 4;
	// ================================================================================平滑系数 //
	@Expose public          double             flying_smooth_halflife                    = 0.45;
	@Expose public          double             t2f_transition_halflife                   = 0.1;
	//------------------------------调节相机
	@Expose public          double             adjusting_camera_offset_smooth_halflife   = 0.04;
	@Expose public          double             adjusting_distance_smooth_halflife        = 0.08;
	//------------------------------正常模式
	@Expose public          double             normal_smooth_halflife_horizon            = 0.25;
	@Expose public          double             normal_smooth_halflife_vertical           = 0.20;
	@Expose public          double             normal_camera_offset_smooth_halflife      = 0.08;
	@Expose public          double             normal_distance_smooth_halflife           = 0.72;
	//------------------------------瞄准模式
	@Expose public          double             aiming_smooth_halflife_horizon            = 0.05;
	@Expose public          double             aiming_smooth_halflife_vertical           = 0.05;
	@Expose public          double             aiming_camera_offset_smooth_halflife      = 0.03;
	@Expose public          double             aiming_distance_smooth_halflife           = 0.04;
	// ================================================================================相机偏移 //
	@Expose public          double             aiming_fov_divisor                        = 1.125;
	//------------------------------正常模式
	@Expose public          double             normal_max_distance                       = 1.5;
	@Expose public          double             normal_offset_x                           = -0.16;
	@Expose public          double             normal_offset_y                           = 0.26;
	@Expose public          boolean            normal_is_centered                        = false;
	@Expose public          double             normal_offset_center                      = 0.24;
	//------------------------------瞄准模式
	@Expose public          double             aiming_max_distance                       = 0.28;
	@Expose public          double             aiming_offset_x                           = -0.36;
	@Expose public          double             aiming_offset_y                           = 0.42;
	@Expose public          boolean            aiming_is_centered                        = false;
	@Expose public          double             aiming_offset_center                      = 0.48;
	// ================================================================================瞄准模式判定 //
	@Expose public          boolean            determine_aim_mode_by_animation           = true;
	@Expose @NotNull public List<String>       hold_to_aim_item_patterns                 = new ArrayList<>();
	@Expose @NotNull public List<String>       use_to_aim_item_patterns                  = new ArrayList<>();
	@Expose @NotNull public List<String>       use_to_first_person_patterns              = new ArrayList<>();
	// ================================================================================其他 //
	@Expose public          String             config_screen_api                         = "YACL";
	@Expose public          CameraDistanceMode camera_distance_mode                      = CameraDistanceMode.STRAIGHT;
	@Expose public          double             rotate_center_height_offset               = 0.3;
	@Expose public          boolean            enable_target_entity_predict              = true;
	@Expose public          boolean            skip_vanilla_second_person_camera         = true;
	@Expose public          boolean            allow_double_tap_sprint                   = false;
	@Expose public          boolean            lock_camera_pitch_angle                   = false;
	@Expose public          boolean            use_camera_pick_in_creative               = false;
	@Expose public          double             camera_ray_trace_length                   = 512;
	//------------------------------准星
	@Expose public          boolean            render_crosshair_when_not_aiming          = true;
	@Expose public          boolean            render_crosshair_when_aiming              = true;
	@Expose public          boolean            hide_crosshair_when_flying                = true;

	public enum CameraDistanceMode {
		PLANE(true),
		STRAIGHT(false);

		public static Component formatter (boolean v) {
			return v ? ConfigManager.getText("option.camera_distance_mode.plane"): ConfigManager.getText("option.camera_distance_mode.straight");
		}

		final boolean bool;

		CameraDistanceMode (boolean bool) {
			this.bool = bool;
		}

		public boolean bool () {
			return bool;
		}
	}
}
