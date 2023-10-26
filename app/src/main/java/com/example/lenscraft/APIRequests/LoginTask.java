package com.example.lenscraft.APIRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginTask extends AsyncTask<String, Void, String> {
    public interface LoginTaskListener {
        void onLoginCompleted(String result);
    }

    private static final String TAG = "LoginTask";
    private LoginTaskListener listener;
    private Context context;

    public LoginTask(Context context, LoginTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        try {
            // Make an HTTP request to the login API
            URL url = new URL("https://jackal-modern-javelin.ngrok-free.app/login_users_lenscraft.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Set the request headers for form data
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Create form data
            String formData = "username=" + username + "&password=" + password;
            byte[] postData = formData.getBytes(StandardCharsets.UTF_8);

            // Write the form data to the request
            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.write(postData);
            }

            // Log the request
            Log.d(TAG, "Request: " + formData);

            // Get the response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the response as JSON to extract the user_id
                try {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    int userID = jsonResponse.optInt("user_id");

                    // Save user_id to shared preferences
                    saveUserIdToSharedPreferences(userID);

                    // Log the response
                    Log.d(TAG, "Response: " + response.toString());

                    return response.toString();
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                }
            } else {
                // Handle error
                Log.e(TAG, "HTTP error code: " + responseCode);
            }
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if (listener != null) {
            listener.onLoginCompleted(response);
        }
    }

    private void saveUserIdToSharedPreferences(int userID) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("user_id", userID);
        editor.apply();

        // Log the saved user ID
        Log.d(TAG, "Saved user ID to SharedPreferences: " + userID);
    }
}
