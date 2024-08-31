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
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.SignUpWelcomeMsgFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUp extends AppCompatActivity {

    EditText email, password, repeatPassword;
    TextInputLayout usernameLayout, emailLayout, passwordLayout, repeatPasswordLayout;
    Button signUp;
    LinearLayout signInClickable;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String TAG = "SignUpActivity";

    private List<List<Object>> tapData = new ArrayList<>();
    private List<Integer> pinDigits = new ArrayList<>();

    private boolean isPinInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Begin the transaction to replace fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace logo fragment
        Fragment logo = new LensCraftLogo();
        transaction.replace(R.id.logoFragmentContainer4, logo);

        // Replace welcome fragment
        Fragment welcome = new SignUpWelcomeMsgFragment();
        transaction.replace(R.id.WelcomeFragmentContainer4, welcome);

        transaction.commit();

        // Initializing Edit Text
        usernameLayout = findViewById(R.id.usernameTextInputLayout);
        email = findViewById(R.id.emailTxt);
        password = findViewById(R.id.passwordTxt);
        repeatPassword = findViewById(R.id.repeatpasswordTxt);

        // Initializing Text Input Layout
        emailLayout = findViewById(R.id.emailTextInputLayout);
        passwordLayout = findViewById(R.id.passwordTextInputLayout);
        repeatPasswordLayout = findViewById(R.id.repeatpasswordTextInputLayout);

        // Initializing Buttons
        signUp = findViewById(R.id.signUpBtn);
        signInClickable = findViewById(R.id.signInClickable);

        // Initializing progress bar
        progressBar = findViewById(R.id.signUpProgressBar);



        changeTextInputLayoutDrawableColors();
        validators();

        signUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "Touch event: action=" + event.getAction() + ", x=" + event.getX() + ", y=" + event.getY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN detected");
                        captureTouchData(event, 0); // 0 for touch down
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_MOVE detected");
                        captureTouchData(event, 1); // 1 for touch up
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "ACTION_MOVE detected");
                        captureTouchData(event, 2); // 2 for touch move
                        break;
                }
                return false; // Return false to allow click event to proceed
            }
        });
    }

    public void changeTextInputLayoutDrawableColors() {
        // Your code for changing icon tint colors
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void validators() {
        signInClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(SignUp.this, SignIn.class);
                startActivity(signInIntent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = usernameLayout.getEditText().getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString();
                String repeatPasswordInput = repeatPassword.getText().toString();

                hideKeyboard();

                if (usernameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || repeatPasswordInput.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid(emailInput) || !passwordInput.equals(repeatPasswordInput)) {
                    if (!isEmailValid(emailInput)) {
                        email.setError("Invalid email address");
                    }
                    if (!passwordInput.equals(repeatPasswordInput)) {
                        repeatPassword.setError("Passwords do not match");
                    }
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Starting user creation...");

                    mAuth.fetchSignInMethodsForEmail(emailInput)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().getSignInMethods().isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.d(TAG, "Email already in use");
                                    Toast.makeText(SignUp.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else {
                                    createNewUser(usernameInput, emailInput, passwordInput);
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "Failed to check email existence", e);
                                Toast.makeText(SignUp.this, "Failed to check email existence: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            private void createNewUser(String usernameInput, String emailInput, String passwordInput) {
                mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User creation successful");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(usernameInput)
                                            .build();
                                    user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            Log.d(TAG, "User profile updated successfully");

                                            // Save email and username to SharedPreferences
                                            saveUserDataToSharedPreferences(usernameInput, emailInput);

                                            // Save user information to the database
                                            User newUser = new User(usernameInput, emailInput);
                                            mDatabase.child("users").child(user.getUid()).setValue(newUser)
                                                    .addOnCompleteListener(dbTask -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (dbTask.isSuccessful()) {
                                                            Log.d(TAG, "User information saved to database successfully");

                                                            // Send touch dynamics data to server
                                                            String touchDataJson = createJsonData(usernameInput, tapData);
                                                            sendDataToServer(touchDataJson);

                                                            Intent intent = new Intent(SignUp.this, CreatePackageItem.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Log.d(TAG, "Failed to save user information to database", dbTask.getException());
                                                            Toast.makeText(SignUp.this, "Database update failed: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Log.d(TAG, "User profile update failed", profileTask.getException());
                                            Toast.makeText(SignUp.this, "Profile update failed: " + profileTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "User creation failed", task.getException());
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(SignUp.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUp.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
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
        });
    }

    private void saveUserDataToSharedPreferences(String username, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.apply();
    }
    // Method to initialize pin digits as individual entries
    private void initializePinDigits(String pin) {
        pinDigits.clear();
        for (char digit : pin.toCharArray()) {
            pinDigits.add(Character.getNumericValue(digit));
        }
        // Log pin digits for verification
        isPinInitialized = true;
        Log.d(TAG, "Pin digits initialized: " + pinDigits.toString());
    }

    // Method to create JSON data with tapData entries
    private String createJsonData(String userId, List<List<Object>> tapData) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            jsonObject.put("user_id", userId);

            // Add tap_data array to the JSON object
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

                // Extract user_id from jsonData
                String userId = extractUserIdFromJson(jsonData);
                if (userId != null) {
                    saveToSharedPreferences("user_id", userId);
                }

                URL url = new URL("https://501f-41-90-35-30.ngrok-free.app/enroll_user");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Set timeouts
                connection.setConnectTimeout(20000); // 20 seconds
                connection.setReadTimeout(20000);    // 20 seconds

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

                    if (responseCode >= 200 && responseCode < 400) {
                        // Extract and save the user_model_directory from the response
                        String userModelDirectory = extractUserModelDirectoryFromJson(response.toString());
                        saveToSharedPreferences("user_model_directory", userModelDirectory);
                    } else {
                        Log.e(TAG, "Error response: " + response.toString());
                    }
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


    // Helper method to extract user ID from the JSON data before sending it
    private String extractUserIdFromJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return jsonObject.getString("user_id");
        } catch (JSONException e) {
            Log.e(TAG, "Error extracting user ID from JSON data", e);
            return null;
        }
    }

    // Helper method to extract user_model_directory from the server response JSON
    private String extractUserModelDirectoryFromJson(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getString("user_model_directory");
        } catch (JSONException e) {
            Log.e(TAG, "Error extracting user_model_directory from JSON response", e);
            return null;
        }
    }

    // Helper method to save data to SharedPreferences
    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Method to capture touch data, using each digit of the pin separately
    private void captureTouchData(MotionEvent event, int actionType) {
        String pinInput = password.getText().toString().trim();
        if (pinInput.isEmpty()) return;

        // Initialize pin digits if they haven't been initialized
        if (!isPinInitialized) {
            initializePinDigits(pinInput);
        }

        Log.d(TAG, "Pin digits list in CaptureTouchData: " + pinDigits.toString()); // Log before processing touch data
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

        // Log touch data
        Log.d(TAG, "Tap data added: " + touchEntry.toString());
        Log.d(TAG, "Total tap data size: " + tapData.size());
    }


}
