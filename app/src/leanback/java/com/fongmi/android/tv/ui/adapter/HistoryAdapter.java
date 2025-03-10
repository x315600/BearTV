package com.fongmi.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.databinding.AdapterWordBinding;
import com.fongmi.android.tv.utils.Prefers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final OnClickListener mListener;
    private final List<String> mItems;
    private final Gson mGson;

    public HistoryAdapter(OnClickListener listener) {
        this.mListener = listener;
        this.mGson = new Gson();
        this.mItems = getItems();
        this.mListener.onDataChanged(mItems.size());
    }

    public interface OnClickListener {

        void onItemClick(String item);

        void onDataChanged(int size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final AdapterWordBinding binding;

        public ViewHolder(@NonNull AdapterWordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(mItems.get(getLayoutPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            mItems.remove(getLayoutPosition());
            notifyItemRemoved(getLayoutPosition());
            mListener.onDataChanged(getItemCount());
            Prefers.putKeyword(mGson.toJson(mItems));
            return true;
        }
    }

    private List<String> getItems() {
        if (Prefers.getKeyword().isEmpty()) return new ArrayList<>();
        return mGson.fromJson(Prefers.getKeyword(), new TypeToken<List<String>>() {}.getType());
    }

    private void checkToAdd(String item) {
        int index = mItems.indexOf(item);
        if (index == -1) {
            mItems.add(0, item);
            notifyItemInserted(0);
        } else {
            mItems.remove(index);
            mItems.add(0, item);
            notifyItemRangeChanged(0, mItems.size());
        }
        if (mItems.size() > 8) {
            mItems.remove(8);
            notifyItemRemoved(8);
        }
    }

    public void add(String item) {
        checkToAdd(item);
        mListener.onDataChanged(getItemCount());
        Prefers.putKeyword(mGson.toJson(mItems));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterWordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.text.setText(mItems.get(position));
    }
}
