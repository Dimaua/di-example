package com.valeo.loyalty.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.LogoutRequest;
import com.valeo.loyalty.android.model.UserAccountInformation;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.DataResponseContainer;
import com.valeo.loyalty.android.network.exception.DataRequestException;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.WebViewUrls;
import com.valeo.loyalty.android.ui.event.AccountLoadedEvent;
import com.valeo.loyalty.android.ui.event.MenuRequestedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * Main application activity.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SCREEN_NAME = "main";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    AppSettings appSettings;

    @Inject
    ApiClient apiClient;

    @Inject AnalyticsTracker tracker;

    private final DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            drawerView.findViewById(R.id.close_menu).setOnClickListener(view -> drawer.closeDrawer(GravityCompat.START));
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        drawer.addDrawerListener(drawerListener);
        initNavigation();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment.build())
                .commit();
        }
    }

    @Subscribe
    public void onMenuRequested(MenuRequestedEvent event) {
        drawer.openDrawer(Gravity.START);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiClient.getUserAccountInformation(this::onAccountLoaded);
    }

    private void onAccountLoaded(DataResponseContainer<UserAccountInformation> container) {
        try {
            appSettings.storeUserInformation(container.getData());
            EventBus.getDefault().post(new AccountLoadedEvent());
        } catch (DataRequestException e) {
            Timber.e(e);
        }
    }

    private void initNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_SCAN);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ScanBarcodeFragment.build())
                    .commit();

        } else if (id == R.id.nav_loyalty_program) {
            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_LOYALTY);
            openWebView(WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.LOYALTY_PROGRAM, SCREEN_NAME),
                    getString(R.string.menu_loyalty_program), AnalyticConstants.WEBVIEW_PROGRAM_SCREEN);

        } else if (id == R.id.nav_gift_shop) {
            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_GIFT_SHOP);
            openWebView(WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.GIFT_SHOP, SCREEN_NAME),
                    getString(R.string.menu_gift_shop), AnalyticConstants.WEBVIEW_SHOP_SCREEN);

//        } else if (id == R.id.nav_promotions) {
//            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_PROMOTIONS);
//            openWebView(WebViewUrls.PROMOTIONS, getString(R.string.menu_promotions), AnalyticConstants.WEBVIEW_PROMOTIONS_SCREEN);

        } else if (id == R.id.nav_my_account) {
            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_ACCOUNT);
            openWebView(WebViewUrls.buildTrackableWebviewUrl(
                    WebViewUrls.buildMyAccountUrl(appSettings.getUserId()), SCREEN_NAME),
                    getString(R.string.menu_my_account), AnalyticConstants.WEBVIEW_ACCOUNT_SCREEN);

        } else if (id == R.id.nav_legal_notice) {
            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_LEGAL_NOTICE);
            openWebView(WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.TERMS_AND_CONDITIONS, SCREEN_NAME),
                    getString(R.string.menu_legal_notice), AnalyticConstants.WEBVIEW_LEGAL_SCREEN);

        } else if (id == R.id.nav_sign_out) {
            tracker.submitEvent(AnalyticConstants.EVENT_NAVIGATION_LOG_OUT);
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openWebView(String url, String title, String name) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, WebviewFragment.build(url, title, name))
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        apiClient.logout(new LogoutRequest(appSettings.getLogoutToken()), this::postLogout);
    }

    private void postLogout(DataResponseContainer<Void> container) {
        try {
            container.getData();
        } catch (DataRequestException e) {
            Timber.w(e);
        } finally {
            appSettings.clearAuthenticationData();
            tracker.clearUserId();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
