package com.example.lenscraft.APIRequests;

import android.os.AsyncTask;
import android.util.Log;

import com.example.lenscraft.PackageRecyclerView.ItemModel;
import com.example.lenscraft.PackageRecyclerView.ItemRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchItemsTask extends AsyncTask<String, Void, List<ItemModel>> {
    private ItemRecyclerViewAdapter adapter;
    private String category;

    public FetchItemsTask(ItemRecyclerViewAdapter adapter, String category) {
        this.adapter = adapter;
        this.category = category;
    }

    protected List<ItemModel> doInBackground(String... params){
        String apiEndpoint = params[0]; // The API endpoint

        List<ItemModel> itemModels = new ArrayList<>();

        try {
            URL url = new URL(apiEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                // Read the JSON response
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                //Log the JSON response for debugging:
                Log.d("JSON Response", response.toString());

                // Use Gson to parse the JSON into a list of ItemModel objects
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ItemModel>>(){}.getType();
                itemModels = gson.fromJson(response.toString(), listType);
            } else {
                Log.e("FetchItemsTask", "HTTP error response: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException e){
            Log.e("FetchItemsTask", "Error: " + e.getMessage() );
        }

        return itemModels;
    }

    protected void onPostExecute(List<ItemModel> itemModels){
        super.onPostExecute(itemModels);

        adapter.setItems(itemModels);
    }
}
