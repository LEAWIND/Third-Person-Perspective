package com.github.leawind.thirdperson.screen;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.config.AbstractConfig;
import com.github.leawind.thirdperson.config.Config;
import com.github.leawind.thirdperson.config.ConfigManager;
import com.github.leawind.util.ItemPredicateUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ClothConfigScreenBuilder extends ConfigScreenBuilder {
	public @NotNull Screen build (@NotNull Config config, @Nullable Screen parent) {
		final var builder = ConfigBuilder.create()    //
										 .setParentScreen(parent)    //
										 .setTitle(ConfigManager.getText("text.title"))    //
										 .setSavingRunnable(ThirdPerson.CONFIG_MANAGER::trySave);
		final var entryBuilder = builder.entryBuilder();
		var       defaults     = Config.DEFAULTS;
		//==================================================================================================================================================//
		final var CATEGORY_COMMON = builder.getOrCreateCategory(ConfigManager.getText("option_category.common"));
		{
			CATEGORY_COMMON.addEntry(buildBooleanEntry("is_mod_enabled", defaults.is_mod_enabled, config.is_mod_enabled, v -> config.is_mod_enabled = v, entryBuilder));
			CATEGORY_COMMON.addEntry(buildBooleanEntry("center_offset_when_flying", defaults.center_offset_when_flying, config.center_offset_when_flying, v -> config.center_offset_when_flying = v, entryBuilder));
			// SubCategory: Player Rotation
			final var SUBCATEGORY_PLAYER_ROTATION = buildSubCategory("player_rotation", entryBuilder);
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("player_rotate_with_camera_when_not_aiming", defaults.player_rotate_with_camera_when_not_aiming, config.player_rotate_with_camera_when_not_aiming, v -> config.player_rotate_with_camera_when_not_aiming = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("player_rotate_to_interest_point", defaults.player_rotate_to_interest_point, config.player_rotate_to_interest_point, v -> config.player_rotate_to_interest_point = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("rotate_to_moving_direction", defaults.rotate_to_moving_direction, config.rotate_to_moving_direction, v -> config.rotate_to_moving_direction = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("auto_rotate_interacting", defaults.auto_rotate_interacting, config.auto_rotate_interacting, v -> config.auto_rotate_interacting = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("do_not_rotate_when_eating", defaults.do_not_rotate_when_eating, config.do_not_rotate_when_eating, v -> config.do_not_rotate_when_eating = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("auto_turn_body_drawing_a_bow", defaults.auto_turn_body_drawing_a_bow, config.auto_turn_body_drawing_a_bow, v -> config.auto_turn_body_drawing_a_bow = v, entryBuilder));
			CATEGORY_COMMON.addEntry(SUBCATEGORY_PLAYER_ROTATION.build());
			// SubCategory: Player Fade out
			final var Subcategory_Player_Fade_Out = buildSubCategory("player_fade_out", entryBuilder);
			Subcategory_Player_Fade_Out.add(buildBooleanEntry("player_fade_out_enabled", defaults.player_fade_out_enabled, config.player_fade_out_enabled, v -> config.player_fade_out_enabled = v, entryBuilder));
			Subcategory_Player_Fade_Out.add(buildDoubleEntry("gaze_opacity", 0D, 1D, defaults.gaze_opacity, config.gaze_opacity, v -> config.gaze_opacity = v, entryBuilder));
			Subcategory_Player_Fade_Out.add(buildDoubleEntry("player_invisible_threshold", 0D, 1D, defaults.player_invisible_threshold, config.player_invisible_threshold, v -> config.player_invisible_threshold = v, entryBuilder));
			CATEGORY_COMMON.addEntry(Subcategory_Player_Fade_Out.build());
			// SubCategory: Camera Distance Adjustment
			final var Subcategory_Camera_Distance_Adjustment = buildSubCategory("camera_distance_adjustment", entryBuilder);
			Subcategory_Camera_Distance_Adjustment.add(buildIntSliderEntry("available_distance_count", 2, 64, defaults.available_distance_count, config.available_distance_count, v -> config.available_distance_count = v, entryBuilder));
			Subcategory_Camera_Distance_Adjustment.add(buildDoubleEntry("camera_distance_min", 0, 6D, defaults.camera_distance_min, config.camera_distance_min, v -> config.camera_distance_min = Math.min(v, config.camera_distance_max), entryBuilder));
			Subcategory_Camera_Distance_Adjustment.add(buildDoubleEntry("camera_distance_max", 0, 6D, defaults.camera_distance_max, config.camera_distance_max, v -> config.camera_distance_max = Math.max(v, config.camera_distance_min), entryBuilder));
			CATEGORY_COMMON.addEntry(Subcategory_Camera_Distance_Adjustment.build());
		}
		//==================================================================================================================================================//
		// Category: smooth factors
		final var CATEGORY_SMOOTH_FACTORS = builder.getOrCreateCategory(ConfigManager.getText("option_category.smooth_halflife"));
		{
			CATEGORY_SMOOTH_FACTORS.addEntry(buildSmoothHalflifeEntry("flying_smooth_halflife", defaults.flying_smooth_halflife, config.flying_smooth_halflife, v -> config.flying_smooth_halflife = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(buildSmoothHalflifeEntry("t2f_transition_halflife", defaults.t2f_transition_halflife, config.t2f_transition_halflife, v -> config.t2f_transition_halflife = v, entryBuilder));
			// SubCategory: Adjusting Camera
			final var Subcategory_Adjusting_Camera = buildSubCategory("adjusting_camera", entryBuilder);
			Subcategory_Adjusting_Camera.add(buildSmoothHalflifeEntry("adjusting_camera_offset_smooth_halflife", defaults.adjusting_camera_offset_smooth_halflife, config.adjusting_camera_offset_smooth_halflife, v -> config.adjusting_camera_offset_smooth_halflife = v, entryBuilder));
			Subcategory_Adjusting_Camera.add(buildSmoothHalflifeEntry("adjusting_distance_smooth_halflife", defaults.adjusting_distance_smooth_halflife, config.adjusting_distance_smooth_halflife, v -> config.adjusting_distance_smooth_halflife = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(Subcategory_Adjusting_Camera.build());
			// SubCategory: Normal Mode
			final var SubCategory_Normal_Mode = buildSubCategory("normal_mode", entryBuilder);
			SubCategory_Normal_Mode.add(buildSmoothHalflifeEntry("smooth_halflife_horizon", defaults.normal_smooth_halflife_horizon, config.normal_smooth_halflife_horizon, v -> config.normal_smooth_halflife_horizon = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildSmoothHalflifeEntry("smooth_halflife_vertical", defaults.normal_smooth_halflife_vertical, config.normal_smooth_halflife_vertical, v -> config.normal_smooth_halflife_vertical = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildSmoothHalflifeEntry("camera_offset_smooth_halflife", defaults.normal_camera_offset_smooth_halflife, config.normal_camera_offset_smooth_halflife, v -> config.normal_camera_offset_smooth_halflife = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildSmoothHalflifeEntry("distance_smooth_halflife", defaults.normal_distance_smooth_halflife, config.normal_distance_smooth_halflife, v -> config.normal_distance_smooth_halflife = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SubCategory_Normal_Mode.build());
			// SubCategory: Aiming Mode
			final var Subcategory_Aiming_Mode = buildSubCategory("aiming_mode", entryBuilder);
			Subcategory_Aiming_Mode.add(buildSmoothHalflifeEntry("smooth_halflife_horizon", defaults.aiming_smooth_halflife_horizon, config.aiming_smooth_halflife_horizon, v -> config.aiming_smooth_halflife_horizon = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildSmoothHalflifeEntry("smooth_halflife_vertical", defaults.aiming_smooth_halflife_vertical, config.aiming_smooth_halflife_vertical, v -> config.aiming_smooth_halflife_vertical = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildSmoothHalflifeEntry("camera_offset_smooth_halflife", defaults.aiming_camera_offset_smooth_halflife, config.aiming_camera_offset_smooth_halflife, v -> config.aiming_camera_offset_smooth_halflife = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildSmoothHalflifeEntry("distance_smooth_halflife", defaults.aiming_distance_smooth_halflife, config.aiming_distance_smooth_halflife, v -> config.aiming_distance_smooth_halflife = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(Subcategory_Aiming_Mode.build());
		}
		//==================================================================================================================================================//
		// Category: Camera Offset
		final var CATEGORY_CAMERA_OFFSET = builder.getOrCreateCategory(ConfigManager.getText("option_category.camera_offset"));
		{
			CATEGORY_CAMERA_OFFSET.addEntry(buildDoubleEntry("aiming_fov_divisor", 1D, 1.25D, defaults.aiming_fov_divisor, config.aiming_fov_divisor, v -> config.aiming_fov_divisor = v, entryBuilder));
			// SubCategory: Normal Mode
			final var SubCategory_Normal_Mode = buildSubCategory("normal_mode", entryBuilder);
			SubCategory_Normal_Mode.add(buildDoubleEntry("max_distance", 0D, 6D, defaults.normal_max_distance, config.normal_max_distance, v -> config.normal_max_distance = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildDoubleEntry("offset_x", -1, +1, defaults.normal_offset_x, config.normal_offset_x, v -> config.normal_offset_x = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildDoubleEntry("offset_y", -1, +1, defaults.normal_offset_y, config.normal_offset_y, v -> config.normal_offset_y = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildBooleanEntry("is_centered", defaults.normal_is_centered, config.normal_is_centered, v -> config.normal_is_centered = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildDoubleEntry("offset_center", -1, +1, defaults.normal_offset_center, config.normal_offset_center, v -> config.normal_offset_center = v, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(SubCategory_Normal_Mode.build());
			// SubCategory: Aiming Mode
			final var Subcategory_Aiming_Mode = buildSubCategory("aiming_mode", entryBuilder);
			Subcategory_Aiming_Mode.add(buildDoubleEntry("max_distance", 0D, 6D, defaults.aiming_max_distance, config.aiming_max_distance, v -> config.aiming_max_distance = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildDoubleEntry("offset_x", -1, +1, defaults.aiming_offset_x, config.aiming_offset_x, v -> config.aiming_offset_x = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildDoubleEntry("offset_y", -1, +1, defaults.aiming_offset_y, config.aiming_offset_y, v -> config.aiming_offset_y = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildBooleanEntry("is_centered", defaults.aiming_is_centered, config.aiming_is_centered, v -> config.aiming_is_centered = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildDoubleEntry("offset_center", -1, +1, defaults.aiming_offset_center, config.aiming_offset_center, v -> config.aiming_offset_center = v, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(Subcategory_Aiming_Mode.build());
		}
		//==================================================================================================================================================//
		// Category: Aiming Check
		final var CATEGORY_AIMING_CHECK = builder.getOrCreateCategory(ConfigManager.getText("option_category.aiming_check"));
		{
			CATEGORY_AIMING_CHECK.addEntry(buildBooleanEntry("determine_aim_mode_by_animation", defaults.determine_aim_mode_by_animation, config.determine_aim_mode_by_animation, v -> config.determine_aim_mode_by_animation = v, entryBuilder));
			CATEGORY_AIMING_CHECK.addEntry(buildStringListEntry("hold_to_aim_item_pattern_expressions", defaults.hold_to_aim_item_patterns, config.hold_to_aim_item_patterns, v -> config.hold_to_aim_item_patterns = v, entryBuilder));
			CATEGORY_AIMING_CHECK.addEntry(buildStringListEntry("use_to_aim_item_pattern_expressions", defaults.use_to_aim_item_patterns, config.use_to_aim_item_patterns, v -> config.use_to_aim_item_patterns = v, entryBuilder));
			CATEGORY_AIMING_CHECK.addEntry(buildStringListEntry("use_to_first_person_pattern_expressions", defaults.use_to_first_person_patterns, config.use_to_first_person_patterns, v -> config.use_to_first_person_patterns = v, entryBuilder));
		}
		//==================================================================================================================================================//
		final var CATEGORY_OTHER = builder.getOrCreateCategory(ConfigManager.getText("option_category.other"));
		{
			if (getAvailableBuidlers().size() > 1) {
				CATEGORY_OTHER.addEntry(entryBuilder.startDropdownMenu(ConfigManager.getText("option.config_screen_api"), config.config_screen_api, v -> config.config_screen_api = v).setSelections(getAvailableBuidlers().keySet()).build());
			}
			CATEGORY_OTHER.addEntry(booleanEntry("camera_distance_mode", defaults.camera_distance_mode.bool(), config.camera_distance_mode.bool(), v -> config.camera_distance_mode = v ? AbstractConfig.CameraDistanceMode.PLANE: AbstractConfig.CameraDistanceMode.STRAIGHT, entryBuilder) //
																																																																							 .setYesNoTextSupplier(AbstractConfig.CameraDistanceMode::formatter).build());
			CATEGORY_OTHER.addEntry(buildDoubleEntry("rotate_center_height_offset", -0.5, 0.5, defaults.rotate_center_height_offset, config.rotate_center_height_offset, v -> config.rotate_center_height_offset = v, entryBuilder));
			CATEGORY_OTHER.addEntry(buildBooleanEntry("enable_target_entity_predict", defaults.enable_target_entity_predict, config.enable_target_entity_predict, v -> config.enable_target_entity_predict = v, entryBuilder));
			CATEGORY_OTHER.addEntry(buildBooleanEntry("skip_vanilla_second_person_camera", defaults.skip_vanilla_second_person_camera, config.skip_vanilla_second_person_camera, v -> config.skip_vanilla_second_person_camera = v, entryBuilder));
			CATEGORY_OTHER.addEntry(buildBooleanEntry("allow_double_tap_sprint", defaults.allow_double_tap_sprint, config.allow_double_tap_sprint, v -> config.allow_double_tap_sprint = v, entryBuilder));
			CATEGORY_OTHER.addEntry(buildBooleanEntry("lock_camera_pitch_angle", defaults.lock_camera_pitch_angle, config.lock_camera_pitch_angle, v -> config.lock_camera_pitch_angle = v, entryBuilder));
			CATEGORY_OTHER.addEntry(buildBooleanEntry("use_camera_pick_in_creative", defaults.use_camera_pick_in_creative, config.use_camera_pick_in_creative, v -> config.use_camera_pick_in_creative = v, entryBuilder));
			CATEGORY_OTHER.addEntry(buildDoubleEntry("camera_ray_trace_length", 32D, 2048D, defaults.camera_ray_trace_length, config.camera_ray_trace_length, v -> config.camera_ray_trace_length = v, entryBuilder));
			// SubCategory: Crosshair
			final var Subcategory_Crosshair = buildSubCategory("crosshair", entryBuilder);
			Subcategory_Crosshair.add(buildBooleanEntry("render_crosshair_when_not_aiming", defaults.render_crosshair_when_not_aiming, config.render_crosshair_when_not_aiming, v -> config.render_crosshair_when_not_aiming = v, entryBuilder));
			Subcategory_Crosshair.add(buildBooleanEntry("render_crosshair_when_aiming", defaults.render_crosshair_when_aiming, config.render_crosshair_when_aiming, v -> config.render_crosshair_when_aiming = v, entryBuilder));
			Subcategory_Crosshair.add(buildBooleanEntry("hide_crosshair_when_flying", defaults.hide_crosshair_when_flying, config.hide_crosshair_when_flying, v -> config.hide_crosshair_when_flying = v, entryBuilder));
			CATEGORY_OTHER.addEntry(Subcategory_Crosshair.build());
		}
		return builder.build();
	}

	private BooleanToggleBuilder booleanEntry (String name, boolean defaultValue, boolean currentValue, Consumer<Boolean> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startBooleanToggle(ConfigManager.getText("option." + name), currentValue).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter);
	}

	private BooleanListEntry buildBooleanEntry (String name, boolean defaultValue, boolean currentValue, Consumer<Boolean> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startBooleanToggle(ConfigManager.getText("option." + name), currentValue).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter).build();
	}

	private SubCategoryBuilder buildSubCategory (String name, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startSubCategory(ConfigManager.getText("option_group." + name)).setExpanded(true).setTooltip(ConfigManager.getText("option_group." + name + ".desc"));
	}

	private IntegerSliderEntry buildIntSliderEntry (String name, int min, int max, int defaultValue, int currentValue, Consumer<Integer> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startIntSlider(ConfigManager.getText("option." + name), currentValue, min, max).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter).build();
	}

	private DoubleListEntry buildDoubleEntry (String name, double min, double max, double defaultValue, double currentValue, Consumer<Double> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startDoubleField(ConfigManager.getText("option." + name), currentValue).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter).setMin(min).setMax(max).build();
	}

	private DoubleListEntry buildSmoothHalflifeEntry (String name, double defaultValue, double currentValue, Consumer<Double> setter, ConfigEntryBuilder entryBuilder) {
		return buildDoubleEntry(name, 0, 4, defaultValue, currentValue, setter, entryBuilder);
	}

	private StringListListEntry buildStringListEntry (String name, List<String> defaultValue, List<String> currentValue, Consumer<List<String>> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startStrList(ConfigManager.getText("option." + name), currentValue).setTooltip(ConfigManager.getText("option." + name + ".desc")).setSaveConsumer(setter).setDefaultValue(defaultValue).setDeleteButtonEnabled(true).setCellErrorSupplier(ItemPredicateUtil::supplyError).setExpanded(true).build();
	}
}
