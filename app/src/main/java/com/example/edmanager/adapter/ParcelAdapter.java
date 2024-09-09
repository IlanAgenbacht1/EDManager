package com.example.edmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.database.DeliveryModel;

import java.util.ArrayList;
import java.util.List;

public class ParcelAdapter extends RecyclerView.Adapter<ParcelAdapter.ViewHolder>{

    private List<String> listItems;
    private DeliveryModel data;
    private OnItemClickListener listener;


    public ParcelAdapter(ArrayList<String> listItems, OnItemClickListener listener) {

        this.listItems = listItems;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_parcel, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (!listItems.isEmpty()) {

            holder.bind(listItems.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {

        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewItem = itemView.findViewById(R.id.itemParcelTextView);
        }

        public void bind(final String item, final OnItemClickListener listener) {

            textViewItem.setText(item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item);
                }
            });
        }
    }
}
