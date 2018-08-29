package com.valeo.loyalty.android.scanner;

import android.support.annotation.Nullable;
import android.view.SurfaceHolder;

import com.google.android.gms.common.images.Size;

/**
 * A camera paired with detector.
 */
public interface ScanningCamera {

	/**
	 * Gets actual camera preview size.
	 * @return  preview size, or null if it's not available.
	 */
	@Nullable Size getPreviewSize();

	/**
	 * Checks if this camera is operational.
	 * @return  true if it's operational.
	 */
	boolean isOperational();

	/**
	 * Checks if the camera started working.
	 * @return  true if the camera was started.
	 */
	boolean isStarted();

	/**
	 * Switches detection on or off.
	 * @param enabled   true if enabled
	 */
	void setDetectionEnabled(boolean enabled);

	/**
	 * Starts the camera.
	 * @param surfaceHolder     surface holder of preview surface
	 */
	void start(SurfaceHolder surfaceHolder);

	/**
	 * Stops the camera.
	 */
	void stop();

	/**
	 * Releases (disposes) the camera.
	 */
	void release();
}
