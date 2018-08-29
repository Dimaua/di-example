package com.valeo.loyalty.android.network;

import android.net.Uri;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.valeo.loyalty.android.app.Injection;
import com.valeo.loyalty.android.app.config.AppConfiguration;
import com.valeo.loyalty.android.network.cookie.WebViewCookieHandler;
import com.valeo.loyalty.android.network.interceptor.AddCookieRequestInterceptor;
import com.valeo.loyalty.android.network.interceptor.AddCsrfTokenRequestInterceptor;
import com.valeo.loyalty.android.network.interceptor.AuthorizingRequestInterceptor;
import com.valeo.loyalty.android.storage.AppSettings;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Initializes network subsystem.
 */
public class ApiInitializer {

    private static final int NETWORK_TIMEOUT = 15;  // seconds

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(Uri.parse(AppConfiguration.SERVER.getServerBaseUrl()).buildUpon().appendEncodedPath("es/").toString())
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .client(createOkHttpClient())
            .build();

    /**
     * Creates configured {@link ServerApi} instances.
     *
     * @return {@link ServerApi} instance.
     */
    public static ServerApi createServerApi() {
        return RETROFIT.create(ServerApi.class);
    }

    private static OkHttpClient createOkHttpClient() {
        AppSettings appSettings = Injection.mainInjector().getAppSettings();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
	            .cookieJar(new WebViewCookieHandler(appSettings))
                .addInterceptor(new AuthorizingRequestInterceptor(appSettings))
                .addInterceptor(new AddCsrfTokenRequestInterceptor(appSettings))
                .addInterceptor(new AddCookieRequestInterceptor(appSettings));

        return NetworkDebug.applyInterceptors(clientBuilder).build();
    }
}
