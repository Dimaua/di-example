package com.valeo.loyalty.android.network.interceptor;

import com.valeo.loyalty.android.app.config.ServerType;
import com.valeo.loyalty.android.network.ServerApi;
import com.valeo.loyalty.android.storage.AppSettings;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Request interceptor that adds cookie headers to login status requests.
 */
public class AddCookieRequestInterceptor implements Interceptor {

    private final AppSettings appSettings;

    public AddCookieRequestInterceptor(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request;
        if (ServerType.urlRequiresSessionCookies(chain.request().url().toString())) {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("Cookie", appSettings.getCombinCookie());
            builder.addHeader("Cookie", appSettings.getCominfCookie());
            builder.addHeader("Cookie", appSettings.getSessionCookie());
            request = builder.build();
        } else {
            request = chain.request();
        }
        return chain.proceed(request);
    }
}
