package com.example.lenscraft.APIRequests;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UserRegistrationTask extends AsyncTask<JSONObject, Void, JSONObject> {

    private static final String TAG = "UserRegistrationTask";

    // Define an interface to handle the result
    public interface RegistrationResultListener {
        void onRegistrationResult(JSONObject result);
    }

    private final RegistrationResultListener listener;

    public UserRegistrationTask(RegistrationResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects) {
        try {
            // The URL for user registration API
            URL url = new URL("https://jackal-modern-javelin.ngrok-free.app/create_users_lenscraft.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the JSON data to the server
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonObjects[0].toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected to the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return new JSONObject(response.toString());
            } else {
                // Handle the error here
                Log.e(TAG, "HTTP error code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onRegistrationResult(result);
        }
    }
}
