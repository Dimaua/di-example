package com.valeo.loyalty.android.scanner;

import com.google.android.gms.vision.Detector;

/**
 * Base class for OCR/barcode scanners.
 */
public interface Scanner {

	/**
	 * Releases allocated resources.
	 */
	void release();

	/**
	 * Gets detector that powers this scanner.
	 * @return  detector instance.
	 */
	Detector<?> getDetector();

	/**
	 * Checks if the scanner is operational.
	 * @return  true if the scanner is operational.
	 */
	boolean isOperational();

	/**
	 * Switches detection on or off.
	 * @param enabled   true if detection should be on
	 */
	void setDetectionEnabled(boolean enabled);
}
