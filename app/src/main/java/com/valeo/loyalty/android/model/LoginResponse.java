package com.valeo.loyalty.android.model;

/**
 * Contains server response to login request.
 */
@SuppressWarnings("unused")
@DataModel
public class LoginResponse {

	private final String accessToken;
	private final String refreshToken;
	private final int expiresIn;

	public LoginResponse(String accessToken, String refreshToken, int expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}
}
