package com.valeo.loyalty.android.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.storage.AppSettings;

import javax.inject.Inject;

import butterknife.BindView;

public class ScanCodeNoticeFragment extends BaseFragment implements View.OnClickListener {

    private static final String KEY_UI_TYPE = "key_ui_type";
    private static final String KEY_CODE_TYPE = "key_code_type";

    public static final int UI_TYPE_INTRO = 0;
    public static final int UI_TYPE_HELP = 1;

    public static final int CODE_TYPE_BARCODE = 0;
    public static final int CODE_TYPE_AUTHCODE = 1;

    @Inject
    AppSettings appSettings;

    @BindView(R.id.notice_button_next)
    Button nextButton;

    @BindView(R.id.notice_button_close)
    ImageView closeButton;

    @BindView(R.id.notice_title_first)
    TextView titleFirst;

    @BindView(R.id.notice_title_second)
    TextView titleSecond;

    @BindView(R.id.notice_hint_text)
    TextView hintText;

    @BindView(R.id.notice_center_image)
    ImageView noticeImage;

    private int uiType = UI_TYPE_HELP;
    private int codeType = CODE_TYPE_BARCODE;

    public static ScanCodeNoticeFragment build(int type, int codeType) {
        ScanCodeNoticeFragment fragment = new ScanCodeNoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_UI_TYPE, type);
        bundle.putInt(KEY_CODE_TYPE, codeType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uiType = getArguments().getInt(KEY_UI_TYPE);
            codeType = getArguments().getInt(KEY_CODE_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner_notice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appSettings.storeNoticeIntroShown(true);

        if (uiType == UI_TYPE_HELP) {
            nextButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
            enableHelp(codeType);
        } else if (uiType == UI_TYPE_INTRO) {
            nextButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
            enableNotice(codeType);
        }

        nextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    private void enableHelp(int codeType) {
        if (codeType == CODE_TYPE_BARCODE) {
            titleFirst.setText(R.string.title_scan_notice_first_barcode);
            titleSecond.setText(R.string.title_scan_notice_second_barcode);
            hintText.setText(R.string.text_scan_notice_help_barcode);
            noticeImage.setImageResource(R.drawable.barcode_notice_image);
        } else if (codeType == CODE_TYPE_AUTHCODE) {
            titleFirst.setText(R.string.title_scan_notice_first_auth);
            titleSecond.setText(R.string.title_scan_notice_second_auth);
            hintText.setText(R.string.text_scan_notice_help_auth);
            noticeImage.setImageResource(R.drawable.authcode_notice_image);
        }
    }

    private void enableNotice(int codeType) {
        if (codeType == CODE_TYPE_AUTHCODE) {
            titleFirst.setText(R.string.authcode_intro_notice_title_first);
            titleSecond.setText(R.string.authcode_intro_notice_title_second);
            hintText.setText(R.string.authcode_intro_notice_description);
            noticeImage.setImageResource(R.drawable.authcode_notice_image);
            nextButton.setText(R.string.authcode_intro_notice_next);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notice_button_next:
                navigateTo(R.id.fragment_container, ScanAuthCodeFragment.build());
                break;
            case R.id.notice_button_close:
                navigateBack();
                break;
        }
    }
}
