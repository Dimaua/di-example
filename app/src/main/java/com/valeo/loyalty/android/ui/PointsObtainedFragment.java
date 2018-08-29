package com.valeo.loyalty.android.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.UserAccountInformation;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.DataResponseContainer;
import com.valeo.loyalty.android.network.exception.DataRequestException;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;
import com.valeo.loyalty.android.storage.WebViewUrls;
import com.valeo.loyalty.android.ui.dialog.ProgressDialogFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Display information after sending scan info
 */
public class PointsObtainedFragment extends BaseFragment {

    private static final String KEY_POINTS_OBTAINED = "key_points_obtained";

    @BindView(R.id.text_added_points)
    TextView textAddedPoints;

    @BindView(R.id.text_scan_msg)
    TextView textScanMsg;

    @BindView(R.id.text_points_top_first_text)
    TextView textPointsTopFirstText;

    @BindView(R.id.text_points)
    TextView textTotalPoints;

    @BindView(R.id.text_points_top_second_text)
    TextView textPointsTopSecondText;

    @BindView(R.id.text_points_bottom_first_text)
    TextView textPointsBottomFirstText;

    @BindView(R.id.text_points_bottom_second_text)
    TextView textPointsBottomSecondText;

    @BindView(R.id.button_scan)
    Button buttonScan;

    @Inject ApiClient apiClient;
    @Inject AppSettings appSettings;
    @Inject ProductScanSequenceHelper sequenceHelper;
    @Inject AnalyticsTracker tracker;

    private int currentPoints;
    private ProgressDialogFragment progressDialogFragment;

    public static PointsObtainedFragment build(int points) {
        PointsObtainedFragment fragment = new PointsObtainedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POINTS_OBTAINED, points);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPoints = getArguments().getInt(KEY_POINTS_OBTAINED);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tracker.setScreenName(getActivity(), AnalyticConstants.POINTS_SCREEN_NAME);
        tracker.submitEvent(AnalyticConstants.EVENT_POINTS_SCREEN_CONFIRM_PRODUCT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_points_obtained, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textAddedPoints.setText(getString(R.string.total_points_template, currentPoints));
        textScanMsg.setText(getString(R.string.scan_product_result_template, sequenceHelper.getCurrentSequence().barcode));
        textScanMsg.setGravity(Gravity.CENTER);
        textPointsTopFirstText.setText(getString(R.string.text_points_top_text));
        updateUserData();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getFragmentManager(), "progress");
        apiClient.getUserAccountInformation(this::onUserAccountLoaded);
    }

    private void onUserAccountLoaded(DataResponseContainer<UserAccountInformation> container) {
        if (getActivity() == null) {
            return;
        }
        progressDialogFragment.dismissAllowingStateLoss();
        try {
            UserAccountInformation data = container.getData();
            appSettings.storeUserInformation(data);
            updateUserData();
        } catch (DataRequestException e) {
            e.printStackTrace();
        }
    }

    private void updateUserData() {
        int points = appSettings.getTotalPoints();
        textTotalPoints.setText(getString(R.string.points_template, points));
    }

    @OnClick(R.id.button_scan)
    void onScanButtonClick() {
        tracker.submitEvent(AnalyticConstants.EVENT_POINTS_SCREEN_SCAN_ANOTHER);
        clearBackStack();
        navigateTo(R.id.fragment_container, ScanBarcodeFragment.build(), false);
    }

    @OnClick(R.id.close_fragment)
    void onCloseButtonClick() {
        clearBackStack();
        navigateTo(R.id.fragment_container, ScanBarcodeFragment.build(), false);
    }

    @OnClick(R.id.text_points_bottom_first_text)
    void onLoyaltyProgramClick() {
        tracker.submitEvent(AnalyticConstants.EVENT_POINTS_SCREEN_GIFT_SHOP);
        openWebFragment(WebViewUrls.buildTrackableWebviewUrl(WebViewUrls.LOYALTY_PROGRAM, AnalyticConstants.POINTS_SCREEN_NAME),
                getString(R.string.menu_loyalty_program), AnalyticConstants.WEBVIEW_PROGRAM_SCREEN);
    }

    @OnClick(R.id.text_points_bottom_second_text)
    void onMyAccountClick() {
        tracker.submitEvent(AnalyticConstants.EVENT_POINTS_SCREEN_MY_ACCOUNT);
        openWebFragment(WebViewUrls.buildTrackableWebviewUrl(
                WebViewUrls.buildMyAccountUrl(appSettings.getUserId()), AnalyticConstants.POINTS_SCREEN_NAME),
                getString(R.string.menu_my_account), AnalyticConstants.WEBVIEW_ACCOUNT_SCREEN);
    }

    private void openWebFragment(String url, String title, String name) {
        navigateTo(R.id.fragment_container, WebviewFragment.build(url, title, name));
    }
}
