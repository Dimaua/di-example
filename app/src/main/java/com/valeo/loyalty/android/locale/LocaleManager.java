package com.valeo.loyalty.android.locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.valeo.loyalty.android.storage.AppSettings;

import java.util.Locale;

/**
 * Manages locale switching.
 * <br/><b>This class cannot use dependency injection - it is initialized before DI is.</b>
 */
public class LocaleManager {

	public static Context setLocale(Context context) {
		return updateResources(context, getLanguage(context));
	}

	public static String getLanguage(Context context) {
		return new AppSettings(context).getLanguage();
	}

	public static Locale getLocale(Resources res) {
		Configuration config = res.getConfiguration();
		return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
	}

	private static Context updateResources(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);

		Resources res = context.getResources();
		Configuration config = new Configuration(res.getConfiguration());
		config.setLocale(locale);
		context = context.createConfigurationContext(config);
		return context;
	}
}
