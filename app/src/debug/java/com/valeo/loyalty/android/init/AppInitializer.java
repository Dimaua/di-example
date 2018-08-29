package com.valeo.loyalty.android.init;

import android.webkit.WebView;

import com.facebook.stetho.Stetho;
import com.valeo.loyalty.android.BuildConfig;
import com.valeo.loyalty.android.app.ValeoLoyaltyApplication;
import com.valeo.loyalty.android.log.TagCapitalizingDebugTree;

import timber.log.Timber;

/**
 * Initializes the application.
 */
public class AppInitializer {

	/**
	 * Is called in app's onCreate() method.
	 * @param application   the application class
	 */
	public static void onAppCreated(ValeoLoyaltyApplication application) {
		Timber.plant(new TagCapitalizingDebugTree());
		Stetho.initializeWithDefaults(application);
		WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
	}
}
