package com.odinseyenavigation.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.odinseyenavigation.E2_accountdetails;
import com.odinseyenavigation.MainActivity;
import com.odinseyenavigation.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class fragment_info extends Fragment {

    public fragment_info() {
        // Required empty public constructor
    }

    CircleImageView profilepic;
    ImageView home_bck_btn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference ref;
    ProgressBar progressBar;
    GridView grid, grid_dev;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        home_bck_btn = v.findViewById(R.id.homeButton);
        home_bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
//        profilepic = v.findViewById(R.id.frag_info_profilepic);
//        progressBar = v.findViewById(R.id.fragment_info_progressBar);


        ////setting names
//        String nam[] = {"Abhinav Singh", "Ansh Chawla", "Palakurthy Guneeth", "Hiya Jain"};
//        String num[] = {"9643271207", "99107 13737", "79933 91140", "97012 34550"};

//        my_adapter adapter = new my_adapter(getContext(), nam, num);
//        grid.setAdapter(adapter);
//
//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:"+num[position]));
//                startActivity(intent);
//            }
//        });

//        String nam_d[] = {"Mudit Choudhary"};
//        String num_d[] = {"9672467580"};
//
//        my_adapter adapter_d = new my_adapter(getContext(), nam_d, num_d);
//        grid_dev.setAdapter(adapter_d);
//
//        grid_dev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:9672467580"));
//                startActivity(intent);
//            }
//        });
        ///////////////


//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(getContext(), gso);
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
//        auth = FirebaseAuth.getInstance();
//
//        if (acct != null) {
//            progressBar.setVisibility(View.VISIBLE);
//            String personName = acct.getDisplayName();
//            String personEmail = acct.getEmail();
//            Uri uri = acct.getPhotoUrl();
//            Picasso.get().load(uri).into(profilepic, new Callback() {
//                @Override
//                public void onSuccess() {
//                    progressBar.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    progressBar.setVisibility(View.GONE);
//                }
//            });
//        }

//        profilepic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(getContext(), profilepic);
//                popupMenu.getMenuInflater().inflate(R.menu.accountpopupmenu_forinfo, popupMenu.getMenu());
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if (item.getItemId() == R.id.details) {
//                            Intent inte = new Intent(getContext(), E2_accountdetails.class);
//                            inte.putExtra("uri", acct.getPhotoUrl().toString());
//                            inte.putExtra("name", acct.getDisplayName());
//                            inte.putExtra("email", acct.getEmail());
//                            startActivity(inte);
//                        } else if (item.getItemId() == R.id.signout) {
//                            progressBar.setVisibility(View.VISIBLE);
//                            add_data_to_firbase_and_signout();
//                        }
//                        return true;
//                    }
//                });
//
//                popupMenu.show();
//            }
//        });


        return v;
    }

    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    public void add_data_to_firbase_and_signout() {
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            try {
                ref = db.getReference().child("allowed").child(sanitizeEmail(user.getEmail()));
                ref.setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOut();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String sanitizeEmail(String email) {
        return email != null ? email.replace(".", "_dot_").replace("@", "_at_") : "error";
    }

    public class my_adapter extends ArrayAdapter<String> {
        String[] mname;
        String[] mnumber;
        Context mContext;

        public my_adapter(Context context, String[] name, String[] number) {
            super(context, R.layout.list_for_info, R.id.list_info_name, name);

            this.mname = name;
            this.mnumber = number;
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
                row = inflater.inflate(R.layout.list_for_info, parent, false);

                vholder = new VHolder(row);
                row.setTag(vholder);
            } else {
                vholder = (VHolder) row.getTag();
            }


            vholder.name_txt.setText(mname[position]);
            vholder.num_txt.setText(mnumber[position]);

            return row;
        }
    }

    public class VHolder {
        TextView name_txt, num_txt;

        public VHolder(View r) {
            name_txt = r.findViewById(R.id.list_info_name);
            num_txt = r.findViewById(R.id.list_info_number);
        }

    }


}