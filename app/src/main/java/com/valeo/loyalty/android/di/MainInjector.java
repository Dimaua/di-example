package com.valeo.loyalty.android.di;

import android.content.Context;

import com.valeo.loyalty.android.app.ValeoLoyaltyApplication;
import com.valeo.loyalty.android.storage.AppSettings;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Main Dagger component.
 */
@Singleton
@Component(modules = {AppModule.class, AnalyticsModule.class, AndroidSupportInjectionModule.class,
	AndroidInjectionModule.class, ActivityBindingModule.class, FragmentBindingModule.class})
public interface MainInjector {
	void inject(ValeoLoyaltyApplication app);
	Context getContext();
	AppSettings getAppSettings();
}
