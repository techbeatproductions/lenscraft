package com.example.lenscraft.APIRequests;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MakeAdminTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        if (params.length != 1) {
            return "Invalid parameters.";
        }

        String username = params[0];
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/make_admin_lenscraft.php";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON data
            String jsonData = "{\"username\":\"" + username + "\"}";

            OutputStream os = connection.getOutputStream();
            os.write(jsonData.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } else {
                return "HTTP Error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the API response here
        // You can display a toast or update the UI based on the result
    }
}

