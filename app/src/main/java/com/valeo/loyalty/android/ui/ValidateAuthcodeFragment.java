package com.valeo.loyalty.android.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Group;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.app.analitycs.AnalyticConstants;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Fragment display and edit autcode by user
 */
public class ValidateAuthcodeFragment extends AbstractValidateFragment implements View.OnClickListener {

    private static final String KEY_AUTHCODE = "key_authcode";

    @BindView(R.id.validate_fragment_root)
    ConstraintLayout root;

    @BindView(R.id.button_validate)
    Button validateView;

    @BindView(R.id.code_chars)
    RecyclerView codeCharsView;

    @BindView(R.id.text_try_another)
    TextView textModify;

    @BindView(R.id.text_validate)
    TextView textValidate;

    @BindView(R.id.button_modify)
    Button buttonModify;

    @BindView(R.id.button_recognize_again)
    Button buttonRecognize;

    @BindView(R.id.expand_button)
    RelativeLayout expandButton;

    @BindView(R.id.expand_button_icon)
    ImageView expandButtonImage;

    @BindView(R.id.expand_button_text)
    TextView expandButtonText;

    @BindView(R.id.expandable_group)
    Group expandableGroup;

    private CharViewAdapter charViewAdapter;
    private boolean expanded;

    public static ValidateAuthcodeFragment build(String authcode) {
        ValidateAuthcodeFragment fragment = new ValidateAuthcodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_AUTHCODE, authcode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ValidateAuthcodeFragment build(EditableAuthcode authcode) {
        ValidateAuthcodeFragment fragment = new ValidateAuthcodeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_EDITABLE_AUTHCODE, authcode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(KEY_EDITABLE_AUTHCODE)) {
                editableAuthcode = getArguments().getParcelable(KEY_EDITABLE_AUTHCODE);
            } else {
                String authCode = getArguments().getString(KEY_AUTHCODE);
                editableAuthcode = new EditableAuthcode(splitAuthCode(authCode), 0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tracker.setScreenName(getActivity(), AnalyticConstants.VALIDATE_AUTHCODE_SCREEN_NAME);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_validate_authcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UiUtility.hideKeyboard(codeCharsView.getContext());
        validateView.setOnClickListener(this);
        textModify.setOnClickListener(this);
        buttonRecognize.setOnClickListener(this);
        buttonModify.setOnClickListener(this);

        charViewAdapter = new CharViewAdapter(editableAuthcode.authCodeChars);
        charViewAdapter.setBoldPosition(editableAuthcode.boldPosition);
        charViewAdapter.setClickListener(item -> {
            editableAuthcode.selectedPosition = codeCharsView.indexOfChild(item);
            navigateToEditScreen();
        });
        codeCharsView.setAdapter(charViewAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_validate:
                startServerCheck(buildAuthCodeFromChars(editableAuthcode.authCodeChars));
                break;
            case R.id.button_modify:
                navigateToEditScreen();
                break;
            case R.id.button_recognize_again:
                navigateBack();
                break;
            case R.id.text_try_another:
                clearBackStack();
                navigateTo(R.id.fragment_container, ScanBarcodeFragment.build(), false);
                break;
        }
    }

    @Override
    protected void showErrorDialog(@StringRes int title, @StringRes int message, @StringRes int buttonText) {
        showErrorDialog(title, message, buttonText, charViewAdapter::notifyDataSetChanged);
    }

    private void navigateToEditScreen() {
        navigateImmediatelyTo(R.id.fragment_container, EditAuthcodeFragment.build(editableAuthcode), false);
    }

    @OnClick(R.id.expand_button)
    public void onExpandClick() {
        if (expanded) {
            collapse();
            expanded = false;
        } else {
            expand();
            expanded = true;
        }
    }

    private void expand() {
        expandButtonText.setText(R.string.authcode_scan_close_information);
        expandButtonImage.setImageResource(R.drawable.ic_remove_white_24dp);
        expandableGroup.setVisibility(View.VISIBLE);
        updateConstraints(R.layout.fragment_validate_authcode_expanded);
    }

    private void collapse() {
        expandButtonText.setText(R.string.authcode_scan_view_information);
        expandButtonImage.setImageResource(R.drawable.ic_add_white_24dp);
        expandableGroup.setVisibility(View.GONE);
        updateConstraints(R.layout.fragment_validate_authcode);
    }

    private void updateConstraints(@LayoutRes int id) {
        ConstraintSet newConstraintSet = new ConstraintSet();
        newConstraintSet.clone(getContext(), id);
        newConstraintSet.applyTo(root);
        TransitionManager.beginDelayedTransition(root);
    }

}
