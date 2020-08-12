package com.geekbrains.myweatherv3;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerDataAdapterForCity extends RecyclerView.Adapter<RecyclerDataAdapterForCity.ViewHolder> {
    private ArrayList<String> data;
    private IRVOnItemClick onItemClickCallback;
    private int selectedPos = 0;

    public RecyclerDataAdapterForCity(ArrayList<String> data, IRVOnItemClick onItemClickCallback) {
        this.data = data;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_layout, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = data.get(position);

        holder.setTextToTextView(text);
        holder.setOnClickForItem(text);
        holder.itemView.setBackgroundColor(selectedPos == position ? Color.GREEN : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTextView);
        }

        void setTextToTextView(String text) {
            textView.setText(text);
        }

        void setOnClickForItem(final String text) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        if (getAdapterPosition() == RecyclerView.NO_POSITION)
                            return;
                        notifyItemChanged(selectedPos);
                        selectedPos = getAdapterPosition();
                        notifyItemChanged(selectedPos);
                        onItemClickCallback.onItemClicked(text);
                    }
                }
            });
        }
    }
}