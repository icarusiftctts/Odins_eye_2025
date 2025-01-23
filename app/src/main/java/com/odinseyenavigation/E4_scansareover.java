package com.odinseyenavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class E4_scansareover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e4_scansareover);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(E4_scansareover.this, E1_afterLogin.class));
        finish();
    }
}