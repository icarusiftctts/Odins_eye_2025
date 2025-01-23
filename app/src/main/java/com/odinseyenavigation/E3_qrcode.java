package com.odinseyenavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.Calendar;

public class E3_qrcode extends AppCompatActivity {

    CodeScanner mCodeScanner;
    int flag = 0;
    FirebaseDatabase db_fire;
    DatabaseReference ref;
    FirebaseAuth auth;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private boolean isListenerEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e3_qrcode);



        db_fire = FirebaseDatabase.getInstance();

        CodeScannerView scannerView = findViewById(R.id.E3_codescanner);

        if (!checkCameraPermission()) {
            requestCameraPermission();
        }

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        afterdecodingqr(result.getText().trim());

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }


    public void afterdecodingqr(String qr_text) {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        //////////////////////////////////////////////////////////////////////////////
        ref = db_fire.getReference().child("QRcodes");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flag = 0;

                for (DataSnapshot mydata : snapshot.getChildren()) {
                    qrdata um = mydata.getValue(qrdata.class);
                    if (um != null) {
                        if (um.getValue().equals(qr_text.trim())) {
                            flag = 1;
                            ////////////////////////////////////////////////////////////////
                            ref = db_fire.getReference().child("Users").child(sanitizeEmail(user.getEmail())).child("Unlocked").child(um.getNumber());
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(E3_qrcode.this, "Quest is already Unlocked", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(E3_qrcode.this, E1_afterLogin.class));
                                        finish();
                                    } else {
                                        if (um.getScans() > 0) {
                                            Calendar cal = Calendar.getInstance();

                                            //add_data_to_firbase() has tosted ki konsa unlock hua hai
                                            add_data_to_firbase(cal.getTime().toString(), um.getNumber(), um.getQuestion(), um.getValue(), um.getScans(), um.getPoints(), um.getQueimagename());

                                            ///////////////update scans//////////////////////////////////////
                                            decrease_scans(um.getAnswer(), um.getNumber(), um.getPoints(), um.getQuestion(), um.getScans(), um.getValue(), um.getQueimagename());
                                            ////////////////////////////////////////////////////

                                            startActivity(new Intent(E3_qrcode.this, E1_afterLogin.class));
                                            finish();
                                        } else {
                                            startActivity(new Intent(E3_qrcode.this, E4_scansareover.class));
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(E3_qrcode.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                            /////////////////////////////////////////////////////////////////
                        }
                    }
                }

                if (flag == 0) {
                    Toast.makeText(E3_qrcode.this, "Wrong QR code", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(E3_qrcode.this, E1_afterLogin.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(E3_qrcode.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ////////////////////////////////////////////////////////


    }

    public void decrease_scans(String answer, String number, int points, String question, int scans, String value, String queimagename) {
        db_fire = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        scans = scans - 1;

        qrdata data = new qrdata(number, question, value, scans, answer, null, points, queimagename);
        if (user != null) {
            try {
                isListenerEnabled = false;
                ref = db_fire.getReference().child("QRcodes").child(value);
                ref.setValue(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void add_data_to_firbase(String time, String number, String question, String value, int scans, int points, String queimagename) {
        db_fire = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        qrdata data = new qrdata(number, question, value, scans, null, time, points, queimagename);
        if (user != null) {
            try {
                ref = db_fire.getReference().child("Users").child(sanitizeEmail(user.getEmail())).child("Unlocked").child(number);
                ref.setValue(data);

                Toast.makeText(E3_qrcode.this, "Unlocked " + number, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(E3_qrcode.this, E1_afterLogin.class));
        finish();
    }


    private String sanitizeEmail(String email) {
        return email != null ? email.replace(".", "_dot_").replace("@", "_at_") : "error";
    }


    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
        }
        // For devices below Android M, permission is granted at installation time
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with camera-related operations
                // For example, initialize the camera or start the camera preview
            } else {
                Toast.makeText(this, "error having permission of Camera", Toast.LENGTH_SHORT).show();
                // Permission denied, handle accordingly (show a message, disable camera features, etc.)
            }
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}