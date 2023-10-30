package com.example.lenscraft;

// SignIn.java
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.example.lenscraft.APIRequests.FetchUsernameTask;
import com.example.lenscraft.APIRequests.LoginTask;
import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.SignInWelcomeFragment;

public class SignIn extends AppCompatActivity implements LoginTask.LoginTaskListener {
    EditText usernameSignIn, passwordSignIn;
    Button signIn;
    LinearLayout signUpClickable;
    private String fetchedUsername;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);

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
        signUpClickable = findViewById(R.id.signUpClickable);
        progressBar = findViewById(R.id.loginProgressBar);

        // Call the AsyncTask to fetch the username
        new FetchUsernameTask(this, new FetchUsernameTask.OnUsernameFetchedListener() {
            @Override
            public void onUsernameFetched(String username) {
                // Handle the fetched username
                fetchedUsername = username;
            }
        }).execute();


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

                hideKeyboard();
                progressBar.setVisibility(View.VISIBLE);

                // Call the LoginTask to perform the login
                // Call the LoginTask to perform the login

                // Call the LoginTask to perform the login
                new LoginTask(SignIn.this, new LoginTask.LoginTaskListener() {
                    @Override
                    public void onLoginCompleted(String result) {
                        progressBar.setVisibility(View.GONE);

                        // Handle the login result here
                        if (result != null) {
                            try {
                                JSONObject jsonResponse = new JSONObject(result);
                                String status = jsonResponse.getString("status");

                                if (status.equals("success")) {
                                    // Login successful, navigate to CreatePackageItem Activity
                                    Intent intent = new Intent(SignIn.this, CreatePackageItem.class);
                                    startActivity(intent);
                                    finish();
                                } else if (status.equals("error")) {
                                    String message = jsonResponse.getString("message");
                                    if (message.equals("User does not exist")) {
                                        // Set an error message for the username field
                                        usernameSignIn.setError("User does not exist");
                                    } else if (message.equals("Invalid password")) {
                                        passwordSignIn.setError("Invalid password");
                                    } else {
                                        // Handle other error cases here
                                        Toast.makeText(SignIn.this, "Login failed: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SignIn.this, "Login failed: JSON parsing error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle null response or errors
                            Toast.makeText(SignIn.this, "Login failed: No response from the server", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).execute(usernameInput, passwordInput);

            }
        });

        signUpClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
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
                    finish();

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
