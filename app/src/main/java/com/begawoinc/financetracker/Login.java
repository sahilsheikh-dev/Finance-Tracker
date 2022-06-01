package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    Button registerBtn, forgetBtn, loginBtn;
    TextInputLayout emailIn, passwordIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        registerBtn = findViewById(R.id.registerBtn);
        forgetBtn = findViewById(R.id.forgotBtn);
        loginBtn = findViewById(R.id.loginBtn);
        emailIn = findViewById(R.id.email);
        passwordIn = findViewById(R.id.password);
        forgetBtn = findViewById(R.id.forgotBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(emailIn.getEditText().getText());
                String password = String.valueOf(passwordIn.getEditText().getText());

                if (checkEmail(email) && !checkPasswordLength(password)){
                    emailIn.setError(null);
                    passwordIn.setError("length must me more than 6");
                } else if (!checkEmail(email) && !checkPassword(password)){
                    emailIn.setError("Please enter Email");
                    passwordIn.setError("Please enter Password");
                } else if (!checkPassword(password)){
                    emailIn.setError(null);
                    passwordIn.setError("Please enter Password");
                } else if (!checkPasswordLength(password)){
                    emailIn.setError(null);
                    passwordIn.setError("length must me more than 6");
                } else if (!checkEmail(email)){
                    passwordIn.setError(null);
                    emailIn.setError("Please enter Email");
                } else {
                    email = filteredEmail(email);
                    login(email, password);
                }

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public boolean checkEmail(String email){
        if (email.isEmpty()) return false;
        else return true;
    }

    public boolean checkPassword(String password){
        if (password.isEmpty()) return false;
        else return true;
    }

    public boolean checkPasswordLength(String password){
        if (password.length() < 6) return false;
        else return true;
    }

    public static String filteredEmail(String email) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(email);
        return matcher.replaceAll("");
    }

    public void login(String email, String password){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(email);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String passfromdatabase = dataSnapshot.child("authUser").child("password").getValue(String.class);
                    if (passfromdatabase.equals(password)){
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USERNAME", email);
                        editor.commit();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("USERNAME", email);
                        startActivity(intent);
                        finish();
                    } else {
                        passwordIn.setError("Wrong Password");
                        passwordIn.requestFocus();
                    }
                } else {
                    emailIn.setError("Wrong Email");
                    emailIn.requestFocus();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Login.this, "Something went wrong:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}