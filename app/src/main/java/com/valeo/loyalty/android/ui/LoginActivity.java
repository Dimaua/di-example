package com.valeo.loyalty.android.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.storage.AppSettings;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Is used for logging in.
 */
public class LoginActivity extends BaseActivity {

	public static final String WEBVIEW_FRAGMENT_TAG = "WEBVIEW_FRAGMENT_TAG";

	@Inject AppSettings appSettings;
	@Inject ApiClient apiClient;
	@Inject AnalyticsTracker tracker;

	@BindView(R.id.progress_bar_activity_login) ProgressBar loadingBar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidInjection.inject(this);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
        if (appSettings.isTokenValid()) {
        	tracker.setUserId(appSettings.getUserId());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            appSettings.clearAuthenticationData();
            tracker.clearUserId();
            loadingBar.setVisibility(View.GONE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.layout_container, new LoginFragment());
            ft.commit();
        }
	}

	@Override
	public void onBackPressed() {
		Fragment webview = getSupportFragmentManager().findFragmentByTag(WEBVIEW_FRAGMENT_TAG);
		if (webview instanceof WebviewFragment) {
			WebviewFragment webviewFragment = (WebviewFragment) webview;
			boolean canGoBack = webviewFragment.canGoBack();
			if (!canGoBack) {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    super.onBackPressed();
                }
            } else {
                webviewFragment.goBack();
            }
		} else {
			super.onBackPressed();
		}
	}
}
