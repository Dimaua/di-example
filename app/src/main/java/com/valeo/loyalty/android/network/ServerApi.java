package com.valeo.loyalty.android.network;


import com.valeo.loyalty.android.model.BarcodeScanRequest;
import com.valeo.loyalty.android.model.LoginResponse;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.model.UserAccountInformation;
import com.valeo.loyalty.android.model.WebLoginRequest;
import com.valeo.loyalty.android.model.WebLoginResponse;

import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Represents server REST API methods.
 */
@Singleton
public interface ServerApi {

	String WEB_LOGIN_ENDPOINT = "user/login?_format=json";
	String LOGOUT_ENDPOINT = "/user/logout?_format=json";
	String LOGIN_STATUS_ENDPOINT = "user/login_status?_format=json";
	String CHECK_BARCODE_ENDPOINT = "rest/check-barcode?_format=json";
	String USER_INFO_ENDPOINT = "rest/account-info?_format=json";

	@GET("session/token")
	Call<String> getSessionToken();

	@POST("oauth/token")
	@FormUrlEncoded
	Call<LoginResponse> login(@Field("grant_type") String grantType,
	                          @Field("client_id") String clientId,
	                          @Field("client_secret") String clientSecret,
	                          @Field("username") String username,
	                          @Field("password") String password);

	@POST(LOGOUT_ENDPOINT)
	Call<Void> logout(@Query("token") String logoutToken);

	@POST(CHECK_BARCODE_ENDPOINT)
	Call<ScanResponse> scanBarcode(@Body BarcodeScanRequest request);

	@GET(USER_INFO_ENDPOINT)
	Call<UserAccountInformation> getUserAccountInformation();

	@POST(WEB_LOGIN_ENDPOINT)
	Call<WebLoginResponse> webLogin(@Body WebLoginRequest request);

	@GET(LOGIN_STATUS_ENDPOINT)
	Call<String> checkLoginStatus();
}
