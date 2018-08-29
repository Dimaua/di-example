package com.valeo.loyalty.android.analytics;

import javax.inject.Inject;

/**
 * Handles analytics events - does nothing in debug.
 */
@SuppressWarnings("unused")
public class AnalyticsEventLogger {

	@Inject
	AnalyticsEventLogger() {  }

	public void onBarcodeScanned(String code) {
		// does nothing in debug
	}

	public void onAuthCodeScanned(String code) {
		// does nothing in debug
	}

	public void onAuthCodeScannedWithRtr(String code) {
		// does nothing in debug
	}
}
