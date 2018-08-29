package com.valeo.loyalty.android.storage;

import android.net.Uri;

import com.valeo.loyalty.android.app.config.AppConfiguration;

/**
 * Stores Web URLs for API endpoints.
 */
public class WebViewUrls {

    private static final Uri BASE_URI = Uri
            .parse(AppConfiguration.SERVER.getServerBaseUrl())
            .buildUpon()
            .appendQueryParameter("utm_source", "valeo")
            .appendQueryParameter("utm_medium", "app-android")
            .build();

    public static final String SIGNUP = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/user/register")
            .query("webview")
            .toString();

    public static final String LOYALTY_PROGRAM = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/specialistclub")
            .query("webview")
            .toString();

    public static final String GIFT_SHOP = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/specialistclub/shop")
            .query("webview")
            .toString();

    public static final String PROMOTIONS = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/specialistclub/promotions")
            .query("webview")
            .toString();

    private static final Uri MY_ACCOUNT_BASE = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/user")
            .build();

    public static final String TERMS_AND_CONDITIONS = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/terminos-y-condiciones-generales")
            .toString();

    public static final String FORGOT_PASSWORD = BASE_URI
            .buildUpon()
            .appendEncodedPath("es/user/password")
            .query("webview")
            .toString();

    public static String buildMyAccountUrl(String userId) {
        return MY_ACCOUNT_BASE.buildUpon()
                .appendPath(userId)
                .appendEncodedPath("edit")
                .query("webview")
                .toString();
    }

    public static String buildTrackableWebviewUrl(String url, String campaignName) {
        return Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("utm_campaign", campaignName)
                .toString();
    }
}
