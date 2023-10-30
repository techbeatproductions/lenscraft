package com.example.lenscraft.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lenscraft.APIRequests.AudioCreationTask;
import com.example.lenscraft.APIRequests.CameraCreationTask;
import com.example.lenscraft.APIRequests.LensCreationTask;
import com.example.lenscraft.APIRequests.LightCreationTask;
import com.example.lenscraft.APIRequests.PrintablesCreationTask;
import com.example.lenscraft.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PackageItemEditorFragment extends Fragment {

    public static final String ARG_IMAGE_RESOURCE = "image_resource";
    public static final String ARG_LABEL_TEXT = "label_text";
    public static final String ARG_ITEM_TEXT = "item";

    private int imageResource;
    private String labelText;

    private String packageItem;
    private TextInputEditText brandInputEditText, amountInputEditText, modelInputEditText, descriptionInputEditText;
    private TextInputLayout modelTextInputLayout, descTextInputLayout;
    ProgressBar progressBar;

    private Button createItem;
    private TextView amount;

    public PackageItemEditorFragment() {
        // Required empty public constructor
    }

    public static PackageItemEditorFragment newInstance(int imageResource, String labelText, String item) {
        PackageItemEditorFragment fragment = new PackageItemEditorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RESOURCE, imageResource);
        args.putString(ARG_LABEL_TEXT, labelText);
        args.putString(ARG_ITEM_TEXT, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_item_editor, container, false);

        ImageView imageView = view.findViewById(R.id.singlePackageItemSelectorImgView);
        brandInputEditText = view.findViewById(R.id.packageItemTxt);
        amountInputEditText = view.findViewById(R.id.packageItemAmountTxt);
        modelInputEditText = view.findViewById(R.id.packageItemModelTxt);
        descriptionInputEditText = view.findViewById(R.id.packageItemDescriptionTxt);
        modelTextInputLayout = view.findViewById(R.id.itemModelInputLayoutCreatePackage);
        descTextInputLayout = view.findViewById(R.id.itemDescriptionInputLayoutCreatePackage);
        createItem = view.findViewById(R.id.createPackageItemBtn);
        amount = view.findViewById(R.id.amountTV);

        // Initializing progress bar
        progressBar = view.findViewById(R.id.createPackageItemProgressBar);

        Button createPkgBtn = getActivity().findViewById(R.id.creatPkgBtn);
        if (createPkgBtn != null){
            createPkgBtn.setVisibility(View.GONE);
        }

        createItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log the value of packageItem
                Log.d("PackageItemEditorFragment", "packageItem: " + packageItem);

                // Check if the user is an admin by making an HTTP request
                checkIfUserIsAdmin();
            }

            private void checkIfUserIsAdmin() {
                // Create a URL for your admin verification API
                String apiUrl = "https://your-api-url.com/verify_isAdmin_lenscraft.php";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(apiUrl);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");

                            // Check for a successful HTTP response code
                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // Read the response from the server
                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }
                                reader.close();

                                String apiResponse = response.toString();

                                // Check if the user is not an admin
                                if (apiResponse.equals("User is not an admin.")) {
                                    // User is not an admin. Access denied.
                                    Log.e(TAG, "User is not an admin. Access denied.");
                                    // Handle the access denial as needed in your Android app
                                } else {
                                    // User is an admin, continue with the camera creation process
                                    onItemCreateButtonClick();
                                }
                            } else {
                                // Handle the case where the HTTP request is not successful
                                Log.e(TAG, "HTTP error code: " + responseCode);
                            }
                        } catch (Exception e) {
                            // Handle exceptions, such as network or URL errors
                            Log.e(TAG, "Error: " + e.getMessage(), e);
                        }
                    }
                }).start();
            }

            private void onItemCreateButtonClick() {
                // Get user input
                String brandInput = brandInputEditText.getText().toString();
                String amountInput = amountInputEditText.getText().toString();
                String modelInput = modelInputEditText.getText().toString();
                String descriptionInput = descriptionInputEditText.getText().toString();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
//                        showToast("Item created successfully!");
                    }
                });
                hideKeyboard();

                // Retrieve the user_id from shared preferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_input", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Use a default value if user_id is not found

                // Create a JSON object with the data
                JSONObject itemData = new JSONObject();
                try {
                    // itemData.put("item_type", itemType);
                    itemData.put("user_id", userId);
                    itemData.put("brand", brandInput);
                    itemData.put("model", modelInput);
                    itemData.put("description", descriptionInput);
                    itemData.put("amount", amountInput);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Log the content of itemData
                Log.d("CreatePackageItem", "itemData content: " + itemData.toString());

                // Choose the appropriate creation task based on the item type
                if ("Create Camera".equals(packageItem)) {
                    CameraCreationTask cameraCreationTask = new CameraCreationTask(new CameraCreationTask.CameraCreationResultListener() {
                        @Override
                        public void onCameraCreationResult(JSONObject result) {
                            progressBar.setVisibility(View.GONE);
                            // Handle the success result
                            try {
                                int cameraID = result.getInt("cameraID");
                                Log.d("CreatePackageItem", "Camera created successfully. Camera ID: " + cameraID);
                                showToast("Camera created successfully!");
                            } catch (JSONException e) {
                                Log.e("CreatePackageItem", "Error extracting camera ID from the response: " + e.getMessage());
                            }
                        }



                        @Override
                        public void onCameraCreationError(String errorMessage) {
                            Log.e("CreatePackageItem", "Error creating camera: " + errorMessage);
                            showErrorToast("Error creating camera: " + errorMessage);
                        }
                    });
                    cameraCreationTask.execute(itemData);
                } else if ("Create Audio Equipment".equals(packageItem)) {
                    AudioCreationTask audioCreationTask = new AudioCreationTask(new AudioCreationTask.AudioCreationResultListener() {
                        @Override
                        public void onAudioCreationResult(JSONObject result) {
                            progressBar.setVisibility(View.GONE);
                            // Handle the success result
                            try {
                                int audioID = result.getInt("audio_equipment_id");
                                Log.d("CreatePackageItem", "Audio equipment created successfully. Audio ID: " + audioID);
                                showToast("Audio equipment created successfully!");
                            } catch (JSONException e) {
                                Log.e("CreatePackageItem", "Error extracting audio ID from the response: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onAudioCreationError(String errorMessage) {
                            Log.e("CreatePackageItem", "Error creating audio equipment: " + errorMessage);
                            showErrorToast("Error creating audio equipment: " + errorMessage);
                        }
                    });
                    audioCreationTask.execute(itemData);
                } else if ("Create Lens".equals(packageItem)) {
                    LensCreationTask lensCreationTask = new LensCreationTask(new LensCreationTask.LensCreationResultListener() {
                        @Override
                        public void onLensCreationResult(JSONObject result) {
                            progressBar.setVisibility(View.GONE);
                            // Handle the success result
                            try {
                                int lensID = result.getInt("lens_id");
                                Log.d("CreatePackageItem", "Lens created successfully. Lens ID: " + lensID);
                                showToast("Lens created successfully!");
                            } catch (JSONException e) {
                                Log.e("CreatePackageItem", "Error extracting lens ID from the response: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onLensCreationError(String errorMessage) {
                            Log.e("CreatePackageItem", "Error creating lens: " + errorMessage);
                            showErrorToast("Error creating lens: " + errorMessage);
                        }
                    });
                    lensCreationTask.execute(itemData);
                } else if ("Create Printable".equals(packageItem)) {
                    PrintablesCreationTask printablesCreationTask = new PrintablesCreationTask(new PrintablesCreationTask.PrintablesCreationResultListener() {
                        @Override
                        public void onPrintablesCreationResult(JSONObject result) {
                            progressBar.setVisibility(View.GONE);
                            // Handle the success result
                            try {
                                int printablesID = result.getInt("printable_id");
                                Log.d("CreatePackageItem", "Printable created successfully. Printable ID: " + printablesID);
                                showToast("Printable created successfully!");
                            } catch (JSONException e) {
                                Log.e("CreatePackageItem", "Error extracting printable ID from the response: " + e.getMessage());
                            }
                        }



                        @Override
                        public void onPrintablesCreationError(String errorMessage) {
                            Log.e("CreatePackageItem", "Error creating printable: " + errorMessage);
                            showErrorToast("Error creating printable: " + errorMessage);
                        }
                    });
                    printablesCreationTask.execute(itemData);
                } else if ("Create Light".equals(packageItem)) {
                    LightCreationTask lightCreationTask = new LightCreationTask(new LightCreationTask.LightCreationResultListener() {
                        @Override
                        public void onLightCreationResult(JSONObject result) {
                            progressBar.setVisibility(View.GONE);
                            // Handle the success result
                            try {
                                int lightID = result.getInt("light_id");
                                Log.d("CreatePackageItem", "Light created successfully. Printable ID: " + lightID);
                                showToast("Light created successfully!");
                            } catch (JSONException e) {
                                Log.e("CreatePackageItem", "Error extracting light ID from the response: " + e.getMessage());
                            }
                        }



                        @Override
                        public void onLightCreationError(String errorMessage) {
                            Log.e("CreatePackageItem", "Error creating light: " + errorMessage);
                            showErrorToast("Error creating light: " + errorMessage);
                        }
                    });
                    lightCreationTask.execute(itemData);
                }




            }
        });





        if (getArguments() != null) {
            imageResource = getArguments().getInt(ARG_IMAGE_RESOURCE);
            labelText = getArguments().getString(ARG_LABEL_TEXT);
            packageItem = getArguments().getString(ARG_ITEM_TEXT);

            imageView.setImageResource(imageResource);
            brandInputEditText.setHint(labelText);
            amountInputEditText.setHint("Amount");
            createItem.setText(packageItem);

            // Set the user's input as the text in the Text Input Edit Text
            restoreUserInput();

            // Hide the amount views for "Package Name" and "Package Description"
            if ("Package Description :".equals(labelText)) {
                View amountContainer = view.findViewById(R.id.amountContainer);

                modelTextInputLayout.setVisibility(View.GONE);
                descTextInputLayout.setVisibility(View.GONE);
                amountContainer.setVisibility(View.GONE);

                Log.d("PackageItemEditorFragment", "Hiding amount views for: " + labelText);
            }

            if ("Package Name :".equals(labelText)) {
                amount.setText("Base Amount");

                modelTextInputLayout.setVisibility(View.GONE);
                descTextInputLayout.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private void hideKeyboard() {
        // Obtain a reference to the Context from the view
        Context context = requireContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocus = getActivity().getCurrentFocus(); // Use getActivity() to get the current focused view
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void showErrorToast(String s) {
        Context context = requireContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        clearInputFields();
        recreateActivity();
    }

    private void recreateActivity() {
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            getActivity().finish();  // Finish the current activity
            startActivity(intent);  // Start the activity again
        }
    }

    private void showToast(String s) {
        Context context = requireContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        clearInputFields();
        recreateActivity();
    }

    private void clearInputFields() {
        brandInputEditText.setText(""); // Clear the brand input field
        amountInputEditText.setText(""); // Clear the amount input field
        modelInputEditText.setText(""); // Clear the model input field
        descriptionInputEditText.setText(""); // Clear the description input field
    }

    public void saveUserInput() {
        if (isAdded()) {
            String userInput = brandInputEditText.getText().toString();
            String modelInput = modelInputEditText.getText().toString();
            String descriptionInput = descriptionInputEditText.getText().toString();
            String amountInput = amountInputEditText.getText().toString();

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_input", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(labelText, userInput);
            editor.putString("model_" + labelText, modelInput); // Save model input
            editor.putString("description_" + labelText, descriptionInput); // Save description input
            editor.putString("amount_" + labelText, amountInput); // Save amount input
            editor.apply();

            Log.d("CreatePackageItem", "Saved user input for labelText: " + labelText + ", value: " + userInput);
            Log.d("CreatePackageItem", "Saved model input for labelText: " + labelText + ", value: " + modelInput);
            Log.d("CreatePackageItem", "Saved description input for labelText: " + labelText + ", value: " + descriptionInput);
            Log.d("CreatePackageItem", "Saved amount input for labelText: " + labelText + ", value: " + amountInput);
        }
    }

    public void restoreUserInput() {
        if (isAdded()) {
            try {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_input", Context.MODE_PRIVATE);
                String userInput = sharedPreferences.getString(labelText, "");
                String modelInput = sharedPreferences.getString("model_" + labelText, "");
                String descriptionInput = sharedPreferences.getString("description_" + labelText, "");
                String amountInput = sharedPreferences.getString("amount_" + labelText, "");

                brandInputEditText.setText(userInput);
                modelInputEditText.setText(modelInput); // Set the model input field
                descriptionInputEditText.setText(descriptionInput); // Set the description input field
                amountInputEditText.setText(amountInput);

                Log.d("RestoreData", "Restored data for " + labelText + ": " + userInput);
                Log.d("RestoreData", "Restored model data for " + labelText + ": " + modelInput);
                Log.d("RestoreData", "Restored description data for " + labelText + ": " + descriptionInput);
                Log.d("RestoreData", "Restored amount data for " + labelText + ": " + amountInput);
            } catch (Exception e) {
                Log.e("RestoreData", "Error restoring data: " + e.getMessage());
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        saveUserInput();
    }
}
