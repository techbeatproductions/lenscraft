package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        mAuth = FirebaseAuth.getInstance();

        // Create a handler with a delayed action
        new Handler().postDelayed(() -> {
            // Check if an email is saved in SharedPreferences
            String userEmail = getEmailFromSharedPreferences();

            if (userEmail != null) {
                // Email is saved, check if user is authenticated
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is authenticated, navigate to SignIn activity
                    Log.d("MainActivity", "Email found and User is authenticated. Proceeding to SignIn activity.");
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                } else {
                    // User is not authenticated, could be logged out or session expired
                    Log.d("MainActivity", "Email found, but User is not authenticated. Redirecting to SignIn activity.");
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                }
            } else {
                // Email is not saved, navigate to Onboarding1 activity
                Log.d("MainActivity", "No email found. Redirecting to Onboarding1 activity.");
                Intent intent = new Intent(MainActivity.this, Onboarding1.class);
                startActivity(intent);
            }

            finish(); // Finish the splash screen activity
        }, SPLASH_DELAY);
    }

    // Retrieve the email from SharedPreferences
    private String getEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", null); // Default value of null if not found
    }
}
