package com.valeo.loyalty.android.network.interceptor;

import android.support.annotation.NonNull;

import com.valeo.loyalty.android.app.config.ServerType;
import com.valeo.loyalty.android.storage.AppSettings;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Request interceptor that adds csrf token to requests.
 */
public class AddCsrfTokenRequestInterceptor implements Interceptor {

    private final AppSettings appSettings;

    public AddCsrfTokenRequestInterceptor(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request;
        if (ServerType.urlRequiresCsrfToken(chain.request().url().toString())) {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-CSRF-Token", appSettings.getCsrfToken());
            request = builder.build();
        } else {
            request = chain.request();
        }
        return chain.proceed(request);
    }
}
