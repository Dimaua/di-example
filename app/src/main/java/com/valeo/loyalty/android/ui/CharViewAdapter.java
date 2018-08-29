package com.valeo.loyalty.android.ui;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valeo.loyalty.android.R;

import java.util.HashSet;
import java.util.Set;

public class CharViewAdapter extends RecyclerView.Adapter<CharViewAdapter.ViewHolder> {

    private String[] dataSet;
    private Set<Integer> boldPosition = new HashSet<>();
    private View.OnClickListener onClickListener;

    public CharViewAdapter(String[] myDataset) {
        dataSet = myDataset;
    }

    public void setClickListener(View.OnClickListener callback) {
        onClickListener = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         public TextView charItem;

        public ViewHolder(View view) {
            super(view);
            charItem = view.findViewById(R.id.char_item);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_char_validate, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(view1 -> onClickListener.onClick(view1));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.charItem.setText(dataSet[holder.getAdapterPosition()]);

        if(boldPosition.contains(position)) {
            holder.charItem.setTypeface(holder.charItem.getTypeface(), Typeface.BOLD);
        } else {
            holder.charItem.setTypeface(holder.charItem.getTypeface(), Typeface.NORMAL);
        }
    }

    public void setBoldPosition(Set<Integer> positions) {
        boldPosition = positions;
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }



}
