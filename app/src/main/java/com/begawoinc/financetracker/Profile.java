package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Profile extends AppCompatActivity {

    public static String USERNAME;
    BottomNavigationView bottomNavigationView;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
//        USERNAME = getIntent().getExtras().getString("USERNAME");
        USERNAME = sharedPreferences.getString("USERNAME", "");
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        logoutBtn = findViewById(R.id.logoutBtn);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.goals:
                        Intent intentHistory = new Intent(Profile.this, Goals.class);
                        intentHistory.putExtra("USERNAME", USERNAME);
                        startActivity(intentHistory);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.home:
                        Intent intentAbout = new Intent(Profile.this, MainActivity.class);
                        intentAbout.putExtra("USERNAME", USERNAME);
                        startActivity(intentAbout);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

//            write code here

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove("USERNAME").commit();

                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
                finish();
            }
        });



    }
}