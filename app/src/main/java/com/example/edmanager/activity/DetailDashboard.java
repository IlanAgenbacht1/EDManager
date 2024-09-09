package com.example.edmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DetailAdapter;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;

public class DetailDashboard extends AppCompatActivity {

    private DeliveryModel[] deliveryArray;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private TextView textViewTrip;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dashboard);

        recyclerView = findViewById(R.id.dashboardDetailRecycler);
        textViewTrip = findViewById(R.id.textViewDetailTripLabel);

        for (int i = 0; i < Constant.deliveryCount; i++) {

            deliveryArray = JsonHelper.returnDeliveryData();
        }

        /*database = new Database(this);
        database.open();

        ArrayList<String> listDeliveries = database.returnDeliveries(Constant.trip);*/

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detailAdapter = new DetailAdapter(deliveryArray, database, new DetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

                if (item.equals("ADD_DELIVERY")) {

                    startActivity(new Intent(DetailDashboard.this, NewDelivery.class));

                    finish();
                }
            }
        });

        recyclerView.setAdapter(detailAdapter);

        textViewTrip.setText("SHOWING DELIVERIES FOR " + Constant.trip);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(DetailDashboard.this, Dashboard.class));

        finish();
    }
}