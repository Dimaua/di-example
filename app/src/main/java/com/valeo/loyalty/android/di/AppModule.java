package com.valeo.loyalty.android.di;

import android.content.Context;

import com.valeo.loyalty.android.app.ValeoLoyaltyApplication;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.ApiClientImpl;
import com.valeo.loyalty.android.network.ApiInitializer;
import com.valeo.loyalty.android.network.ServerApi;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Main app DI module.
 */
@Module
public class AppModule {

	private final ValeoLoyaltyApplication app;

	public AppModule(ValeoLoyaltyApplication app) {
		this.app = app;
	}

	@Provides
	Context provideContext() {
		return app;
	}

	@Provides @Singleton
	ServerApi provideServerApi() {
		return ApiInitializer.createServerApi();
	}

	@Provides @Singleton
	ApiClient provideApiClient(ApiClientImpl apiClient) {
		return apiClient;
	}

	@Provides @Singleton
	ProductScanSequenceHelper provideScanSequenceHelper() {
		return new ProductScanSequenceHelper();
	}
}
