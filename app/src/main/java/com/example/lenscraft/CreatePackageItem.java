package com.example.lenscraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lenscraft.Recycler_view.ImageButtonAdapter;
import com.example.lenscraft.Recycler_view.ImageButtonModel;
import com.example.lenscraft.Recycler_view.ItemSpacingDecoration;
import com.example.lenscraft.fragments.LensCraftLogo;
import com.example.lenscraft.fragments.PackageItemEditorFragment;
import com.example.lenscraft.fragments.WeddingPackageFragment;

import java.util.ArrayList;
import java.util.List;

public class CreatePackageItem extends AppCompatActivity {

    RecyclerView recyclerView;
    FragmentManager fragmentManager;
    PackageItemEditorFragment currentFragment;

    Button createPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package_item);

        //Cover full screen
        int flags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Create Package Button initialization
        createPackage = findViewById(R.id.creatPkgBtn);

        // Navigating to Create Package Screen
        createPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePackageItem.this, CreatePackage.class);
                startActivity(intent);
            }
        });

        // Initialize the fragment manager
        fragmentManager = getSupportFragmentManager();

        Log.d("CreatePackageItem", "Activity onCreate");

        // Top Logo and Welcome Fragment
        replaceFragment(new LensCraftLogo(), R.id.logoFragmentContainerCreatePackageItemScreen);
        replaceFragment(new WeddingPackageFragment(), R.id.WelcomeFragmentContainerCreatePackageItemScreen);

        recyclerView = findViewById(R.id.packageBtnRecyclerView);
        // Use a LinearLayoutManager with horizontal orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        List<ImageButtonModel> imageButtonList = new ArrayList<>();

        imageButtonList.add(new ImageButtonModel(R.drawable.package_camera, "Camera"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_audio, "Audio"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_lens, "Lens"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_printables, "Printable"));
        imageButtonList.add(new ImageButtonModel(R.drawable.package_lights, "Lights"));

        ImageButtonAdapter adapter = new ImageButtonAdapter(imageButtonList);
        recyclerView.setAdapter(adapter);

        // Set the item click listener
        adapter.setOnItemClickListener(new ImageButtonAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                handleItemClick(position);
            }
        });

        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(itemSpacing));
    }

    private void handleItemClick(int position) {
        String prefix = "Brand of ";
        switch (position) {


            case 0:
                replacePackageItemFragment(R.drawable.packagecamera, prefix + "Camera :", "Create Camera");
                break;
            case 1:
                replacePackageItemFragment(R.drawable.packageaudio, prefix + "Audio Equipment :", "Create Audio Equipment");
                break;
            case 2:
                replacePackageItemFragment(R.drawable.packagelens, prefix + "Lens :", "Create Lens");
                break;
            case 3:
                replacePackageItemFragment(R.drawable.packageprintables, prefix + "Printable :", "Create Printable");
                break;
            case 4:
                replacePackageItemFragment(R.drawable.packagelights, prefix + "Lights :", "Create Light");
                break;
            default:
                // Handle unexpected position
        }


    }

    private void replacePackageItemFragment(int imageResource, String labelText, String item) {
        if (currentFragment != null && currentFragment.isAdded()) {

                currentFragment.saveUserInput(); // Call the saveUserInput() method without arguments
                Log.d("CreatePackageItem", "Saved user input for labelText: " + labelText);

            fragmentManager.beginTransaction().hide(currentFragment).commit();
        }

        currentFragment = PackageItemEditorFragment.newInstance(imageResource, labelText, item);
        currentFragment.restoreUserInput(); // Call the restoreUserInput() method

        Log.d("CreatePackageItem", "Restored user input for labelText: " + labelText);

        replaceFragment(currentFragment, R.id.packageItemFragmentContainer);
        Log.d("CreatePackageItem", "Replaced with PackageItemEditorFragment for labelText: " + labelText);
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();

        Log.d("CreatePackageItem", "Replaced fragment with ID: " + containerId);
    }
}
