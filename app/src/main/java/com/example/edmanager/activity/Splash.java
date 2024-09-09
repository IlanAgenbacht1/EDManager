package com.example.edmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.edmanager.R;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.utility.Constant;
import com.google.android.material.textfield.TextInputEditText;

public class Splash extends AppCompatActivity {

    ImageView ivLogo;

    RelativeLayout rlLayout;

    TextInputEditText editTextCompany;

    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = findViewById(R.id.iv_splashLogo);
        rlLayout = findViewById(R.id.rl_main);
        proceed = findViewById(R.id.btn_login);
        editTextCompany = findViewById(R.id.et_com);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constant.company = editTextCompany.getText().toString();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        DropboxHelper.fetchAllTrips(getApplicationContext());

                        startActivity(new Intent(Splash.this, Dashboard.class));
                        finish();
                    }
                });

                thread.start();
            }
        });

        animateLogo();
    }


    public void animateLogo() {

        ivLogo.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(500);

        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide.setStartOffset(1950);
        slide.setDuration(250);
        slide.setInterpolator(Splash.this, android.R.anim.accelerate_decelerate_interpolator);
        slide.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(fadeIn);
        set.addAnimation(slide);
        set.setFillAfter(true);

        ivLogo.startAnimation(set);

        rlLayout.setVisibility(View.VISIBLE);
        proceed.setVisibility(View.VISIBLE);

        Animation fadeInLayout = new AlphaAnimation(0, 1);
        fadeInLayout.setInterpolator(new DecelerateInterpolator()); //add this
        fadeInLayout.setDuration(600);
        fadeInLayout.setStartOffset(2225);

        rlLayout.setAnimation(fadeInLayout);
        proceed.setAnimation(fadeInLayout);
    }
}