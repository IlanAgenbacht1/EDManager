package com.example.edmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<String> listTrips, listConfiguredTrips;
    private OnItemClickListener listener;
    private Database database;


    public DashboardAdapter(ArrayList<String> listTrips, ArrayList<String> listConfiguredTrips, Database database, OnItemClickListener listener) {

        this.listTrips = listTrips;
        this.listConfiguredTrips = listConfiguredTrips;
        this.database = database;
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(String tripNumber);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        if (viewType == 0) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_empty, parent, false);

            return new EmptyTripViewHolder(view);

        } else {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);

            return new ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof EmptyTripViewHolder) {

            ((EmptyTripViewHolder) holder).bind(listTrips.get(position), listener);

        } else {

            int deliveryCount = database.returnDeliveryCount(listTrips.get(position));

            ((ViewHolder) holder).bind(listTrips.get(position), deliveryCount, listener);
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (listConfiguredTrips.contains(listTrips.get(position))) {

            return 1;

        } else {

            return 0;
        }
    }


    @Override
    public int getItemCount() {

        return listTrips.size();
    }


    public static class EmptyTripViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTrip;

        public EmptyTripViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTrip = itemView.findViewById(R.id.itemTripTextView);
        }

        public void bind(final String tripName, final OnItemClickListener listener) {

            textViewTrip.setText(tripName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(tripName);
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTrip;
        private TextView textViewCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTrip = itemView.findViewById(R.id.textViewTrip);
            textViewCount = itemView.findViewById(R.id.textViewDeliveryCount);
        }

        public void bind(final String tripName, final int deliveryCount, final OnItemClickListener listener) {

            textViewTrip.setText(tripName);

            if (deliveryCount > 1) {

                textViewCount.setText(deliveryCount + " Deliveries");

            } else {

                textViewCount.setText(deliveryCount + " Delivery");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(tripName);
                }
            });
        }

    }
}
