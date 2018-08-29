package com.valeo.loyalty.android.network;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.BarcodeScanRequest;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.network.exception.DataRequestException;
import com.valeo.loyalty.android.storage.ProductScanSequenceDetails;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;
import com.valeo.loyalty.android.ui.dialog.AlertDialogFragment;

import timber.log.Timber;

public class ProductScanManager {

    @NonNull
    private ApiClient apiClient;
    @NonNull
    private OnProductScanned scannedListener;
    @NonNull
    private ProductScanSequenceHelper sequenceHelper;
    @NonNull
    private AnalyticsTracker tracker;

    public ProductScanManager(@NonNull ApiClient apiClient, @NonNull OnProductScanned scannedListener,
                              @NonNull ProductScanSequenceHelper sequenceHelper, @NonNull AnalyticsTracker tracker) {
        this.apiClient = apiClient;
        this.scannedListener = scannedListener;
        this.sequenceHelper = sequenceHelper;
        this.tracker = tracker;
    }

    public void checkBarcode(String barcode) {
        sequenceHelper.initiateNewSequence(new ProductScanSequenceDetails(barcode));
        apiClient.checkBarcode(new BarcodeScanRequest(barcode), this::handleServerResponse);
    }

    public void checkProduct(String barcode, String authcode) {
        sequenceHelper.updateSequenceBarcode(barcode);
        sequenceHelper.updateSequenceAuthCode(authcode);
        apiClient.checkBarcode(new BarcodeScanRequest(barcode, authcode), this::handleServerResponse);
    }

    private void handleServerResponse(DataResponseContainer<ScanResponse> response) {
        try {
            ScanResponse data = response.getData();
            switch (data.getResponseType()) {
                case AUTH_CODE_REQUIRED:
                    scannedListener.onSuccess(data);
                    break;

                case SUCCESS:
                    scannedListener.onSuccess(data);
                    break;

                case NOT_ELIGIBLE:
                    tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_LOYALTY_ERROR_MESSAGE);
                    scannedListener.onError(
                            R.string.barcode_not_eligible_title,
                            R.string.barcode_not_eligible,
                            R.string.barcode_not_eligible_button
                    );
                    break;

                case UNKNOWN_PRODUCT:
                    tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_PRODUCT_ERROR_MESSAGE);
                    scannedListener.onError(
                            R.string.barcode_not_valeo_title,
                            R.string.barcode_not_valeo,
                            R.string.barcode_not_valeo_button
                    );
                    break;

                case SCAN_LIMIT_REACHED:
                    tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_MAXIMUM_QUANTITY_ERROR_MESSAGE);
                    scannedListener.onError(
                            R.string.scan_limit_reached_title,
                            R.string.scan_limit_reached,
                            R.string.scan_limit_reached_button
                    );
                    break;

                case INVALID_AUTH_CODE:
                    tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_INVALID_CODE_ERROR);
                    scannedListener.onError(
                            R.string.authcode_invalid_title,
                            R.string.authcode_invalid,
                            R.string.authcode_invalid_button
                    );
                    break;

                case PRODUCT_ALREADY_SCANNED:
                    tracker.submitEvent(AnalyticConstants.EVENT_SCAN_SCREEN_SCAN_ERROR_MESSAGE);
                    scannedListener.onError(
                            R.string.product_already_scanned_title,
                            R.string.product_already_scanned,
                            R.string.product_already_scanned_button
                    );
                    break;

                default:
                    scannedListener.onFailure();
            }
        } catch (DataRequestException e) {
            scannedListener.onFailure();
            Timber.e(e);
        }
    }

    public interface OnProductScanned {
        /**
         * Called when product scanned successfully
         *
         * @param response successful response from the server
         */
        void onSuccess(ScanResponse response);

        /**
         * Called when product scan request are unsuccessful
         *
         * @param title      String resource id that represents dialog title
         *                   {@link AlertDialogFragment.Builder#setTitle(int)}
         * @param message    String resource id that represents dialog message text
         *                   {@link AlertDialogFragment.Builder#setMessage(int)}
         * @param buttonText String resource id that represents dialog positive button text
         *                   {@link AlertDialogFragment.Builder#setButtonText(int)}
         */
        void onError(@StringRes int title, @StringRes int message, @StringRes int buttonText);

        /**
         * Called when server returns unknown response type or nothing at all
         */
        void onFailure();
    }
}
