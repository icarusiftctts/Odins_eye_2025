package com.odinseyenavigation;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.odinseyenavigation.fragments.fragment_info;
import com.odinseyenavigation.fragments.fragment_locationupdates;
import com.odinseyenavigation.fragments.fragment_qrscan_home;

public class E1_afterLogin extends AppCompatActivity {

    ImageView homeButton, infoButton, hintsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e1_after_login); // Adjusted layout name to match your XML file

        // Initialize buttons
        homeButton = findViewById(R.id.home_btn);
        infoButton = findViewById(R.id.infoButton);
        hintsButton = findViewById(R.id.hintsButton);

        // Load the default fragment
        loadFragment(new fragment_qrscan_home());

        // Set click listeners for navigation
        homeButton.setOnClickListener(v -> loadFragment(new fragment_qrscan_home()));
        infoButton.setOnClickListener(v -> loadFragment(new fragment_info()));
        hintsButton.setOnClickListener(v -> loadFragment(new fragment_locationupdates()));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.e1_container, fragment); // Adjusted container ID to match your XML
        ft.commit();
    }
}
