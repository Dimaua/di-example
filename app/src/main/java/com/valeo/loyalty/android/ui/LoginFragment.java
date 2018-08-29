package com.valeo.loyalty.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.LoginRequest;
import com.valeo.loyalty.android.model.LoginResponse;
import com.valeo.loyalty.android.model.WebLoginRequest;
import com.valeo.loyalty.android.model.WebLoginResponse;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.DataResponseContainer;
import com.valeo.loyalty.android.network.exception.ApiException;
import com.valeo.loyalty.android.network.exception.DataRequestException;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.WebViewUrls;
import com.valeo.loyalty.android.ui.dialog.ProgressDialogFragment;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Fragment containing login logic.
 */
public class LoginFragment extends BaseFragment {

    private static final String EMAIL_PATTERN = "(?i:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";

    @Inject
    AppSettings appSettings;

    @Inject
    AnalyticsTracker tracker;

    @Inject
    ApiClient apiClient;

    @BindView(R.id.button_login)
    Button loginView;

    @BindView(R.id.text_sign_up)
    TextView signUpView;

    @BindView(R.id.text_email)
    EditText emailView;

    @BindView(R.id.text_password)
    TextInputEditText passwordView;

    @BindView(R.id.layout_email_wrapper)
    TextInputLayout emailWrapper;

    @BindView(R.id.layout_password_wrapper)
    TextInputLayout passwordWrapper;

    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private ProgressDialogFragment progressDialogFragment;
    private String email;
    private String password;

    public static LoginFragment build() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tracker.setScreenName(getActivity(), AnalyticConstants.LOGIN_SCREEN_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @OnClick(R.id.button_login)
    void doLogin() {
        UiUtility.hideKeyboard(getContext());

        email = emailView.getText().toString();
        password = passwordView.getText().toString();

        if (!validateEmail(email)) {
            tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE);
            emailWrapper.setError(getString(R.string.text_error_email));
        } else if (!validatePassword(password)) {
            tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE);
            passwordWrapper.setError(getString(R.string.text_error_password));
        } else {
            emailWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);
            showProgressDialog();
            apiClient.login(new LoginRequest(email, password), this::onLoginAttemptResult);
        }
    }

    private void onLoginAttemptResult(DataResponseContainer<LoginResponse> container) {
        try {
            LoginResponse response = container.getData();
            appSettings.storeAccessToken(response.getAccessToken(), response.getExpiresIn());
            obtainWebLoginTokens();
        } catch (ApiException e) {
            hideProgressDialog();
            showApiExceptionErrorMessage(e.getHttpStatus());
            tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE);
        } catch (DataRequestException e) {
            hideProgressDialog();
            showFailedDialog();
            tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE);
        }
    }

    private void obtainWebLoginTokens() {
        apiClient.webLogin(new WebLoginRequest(email, password), this::onWebLoginAttemptResult);
    }

    private void onWebLoginAttemptResult(DataResponseContainer<WebLoginResponse> container) {
        hideProgressDialog();
        try {
            WebLoginResponse data = container.getData();
            appSettings.storeWebLoginParameters(data.getUserId(), data.getCsrfToken(), data.getLogoutToken());
            tracker.setUserId(data.getUserId());
            proceedToNextScreen();
        } catch (ApiException e) {
            appSettings.clearAuthenticationData();
            showApiExceptionErrorMessage(e.getHttpStatus());
            tracker.clearUserId();
            tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE);
        } catch (DataRequestException e) {
            appSettings.clearAuthenticationData();
            showFailedDialog();
            tracker.clearUserId();
            tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE);
        }
    }

    private void showApiExceptionErrorMessage(int exceptionCode) {
        if (exceptionCode == ApiException.HTTP_UNAUTHORIZED) {
            showErrorDialog(R.string.invalid_credentials_title, R.string.invalid_credentials, R.string.invalid_credentials_button);
        } else {
            showFailedDialog();
        }
    }

    private void proceedToNextScreen() {
        tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_LOG_IN);
        getActivity().finish();
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @OnClick(R.id.text_sign_up)
    void doSignUp() {
        tracker.submitEvent(AnalyticConstants.EVENT_LOGIN_SCREEN_CREATE_ACCOUNT);
        openWebView(R.id.layout_container, WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.SIGNUP, AnalyticConstants.LOGIN_SCREEN_NAME),
                R.string.create_account_webview_title, LoginActivity.WEBVIEW_FRAGMENT_TAG);
    }

    @OnClick(R.id.text_forgot_passwd)
    void onForgotPasswordClick() {
        openWebView(R.id.layout_container, WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.FORGOT_PASSWORD, AnalyticConstants.LOGIN_SCREEN_NAME),
                R.string.forgot_password_title, LoginActivity.WEBVIEW_FRAGMENT_TAG);
    }

    private boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void showProgressDialog() {
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getFragmentManager(), "progress");
    }

    private void hideProgressDialog() {
        if (progressDialogFragment != null) {
            progressDialogFragment.dismissAllowingStateLoss();
            progressDialogFragment = null;
        }
    }

    private boolean validatePassword(String password) {
        return password.length() > 0;
    }
}
