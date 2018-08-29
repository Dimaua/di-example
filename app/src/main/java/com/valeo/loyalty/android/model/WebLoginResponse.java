package com.valeo.loyalty.android.model;

/**
 * Server response for web login.
 */
@SuppressWarnings("unused")
@DataModel
public class WebLoginResponse {

	private final UserId currentUser;
	private final String csrfToken;
	private final String logoutToken;

	public WebLoginResponse(UserId currentUser, String csrfToken, String logoutToken) {
		this.currentUser = currentUser;
		this.csrfToken = csrfToken;
		this.logoutToken = logoutToken;
	}

	public String getCsrfToken() {
		return csrfToken;
	}

	public String getLogoutToken() {
		return logoutToken;
	}

	public String getUserId() {
		return currentUser.getUid();
	}
}
