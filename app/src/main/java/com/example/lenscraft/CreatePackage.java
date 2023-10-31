package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lenscraft.APIRequests.FetchItemsTask;
import com.example.lenscraft.PackageRecyclerView.ItemRecyclerViewAdapter;
import com.example.lenscraft.Recycler_view.ImageButtonAdapter;
import com.example.lenscraft.Recycler_view.ImageButtonModel;
import com.example.lenscraft.Recycler_view.ItemSpacingDecoration;
import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.PackageItemEditorFragment;
import com.example.lenscraft.fragments.WeddingPackageFragment;

import java.util.ArrayList;
import java.util.List;

public class CreatePackage extends AppCompatActivity {

    RecyclerView recyclerView, itemRecyclerView;
    FragmentManager fragmentManager;
    PackageItemEditorFragment currentFragment;
    ItemRecyclerViewAdapter itemRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package);

        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Initialize the fragment manager
        fragmentManager = getSupportFragmentManager();

        Log.d("CreatePackage", "Activity onCreate");

        // Logo Fragment
        replaceFragment(new LensCraftLogo(), R.id.logoFragmentContainerCreatePackageScreen);
        replaceFragment(new WeddingPackageFragment(), R.id.WelcomeFragmentContainerCreatePackageScreen);

        recyclerView = findViewById(R.id.packageBtnRecyclerView);
        // Use a LinearLayoutManager with horizontal orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        List<ImageButtonModel> imageButtonList = new ArrayList<>();

        imageButtonList.add(new ImageButtonModel(R.drawable.package_name, "Package Name"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_description, "Package Description"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_camera, "Camera"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_audio, "Audio"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_lens, "Lens"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_printables, "Printable"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_lights, "Lights"));

        ImageButtonAdapter adapter = new ImageButtonAdapter(imageButtonList);
        recyclerView.setAdapter(adapter);

        //Set a click listener for category selection
        adapter.setOnItemClickListener(new ImageButtonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                handleCategorySelection(position);
            }
        });

        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(itemSpacing));


        int packageDbItemSpacing = getResources().getDimensionPixelSize(R.dimen.package_item_fetched_spacing);
        // Initialize the second RecyclerView for items
        itemRecyclerView = findViewById(R.id.packageItemSelectorRecyclerView);
        LinearLayoutManager itemLayoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(packageDbItemSpacing));

        //Adapter for the second recycler view
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(new ArrayList<>());
        itemRecyclerView.setAdapter(itemRecyclerViewAdapter);

    }

    private void replaceFragment(Fragment fragment, int containerId) {
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();

        Log.d("CreatePackage", "Replaced fragment with ID: " + containerId);
    }

    private void handleCategorySelection(int position) {
        String selectedCategory;
        String apiEndpoint;

        switch (position) {

            case 2:
                selectedCategory = "Camera";
                apiEndpoint = "https://jackal-modern-javelin.ngrok-free.app/fetch_all_cameras_lenscraft.php";
                break;

            case 3:
                selectedCategory = "Audio";
                apiEndpoint = "https://jackal-modern-javelin.ngrok-free.app/fetch_all_audioEquipment_lenscraft.php";
                break;

            case 4:
                selectedCategory = "Lens";
                apiEndpoint = "https://jackal-modern-javelin.ngrok-free.app/fetch_all_lens_lenscraft.php";
                break;

            case 5:
                selectedCategory = "Printable";
                apiEndpoint = "https://jackal-modern-javelin.ngrok-free.app/fetch_all_printables_lenscraft.php";
                break;

            case 6:
                selectedCategory = "Lights";
                apiEndpoint = "https://jackal-modern-javelin.ngrok-free.app/fetch_all_lights_lenscraft.php";
                break;
            default:
                selectedCategory = null;
                apiEndpoint = null;
                // Handle unexpected position
        }

        if (selectedCategory != null && apiEndpoint != null){
            fetchItems(selectedCategory, apiEndpoint);
        }


    }

    private void fetchItems(String category, String apiEndpoint){

        new FetchItemsTask(itemRecyclerViewAdapter, category).execute(apiEndpoint);

    }

    private void replacePackageItemFragment(int imageResource, String labelText, String item) {
        if (currentFragment != null && currentFragment.isAdded()) {
            currentFragment.saveUserInput();
            Log.d("CreatePackage", "Saved user input for labelText: " + labelText);
            fragmentManager.beginTransaction().hide(currentFragment).commit();
        }

        currentFragment = PackageItemEditorFragment.newInstance(imageResource, labelText, item);

        Log.d("CreatePackage", "Restored user input for labelText: " + labelText);

        replaceFragment(currentFragment, R.id.packageItemFragmentContainer);
        Log.d("CreatePackage", "Replaced with PackageItemEditorFragment for labelText: " + labelText);
    }
}
