package com.valeo.loyalty.android.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.barcode.Barcode;
import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.analytics.AnalyticsEventLogger;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.network.ProductScanManager;
import com.valeo.loyalty.android.scanner.CameraFactory;
import com.valeo.loyalty.android.scanner.ScanningCamera;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;
import com.valeo.loyalty.android.ui.dialog.ProgressDialogFragment;

import java.util.List;

import javax.inject.Inject;

import java8.util.stream.StreamSupport;
import timber.log.Timber;

/**
 * Scans barcodes using Google Mobile Vision.
 */
public class ScanBarcodeFragment extends AbstractScanFragment implements ProductScanManager.OnProductScanned {

    @Inject CameraFactory cameraFactory;
    @Inject AnalyticsTracker tracker;
    @Inject AnalyticsEventLogger eventLogger;
    @Inject AppSettings appSettings;
    @Inject ProductScanSequenceHelper sequenceHelper;

    private ProductScanManager scanManager;

    public static ScanBarcodeFragment build() {
        return new ScanBarcodeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tracker.setScreenName(getActivity(), AnalyticConstants.SCAN_BARCODE_SCREEN_NAME);
        scanManager = new ProductScanManager(apiClient, this, sequenceHelper, tracker);
    }

    @Override
    protected ScanningCamera onCreateCamera() {
        return cameraFactory.createBarcodeScanningCamera(this::onBarcodesDetectedProxy);
    }

    private void onBarcodesDetectedProxy(List<Barcode> barcodes) {
        tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_SCAN_PRODUCT);
        getActivity().runOnUiThread(() -> onBarcodesDetected(barcodes));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hint.setText(R.string.barcode_scanner_hint);
        hint.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        capsuleText.setText(R.string.text_capsule_error_barcode);
        manualButton.setText(R.string.text_enter_barcode_manually);
    }

    private boolean isBarcodeInDetectionArea(Rect barcodeBox) {
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

        updateBarcodeBox(barcodeBox, widthMultiplier, heightMultiplier);

        return barcodeBox.top >= top
                && barcodeBox.left >= left
                && barcodeBox.bottom <= (detectionFrameWidth + left)
                && barcodeBox.right <= (detectionFrameHeight + top);
    }

    private void onBarcodesDetected(List<Barcode> barcodes) {
        StreamSupport.stream(barcodes)
                .filter(barcode -> isBarcodeInDetectionArea(barcode.getBoundingBox()))
                .findFirst()
                .ifPresent(this::onValidBarcodeFound);
    }

    private void onValidBarcodeFound(Barcode barcode) {
        setDetectionEnabled(false);
        Timber.i("barcode: " + barcode.rawValue);
        if (isDetached()) {
            setDetectionEnabled(true);
            return;
        }
        setDetectionFrameActive(true);

        eventLogger.onBarcodeScanned(barcode.rawValue);
        uiHandler.postDelayed(() -> startServerCheck(barcode.rawValue), SERVER_CHECK_DELAY);
    }

    private void startServerCheck(String barcode) {
        if (!isDetached()) setDetectionFrameActive(false);
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getFragmentManager(), "progress");
        scanManager.checkBarcode(barcode);
    }

    @Override
    public void onSuccess(ScanResponse response) {
        if (!isDetached()) {
            progressDialogFragment.dismissAllowingStateLoss();
            switch (response.getResponseType()) {
                case AUTH_CODE_REQUIRED:
                    if (appSettings.shouldShowNoticeIntro()) {
                        showAuthCodeNotice();
                    } else {
                        showAuthCodeScanner();
                    }
                    break;

                case SUCCESS:
                    showSuccessfulResult(response.getPoints());
                    break;
            }
        }
    }

    @Override
    public void onError(int title, int message, int buttonText) {
        if (!isDetached()) {
            progressDialogFragment.dismissAllowingStateLoss();
            showErrorDialog(title, message, buttonText);
        }
    }

    @Override
    public void onFailure() {
        if (!isDetached()) {
            progressDialogFragment.dismissAllowingStateLoss();
            showFailedDialog();
        }
    }

    @Override
    protected void setDetectionFrameActive(boolean active) {
        detectionFrame.setImageResource(active
                ? R.drawable.barcode_frame_active
                : R.drawable.barcode_frame);
    }

    @Override
    protected void onHelpButtonClick() {
        slideFromTop(R.id.fragment_container, ScanCodeNoticeFragment.build(
                ScanCodeNoticeFragment.UI_TYPE_HELP,
                ScanCodeNoticeFragment.CODE_TYPE_BARCODE));
    }

    @Override
    protected void onManualEnterClick() {
        slideFromBottom(R.id.fragment_container, ManualCodeFragment.build(ManualCodeFragment.CODE_TYPE_BARCODE));
    }

    @Override
    void onBackIconClick() {
        clearBackStack();
        navigateToWithBackAnimation(R.id.fragment_container, WelcomeFragment.build());
    }

    private void showAuthCodeScanner() {
        navigateTo(R.id.fragment_container, ScanAuthCodeFragment.build());
    }

    private void showAuthCodeNotice() {
        navigateTo(R.id.fragment_container, ScanCodeNoticeFragment.build(
                ScanCodeNoticeFragment.UI_TYPE_INTRO,
                ScanCodeNoticeFragment.CODE_TYPE_AUTHCODE));
    }
}
