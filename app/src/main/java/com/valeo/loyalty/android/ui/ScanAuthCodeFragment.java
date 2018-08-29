package com.valeo.loyalty.android.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.google.android.gms.common.images.Size;
import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.analytics.AnalyticsEventLogger;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.scanner.CameraFactory;
import com.valeo.loyalty.android.scanner.RecognizedCode;
import com.valeo.loyalty.android.scanner.ScanningCamera;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Is used to scan authenticity codes.
 */
public class ScanAuthCodeFragment extends AbstractScanFragment {

    private static final String SCANNER_FRAME_ASPECT_RATIO = "W,6:1";

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject CameraFactory cameraFactory;
    @Inject AnalyticsEventLogger eventLogger;
    @Inject AnalyticsTracker tracker;
    @Inject ProductScanSequenceHelper sequenceHelper;

    public static ScanAuthCodeFragment build() {
        return new ScanAuthCodeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tracker.setScreenName(getActivity(), AnalyticConstants.SCAN_AUTHCODE_SCREEN_NAME);
    }

    @Override
    protected ScanningCamera onCreateCamera() {
        return cameraFactory.createAuthCodeScanningCamera(this::onAuthCodeScannedProxy);
    }

    private void onAuthCodeScannedProxy(RecognizedCode authCode) {
        tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_SCAN_AUTH_CODE);
        handler.post(() -> onAuthCodeScanned(authCode));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detectionFrame.setImageResource(R.drawable.authcode_frame);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) detectionFrame.getLayoutParams();
        layoutParams.dimensionRatio = SCANNER_FRAME_ASPECT_RATIO;
        hint.setText(R.string.authcode_scanner_hint);
        hint.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        detectionFrame.setLayoutParams(layoutParams);
        capsuleText.setText(R.string.text_capsule_error_authcode);
        manualButton.setText(R.string.text_enter_authcode_manually);
    }

    private boolean isRectInDetectionArea(Rect box) {
        View rootView = getView();
        if (!getPreviewSize().isPresent() || rootView == null) {
            return false;
        }

        Size previewSize = getPreviewSize().get();
        int previewHeight = Math.max(previewSize.getHeight(), previewSize.getWidth());
        int previewWidth = Math.min(previewSize.getHeight(), previewSize.getWidth());
        Size surfaceSize = getSurfaceSize();
        double widthMultiplier = (double) surfaceSize.getWidth() / previewWidth;
        double heightMultiplier = (double) surfaceSize.getHeight() / previewHeight;
        int detectionFrameWidth = (int) (detectionFrame.getWidth() / widthMultiplier);
        int detectionFrameHeight = (int) (detectionFrame.getHeight() / heightMultiplier);
        int left = (previewWidth - detectionFrameWidth) / 2;
        int top = (previewHeight - detectionFrameHeight) / 2;

        updateBarcodeBox(box, widthMultiplier, heightMultiplier);

        return box.intersect(left, top, detectionFrameWidth + left, detectionFrameHeight + top);
    }

    private void onAuthCodeScanned(RecognizedCode authCode) {
        if (!isRectInDetectionArea(authCode.getRect())) {
            Timber.w("auth code not in target area");
            return;
        }
        setDetectionEnabled(false);
        Timber.w("auth code: %1$s", authCode);

        eventLogger.onAuthCodeScanned(authCode.getText());

        View root = getView();
        if (root == null) {
            setDetectionEnabled(true);
            return;
        }

        navigateTo(R.id.fragment_container, ValidateAuthcodeFragment.build(authCode.getText()));
    }

    @Override
    protected void onHelpButtonClick() {
        tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_SHOW_HELP);
        slideFromTop(R.id.fragment_container, ScanCodeNoticeFragment.build(
                ScanCodeNoticeFragment.UI_TYPE_HELP,
                ScanCodeNoticeFragment.CODE_TYPE_AUTHCODE));
    }

    @Override
    protected void onManualEnterClick() {
        slideFromBottom(R.id.fragment_container, ManualCodeFragment.build(ManualCodeFragment.CODE_TYPE_AUTH));
    }

    @Override
    protected void setDetectionFrameActive(boolean active) {
        detectionFrame.setImageResource(active
                ? R.drawable.authcode_frame
                : R.drawable.authcode_frame_active);
    }
}
