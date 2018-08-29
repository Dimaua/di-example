package com.valeo.loyalty.android.ui;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.ProductScanManager;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;
import com.valeo.loyalty.android.ui.dialog.ProgressDialogFragment;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import java.util.HashSet;

import javax.inject.Inject;

public class AbstractValidateFragment extends BaseFragment implements ProductScanManager.OnProductScanned {

    protected static final String KEY_EDITABLE_AUTHCODE = "key_editable_authcode";

    @Inject ApiClient apiClient;
    @Inject AnalyticsTracker tracker;
    @Inject ProductScanSequenceHelper sequenceHelper;

    protected ProductScanManager scanManager;
    protected ProgressDialogFragment progressDialogFragment;
    protected EditableAuthcode editableAuthcode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        scanManager = new ProductScanManager(apiClient, this, sequenceHelper, tracker);
    }

    protected void startServerCheck(String authcode) {
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getFragmentManager(), "progress");
        scanManager.checkProduct(sequenceHelper.getCurrentSequence().barcode, authcode);
    }

    protected String buildAuthCodeFromChars(String[] chars) {
        StringBuilder builder = new StringBuilder();
        for(String s : chars) {
            builder.append(s);
        }
        return builder.toString();
    }
    @Override
    public void onSuccess(ScanResponse response) {
        tracker.submitEvent(AnalyticConstants.EVENT_VALIDATION_SCREEN_AUTH_VALIDATION);
        if (!isDetached()) {
            UiUtility.hideKeyboard(getContext());
            progressDialogFragment.dismissAllowingStateLoss();
            navigateTo(R.id.fragment_container, PointsObtainedFragment.build(response.getPoints()), false);
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

    protected String[] splitAuthCode(String authCode) {
        return authCode.toUpperCase().split("(?!^)");
    }

    protected static class EditableAuthcode implements Parcelable {
        public HashSet<Integer> boldPosition;
        public HashSet<Integer> changedPosition;
        public String[] dataUndoSet;
        public String[] authCodeChars;
        public int selectedPosition;

        public EditableAuthcode(String[] authCodeChars, int selectedPosition) {
            this.authCodeChars = authCodeChars;
            this.selectedPosition = selectedPosition;
            this.boldPosition = new HashSet<>();
            this.changedPosition = new HashSet<>();
            this.dataUndoSet = new String[authCodeChars.length];
            System.arraycopy(this.authCodeChars, 0, dataUndoSet, 0, this.authCodeChars.length);
        }

        protected EditableAuthcode(Parcel in) {
            dataUndoSet = in.createStringArray();
            authCodeChars = in.createStringArray();
            selectedPosition = in.readInt();
            int countBold = in.readInt();
            boldPosition = new HashSet<>();
            for (int i = 0; i < countBold; i++) {
                boldPosition.add(in.readInt());
            }

            int countChanged = in.readInt();
            changedPosition = new HashSet<>();
            for (int i = 0; i < countChanged; i++) {
                changedPosition.add(in.readInt());
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(dataUndoSet);
            dest.writeStringArray(authCodeChars);
            dest.writeInt(selectedPosition);
            dest.writeInt(boldPosition.size());
            for (int i: boldPosition) {
                dest.writeInt(i);
            }
            dest.writeInt(changedPosition.size());
            for (int j: changedPosition) {
                dest.writeInt(j);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<EditableAuthcode> CREATOR = new Creator<EditableAuthcode>() {
            @Override
            public EditableAuthcode createFromParcel(Parcel in) {
                return new EditableAuthcode(in);
            }

            @Override
            public EditableAuthcode[] newArray(int size) {
                return new EditableAuthcode[size];
            }
        };
    }
}
