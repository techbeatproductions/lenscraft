package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignIn extends AppCompatActivity {

    EditText email, password;
    TextInputLayout emailLayout, passwordLayout;
    Button signIn;
    LinearLayout signUpClickable;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private static final String TAG = "SignInActivity";

    private List<List<Object>> tapData = new ArrayList<>();
    private List<Integer> pinDigits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initializing Edit Texts
        email = findViewById(R.id.emailSignInTxt);
        password = findViewById(R.id.passwordSignInTxt);

        // Initializing Text Input Layouts
        emailLayout = findViewById(R.id.usernameTextInputLayout);
        passwordLayout = findViewById(R.id.passwordTextInputLayoutSignIn);

        // Initializing Buttons
        signIn = findViewById(R.id.signInBtn);
        signUpClickable = findViewById(R.id.signUpClickable);

        // Initializing progress bar
        progressBar = findViewById(R.id.loginProgressBar);

        // Setting up event listeners
        setupListeners();

        // Capture touch data for authentication
        signIn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    captureTouchData(event, 0); // 0 for touch down
                    break;
                case MotionEvent.ACTION_UP:
                    captureTouchData(event, 1); // 1 for touch up
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ACTION_MOVE detected");
                    captureTouchData(event, 2); // 2 for touch move
                    break;
            }
            return false; // Return false to allow click event to proceed
        });
    }

    private void setupListeners() {
        signUpClickable.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(SignIn.this, SignUp.class);
            startActivity(signUpIntent);
        });

        signIn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString();

            hideKeyboard();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(SignIn.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEmailValid(emailInput)) {
                email.setError("Invalid email address");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "Starting user sign-in...");

            mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign-in successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserDataToSharedPreferences(user.getDisplayName(), emailInput);

                                // Retrieve user_id and user_model_directory from SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                String userId = sharedPreferences.getString("user_id", "defaultUserId");
                                String userModelDirectory = sharedPreferences.getString("user_model_directory", "defaultModelDirectory");

                                // Send touch dynamics data to server
                                String touchDataJson = createJsonData(userId, tapData, userModelDirectory);
                                sendDataToServer(touchDataJson);

                                // Navigate to another activity
                                Intent intent = new Intent(SignIn.this, CreatePackageItem.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "Sign-in failed", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void saveUserDataToSharedPreferences(String username, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        // Optional: Consider storing user_id and user_model_directory if they are available
        editor.apply();
    }

    // Method to initialize pin digits as individual entries
    private void initializePinDigits(String pin) {
        pinDigits.clear();
        for (char digit : pin.toCharArray()) {
            pinDigits.add(Character.getNumericValue(digit));
        }
    }

    // Method to create JSON data with tapData entries
    private String createJsonData(String userId, List<List<Object>> tapData, String userModelDirectory) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("user_model_directory", userModelDirectory);

            for (List<Object> entry : tapData) {
                JSONArray jsonEntry = new JSONArray();
                jsonEntry.put((Integer) entry.get(0)); // Pin digit
                jsonEntry.put((Double) entry.get(1)); // Event time
                jsonEntry.put((Double) entry.get(2)); // X position
                jsonEntry.put((Double) entry.get(3)); // Y position
                jsonEntry.put((Double) entry.get(4)); // Pressure
                jsonEntry.put((Integer) entry.get(5)); // Action type

                jsonArray.put(jsonEntry);
            }

            jsonObject.put("tap_data", jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON data", e);
            // Handle the exception, e.g., show a Toast or use default values
        }

        return jsonObject.toString();
    }

    private void sendDataToServer(final String jsonData) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                Log.d(TAG, "JSON Data to be sent: " + jsonData);

                URL url = new URL("https://501f-41-90-35-30.ngrok-free.app/authenticate"); // Replace with your actual URL
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Server response code: " + responseCode);

                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 400 ? connection.getInputStream() : connection.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d(TAG, "Server response: " + response.toString());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending data to server", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // Method to capture touch data, using each digit of the pin separately
    private void captureTouchData(MotionEvent event, int actionType) {
        String pinInput = password.getText().toString().trim();
        if (pinInput.isEmpty()) return;

        if (pinDigits.isEmpty()) {
            initializePinDigits(pinInput);
        }

        int pinIndex = tapData.size() % pinDigits.size(); // Cycle through pin digits
        int pinDigit = pinDigits.get(pinIndex);

        List<Object> touchEntry = new ArrayList<>();
        touchEntry.add(pinDigit); // Use the current pin digit
        touchEntry.add((double) event.getEventTime()); // Event time
        touchEntry.add((double) event.getX()); // X position
        touchEntry.add((double) event.getY()); // Y position
        touchEntry.add((double) event.getPressure()); // Pressure
        touchEntry.add(actionType); // Action type

        tapData.add(touchEntry);
    }
}
