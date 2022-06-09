package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    Button loginBtn, registerBtn, verifyOtpBtn;
    TextInputLayout nameIn, emailIn, numberIn, passwordIn;
    EditText otpInput1, otpInput2, otpInput3, otpInput4, otpInput5, otpInput6;
    TextView incorrectOtp;
    ProgressBar progressBar;
    LinearLayout otpSection;
    FirebaseAuth mAuth;
    String mVerificationId;
    boolean isChecked = false;

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
        mAuth = FirebaseAuth.getInstance();

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
                            if(dataSnapshot.exists() && !isChecked) {
                                nameIn.setError(null);
                                numberIn.setError(null);
                                passwordIn.setError(null);
                                emailIn.setError("Email is already available, please change");
                                progressBar.setVisibility(View.GONE);
                            } else {
                                isChecked = true;
                                nameIn.setError(null);
                                emailIn.setError(null);
                                numberIn.setError(null);
                                passwordIn.setError(null);

//                                to disable the input boxes
                                nameIn.setEnabled(false);
                                emailIn.setEnabled(false);
                                numberIn.setEnabled(false);
                                passwordIn.setEnabled(false);

//                                to send otp verification code
                                sendOtp(number.toString().trim());

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

                                            verifyOtpAndRegister(otp, name, username, email, number, password);

                                        } else {
                                            incorrectOtp.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                numberOtpMove();
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
        myRef.child("financialGoals").child("financialGoalMaxCount").setValue(1);
        myRef.child("financialGoals").child("1").child("amtNeed").setValue(1);
        myRef.child("financialGoals").child("1").child("amtHaving").setValue(0);
        myRef.child("financialGoals").child("1").child("goal").setValue("My First Goal");
        myRef.child("financialGoals").child("1").child("goalId").setValue(1);
        myRef.child("financialGoals").child("1").child("goalDate").setValue(2017+"-"+01+"-"+02);

//        net worth - assets
        myRef.child("networth").child("assets").child("cash").setValue("0");
        myRef.child("networth").child("assets").child("digital").setValue("0");
        myRef.child("networth").child("assets").child("realestate").setValue("0");
        myRef.child("networth").child("assets").child("stock").setValue("0");
        myRef.child("networth").child("assets").child("emergencyFund").setValue("0");

//        net worth - liabilities
        myRef.child("networth").child("liabilities").child("creditloan").setValue("0");
        myRef.child("networth").child("liabilities").child("homeloan").setValue("0");
        myRef.child("networth").child("liabilities").child("otherloan").setValue("0");
        myRef.child("networth").child("liabilities").child("personalloan").setValue("0");

//        planOfAction
        myRef.child("planOfAction").child("insurance").setValue("0");
        myRef.child("planOfAction").child("investment").setValue("0");
        myRef.child("planOfAction").child("monthlyExpenses").setValue("0");
        myRef.child("planOfAction").child("recNetIncome").setValue("0");
        myRef.child("planOfAction").child("saving").setValue("0");

//        monthlyExpenses
        myRef.child("monthlyExpenses").child(01 + "-" + 2022).child("expense").setValue("Food");
        myRef.child("monthlyExpenses").child(01 + "-" + 2022).child("amtNeed").setValue("10000");
        myRef.child("monthlyExpenses").child(01 + "-" + 2022).child("date").setValue(2022+"-"+01+"-"+01);

        progressBar.setVisibility(View.GONE);
        Toast.makeText(Register.this, "OTP Verified, Please Login to Continue", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();

    }

    public void numberOtpMove(){

        otpInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otpInput2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otpInput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otpInput3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()){
                    otpInput1.requestFocus();
                }
            }
        });

        otpInput3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otpInput4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()){
                    otpInput2.requestFocus();
                }
            }
        });

        otpInput4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otpInput5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()){
                    otpInput3.requestFocus();
                }
            }
        });

        otpInput5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otpInput6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()){
                    otpInput4.requestFocus();
                }
            }
        });

        otpInput6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()){
                    otpInput5.requestFocus();
                }
            }
        });

    }

    public void sendOtp(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if (code != null){
//                verifyOtp(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
//            to enable the input boxes
            nameIn.setEnabled(true);
            emailIn.setEnabled(true);
            numberIn.setEnabled(true);
            passwordIn.setEnabled(true);

            otpSection.setVisibility(View.GONE);
            registerBtn.setVisibility(View.VISIBLE);
            verifyOtpBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
            mVerificationId = verificationId;
            otpSection.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.GONE);
            verifyOtpBtn.setVisibility(View.VISIBLE);
            incorrectOtp.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Register.this, "OTP has been sent, please enter to verify", Toast.LENGTH_LONG).show();
        }
    };

    public void verifyOtpAndRegister(String otp, String name, String username, String email, String number, String password){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.toString().trim());
        signinByCredentials(credential, name, username, email, number, password);
    }

    public void signinByCredentials(PhoneAuthCredential credential, String name, String username, String email, String number, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            otpSection.setVisibility(View.VISIBLE);
                            registerBtn.setVisibility(View.GONE);
                            verifyOtpBtn.setVisibility(View.VISIBLE);
                            incorrectOtp.setVisibility(View.GONE);
                            register(name, username, email, Long.parseLong(number), password);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            otpSection.setVisibility(View.VISIBLE);
                            registerBtn.setVisibility(View.GONE);
                            verifyOtpBtn.setVisibility(View.VISIBLE);
                            incorrectOtp.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        otpSection.setVisibility(View.VISIBLE);
                        registerBtn.setVisibility(View.GONE);
                        verifyOtpBtn.setVisibility(View.VISIBLE);
                        incorrectOtp.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

}