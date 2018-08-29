package com.valeo.loyalty.android.network;

import okhttp3.OkHttpClient;

/**
 * Adds debug interceptors to network client.
 */
final class NetworkDebug {

	private NetworkDebug() {  }

	/**
	 * Does nothing in release mode.
	 * @param   clientBuilder OkHttp client builder
	 * @return  the same builder as in input.
	 */
	static OkHttpClient.Builder applyInterceptors(OkHttpClient.Builder clientBuilder) {
		return clientBuilder;
	}
}
