package com.valeo.loyalty.android.network;

import com.valeo.loyalty.android.model.BarcodeScanRequest;
import com.valeo.loyalty.android.model.LoginRequest;
import com.valeo.loyalty.android.model.LoginResponse;
import com.valeo.loyalty.android.model.LogoutRequest;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.model.UserAccountInformation;
import com.valeo.loyalty.android.model.WebLoginRequest;
import com.valeo.loyalty.android.model.WebLoginResponse;

import javax.inject.Inject;

import java8.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class ApiClientImpl implements ApiClient {

	@Inject
	ServerApi serverApi;

	@Inject
	ApiClientImpl() {  }

	@Override
	public void login(LoginRequest loginRequest, Consumer<DataResponseContainer<LoginResponse>> callback) {
		serverApi.login(
			loginRequest.getGrantType(),
			loginRequest.getClientId(),
			loginRequest.getClientSecret(),
			loginRequest.getUsername(),
			loginRequest.getPassword()
		).enqueue(new RetrofitCallbackAdapter<>(callback));
	}

	@Override
	public void logout(LogoutRequest logoutRequest, Consumer<DataResponseContainer<Void>> callback) {
		serverApi.logout(logoutRequest.logoutToken).enqueue(new RetrofitCallbackAdapter<>(callback));
	}

	@Override
	public void getSessionToken(Consumer<DataResponseContainer<String>> callback) {
		serverApi.getSessionToken().enqueue(new RetrofitCallbackAdapter<>(callback));
	}

	@Override
	public void checkBarcode(BarcodeScanRequest request, Consumer<DataResponseContainer<ScanResponse>> callback) {
		serverApi.scanBarcode(request).enqueue(new RetrofitCallbackAdapter<>(callback));
	}

	@Override
	public void checkLoginStatus(Consumer<DataResponseContainer<String>> callback) {
		serverApi.checkLoginStatus().enqueue(new RetrofitCallbackAdapter<>(callback));
	}

	@Override
	public void getUserAccountInformation(Consumer<DataResponseContainer<UserAccountInformation>> callback) {
		serverApi.getUserAccountInformation().enqueue(new RetrofitCallbackAdapter<>(callback));
	}

	@Override
	public void webLogin(WebLoginRequest request, Consumer<DataResponseContainer<WebLoginResponse>> callback) {
		serverApi.webLogin(request).enqueue(new RetrofitCallbackAdapter<>(callback));
	}
}
