package com.valeo.loyalty.android.init;

import com.crashlytics.android.Crashlytics;
import com.valeo.loyalty.android.app.ValeoLoyaltyApplication;

import io.fabric.sdk.android.Fabric;

/**
 * Initializes the application.
 */
public class AppInitializer {

	public static void onAppCreated(ValeoLoyaltyApplication application) {
		Fabric.with(application, new Crashlytics());
	}
}
