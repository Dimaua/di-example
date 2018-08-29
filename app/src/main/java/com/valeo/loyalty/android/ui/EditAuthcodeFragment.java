package com.valeo.loyalty.android.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.ui.utility.AuthcodeValidationListener;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import butterknife.BindView;
import butterknife.OnClick;

public class EditAuthcodeFragment extends AbstractValidateFragment implements AuthcodeValidationListener {

    private static final String KEY_CODE = "key_code";
    private static final String KEY_POSITION = "key_position";

    private CharEditAdapter charEditAdapter;

    @BindView(R.id.edit_authcode_error)
    TextView authcodeError;

    @BindView(R.id.edit_authcode_validate)
    Button validateButton;

    @BindView(R.id.edit_authcode_recycler)
    RecyclerView codeRecycler;

    public static EditAuthcodeFragment build(String authCode, int selectedPosition) {
        EditAuthcodeFragment fragment = new EditAuthcodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CODE, authCode);
        bundle.putInt(KEY_CODE, selectedPosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static EditAuthcodeFragment build(EditableAuthcode authcode) {
        EditAuthcodeFragment fragment = new EditAuthcodeFragment();
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
                String authCode = getArguments().getString(KEY_CODE);
                int selectedPosition = getArguments().getInt(KEY_POSITION, 0);
                editableAuthcode = new EditableAuthcode(splitAuthCode(authCode), selectedPosition);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_authcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        charEditAdapter = new CharEditAdapter(editableAuthcode.authCodeChars, this);
        charEditAdapter.setChangedPosition(editableAuthcode.changedPosition);
        charEditAdapter.setDataUndoSet(editableAuthcode.dataUndoSet);
        charEditAdapter.setRecyclerView(codeRecycler);
        charEditAdapter.setSelectedItem(editableAuthcode.selectedPosition);

        codeRecycler.setAdapter(charEditAdapter);
        if (editableAuthcode.selectedPosition > 3) {
            codeRecycler.scrollToPosition(editableAuthcode.selectedPosition - 3);
        }
    }

    @OnClick(R.id.edit_authcode_root)
    public void onRootClick() {
        navigateToValidateScreen();
    }

    private void navigateToValidateScreen() {
        UiUtility.hideKeyboard(getContext());
        if (charEditAdapter != null) {
            editableAuthcode.authCodeChars = charEditAdapter.getDataSet();
            editableAuthcode.dataUndoSet = charEditAdapter.getUndoDataSet();
            editableAuthcode.changedPosition = charEditAdapter.getChangedPosition();
        }
        navigateImmediatelyTo(R.id.fragment_container, ValidateAuthcodeFragment.build(editableAuthcode), false);
    }

    @OnClick(R.id.edit_authcode_validate)
    public void onValidateClick() {
        startServerCheck(buildAuthCodeFromChars(editableAuthcode.authCodeChars));
    }

    @Override
    protected void showErrorDialog(int title, int message, int buttonText) {
        super.showErrorDialog(title, message, buttonText, this::navigateToValidateScreen);
    }

    @Override
    public void onItemChanged(int position) {
        if (!editableAuthcode.boldPosition.contains(position)) {
            editableAuthcode.boldPosition.add(position);
        }
    }

    @Override
    public void onItemUndoChanged(int position) {
        if (editableAuthcode.boldPosition.contains(position)) {
            editableAuthcode.boldPosition.remove(position);
        }
    }

    @Override
    public void setHasError(boolean hasError) {
        if (hasError) {
            authcodeError.setVisibility(View.VISIBLE);
            validateButton.setEnabled(false);
        } else {
            authcodeError.setVisibility(View.GONE);
            validateButton.setEnabled(true);
        }
    }
}
