package com.example.lenscraft.APIRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchUsernameTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private OnUsernameFetchedListener listener;
    private String apiUrl;

    public FetchUsernameTask(Context context, OnUsernameFetchedListener listener) {
        this.context = context;
        this.listener = listener;
        this.apiUrl = "https://jackal-modern-javelin.ngrok-free.app/fetch_user_lenscraft.php";
    }

    @Override
    protected String doInBackground(Void... voids) {
        String username = null;

        // Retrieve the user ID from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", -1);

        // Log the user ID
        android.util.Log.d("FetchUsernameTask", "User ID: " + userID);

        if (userID == -1) {
            // Handle the case where the user ID is not found in shared preferences
            return null;
        }

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Create a JSON object to send the user ID
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("userID", userID);

            // Write the JSON data to the request body
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(jsonInput.toString());
            outputStream.flush();
            outputStream.close();

            // Check for a successful response code (e.g., HTTP 200)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response to get the username
                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("username")) {
                    username = jsonResponse.getString("username");
                }
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return username;
    }

    @Override
    protected void onPostExecute(String username) {
        if (listener != null) {
            listener.onUsernameFetched(username);

            // Log the fetched username
            if (username != null) {
                android.util.Log.d("FetchUsernameTask", "Fetched Username: " + username);
            } else {
                android.util.Log.d("FetchUsernameTask", "Username not found or error occurred");
            }
        }
    }

    public interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }
}
