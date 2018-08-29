package com.valeo.loyalty.android.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.model.ScanResponse;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.network.ProductScanManager;
import com.valeo.loyalty.android.storage.AppSettings;
import com.valeo.loyalty.android.storage.ProductScanSequenceHelper;
import com.valeo.loyalty.android.ui.dialog.ProgressDialogFragment;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ManualCodeFragment extends BaseFragment implements ProductScanManager.OnProductScanned {

    private static final String KEY_CODE_TYPE = "key_code_type";
    public static final int CODE_TYPE_BARCODE = 0;
    public static final int CODE_TYPE_AUTH = 1;

    @Inject ApiClient apiClient;
    @Inject ProductScanSequenceHelper sequenceHelper;
    @Inject AnalyticsTracker tracker;
    @Inject AppSettings appSettings;

    @BindView(R.id.manual_code_input)
    EditText codeInput;

    @BindView(R.id.manual_code_validate_button)
    Button validateButton;

    @BindView(R.id.manual_code_title_first)
    TextView firstTitle;

    @BindView(R.id.manual_code_title_second)
    TextView secondTitle;

    @BindView(R.id.manual_code_image)
    ImageView codeImage;

    private ProductScanManager scanManager;
    private int codeType = CODE_TYPE_BARCODE;
    private ProgressDialogFragment progressDialogFragment;

    public static ManualCodeFragment build(int codeType) {
        ManualCodeFragment fragment =  new ManualCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CODE_TYPE, codeType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            codeType = getArguments().getInt(KEY_CODE_TYPE);
        }
        scanManager = new ProductScanManager(apiClient, this, sequenceHelper, tracker);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean enabled = s.length() == 13;
                if (enabled != validateButton.isEnabled()) {
                    TransitionManager.beginDelayedTransition((ViewGroup) getView());
                    validateButton.setEnabled(enabled);
                }
            }
        });
        if (codeType == CODE_TYPE_BARCODE) {
            firstTitle.setText(R.string.text_enter_barcode);
            secondTitle.setText(R.string.text_barcode_explanation);
            codeInput.setHint(R.string.hint_enter_barcode);
            codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            codeImage.setImageResource(R.drawable.barcode_manually);
            validateButton.setText(R.string.text_submit_barcode);
        } else if (codeType == CODE_TYPE_AUTH) {
            firstTitle.setText(R.string.text_enter_authcode);
            codeInput.setHint(R.string.hint_enter_authcode);
            codeInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            secondTitle.setText(R.string.text_authcode_explanation);
            codeImage.setImageResource(R.drawable.authcode_manually);
            validateButton.setText(R.string.text_submit_authcode);
        }
        codeInput.post(() -> UiUtility.showKeyboard(codeInput));
    }

    @Override
    public void onPause() {
        super.onPause();
        UiUtility.hideKeyboard(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        UiUtility.showKeyboard(codeInput);
    }

    @OnClick(R.id.manual_code_close_button)
    public void onCloseClick() {
        navigateBack();
    }

    @OnClick(R.id.manual_code_validate_button)
    public void onValidateClick() {
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getFragmentManager(), "progress");
        if (codeType == CODE_TYPE_BARCODE) {
            scanManager.checkBarcode(codeInput.getText().toString());
        } else if (codeType == CODE_TYPE_AUTH) {
            scanManager.checkProduct(sequenceHelper.getCurrentSequence().barcode, codeInput.getText().toString());
        }
    }

    @Override
    public void onSuccess(ScanResponse response) {
        if (!isDetached()) {
            progressDialogFragment.dismissAllowingStateLoss();
            if (codeType == CODE_TYPE_BARCODE) {
                switch (response.getResponseType()) {
                    case AUTH_CODE_REQUIRED:
                        if (appSettings.shouldShowNoticeIntro()) {
                            navigateTo(R.id.fragment_container, ScanCodeNoticeFragment.build(
                                    ScanCodeNoticeFragment.UI_TYPE_INTRO,
                                    ScanCodeNoticeFragment.CODE_TYPE_AUTHCODE));
                        } else {
                            navigateTo(R.id.fragment_container, ScanAuthCodeFragment.build());
                        }
                        break;

                    case SUCCESS:
                        navigateTo(R.id.fragment_container, PointsObtainedFragment.build(response.getPoints()));
                        break;
                }
            } else if (codeType == CODE_TYPE_AUTH) {
                tracker.submitEvent(AnalyticConstants.EVENT_VALIDATION_SCREEN_AUTH_VALIDATION);
                progressDialogFragment.dismissAllowingStateLoss();
                navigateTo(R.id.fragment_container, PointsObtainedFragment.build(response.getPoints()), false);
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
}
