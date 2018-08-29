package com.valeo.loyalty.android.model;

import com.valeo.loyalty.android.app.config.AppConfiguration;

/**
 * Contains login request parameters.
 */
@SuppressWarnings("unused")
@DataModel
public class LoginRequest {

	@SuppressWarnings("FieldCanBeLocal")
	private final String grantType = "password";
	private final String clientId = AppConfiguration.SERVER.getClientId();
	private final String clientSecret = AppConfiguration.SERVER.getClientSecret();

	private final String username;
	private final String password;

	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getGrantType() {
		return grantType;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
