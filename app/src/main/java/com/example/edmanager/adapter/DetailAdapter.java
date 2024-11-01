package com.example.edmanager.adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.utility.Constant;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<DeliveryModel> listDeliveries;
    private OnItemClickListener listener;


    public DetailAdapter(ArrayList<DeliveryModel> listDeliveries, OnItemClickListener listener) {

        this.listDeliveries = listDeliveries;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
        void onItemLongClick(String deliveryNumber, int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1 && Constant.tripStaging) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_add_button, parent, false);

            return new ButtonViewHolder(view);

        } else if (Constant.deliveryCompleted) {

            if (Constant.archive) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_delivery_archived, parent, false);

                return new ViewHolder(view);

            } else {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_delivery_completed, parent, false);

                return new ViewHolder(view);
            }

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_delivery, parent, false);

            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ButtonViewHolder) {

            ((ButtonViewHolder) holder).bind(listener);

        } else {

            if (!listDeliveries.isEmpty()) {

                DeliveryModel data = listDeliveries.get(position);

                ((ViewHolder) holder).bind(data, listener);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == getItemCount() - 1) {

            return 1;

        } else {

            return 0;
        }
    }

    @Override
    public int getItemCount() {

        if (Constant.tripStaging) {

            return listDeliveries.size() + 1;

        } else {

            return listDeliveries.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDelivery;
        private TextView textViewParcelCount;
        private TextView textViewCustomer;
        private TextView textViewAddress;
        private TextView textViewContactName;
        private TextView textViewContactNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDelivery = itemView.findViewById(R.id.textViewDetailDeliveryName);
            textViewParcelCount = itemView.findViewById(R.id.textViewDetailParcelCount);
            textViewCustomer = itemView.findViewById(R.id.textViewDetailCustomer);
            textViewAddress = itemView.findViewById(R.id.textViewDetailAddress);
            textViewContactName = itemView.findViewById(R.id.textViewDetailContactName);
            textViewContactNumber = itemView.findViewById(R.id.textViewDetailContactNumber);
        }

        public void bind(final DeliveryModel data, final OnItemClickListener listener) {
            Log.i("UI", "DetailAdapter binded " + data.getDocument());

            if (Constant.listDeleteDeliveries.contains(data.getDocument())) {

                itemView.setForeground(itemView.getResources().getDrawable(R.drawable.border_dashboard_status_inprogress, null));

            } else {

                TypedValue typedValue = new TypedValue();
                Context context = itemView.getContext();
                context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                itemView.setForeground(context.getDrawable(typedValue.resourceId));
            }

            textViewDelivery.setText(data.getDocument());
            textViewParcelCount.setText(data.getNumberOfParcels() + " Parcels");
            textViewCustomer.setText(data.getCustomerName());
            textViewAddress.setText(data.getAddress());
            textViewContactName.setText(data.getContactName());
            textViewContactNumber.setText(data.getContactNumber());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(data.getDocument());
                }
            });

            itemView.setOnLongClickListener(view -> {
                listener.onItemLongClick(data.getDocument(), getAdapterPosition());
                return true; // Return true to indicate the event was handled
            });
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        public void bind(final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick("ADD_DELIVERY");
                }
            });
        }
    }
}
