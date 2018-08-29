package com.valeo.loyalty.android.ui.utility;


public interface  AuthcodeDatasetChanged {
    void onItemChanged(int position);
    void onItemUndoChanged(int position);
    void setHasError(boolean hasError);

}