package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Create a handler with a delayed action
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if a user ID is saved in SharedPreferences
                int userID = getUserIDFromSharedPreferences();

                if (userID != -1 && isUsernameFetched()) {
                    // User ID is saved, navigate to SignIn activity

                    if (isUsernameFetched()) {
                        // Username is fetched, navigate to SignIn activity
                        Log.d("MainActivity", "User ID found and Username has been fetched");
                        Intent intent = new Intent(MainActivity.this, SignIn.class);
                        startActivity(intent);
                        finish(); // Finish the splash screen activity
                    } else {
                        // Username is not fetched, you can handle this case or show a message
                        Log.d("MainActivity", "User ID found, but Username has not been fetched");
                    }

                } else {
                    // User ID is not saved, navigate to Onboarding1 activity
                    Intent intent = new Intent(MainActivity.this, Onboarding1.class);
                    startActivity(intent);
                }
                finish(); // Finish the splash screen activity
            }
        }, SPLASH_DELAY);
    }

    // Retrieve the user ID from SharedPreferences
    private int getUserIDFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("userID", -1); // Default value of -1 if not found
    }

    private boolean isUsernameFetched() {
        // You can implement your logic to check if the username is fetched here
        // Return true if fetched, false otherwise
        return true; // Change this as needed
    }



}