package com.github.leawind.thirdperson.cameraoffset;


import com.github.leawind.thirdperson.config.Config;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class CameraOffsetModeNormal extends AbstractCameraOffsetMode {
	public CameraOffsetModeNormal (@NotNull Config config) {
		super(config);
	}

	@Override
	public @NotNull Vector3d getEyeSmoothHalflife () {
		return new Vector3d(config.normal_smooth_halflife_horizon, config.normal_smooth_halflife_vertical, config.normal_smooth_halflife_horizon);
	}

	@Override
	public double getDistanceSmoothHalflife () {
		return config.normal_distance_smooth_halflife;
	}

	@Override
	public @NotNull Vector2d getOffsetSmoothHalflife () {
		return new Vector2d(config.normal_camera_offset_smooth_halflife);
	}

	@Override
	public double getDistanceLimit () {
		return config.normal_max_distance;
	}

	@Override
	public void setDistanceLimit (double distance) {
		config.normal_max_distance = distance;
	}

	@Override
	public boolean isCentered () {
		return config.normal_is_centered;
	}

	@Override
	public void setCentered (boolean isCentered) {
		config.normal_is_centered = isCentered;
	}

	@Override
	public boolean isCameraLeftOfPlayer () {
		return config.normal_offset_x > 0;
	}

	@Override
	public void toNextSide () {
		if (isCentered()) {
			setCentered(false);
		} else {
			config.normal_offset_x = -config.normal_offset_x;
		}
	}

	@Override
	public void setSideOffsetRatio (@NotNull Vector2d v) {
		config.normal_offset_x = v.x;
		config.normal_offset_y = v.y;
	}

	@Override
	public double getCenterOffsetRatio () {
		return config.normal_offset_center;
	}

	@Override
	public void setCenterOffsetRatio (double offset) {
		config.normal_offset_center = offset;
	}

	@Override
	public @NotNull Vector2d getSideOffsetRatio (@NotNull Vector2d v) {
		return v.set(config.normal_offset_x, config.normal_offset_y);
	}
}
