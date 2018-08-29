package com.valeo.loyalty.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.valeo.loyalty.android.R;

import java8.util.Optional;

/**
 * Is used to create and show dialogs.
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String BUNDLE_FIELD_TITLE_TEXT = "titleTxt";
    private static final String BUNDLE_FIELD_MESSAGE_TEXT = "messageTxt";
    private static final String BUNDLE_FIELD_BUTTON_TEXT = "buttonTxt";

    @StringRes
    private int title = R.string.error;
    @StringRes
    private int message;
    @StringRes
    private int buttonText = android.R.string.ok;
    private Optional<Runnable> dismissActionOptional = Optional.empty();

    private static AlertDialogFragment build(@StringRes int title, @StringRes int message, @StringRes int buttonTxt) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_FIELD_TITLE_TEXT, title);
        bundle.putInt(BUNDLE_FIELD_MESSAGE_TEXT, message);
        bundle.putInt(BUNDLE_FIELD_BUTTON_TEXT, buttonTxt);
        alertDialogFragment.setArguments(bundle);

        return alertDialogFragment;
    }

    private AlertDialogFragment setOnDismissAction(Runnable onDismissAction) {
        dismissActionOptional = Optional.ofNullable(onDismissAction);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            title = arguments.getInt(BUNDLE_FIELD_TITLE_TEXT);
            message = arguments.getInt(BUNDLE_FIELD_MESSAGE_TEXT);
            buttonText = arguments.getInt(BUNDLE_FIELD_BUTTON_TEXT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(buttonText, (dialog, which) -> onNegativeButtonClick())
                .create();
    }

    private void onNegativeButtonClick() {
        dismissActionOptional.ifPresent(Runnable::run);
    }

    public static class Builder {
        @StringRes
        private int title = R.string.error;
        @StringRes
        private int message;
        @StringRes
        private int buttonText = android.R.string.ok;
        private Runnable onDismissAction;

        public Builder setTitle(@StringRes int title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(@StringRes int message) {
            this.message = message;
            return this;
        }

        public Builder setButtonText(@StringRes int buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public Builder setOnDismissAction(Runnable onDismissAction) {
            this.onDismissAction = onDismissAction;
            return this;
        }

        public AlertDialogFragment build() {
            return AlertDialogFragment
                    .build(title, message, buttonText)
                    .setOnDismissAction(onDismissAction);
        }
    }
}
