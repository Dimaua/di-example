package com.valeo.loyalty.android.network;

import com.valeo.loyalty.android.model.BarcodeScanRequest;
import com.valeo.loyalty.android.model.LoginRequest;
import com.valeo.loyalty.android.model.LoginResponse;
import com.valeo.loyalty.android.model.LogoutRequest;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.model.UserAccountInformation;
import com.valeo.loyalty.android.model.WebLoginRequest;
import com.valeo.loyalty.android.model.WebLoginResponse;

import java8.util.function.Consumer;

/**
 * Main API client.
 */
public interface ApiClient {

	/**
	 * Logs the user in.
	 * @param loginRequest  login request
	 * @param callback      the callback that handles server response
	 */
	void login(LoginRequest loginRequest, Consumer<DataResponseContainer<LoginResponse>> callback);

	/**
	 * Logs the user out.
	 * @param logoutRequest  logout request
	 * @param callback      the callback that handles server response
	 */
	void logout(LogoutRequest logoutRequest, Consumer<DataResponseContainer<Void>> callback);

	/**
	 * Obtains a session token.
	 * @param callback      the callback that handles server response
	 */
	void getSessionToken(Consumer<DataResponseContainer<String>> callback);

	/**
	 * Checks a barcode.
	 * @param request   scan request
	 * @param callback  the callback that handles server response
	 */
	void checkBarcode(BarcodeScanRequest request, Consumer<DataResponseContainer<ScanResponse>> callback);

	/**
	 * Checks whether user is logged in
	 * @param callback the callback that handles server response
	 */
	void checkLoginStatus(Consumer<DataResponseContainer<String>> callback);

	/**
	 * Gets user account information.
	 * @param   callback    the callback that handles server response
	 */
	void getUserAccountInformation(Consumer<DataResponseContainer<UserAccountInformation>> callback);

	void webLogin(WebLoginRequest request, Consumer<DataResponseContainer<WebLoginResponse>> callback);
}
