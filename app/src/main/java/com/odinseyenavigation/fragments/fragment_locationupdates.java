package com.odinseyenavigation.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.odinseyenavigation.MainActivity;
import com.odinseyenavigation.R;

import java.util.ArrayList;


public class fragment_locationupdates extends Fragment {


    public fragment_locationupdates() {
    }

    ListView list;
    FirebaseDatabase db;
    DatabaseReference ref;
    FirebaseAuth auth;
    ArrayList<String> location_hint_list;
    ProgressBar progressBar;
    TextView time_txt;
    ImageView home_bck_btn2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {






        View v = inflater.inflate(R.layout.fragment_locationupdates, container, false);
        home_bck_btn2 = v.findViewById(R.id.homeButton2);
        home_bck_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("HomeButton", "Button clicked");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });






        list = v.findViewById(R.id.fragment_locationup_list);
        progressBar = v.findViewById(R.id.progressBar);
        time_txt = v.findViewById(R.id.fragmentupdates_time_txt);


        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        ref = db.getReference().child("locationhint");

        location_hint_list = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        fetchdata();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow(location_hint_list.get(position));
            }
        });





        return v;
    }


    private void fetchdata() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location_hint_list.clear();


                for (DataSnapshot mydata : snapshot.getChildren()) {
                    if (mydata != null) {
                        if(mydata.getKey().trim().equals("nexthinttime")){
                            time_txt.setText(mydata.getValue().toString().trim());
                        } else {
                            String s = mydata.getValue().toString().trim();
                            String formattedText = s.replace("\\n", "\n");
                            location_hint_list.add(formattedText);
                        }
                    }
                }

                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.layout_for_locationupdates, R.id.layout_lonupdates_txt, location_hint_list.toArray(new String[location_hint_list.size()]));
                    list.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showPopupWindow(String selectedItemText) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_popup_window_forlocationhints, null);

        if (popupView != null) {
            TextView popupTextView = popupView.findViewById(R.id.layout_popup_forlocationhint_txt);

            if (popupTextView != null) {
                popupTextView.setText(selectedItemText);

                // Calculate the width and height for 70% of the screen
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                int popupWidth = (int) (screenWidth * 0.9);
                int popupHeight = (int) (screenHeight * 0.5);

                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        popupWidth,
                        popupHeight,
                        true
                );

                popupWindow.showAtLocation(list, Gravity.CENTER, 0, 0);

                // Set a dismiss listener to handle the popup window dismiss event
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // Handle dismiss event if needed
                    }
                });
            } else {
                Toast.makeText(getContext(), "TextView is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "popupView is null", Toast.LENGTH_SHORT).show();
        }
    }


}