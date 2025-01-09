package com.example.edmanager.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReturnsAdapter extends RecyclerView.Adapter<ReturnsAdapter.ViewHolder> {

    //private OnItemClickListener listener;
    private AdapterView.OnItemLongClickListener longClickListener;
    private ArrayList<DeliveryModel> listReturns;
    private Context context;


    public ReturnsAdapter(ArrayList<DeliveryModel> listReturns, Context context) {
        this.context = context;
        //this.listener = listener;
        this.listReturns = listReturns;
    }


    /*public interface OnItemClickListener {
        void onItemClick(String tripNumber);
        void onItemLongClick(String tripNumber);
    }*/


    @NonNull
    @Override
    public ReturnsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i("RETURNS", "onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_return_dashboard, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.i("RETURNS", "adapter pos: " + position);

        DeliveryModel returnData = listReturns.get(position);

        try {
            holder.bind(returnData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getItemCount() {

        Log.i("RETURNS", "getItemCount " + listReturns.size());

        return listReturns.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCustomer;
        private TextView textViewItemCode;
        private TextView textViewQuantity;
        private TextView textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCustomer = itemView.findViewById(R.id.textViewCustomer);
            textViewItemCode = itemView.findViewById(R.id.textViewItemCode);
            textViewQuantity = itemView.findViewById(R.id.textViewItemQty);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(DeliveryModel data) throws ParseException {

            textViewCustomer.setText(data.getCustomerName());
            textViewItemCode.setText(data.getItemCode());
            textViewQuantity.setText(data.getQuantity() + " items");

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
            Date date = inputFormat.parse(data.getTime());

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            String formattedDate = outputFormat.format(date);

            textViewDate.setText(formattedDate);

            /*itemView.setOnClickListener(view -> listener.onItemClick(data.getItemCode()));

            itemView.setOnLongClickListener(view -> {
                listener.onItemLongClick(data.getItemCode());
                return true; // Return true to indicate the event was handled
            });*/
        }

    }
}
