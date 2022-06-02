package com.begawoinc.financetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    Button loginBtn, registerBtn, verifyOtpBtn;
    TextInputLayout nameIn, emailIn, numberIn, passwordIn;
    EditText otpInput1, otpInput2, otpInput3, otpInput4, otpInput5, otpInput6;
    TextView incorrectOtp;
    ProgressBar progressBar;
    LinearLayout otpSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        nameIn = findViewById(R.id.name);
        emailIn = findViewById(R.id.email);
        numberIn = findViewById(R.id.number);
        passwordIn = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        otpSection = findViewById(R.id.otpSection);
        otpInput1 = findViewById(R.id.otpInput1);
        otpInput2 = findViewById(R.id.otpInput2);
        otpInput3 = findViewById(R.id.otpInput3);
        otpInput4 = findViewById(R.id.otpInput4);
        otpInput5 = findViewById(R.id.otpInput5);
        otpInput6 = findViewById(R.id.otpInput6);
        incorrectOtp = findViewById(R.id.incorrectOtp);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(nameIn.getEditText().getText()).trim();
                String email = String.valueOf(emailIn.getEditText().getText()).trim();
                String number = String.valueOf(numberIn.getEditText().getText()).trim();
                String password = String.valueOf(passwordIn.getEditText().getText()).trim();

                if (!checkName(name) && !checkEmail(email) && !checkNumber(String.valueOf(number)) && !checkPassword(password)){
                    nameIn.setError("Please enter Name");
                    emailIn.setError("Please enter Email");
                    numberIn.setError("Please enter number");
                    passwordIn.setError("Please enter Password");
                } else if (!checkPassword(password)){
                    nameIn.setError(null);
                    emailIn.setError(null);
                    numberIn.setError(null);
                    passwordIn.setError("Please enter Password");
                } else if (!checkName(name)){
                    emailIn.setError(null);
                    numberIn.setError(null);
                    passwordIn.setError(null);
                    nameIn.setError("Please enter Name");
                } else if (!checkEmail(email)){
                    nameIn.setError(null);
                    numberIn.setError(null);
                    passwordIn.setError(null);
                    emailIn.setError("Please enter Email");
                } else if (!checkNumber(String.valueOf(number))) {
                    nameIn.setError(null);
                    emailIn.setError(null);
                    passwordIn.setError(null);
                    numberIn.setError("Please enter Number");
                } else if (!checkPasswordLength(password)){
                    nameIn.setError(null);
                    emailIn.setError(null);
                    numberIn.setError(null);
                    passwordIn.setError("length must me more than 6");
                } else {
                    nameIn.setError(null);
                    emailIn.setError(null);
                    numberIn.setError(null);
                    passwordIn.setError(null);

                    String username = filteredEmail(email).trim();
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(username);

//                    to check is email is already registered or not
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                nameIn.setError(null);
                                numberIn.setError(null);
                                passwordIn.setError(null);
                                emailIn.setError("Email is already available, please change");
                                progressBar.setVisibility(View.GONE);
                            } else {
                                nameIn.setError(null);
                                emailIn.setError(null);
                                numberIn.setError(null);
                                passwordIn.setError(null);

                                otpSection.setVisibility(View.VISIBLE);
                                registerBtn.setVisibility(View.GONE);
                                verifyOtpBtn.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                                nameIn.setEnabled(false);
                                emailIn.setEnabled(false);
                                numberIn.setEnabled(false);
                                passwordIn.setEnabled(false);

                                verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String otpInput1In = otpInput1.getText().toString().trim();
                                        String otpInput2In = otpInput2.getText().toString().trim();
                                        String otpInput3In = otpInput3.getText().toString().trim();
                                        String otpInput4In = otpInput4.getText().toString().trim();
                                        String otpInput5In = otpInput5.getText().toString().trim();
                                        String otpInput6In = otpInput6.getText().toString().trim();

                                        if (checkOTPInput(String.valueOf(otpInput1In)) && checkOTPInput(String.valueOf(otpInput2In)) && checkOTPInput(String.valueOf(otpInput3In)) &&
                                                checkOTPInput(String.valueOf(otpInput4In)) && checkOTPInput(String.valueOf(otpInput5In)) && checkOTPInput(String.valueOf(otpInput6In))){
                                            progressBar.setVisibility(View.VISIBLE);
                                            String otp = otpInput1In + "" + otpInput2In + "" + otpInput3In + "" +
                                                    otpInput4In + "" + otpInput5In + "" + otpInput6In;
                                            if(verifyOtp(otp)){
                                                incorrectOtp.setVisibility(View.GONE);
                                                register(name, username, email, Long.parseLong(number), password);
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                incorrectOtp.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            incorrectOtp.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(Register.this, "Something went wrong:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public boolean checkName(String name){
        if (name.isEmpty()) return false;
        else return true;
    }

    public boolean checkEmail(String email){
        if (email.isEmpty()) return false;
        else return true;
    }

    public boolean checkNumber(String number){
        if (number.isEmpty() || number.length() < 10) return false;
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

    public boolean checkOTPInput(String otp){
//        if (otp == 0 || otp < 999999999) return false;
        if (otp.isEmpty() || otp.length() != 1) return false;
        else return true;
    }

    public boolean isUsernameAvailable(String username){
        final boolean[] check = {false};
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(username);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) check[0] = true;
                else check[0] = false;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                check[0] = false;
                Toast.makeText(Register.this, "Something went wrong:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return check[0];
    }

    public void register(String name,String username, String email, long number, String password){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(username);

//       userAuth
        myRef.child("authUser").child("name").setValue(name);
        myRef.child("authUser").child("email").setValue(email);
        myRef.child("authUser").child("number").setValue(number);
        myRef.child("authUser").child("password").setValue(password);

//        financialGoals
        myRef.child("financialGoals").child("1").child("amtNeed").setValue(0);
        myRef.child("financialGoals").child("1").child("goal").setValue("My First Goal");
        myRef.child("financialGoals").child("1").child("isCompleted").setValue(0);
        myRef.child("financialGoals").child("1").child("year").setValue(1999);

//        net worth - assets
        myRef.child("networth").child("assets").child("cash").setValue(0);
        myRef.child("networth").child("assets").child("digital").setValue(0);
        myRef.child("networth").child("assets").child("realestate").setValue(0);
        myRef.child("networth").child("assets").child("stock").setValue(0);
        myRef.child("networth").child("assets").child("emergencyFund").setValue(0);

//        net worth - liabilities
        myRef.child("networth").child("liabilities").child("creditloan").setValue(0);
        myRef.child("networth").child("liabilities").child("homeloan").setValue(0);
        myRef.child("networth").child("liabilities").child("otherloan").setValue(0);
        myRef.child("networth").child("liabilities").child("personalloan").setValue(0);

//        planOfAction
        myRef.child("planOfAction").child("insurance").setValue(0);
        myRef.child("planOfAction").child("investment").setValue(0);
        myRef.child("planOfAction").child("monthlyExpenses").setValue(0);
        myRef.child("planOfAction").child("recNetIncome").setValue(0);
        myRef.child("planOfAction").child("saving").setValue(0);

        progressBar.setVisibility(View.GONE);
        Toast.makeText(Register.this, "Registered Successful, Please Login to Continue", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();

    }

    public boolean verifyOtp(String otp){
        if (otp.equals("123456")) return true;
        else return false;
    }

}