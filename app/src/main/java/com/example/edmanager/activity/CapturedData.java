package com.example.edmanager.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.edmanager.R;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.utility.Constant;

public class CapturedData extends AppCompatActivity {

    private TextView textViewCustomer, textViewDeliveryNumber;
    private ImageView imageViewItem, imageViewSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_data);

        textViewCustomer = findViewById(R.id.CapturedData_customerName);
        textViewDeliveryNumber = findViewById(R.id.CapturedData_documentNumber);
        imageViewItem = findViewById(R.id.CapturedData_imageViewItem);
        imageViewSignature = findViewById(R.id.CapturedData_imageViewSignature);

        textViewCustomer.setText(Constant.deliveryData.getCustomerName());
        textViewDeliveryNumber.setText(Constant.deliveryData.getDocument());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap itemBitMap = DropboxHelper.returnCapturedImage("/Customers/" + Constant.company + "/Completed/" + Constant.trip + "/" + Constant.deliveryData.getDocument() + "/" + Constant.deliveryData.getImagePath() + ".jpg");
                Bitmap signBitMap = DropboxHelper.returnCapturedImage("/Customers/" + Constant.company + "/Completed/" + Constant.trip + "/" + Constant.deliveryData.getDocument() + "/" + Constant.deliveryData.getSignPath() + ".jpg");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        imageViewItem.setImageBitmap(itemBitMap);
                        imageViewSignature.setImageBitmap(signBitMap);
                    }
                });
            }
        });

        thread.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(CapturedData.this, DetailDashboardCompleted.class));

        finish();
    }
}