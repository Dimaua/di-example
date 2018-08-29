package com.valeo.loyalty.android.analytics;

import javax.inject.Inject;

/**
 * Handles analytics events.
 */
public class AnalyticsEventLogger {

	@Inject
	AnalyticsEventLogger() {  }

	public void onBarcodeScanned(String code) {
		// does nothing
	}

	public void onAuthCodeScanned(String code) {
		// does nothing
	}
}
