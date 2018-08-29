package com.valeo.loyalty.android.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;

import com.valeo.loyalty.android.di.AppModule;
import com.valeo.loyalty.android.di.DaggerMainInjector;
import com.valeo.loyalty.android.di.MainInjector;
import com.valeo.loyalty.android.init.AppInitializer;
import com.valeo.loyalty.android.locale.LocaleManager;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Main application class.
 */
public class ValeoLoyaltyApplication extends Application implements HasSupportFragmentInjector,
	HasActivityInjector {

	@Inject DispatchingAndroidInjector<Activity> activityAndroidInjector;
	@Inject DispatchingAndroidInjector<Fragment> fragmentAndroidInjector;

	@Override
	public void onCreate() {
		super.onCreate();
		initInjection();
		AppInitializer.onAppCreated(this);
	}

	private void initInjection() {
		MainInjector injector = DaggerMainInjector.builder()
			.appModule(new AppModule(this))
			.build();

		Injection.setInjector(injector);
		injector.inject(this);
	}

	@Override
	public AndroidInjector<Activity> activityInjector() {
		return activityAndroidInjector;
	}

	@Override
	public AndroidInjector<Fragment> supportFragmentInjector() {
		return fragmentAndroidInjector;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleManager.setLocale(base));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LocaleManager.setLocale(this);
	}
}
