package com.valeo.loyalty.android.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

/**
 * Adds debug interceptors to network client.
 */
final class NetworkDebug {

	private NetworkDebug() {  }

	/**
	 * Adds Stetho interceptor to OkHttp client.
	 * @param   clientBuilder     OkHttp client builder
	 * @return  the same builder instance.
	 */
	static OkHttpClient.Builder applyInterceptors(OkHttpClient.Builder clientBuilder) {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
			msg -> Timber.tag("OKHTTP").d(msg));
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
		return clientBuilder
			.addNetworkInterceptor(new StethoInterceptor())
			.addInterceptor(loggingInterceptor);
	}
}
