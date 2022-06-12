package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public static String USERNAME;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isUsernameEmpty = sharedPreferences.getString("USERNAME", "").isEmpty();

        if (isUsernameEmpty){
            Intent intent = new Intent(MainActivity.this, Login.class );
            startActivity(intent);
            finish();
        } else {
            //            USERNAME = getIntent().getExtras().getString("USERNAME");
            USERNAME = sharedPreferences.getString("USERNAME", "");
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.home);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.expenses:
                            Intent intentExpenses = new Intent(MainActivity.this, MonthlyTracker.class);
                            intentExpenses.putExtra("USERNAME", USERNAME);
                            startActivity(intentExpenses);
                            overridePendingTransition(0,0);
                            finish();
                            return true;

                        case R.id.goals:
                            Intent intentHistory = new Intent(MainActivity.this, Goals.class);
                            intentHistory.putExtra("USERNAME", USERNAME);
                            startActivity(intentHistory);
                            overridePendingTransition(0,0);
                            finish();
                            return true;

                        case R.id.profile:
                            Intent intentAbout = new Intent(MainActivity.this, Profile.class);
                            intentAbout.putExtra("USERNAME", USERNAME);
                            startActivity(intentAbout);
                            overridePendingTransition(0,0);
                            finish();
                            return true;

                        case R.id.home:
                            return true;
                    }
                    return false;
                }
            });

//            write code here


        }

    }
}