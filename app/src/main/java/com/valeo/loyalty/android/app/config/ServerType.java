package com.valeo.loyalty.android.app.config;

import com.valeo.loyalty.android.network.ServerApi;

import java8.util.stream.RefStreams;

/**
 * Defines possible server types.
 */
public enum ServerType {

	/**
	 * Staging environment.
	 */
	STAGING(
		"https://url1/",
		"str1",
		"str1"
	),

	/**
	 * Staging environment.
	 */
	PREPRODUCTION(
		"https://preprod-static.url/",
		"str2",
		"str2"
	),

	/**
	 * Production environment.
	 */
	PRODUCTION(
		"https://url3/",
		"str3",
		"str3"
	);

	private final String serverUrl;
	private final String clientId;
	private final String clientSecret;

	ServerType(String serverUrl, String clientId, String clientSecret) {
		this.serverUrl = serverUrl;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String getServerBaseUrl() {
		return serverUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * Checks if requests to the given URL require Http-Authentication header.
	 * @param   url   the URL to check
	 * @return  true if the URL requires authentication.
	 */
	public static boolean urlRequiresAuthenticationHeader(String url) {
		return !RefStreams.of(ServerApi.WEB_LOGIN_ENDPOINT, ServerApi.LOGOUT_ENDPOINT,
				ServerApi.LOGIN_STATUS_ENDPOINT)
			.anyMatch(url::contains);
	}

	/**
	 * Checks if requests to the given URL require session cookies.
	 * @param   url   the URL to check
	 * @return  true if the URL require cookies.
	 */
	public static boolean urlRequiresSessionCookies(String url) {
		return RefStreams.of(ServerApi.LOGIN_STATUS_ENDPOINT, ServerApi.LOGOUT_ENDPOINT)
                .anyMatch(url::contains);
	}

	/**
	 * Checks if requests to the given URL require csrf token.
	 * @param   url   the URL to check
	 * @return  true if the URL require csrf token.
	 */
	public static boolean urlRequiresCsrfToken(String url) {
		return url.contains(ServerApi.CHECK_BARCODE_ENDPOINT);
	}
}
