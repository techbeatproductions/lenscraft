package com.example.lenscraft;

// SignIn.java
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.example.lenscraft.APIRequests.LoginTask;
import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.SignInWelcomeFragment;

public class SignIn extends AppCompatActivity implements LoginTask.LoginTaskListener {
    EditText usernameSignIn, passwordSignIn;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Begin the transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Logo Fragment
        Fragment logo = new LensCraftLogo();
        transaction.replace(R.id.logoFragmentContainer5, logo);

        Fragment welcome = new SignInWelcomeFragment();
        transaction.replace(R.id.WelcomeFragmentContainer5, welcome);

        transaction.commit();

        //Initialize UI elements
        usernameSignIn = findViewById(R.id.usernameTxt);
        passwordSignIn = findViewById(R.id.passwordSignInTxt);
        signIn = findViewById(R.id.signInBtn);

        validators();
    }

    public void validators() {
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = usernameSignIn.getText().toString().trim();
                String passwordInput = passwordSignIn.getText().toString();

                if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(SignIn.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call the LoginTask to perform the login
                new LoginTask(SignIn.this, SignIn.this).execute(usernameInput, passwordInput);
            }
        });
    }

    @Override
    public void onLoginCompleted(String result) {
        if (result != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                String status = jsonResponse.getString("status");

                if (status.equals("success")) {
                    // Login successful, navigate to CreatePackageItem Activity
                    Intent intent = new Intent(this, CreatePackageItem.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Handle null response or errors
        }
    }
}
