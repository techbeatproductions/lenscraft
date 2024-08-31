package com.example.lenscraft.APIRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class FetchUsernameTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "FetchUsernameTask";

    private Context context;
    private OnUsernameFetchedListener listener;

    public FetchUsernameTask(Context context, OnUsernameFetchedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        // Log the retrieved username
        Log.d(TAG, "Retrieved Username from SharedPreferences: " + username);

        return username;
    }

    @Override
    protected void onPostExecute(String username) {
        if (listener != null) {
            listener.onUsernameFetched(username);

            // Log the fetched username
            if (username != null) {
                Log.d(TAG, "Fetched Username: " + username);
            } else {
                Log.d(TAG, "Username not found or error occurred");
            }
        }
    }

    public interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }
}
