package com.example.edmanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private ArrayList<String> listTrips;
    private OnItemClickListener listener;
    private Context context;


    public DashboardAdapter(ArrayList<String> listTrips, Context context, OnItemClickListener listener) {

        this.listTrips = listTrips;
        this.context = context;
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(String tripNumber);
        void onItemLongClick(String tripNumber);
    }


    @NonNull
    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int deliveryCount = JsonHelper.returnDeliveryCount(context, listTrips.get(position));

        holder.bind(listTrips.get(position), deliveryCount, listener);
    }


    @Override
    public int getItemCount() {

        return listTrips.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTrip;
        private TextView textViewCount;
        private TextView textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTrip = itemView.findViewById(R.id.textViewTrip);
            textViewCount = itemView.findViewById(R.id.textViewDeliveryCount);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(final String tripName, final int deliveryCount, OnItemClickListener listener) {

            if (Constant.highlightTripDelete) {

                if (Constant.listDeleteTrips.contains(tripName)) {

                    itemView.setForeground(itemView.getResources().getDrawable(R.drawable.border_dashboard_status_inprogress, null));

                } else {

                    itemView.setForeground(null);
                }

                Constant.highlightTripDelete = false;
            }

            textViewTrip.setText(tripName);

            if (deliveryCount > 1) {

                textViewCount.setText(deliveryCount + " Deliveries");

            } else if (deliveryCount == 0){

                textViewCount.setText("No Deliveries");

            } else {

                textViewCount.setText(deliveryCount + " Delivery");
            }

            if (Constant.listInProgressTrips.contains(tripName)) {

                textViewStatus.setVisibility(View.VISIBLE);

                textViewStatus.setText("In Progress");
                textViewStatus.setTextColor(itemView.getResources().getColor(R.color.gold, null));

            } else if (Constant.listCompletedTrips.contains(tripName)) {

                textViewStatus.setVisibility(View.VISIBLE);

                textViewStatus.setText("Completed");
                textViewStatus.setTextColor(itemView.getResources().getColor(R.color.green, null));

            } else if (Constant.listConfiguredTrips.contains(tripName) && !Constant.listInProgressTrips.contains(tripName) && !Constant.listCompletedTrips.contains(tripName)) {

                Log.i("SYNC", tripName + " configured");

                textViewStatus.setVisibility(View.INVISIBLE)    ;
            }

            // Set click listener
            itemView.setOnClickListener(view -> listener.onItemClick(tripName));

            // Set long click listener
            itemView.setOnLongClickListener(view -> {
                listener.onItemLongClick(tripName);
                return true; // Return true to indicate the event was handled
            });
        }

    }
}
