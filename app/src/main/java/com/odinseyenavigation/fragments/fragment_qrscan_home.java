package com.odinseyenavigation.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.odinseyenavigation.E3_qrcode;
import com.odinseyenavigation.E5_inside_quest;
import com.odinseyenavigation.R;
import com.odinseyenavigation.qrdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class fragment_qrscan_home extends Fragment {


    public fragment_qrscan_home() {
        // Required empty public constructor
    }

    ImageView scanner_img;
    FirebaseAuth auth;

    FirebaseDatabase db;
    DatabaseReference ref;
    ListView list;
    ArrayList<String> quest_no, solvedque_no;
    //    ProgressDialog pg;
    TextView point_txt;
    ProgressBar progressBar;
    my_adapter myadapter;
    ImageView scanbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qrscan_home, container, false);

//        scanner_img = v.findViewById(R.id.fragment_qrscan_qrbtn);
        scanbtn = v.findViewById(R.id.fragment_qrscan_qrbtn);
        list = v.findViewById(R.id.fragment_qrcode_list);
        point_txt = v.findViewById(R.id.fragment_qrcode_point);
        progressBar = v.findViewById(R.id.fragment_qrcode_progressBar);

        point_txt.setTextColor(getResources().getColor(R.color.white));

        progressBar.setVisibility(View.VISIBLE);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        ref = db.getReference().child("Users").child(sanitizeEmail(user.getEmail())).child("Unlocked");


        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), E3_qrcode.class));
                getActivity().finish();
            }
        });

//        scanner_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), E3_qrcode.class));
//                getActivity().finish();
//            }
//        });

        quest_no = new ArrayList<>();
        solvedque_no = new ArrayList<>();


        fetchdata();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                // check if the quest is already solved
                FirebaseUser user = auth.getCurrentUser();

                ref = db.getReference().child("Users").child(sanitizeEmail(user.getEmail())).child("Solved").child(quest_no.get(position));
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(getContext(), quest_no.get(position) + " is Already Solved", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Intent intent = new Intent(getContext(), E5_inside_quest.class);
                            intent.putExtra("quetitle", quest_no.get(position));
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            getActivity().finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        return v;
    }

    private void fetchdata() {
        FirebaseUser user = auth.getCurrentUser();

        //fetching question number
        ref = db.getReference().child("Users").child(sanitizeEmail(user.getEmail())).child("Unlocked");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quest_no.clear();
//                ArrayList<String> temp = new ArrayList<>();
//                temp.clear();
                for (DataSnapshot mydata : snapshot.getChildren()) {
                    qrdata um = mydata.getValue(qrdata.class);
                    if (um != null) {
                        quest_no.add(um.getNumber());
                    }
                }

                // assembling quest 1, 2 ..... 18
                Collections.sort(quest_no, (quest1, quest2) -> {
                    int number1 = Integer.parseInt(quest1.split(" ")[1]);
                    int number2 = Integer.parseInt(quest2.split(" ")[1]);
                    return Integer.compare(number1, number2);
                });

                ///////////////////////////////////////////////////////
                //fetching points
                ref = db.getReference().child("Users").child(sanitizeEmail(user.getEmail())).child("Solved");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int point = 0;
                        solvedque_no.clear();

                        for (DataSnapshot mydata : snapshot.getChildren()) {
                            qrdata um = mydata.getValue(qrdata.class);
                            if (um != null) {
                                point = point + um.getPoints();
                                solvedque_no.add(um.getNumber());
                            }
                        }

                        point_txt.setText(point + "");


                        if (getContext() != null) {
                            myadapter = new my_adapter(getContext(), quest_no.toArray(new String[quest_no.size()]), solvedque_no.toArray(new String[solvedque_no.size()]));
                            list.setAdapter(myadapter);
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public class my_adapter extends ArrayAdapter<String> {
        String[] mtext;
        String[] mcheckbox;
        Context mContext;

        public my_adapter(Context context, String[] text, String[] checkbox) {
            super(context, R.layout.list_item_for_frag_qrcode, R.id.list_item_frag_qrcode_text, text);

            this.mtext = text;
            this.mcheckbox = checkbox;
            mContext = context;

        }


        @SuppressLint("ResourceAsColor")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            VHolder vholder;

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_for_frag_qrcode, parent, false);

                vholder = new VHolder(row);
                row.setTag(vholder);
            } else {
                vholder = (VHolder) row.getTag();
            }


            vholder.text_vh.setText(mtext[position]);

            if (Arrays.asList(mcheckbox).contains(mtext[position])) {
                vholder.checkbox_imgvh.setImageResource(R.drawable.checkedtick);
            } else {
                vholder.checkbox_imgvh.setImageResource(R.drawable.qmark);
            }


            return row;
        }
    }

    public class VHolder {
        TextView text_vh;
        ImageView checkbox_imgvh;

        public VHolder(View r) {

            text_vh = r.findViewById(R.id.list_item_frag_qrcode_text);
            checkbox_imgvh = r.findViewById(R.id.list_item_frag_qrcode_checkbox_img);
        }

    }

    private String sanitizeEmail(String email) {
        return email != null ? email.replace(".", "_dot_").replace("@", "_at_") : "error";
    }
}