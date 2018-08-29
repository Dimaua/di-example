package com.valeo.loyalty.android.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.valeo.loyalty.android.app.config.AppConfiguration;
import com.valeo.loyalty.android.model.UserAccountInformation;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Stores application settings and some persisted data.
 */
@Singleton
public class AppSettings {

	private static final String PREFERENCES_NAME = "app_settings";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES = "expires";
	private static final String KEY_LANGUAGE = "language";
	private static final String KEY_TOTAL_POINTS = "total_points";
	private static final String KEY_USER_NAME = "user_name";
	private static final String KEY_CSRF_TOKEN = "csrf_token";
	private static final String KEY_LOGOUT_TOKEN = "logout_token";
	private static final String KEY_COMBIN_COOKIE = "combin_cookie";
	private static final String KEY_COMINF_COOKIE = "cominf_cookie";
	private static final String KEY_SESSION_COOKIE = "session_cookie";
	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_INTRO_NOTICE_SHOWN = "key_intro_notice_shown";

	private final SharedPreferences preferences;

	@Inject
	public AppSettings(Context context) {
		preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public void storeAccessToken(String accessToken, int expiresIn) {
		preferences.edit()
			.putString(KEY_ACCESS_TOKEN, accessToken)
			.putLong(KEY_EXPIRES, expiresIn * 1000 + System.currentTimeMillis())
			.apply();
	}

	public int getTotalPoints() {
		return preferences.getInt(KEY_TOTAL_POINTS, 0);
	}

	public void storeUserInformation(UserAccountInformation userAccount) {
		preferences.edit()
			.putInt(KEY_TOTAL_POINTS, userAccount.getPoints())
			.putString(KEY_USER_NAME, userAccount.getDisplayName())
			.putString(KEY_LANGUAGE, userAccount.getLanguage())
			.apply();
	}

	public void storeWebLoginParameters(String userId, String csrfToken, String logoutToken) {
		preferences.edit()
			.putString(KEY_USER_ID, userId)
			.putString(KEY_CSRF_TOKEN, csrfToken)
			.putString(KEY_LOGOUT_TOKEN, logoutToken)
			.apply();
	}

	public void storeCombinCookie(String combin) {
		preferences.edit()
			.putString(KEY_COMBIN_COOKIE, combin)
			.apply();
	}

	public void storeCominfCookie(String cominf) {
		preferences.edit()
			.putString(KEY_COMINF_COOKIE, cominf)
			.apply();
	}

	public void storeSessionCookie(String sessionCookie) {
		preferences.edit()
			.putString(KEY_SESSION_COOKIE, sessionCookie)
			.apply();
	}

	public void storeNoticeIntroShown(boolean isShown) {
		preferences.edit()
				.putBoolean(KEY_INTRO_NOTICE_SHOWN, isShown)
				.apply();
	}

	public String getUserDisplayName() {
		return preferences.getString(KEY_USER_NAME, "");
	}

	public String getLanguage() {
		// force app language to Spanish per customer request.
		return AppConfiguration.DEFAULT_LANGUAGE;
	}

	public boolean isTokenValid() {
		long expiresIn = preferences.getLong(KEY_EXPIRES, 0);
		return hasAccessToken() && expiresIn >= System.currentTimeMillis();
	}

	/**
	 * Clears authentication data (logs out the user).
	 */
    public void clearAuthenticationData() {
        preferences.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_TOTAL_POINTS)
                .remove(KEY_EXPIRES)
                .remove(KEY_LANGUAGE)
                .remove(KEY_USER_NAME)
                .remove(KEY_CSRF_TOKEN)
                .remove(KEY_LOGOUT_TOKEN)
                .remove(KEY_COMBIN_COOKIE)
                .remove(KEY_COMINF_COOKIE)
                .remove(KEY_SESSION_COOKIE)
                .remove(KEY_INTRO_NOTICE_SHOWN)
                .apply();
    }

	public String getAccessToken() {
		return preferences.getString(KEY_ACCESS_TOKEN, "");
	}

	public String getLogoutToken() {
		return preferences.getString(KEY_LOGOUT_TOKEN, "");
	}

	public String getCombinCookie() {
		return preferences.getString(KEY_COMBIN_COOKIE, "anonymous");
	}

	public String getCominfCookie() {
		return preferences.getString(KEY_COMINF_COOKIE, "anonymous");
	}

	public String getSessionCookie() {
		return preferences.getString(KEY_SESSION_COOKIE, "");
	}

	public boolean hasAccessToken() {
		return !TextUtils.isEmpty(getAccessToken());
	}

	public String getCsrfToken() {
		return preferences.getString(KEY_CSRF_TOKEN, "");
	}

	public String getUserId() {
		return preferences.getString(KEY_USER_ID, "");
	}

	public boolean shouldShowNoticeIntro() {
		return !preferences.getBoolean(KEY_INTRO_NOTICE_SHOWN, false); //reverse
	}
}
