package com.valeo.loyalty.android.network.cookie;

import android.support.annotation.NonNull;
import android.webkit.CookieManager;

import com.valeo.loyalty.android.network.ServerApi;
import com.valeo.loyalty.android.storage.AppSettings;

import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class WebViewCookieHandler implements CookieJar {

    private static final String COMBIN_COOKIE_NAME = "COMBIN";
    private static final String COMINF_COOKIE_NAME = "COMINF";
    private static final String SESSION_COOKIE_PREFIX_HTTP = "SESS";
    private static final String SESSION_COOKIE_PREFIX_HTTPS = "SSESS";

    private final CookieManager webviewCookieManager = CookieManager.getInstance();
    private AppSettings appSettings;

    public WebViewCookieHandler(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        String urlString = url.toString();
        if (urlString.contains(ServerApi.WEB_LOGIN_ENDPOINT)) {
            for (Cookie cookie : cookies) {
                if (cookie.name().equals(COMBIN_COOKIE_NAME)) {
                    appSettings.storeCombinCookie(cookie.toString());
                } else if (cookie.name().equals(COMINF_COOKIE_NAME)) {
                    appSettings.storeCominfCookie(cookie.toString());
                } else if (cookie.name().startsWith(SESSION_COOKIE_PREFIX_HTTP)) {
                    appSettings.storeSessionCookie(cookie.toString());
                } else if (cookie.name().startsWith(SESSION_COOKIE_PREFIX_HTTPS)) {
                    appSettings.storeSessionCookie(cookie.toString());
                } else {
                    webviewCookieManager.setCookie(urlString, cookie.toString());
                }
            }
        }
    }

	@Override
	public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
		return Collections.emptyList();
	}
}
