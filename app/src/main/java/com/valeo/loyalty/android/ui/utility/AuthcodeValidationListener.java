package com.valeo.loyalty.android.ui.utility;


public interface AuthcodeValidationListener {
    void onItemChanged(int position);
    void onItemUndoChanged(int position);
    void setHasError(boolean hasError);

}