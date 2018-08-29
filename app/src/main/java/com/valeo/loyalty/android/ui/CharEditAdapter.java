package com.valeo.loyalty.android.ui;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.ui.utility.AuthcodeValidationListener;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public class CharEditAdapter extends RecyclerView.Adapter<CharEditAdapter.ViewHolder> {

    private String[] dataSet;
    private String[] dataUndoSet;

    private final Handler handler = new Handler();

    private HashSet<Integer> changedPosition = new HashSet<>();
    private Set<Integer> dataErrorSet = new HashSet<>();
    private AuthcodeValidationListener datasetlistener;
    private RecyclerView recyclerView;
    private int selectedPosition;

    public CharEditAdapter(String[] myDataset, AuthcodeValidationListener datasetlistener) {
        dataSet = myDataset;
        dataUndoSet = new String[dataSet.length];
        System.arraycopy(dataSet, 0, dataUndoSet, 0, dataSet.length);
        this.datasetlistener = datasetlistener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public String[] getDataSet() {
        return dataSet;
    }

    public String[] getUndoDataSet() {
        return dataUndoSet;
    }

    public void setDataUndoSet(String[] undoDataSet) {
        dataUndoSet = undoDataSet;
    }

    public void setSelectedItem(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText charItem;
        ImageView undoButton;
        TextWatcher textListener;

        public ViewHolder(View view) {
            super(view);
            charItem = view.findViewById(R.id.char_item);
            undoButton = view.findViewById(R.id.image_undo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_edit_validate_authcode, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.charItem.removeTextChangedListener(holder.textListener);
        holder.charItem.setText(dataSet[position].toUpperCase());
        holder.textListener = new LocalEditTextListener(position);
        holder.charItem.addTextChangedListener(holder.textListener);
        holder.charItem.setOnFocusChangeListener((v, hasFocus) -> holder.undoButton.setActivated(hasFocus));
        holder.charItem.setSelectAllOnFocus(true);

        holder.charItem.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus();
                UiUtility.hideKeyboard(v.getContext());
                return true;
            }
            return false;
        });

        if (selectedPosition == position) {
            holder.charItem.requestFocus();
            holder.charItem.post(() -> UiUtility.showKeyboard(holder.charItem));
        }

        holder.undoButton.setOnClickListener(v -> {
            dataSet[position] = dataUndoSet[position];
            changedPosition.remove(position);
            datasetlistener.onItemUndoChanged(position);
            if (dataErrorSet.contains(position)) {
                dataErrorSet.remove(position);
                if (dataErrorSet.size() > 0) {
                    datasetlistener.setHasError(true);
                } else {
                    datasetlistener.setHasError(false);
                }
            }
            notifyItemChanged(position);
        });

        if (changedPosition.contains(position)) {
            holder.undoButton.setVisibility(View.VISIBLE);
        } else {
            holder.undoButton.setVisibility(View.GONE);
        }

        if (dataErrorSet.contains(position)) {
            holder.undoButton.setBackgroundResource(R.drawable.i_exclamation_selector);
            holder.charItem.setBackgroundResource(R.drawable.authcode_edit_char_red_border);
        } else {
            holder.undoButton.setBackgroundResource(R.drawable.i_back_selector);
            holder.charItem.setBackgroundResource(R.drawable.authcode_edit_char_border);
        }
    }

    public void setChangedPosition(HashSet<Integer> positions) {
        changedPosition = positions;
    }

    public HashSet<Integer> getChangedPosition() {
        return changedPosition;
    }


    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    private class LocalEditTextListener implements TextWatcher {
        private final int position;

        public LocalEditTextListener(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            Timber.i("text changed: position %1$d", position);
            dataSet[position] = charSequence.toString().toUpperCase();
            datasetlistener.onItemChanged(position);

            if (!changedPosition.contains(position)) {
                changedPosition.add(position);
                postAndNotifyAdapter(position);
            }

            if (dataSet[position].length() < 1) {
                dataErrorSet.add(position);
                datasetlistener.setHasError(true);

            } else {
                if (dataErrorSet.contains(position)) {
                    dataErrorSet.remove(position);
                    if (dataErrorSet.size() > 0) {
                        datasetlistener.setHasError(true);
                    } else {
                        datasetlistener.setHasError(false);
                    }
                }
            }
            postAndNotifyAdapter(position);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().equals("\n")) editable.clear();
        }
    }

    protected void postAndNotifyAdapter(int position) {
        handler.postDelayed(() -> {
            if (!recyclerView.isComputingLayout()) {
                this.notifyItemChanged(position);
            } else {
                postAndNotifyAdapter(position);
            }
        }, 50);
    }
}
