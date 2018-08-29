package com.valeo.loyalty.android.network.interceptor;

import android.support.annotation.NonNull;

import com.valeo.loyalty.android.app.config.ServerType;
import com.valeo.loyalty.android.storage.AppSettings;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Request interceptor that adds authorization header to all Retrofit requests.
 */
public class AuthorizingRequestInterceptor implements Interceptor {

	private final AppSettings appSettings;

	public AuthorizingRequestInterceptor(AppSettings appSettings) {
		this.appSettings = appSettings;
	}

	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		Request request = appSettings.hasAccessToken()
			&& ServerType.urlRequiresAuthenticationHeader(chain.request().url().toString())
			? chain.request().newBuilder()
				.addHeader("Authorization", "Bearer " + appSettings.getAccessToken())
				.build()
			: chain.request();

		return chain.proceed(request);
	}
}
