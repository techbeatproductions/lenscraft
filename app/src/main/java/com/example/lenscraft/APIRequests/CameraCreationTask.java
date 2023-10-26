package com.example.lenscraft.APIRequests;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class CameraCreationTask extends AsyncTask<JSONObject, Void, JSONObject> {

    private static final String TAG = "CameraCreationTask";

    public interface CameraCreationResultListener {
        void onCameraCreationResult(JSONObject result);
        void onCameraCreationError(String errorMessage);
    }

    private final CameraCreationResultListener listener;

    public CameraCreationTask(CameraCreationResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects) {
        try {
            URL url = new URL("https://jackal-modern-javelin.ngrok-free.app/create_camera_lenscraft.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(15)); // Set connection timeout
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(15)); // Set read timeout
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

                String responseString = response.toString();

                // Log the response data
                Log.d(TAG, "API Response Data: " + responseString);

                // Check if the response is valid JSON
                try {
                    return new JSONObject(responseString);
                } catch (JSONException e) {
                    // Handle non-JSON response
                    Log.e(TAG, "Non-JSON response: " + responseString);
                    // You can choose to handle this case based on the response format.
                    // For example, if it's an error message, you can show it to the user.
                    if (listener != null) {
                        listener.onCameraCreationError("Non-JSON response: " + responseString);
                    }
                }
            } else {
                // Handle the error here
                Log.e(TAG, "HTTP error code: " + responseCode);
                if (listener != null) {
                    listener.onCameraCreationError("HTTP error code: " + responseCode);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
            if (listener != null) {
                listener.onCameraCreationError("Error: " + e.getMessage());
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (listener != null) {
            if (result != null) {
                listener.onCameraCreationResult(result);
            } else {
                // Handle the case where result is null (indicating an error)
            }
        }
    }
}
