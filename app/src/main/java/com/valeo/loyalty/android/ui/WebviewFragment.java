package com.valeo.loyalty.android.ui;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.LogoutRequest;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.DataResponseContainer;
import com.valeo.loyalty.android.network.exception.DataRequestException;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.WebViewUrls;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import java8.util.Optional;
import timber.log.Timber;

/**
 * Fragment containing webview for displaing server-side pages.
 */
public class WebviewFragment extends BaseFragment {

    private static final String KEY_URL = "key_url";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_SCREEN_NAME = "key_screen_name";

    @Inject
    AppSettings appSettings;
    @Inject
    ApiClient apiClient;
    @Inject
    AnalyticsTracker tracker;

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.webview_title_txt)
    TextView webViewTile;
    @BindView(R.id.progress_bar_webview_fragment)
    ProgressBar loadingBar;

    private String currentURL;
    private String pageTitle;
    private String screenName = null;

    public static WebviewFragment build(String url, String title) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static WebviewFragment build(String url, String title, String screenName) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_SCREEN_NAME, screenName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentURL = getArguments().getString(KEY_URL);
            pageTitle = getArguments().getString(KEY_TITLE);
            screenName = getArguments().getString(KEY_SCREEN_NAME);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (urlRequiresAuthentication()) {
            apiClient.checkLoginStatus(this::onLoginStatusResult);
        } else {
            startLoading();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (screenName != null) {
            tracker.setScreenName(getActivity(), screenName);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (pageTitle != null) {
            webViewTile.setText(pageTitle);
        }

        if (currentURL != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new CustomWebViewClient());
        }
    }

    @OnClick(R.id.close_fragment)
    void onCloseButtonClick() {
        navigateBack();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }

    private boolean urlRequiresAuthentication() {
        return !(currentURL.contains(WebViewUrls.FORGOT_PASSWORD) || currentURL.contains(WebViewUrls.SIGNUP));
    }

    private void onLoginStatusResult(DataResponseContainer<String> result) {
        try {
            if (isUserLoggedIn(result.getData())) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    loadingBar.setVisibility(View.GONE);
                    startLoading();
                }
            } else {
                Optional.ofNullable(getContext())
                        .ifPresent(
                                context -> Toast.makeText(context, R.string.session_expired, Toast.LENGTH_LONG).show()
                        );
                logout();
            }
        } catch (DataRequestException e) {
            Timber.w(e);
        }
    }

    private void startLoading() {
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(currentURL);
    }

    private void logout() {
        apiClient.logout(new LogoutRequest(appSettings.getLogoutToken()), this::postLogout);
    }

    private void postLogout(DataResponseContainer<Void> container) {
        try {
            container.getData();
        } catch (DataRequestException e) {
            Timber.w(e);
        }
        // we have to continue anyway, even if logout method failed
        appSettings.clearAuthenticationData();
        tracker.clearUserId();
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

    private boolean isUserLoggedIn(String response) {
        return response.equals("1");
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (appSettings.isTokenValid()) {
                CookieManager.getInstance().setCookie(url, appSettings.getCombinCookie());
                CookieManager.getInstance().setCookie(url, appSettings.getCominfCookie());
                CookieManager.getInstance().setCookie(url, appSettings.getSessionCookie());
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
