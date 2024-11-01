package com.example.edmanager.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private ArrayList<String> listTrips;
    private OnItemClickListener listener;
    private AdapterView.OnItemLongClickListener longClickListener;
    private Context context;


    /*public DashboardAdapter(ArrayList<String> listTrips, Context context, AdapterView.OnItemLongClickListener longClickListener) {

        this.listTrips = listTrips;
        this.context = context;
        this.longClickListener = longClickListener;
    }*/

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

        if (Constant.archive) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archive_dashboard, parent, false);

            return new ViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);

            return new ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.i("SYNC", (position + 1) + " holder binded");

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
            Log.i("SYNC", "adapter binded " + tripName);

            // Set the foreground based on trip status
            if (Constant.listDeleteTrips.contains(tripName)) {

                itemView.setForeground(itemView.getResources().getDrawable(R.drawable.border_dashboard_status_inprogress, null));

            } else {

                TypedValue typedValue = new TypedValue();
                Context context = itemView.getContext();
                context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                itemView.setForeground(context.getDrawable(typedValue.resourceId));
            }

            // Set trip name
            textViewTrip.setText(tripName);

            // Set delivery count text
            String deliveryText = deliveryCount > 1 ? deliveryCount + " Deliveries" :
                    deliveryCount == 0 ? "No Deliveries" :
                            deliveryCount + " Delivery";
            textViewCount.setText(deliveryText);

            // Set trip status
            if (!Constant.archive) {

                if (Constant.listInProgressTrips.contains(tripName)) {

                    textViewStatus.setVisibility(View.VISIBLE);
                    textViewStatus.setText("In Progress");
                    textViewStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gold));

                    Constant.adapterInProgressTrips.add(tripName);

                    if (Constant.adapterRemoveInProgressTrip.equals(tripName)) {

                        textViewStatus.setVisibility(View.INVISIBLE);

                        Constant.listInProgressTrips.remove(tripName);
                        Constant.adapterInProgressTrips.remove(tripName);

                        Constant.adapterRemoveInProgressTrip = "";
                    }

                } else if (Constant.listCompletedTrips.contains(tripName)) {
                    textViewStatus.setVisibility(View.VISIBLE);
                    textViewStatus.setText("Completed");
                    textViewStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));

                    startFlashingAnimation(itemView);

                    Log.i("SYNC", tripName + "adapter completed");

                } else if (Constant.listStagingTrips.contains(tripName)) {
                    textViewStatus.setVisibility(View.VISIBLE);
                    textViewStatus.setText("Staging");
                    textViewStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pearl));

                    Constant.adapterStagingTrips.add(tripName);

                    if (Constant.adapterRemoveStagingTrip.equals(tripName)) {

                        textViewStatus.setVisibility(View.INVISIBLE);

                        Constant.listStagingTrips.remove(tripName);
                        Constant.adapterStagingTrips.remove(tripName);

                        Constant.adapterRemoveStagingTrip = "";
                    }

                } else if (Constant.listConfiguredTrips.contains(tripName)) {

                    textViewStatus.setVisibility(View.INVISIBLE);

                    Log.i("SYNC", tripName + "adapter open");

                } else {

                    textViewStatus.setVisibility(View.GONE); // In case none of the conditions are met
                }
            }

            // Set click listener
            itemView.setOnClickListener(view -> listener.onItemClick(tripName));

            // Set long click listener
            itemView.setOnLongClickListener(view -> {
                listener.onItemLongClick(tripName);
                Constant.tripHighlightedForDelete = true;
                return true; // Return true to indicate the event was handled
            });
        }

        private void startFlashingAnimation(View view) {
            view.setAlpha(1f); // Ensure it's fully visible

            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.75f);
            fadeOut.setDuration(500); // Duration of fade out
            fadeOut.setRepeatCount(ValueAnimator.INFINITE);
            fadeOut.setRepeatMode(ValueAnimator.REVERSE);

            fadeOut.start();
        }

    }
}
