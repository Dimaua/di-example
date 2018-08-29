package com.valeo.loyalty.android.ui.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import com.valeo.loyalty.android.R;

/**
 * Is used to create and show dialogs.
 */
public final class DialogUtils {

    private DialogUtils() {
    }

    /**
     * Shows a dialog that automatically closes the activity when the user doesn't grant
     * required permissions.
     *
     * @param activity target activity
     */
    public static void showPermissionDeniedDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.permission_not_granted)
                .setMessage(R.string.permission_not_granted_dialog_message)
                .setPositiveButton(R.string.permission_not_granted_button, (dialog, which) -> {
                    dialog.dismiss();
                    activity.finish();
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.stat_sys_warning)
                .show();
    }

}
