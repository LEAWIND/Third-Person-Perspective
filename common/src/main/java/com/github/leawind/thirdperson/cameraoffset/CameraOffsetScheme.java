package com.github.leawind.thirdperson.cameraoffset;


import com.github.leawind.thirdperson.config.Config;
import org.jetbrains.annotations.NotNull;

/**
 * 第三人称相机的偏移方案
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 */
public class CameraOffsetScheme {
	private final @NotNull AbstractCameraOffsetMode normalMode;
	private final @NotNull AbstractCameraOffsetMode aimingMode;

	private boolean isAiming = false;

	public CameraOffsetScheme (@NotNull Config config) {
		normalMode = new CameraOffsetModeNormal(config);
		aimingMode = new CameraOffsetModeAiming(config);
	}

	/**
	 * 获取当前模式
	 */
	public @NotNull AbstractCameraOffsetMode getMode () {
		return isAiming() ? aimingMode: normalMode;
	}

	public AbstractCameraOffsetMode getNormalMode () {
		return normalMode;
	}

	public AbstractCameraOffsetMode getAimingMode () {
		return aimingMode;
	}

	/**
	 * 获取当前未启用的模式
	 */
	public @NotNull AbstractCameraOffsetMode getAnotherMode () {
		return isAiming() ? normalMode: aimingMode;
	}

	/**
	 * 设置相机相对于玩家的方向
	 * <p>
	 *
	 * @param side 大于0表示相机在玩家左侧
	 */
	public void setSide (double side) {
		setSide(side > 0);
	}

	/**
	 * 设置相机相对于玩家的方向
	 *
	 * @param isCameraLeftOfPlayer 相机是否在玩家左侧
	 */
	public void setSide (boolean isCameraLeftOfPlayer) {
		aimingMode.setSide(isCameraLeftOfPlayer);
		normalMode.setSide(isCameraLeftOfPlayer);
	}

	/**
	 * 切换到另一边
	 */
	public void toNextSide () {
		aimingMode.toNextSide();
		normalMode.toNextSide();
	}

	/**
	 * 当前是否居中
	 */
	public boolean isCentered () {
		return getMode().isCentered();
	}

	/**
	 * 设置当前是否居中
	 */
	public void setCentered (boolean isCentered) {
		getMode().setCentered(isCentered);
		getAnotherMode().setCentered(isCentered);
	}

	/**
	 * 设置当前是否处于瞄准模式
	 */
	public boolean isAiming () {
		return isAiming;
	}

	/**
	 * 当前是否处于瞄准模式
	 */
	public void setAiming (boolean aiming) {
		isAiming = aiming;
	}
}
