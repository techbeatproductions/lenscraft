package com.example.lenscraft.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.lenscraft.R;
import com.example.lenscraft.APIRequests.FetchUsernameTask;

public class SignInWelcomeFragment extends Fragment {
    private TextView welcomeBackSignInTv;

    public SignInWelcomeFragment() {
        // Required empty public constructor
    }

    public static SignInWelcomeFragment newInstance() {
        return new SignInWelcomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in_welcome, container, false);

        welcomeBackSignInTv = view.findViewById(R.id.welcomeBackSignInTV);

        // Fetch the username asynchronously and set the text when available
        fetchUsername();

        return view;
    }

    private void fetchUsername() {
        FetchUsernameTask fetchUsernameTask = new FetchUsernameTask(getContext(), new FetchUsernameTask.OnUsernameFetchedListener() {
            @Override
            public void onUsernameFetched(String username) {
                if (username != null && !username.isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            welcomeBackSignInTv.setText("Welcome Back " + username + " 👋");
                        }
                    });
                } else {
                    // Handle the case where the username is not found or an error occurred
                }
            }
        });

        fetchUsernameTask.execute();
    }
}
