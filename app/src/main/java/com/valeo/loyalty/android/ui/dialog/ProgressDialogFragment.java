package com.valeo.loyalty.android.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.valeo.loyalty.android.R;

/**
 * Shows an indeterminate progress/loading dialog.
 */
public class ProgressDialogFragment extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getContext());
		dialog.setMessage(getString(R.string.http_request_loading_msg));
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		return dialog;
	}
}
