package com.example.lenscraft.APIRequests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CreatePackageAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {

    private static final String TAG = "CreatePackageAsyncTask";
    private Context context;
    private OnPackageCreatedListener listener;

    public CreatePackageAsyncTask(Context context, OnPackageCreatedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(JSONObject... packageData) {
        if (packageData.length == 0) {
            return false;
        }

        JSONObject packageJson = packageData[0];
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/create_package_lenscraft.php";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = packageJson.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream responseStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
                String responseString = reader.readLine();

                // Check the response from the API
                if (responseString.contains("Package created with ID")) {
                    return true;  // Package created successfully
                } else {
                    return false;  // Package creation failed
                }
            } else {
                return false;  // API request failed
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during package creation: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (listener != null) {
            listener.onPackageCreated(success);
        }
    }

    public interface OnPackageCreatedListener {
        void onPackageCreated(boolean success);
    }
}

