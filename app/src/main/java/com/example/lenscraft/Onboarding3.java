package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.WelcomeToLensCraftFragment;

public class Onboarding3 extends AppCompatActivity {

    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding3);

        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        next = findViewById(R.id.nextBtn3);


        // Begin the transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //Logo Fragment
        Fragment logo = new LensCraftLogo(); // Replace with the actual fragment you want to add
        transaction.replace(R.id.logoFragmentContainer3, logo);


        Fragment welcome = new WelcomeToLensCraftFragment(); // Replace with the actual fragment you want to add
        transaction.replace(R.id.welcomeFragmentContainer3, welcome);


        transaction.commit();

        onClickListeners();


    }

    public void onClickListeners(){


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Onboarding3.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}