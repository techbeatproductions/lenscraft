package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lenscraft.APIRequests.UserRegistrationTask;
import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.SignUpWelcomeMsgFragment;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity implements UserRegistrationTask.RegistrationResultListener {

    EditText username, email, password, repeatPassword;
    TextInputLayout usernameLayout, emailLayout, passwordLayout, repeatPasswordLayout;
    Button signUp;
    LinearLayout signInClickable;

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Begin the transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Logo Fragment
        Fragment logo = new LensCraftLogo(); // Replace with the actual fragment you want to add
        transaction.replace(R.id.logoFragmentContainer4, logo);

        Fragment welcome = new SignUpWelcomeMsgFragment(); // Replace with the actual fragment you want to add
        transaction.replace(R.id.WelcomeFragmentContainer4, welcome);

        transaction.commit();

        // Initializing Edit Text
        username = findViewById(R.id.usernameTxt);
        email = findViewById(R.id.emailTxt);
        password = findViewById(R.id.passwordTxt);
        repeatPassword = findViewById(R.id.repeatpasswordTxt);

        // Initializing Text Input Layout
        usernameLayout = findViewById(R.id.usernameTextInputLayout);
        emailLayout = findViewById(R.id.emailTextInputLayout);
        passwordLayout = findViewById(R.id.passwordTextInputLayout);
        repeatPasswordLayout = findViewById(R.id.repeatpasswordTextInputLayout);

        // Initializing Buttons
        signUp = findViewById(R.id.signInBtn);
        signInClickable = findViewById(R.id.signInClickable);

        changeTextInputLayoutDrawableColors();
        validators();
    }

    public void changeTextInputLayoutDrawableColors() {
        // Your code for changing icon tint colors
    }

    private boolean isEmailValid(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    public void validators() {
        // Set an OnClickListener for signInClickable
        signInClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignIn activity when signInClickable is clicked
                Intent signInIntent = new Intent(SignUp.this, SignIn.class);
                startActivity(signInIntent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString();
                String repeatPasswordInput = repeatPassword.getText().toString();

                // Check if any of the fields are empty
                if (usernameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || repeatPasswordInput.isEmpty()) {
                    // Show an error message or handle it as needed
                    // For example, show a toast message:
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick function
                }

                if (!isEmailValid(emailInput) || !passwordInput.equals(repeatPasswordInput)) {
                    // Additional validation checks
                    if (!isEmailValid(emailInput)) {
                        // The email is not valid; show an error message or handle it as needed
                        email.setError("Invalid email address");
                    }
                    if (!passwordInput.equals(repeatPasswordInput)) {
                        // Passwords do not match; show an error message or handle it as needed
                        repeatPassword.setError("Passwords do not match");
                    }
                } else {
                    // Create a JSON object with user registration data
                    JSONObject userData = new JSONObject();
                    try {
                        userData.put("firstName", "");  // You can set these fields as needed
                        userData.put("lastName", "");   // You can set these fields as needed
                        userData.put("username", usernameInput);
                        userData.put("email", emailInput);
                        userData.put("password", passwordInput);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Log the registration request data
                    Log.d("RegistrationRequest", "Request: " + userData.toString());

                    // Execute the AsyncTask to perform user registration
                    UserRegistrationTask registrationTask = new UserRegistrationTask(SignUp.this);
                    registrationTask.execute(userData);
                }
            }
        });
    }


    @Override
    public void onRegistrationResult(JSONObject result) {
        // Log the registration response data
        Log.d("RegistrationResponse", "Response: " + result.toString());

        if (result != null) {
            try {
                if (result.has("success") && result.getBoolean("success")) {
                    // Registration was successful, retrieve the user ID
                    int userID = result.getInt("userID");

                    // Save the user ID to shared preferences
                    saveUserIDToSharedPreferences(userID);

                    // Login successful, navigate to CreatePackageItem Activity
                    Intent intent = new Intent(this, CreatePackageItem.class);
                    startActivity(intent);

                    // Example: You can navigate to a new activity or perform other actions here
                } else {
                    // Registration was not successful
                    String error = result.optString("error");
                    // Handle the error, display an error message, etc.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Save the user ID to shared preferences
    private void saveUserIDToSharedPreferences(int userID) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userID", userID);
        editor.apply();
    }
}
