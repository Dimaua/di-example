package com.valeo.loyalty.android.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.WebViewUrls;
import com.valeo.loyalty.android.ui.event.AccountLoadedEvent;
import com.valeo.loyalty.android.ui.event.MenuRequestedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class WelcomeFragment extends BaseFragment {

    @Inject AppSettings appSettings;

    @BindView(R.id.welcome_screen_title_second)
    TextView titleSecond;

    @BindView(R.id.welcome_screen_points_description_first)
    TextView pointsDescriptionFirst;

    @BindView(R.id.welcome_screen_points)
    TextView points;

    @BindView(R.id.welcome_screen_points_description_second)
    TextView pointsDescriptionSecond;

    @BindView(R.id.welcome_screen_no_points)
    TextView noPoints;


    public static WelcomeFragment build() {
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateText();
    }

    private void updateText() {
        TransitionManager.beginDelayedTransition((ViewGroup) getView(), new Fade(Fade.IN));
        if (appSettings.getTotalPoints() <= 0) {
            pointsDescriptionFirst.setVisibility(View.GONE);
            points.setVisibility(View.GONE);
            pointsDescriptionSecond.setVisibility(View.GONE);
            noPoints.setVisibility(View.VISIBLE);
        } else {
            pointsDescriptionFirst.setVisibility(View.VISIBLE);
            points.setVisibility(View.VISIBLE);
            pointsDescriptionSecond.setVisibility(View.VISIBLE);
            noPoints.setVisibility(View.GONE);
        }
        points.setText(String.valueOf(appSettings.getTotalPoints()));
        titleSecond.setText(appSettings.getUserDisplayName());
    }

    @OnClick(R.id.welcome_screen_menu_button)
    void onMenuIconClick() {
        EventBus.getDefault().post(new MenuRequestedEvent());
    }

    @OnClick(R.id.welcome_screen_scan_button)
    void onScanClick() {
        navigateTo(R.id.fragment_container, ScanBarcodeFragment.build());
    }

    @OnClick(R.id.welcome_screen_gift_button)
    void onGiftShopClick() {
        openWebView(R.id.fragment_container,
                WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.GIFT_SHOP, AnalyticConstants.WELCOME_SCREEN_NAME),
                R.string.menu_gift_shop);
    }

    @Subscribe
    public void onAccountLoaded(AccountLoadedEvent event) {
        updateText();
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
