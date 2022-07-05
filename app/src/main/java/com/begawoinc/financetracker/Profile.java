package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Profile extends AppCompatActivity {

    public static String USERNAME;
    BottomNavigationView bottomNavigationView;
    TextView nameIn, emailIn, usernameIn, numberIn, genderIn, privacyPolicy, aboutUs, reportbug, logoutBtn;
    ImageButton updateUserBtn;
    final Context context = this;
    AlertDialog alertDialog;
    FirebaseAuth mAuth;
    TextView incorrectOtp, resendOtp;
    EditText otpInput1, otpInput2, otpInput3, otpInput4, otpInput5, otpInput6;
    ProgressBar progressBarIn, progressBar;
    LinearLayout otpSection;
    String mVerificationId;
    boolean isChecked = false, otpSend = false, otpResendCheck = false;
    Switch numberFormatToggle, currencyFormatToggle, darkModeToggle;
    String fbName, fbNumber, fbGender;
    Button saveInfo, verifyOtpBtn, reportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
//        USERNAME = getIntent().getExtras().getString("USERNAME");
        USERNAME = sharedPreferences.getString("USERNAME", "");
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        logoutBtn = findViewById(R.id.logoutBtn);
        nameIn = findViewById(R.id.name);
        usernameIn = findViewById(R.id.username);
//        emailIn = findViewById(R.id.email);
        numberIn = findViewById(R.id.number);
        genderIn = findViewById(R.id.gender);
        mAuth = FirebaseAuth.getInstance();
        progressBarIn = findViewById(R.id.progressBar);
        numberFormatToggle = findViewById(R.id.numberFormatToggle);
        currencyFormatToggle = findViewById(R.id.currencyFormatToggle);
        darkModeToggle = findViewById(R.id.darkModeToggle);
        updateUserBtn = findViewById(R.id.updateUserBtn);
        reportbug = findViewById(R.id.reportbug);
        privacyPolicy = findViewById(R.id.privacyPolicy);
        aboutUs = findViewById(R.id.aboutUs);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent intentAbout = new Intent(Profile.this, MainActivity.class);
                        intentAbout.putExtra("USERNAME", USERNAME);
                        startActivity(intentAbout);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.expenses:
                        Intent intentExpenses = new Intent(Profile.this, MonthlyTracker.class);
                        intentExpenses.putExtra("USERNAME", USERNAME);
                        startActivity(intentExpenses);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.goals:
                        Intent intentHistory = new Intent(Profile.this, Goals.class);
                        intentHistory.putExtra("USERNAME", USERNAME);
                        startActivity(intentHistory);
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
        progressBarIn.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERNAME).child("authUser");

//        to get all the data of userAuth
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarIn.setVisibility(View.VISIBLE);
                if (snapshot.exists()) {
                    fbName = snapshot.child("name").getValue().toString().trim();
                    nameIn.setText(fbName);
                    fbNumber = snapshot.child("number").getValue().toString().trim();
                    numberIn.setText("+91 " + fbNumber);
                    usernameIn.setText(snapshot.child("email" + "").getValue().toString().trim());
                    if (snapshot.child("gender").exists()){
                        fbGender = snapshot.child("gender").getValue().toString().trim();
                        genderIn.setText(fbGender);
                    } else fbGender = "Gender";
                }
                progressBarIn.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarIn.setVisibility(View.GONE);
                Toast.makeText(Profile.this, "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View updateUserProfile = li.inflate(R.layout.update_user_info, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(updateUserProfile);

                TextInputLayout name = updateUserProfile.findViewById(R.id.nameIn);
                TextInputLayout number = updateUserProfile.findViewById(R.id.numberIn);
                TextInputLayout gender = updateUserProfile.findViewById(R.id.genderIn);
                Button closeBtn = updateUserProfile.findViewById(R.id.closeBtn);
                otpInput1 = updateUserProfile.findViewById(R.id.otpInput1);
                otpInput2 = updateUserProfile.findViewById(R.id.otpInput2);
                otpInput3 = updateUserProfile.findViewById(R.id.otpInput3);
                otpInput4 = updateUserProfile.findViewById(R.id.otpInput4);
                otpInput5 = updateUserProfile.findViewById(R.id.otpInput5);
                otpInput6 = updateUserProfile.findViewById(R.id.otpInput6);
                verifyOtpBtn = updateUserProfile.findViewById(R.id.verifyOtpBtn);
                saveInfo = updateUserProfile.findViewById(R.id.saveInfo);
                otpSection = updateUserProfile.findViewById(R.id.otpSection);
                progressBar = updateUserProfile.findViewById(R.id.progressBar);
                incorrectOtp = updateUserProfile.findViewById(R.id.incorrectOtp);

                name.getEditText().setText(fbName);
                number.getEditText().setText(fbNumber);
                gender.getEditText().setText(fbGender);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);

                        if (isEmpty(name.getEditText().getText().toString()) && isEmpty(number.getEditText().getText().toString()) && (isEmpty(gender.getEditText().getText().toString()) || gender.getEditText().getText().toString().equals("Gender"))) {
                            name.setError("Enter Name");
                            number.setError("Enter Number");
                            gender.setError("Please Select your Gender Type");
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(name.getEditText().getText().toString()) && isEmpty(number.getEditText().getText().toString())) {
                            name.setError("Enter Name");
                            number.setError("Enter Number");
                            gender.setError(null);
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(number.getEditText().getText().toString()) && (isEmpty(gender.getEditText().getText().toString()) || gender.getEditText().getText().toString().equals("Gender"))) {
                            name.setError(null);
                            number.setError("Enter Number");
                            gender.setError("Please Select your Gender Type");
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(name.getEditText().getText().toString()) && (isEmpty(gender.getEditText().getText().toString()) || gender.getEditText().getText().toString().equals("Gender"))) {
                            name.setError("Enter Name");
                            number.setError(null);
                            gender.setError("Please Select your Gender Type");
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(name.getEditText().getText().toString())) {
                            name.setError("Enter Name");
                            number.setError(null);
                            gender.setError(null);
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(number.getEditText().getText().toString())) {
                            name.setError(null);
                            number.setError("Enter Number");
                            gender.setError(null);
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(gender.getEditText().getText().toString()) || gender.getEditText().getText().toString().equals("Gender")) {
                            name.setError(null);
                            number.setError(null);
                            gender.setError("Please Select your Gender Type");
                            progressBar.setVisibility(View.GONE);
                        } else {
                            name.setError(null);
                            number.setError(null);
                            gender.setError(null);

                            if (!fbNumber.equals(number.getEditText().getText().toString())){
                                progressBar.setVisibility(View.VISIBLE);
                                isChecked = true;
                                name.setEnabled(false);
                                number.setEnabled(false);
                                gender.setEnabled(false);

//                                to send otp verification code
                                if (!otpSend) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    sendOtp(number.getEditText().getText().toString().trim());
                                    otpSend = true;
                                }

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
                                            verifyOtpAndUpdateInfo(otp, name.getEditText().getText().toString(), number.getEditText().getText().toString(), gender.getEditText().getText().toString());
                                        } else {
                                            incorrectOtp.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                numberOtpMove();
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                saveInfo(name.getEditText().getText().toString(), Long.parseLong(number.getEditText().getText().toString()), gender.getEditText().getText().toString());
                            }
                        }
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        numberFormatToggle.setChecked(!inLakhsCrore());
        numberFormatToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean d) {
                if (d){
                    if (inLakhsCrore()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("numberFormatDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("inLakhsCrore", "inLakhsCrore").commit();
                        Toast.makeText(Profile.this, "Enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!inLakhsCrore()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("numberFormatDetails", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove("inLakhsCrore").commit();
                        Toast.makeText(Profile.this, "Disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        currencyFormatToggle.setChecked(!isDollar());
        currencyFormatToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    if (isDollar()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("currencyDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("isDollar", "isDollar").commit();
                        Toast.makeText(Profile.this, "Enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!isDollar()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("currencyDetails", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove("isDollar").commit();
                        Toast.makeText(Profile.this, "Disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        darkModeToggle.setChecked(isDarkMode());
        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    if (!isDarkMode()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("isDarkMode", "isDarkMode").commit();
                        Toast.makeText(Profile.this, "Enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isDarkMode()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove("isDarkMode").commit();
                        Toast.makeText(Profile.this, "Disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, PrivacyPolicy.class);
                startActivity(intent);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, AboutUs.class);
                startActivity(intent);
            }
        });

        reportbug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View updateUserProfile = li.inflate(R.layout.report_bug, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(updateUserProfile);

                TextInputLayout name = updateUserProfile.findViewById(R.id.nameIn);
                TextInputLayout email = updateUserProfile.findViewById(R.id.emailIn);
                TextInputLayout bugDetail = updateUserProfile.findViewById(R.id.bugDetailIn);
                Button closeBtn = updateUserProfile.findViewById(R.id.closeBtn);

                reportBtn = updateUserProfile.findViewById(R.id.reportBtn);
                progressBar = updateUserProfile.findViewById(R.id.progressBar);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);

                        if (isEmpty(name.getEditText().getText().toString()) && isEmpty(email.getEditText().getText().toString()) && isEmpty(bugDetail.getEditText().getText().toString())) {
                            name.setError("Enter Name");
                            email.setError("Enter Email");
                            bugDetail.setError("Enter Desctiption of the bug");
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(name.getEditText().getText().toString()) && isEmpty(email.getEditText().getText().toString())) {
                            name.setError("Enter Name");
                            email.setError("Enter Email");
                            bugDetail.setError(null);
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(email.getEditText().getText().toString()) && isEmpty(bugDetail.getEditText().getText().toString())) {
                            name.setError(null);
                            email.setError("Enter Email");
                            bugDetail.setError("Enter Desctiption of the bug");
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(name.getEditText().getText().toString()) && isEmpty(bugDetail.getEditText().getText().toString())) {
                            name.setError("Enter Name");
                            email.setError(null);
                            bugDetail.setError("Enter Desctiption of the bug");
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(name.getEditText().getText().toString())) {
                            name.setError("Enter Name");
                            email.setError(null);
                            bugDetail.setError(null);
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(email.getEditText().getText().toString())) {
                            name.setError(null);
                            email.setError("Enter Email");
                            bugDetail.setError(null);
                            progressBar.setVisibility(View.GONE);
                        } else if (isEmpty(bugDetail.getEditText().getText().toString())) {
                            name.setError(null);
                            email.setError(null);
                            bugDetail.setError("Enter Desctiption of the bug");
                            progressBar.setVisibility(View.GONE);
                        } else {
                            name.setError(null);
                            email.setError(null);
                            bugDetail.setError(null);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(USERNAME).child("BugReports");
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long bugCount = dataSnapshot.getChildrenCount();
                                    Date date = new Date();
                                    myRef.child(String.valueOf(bugCount)).child("name").setValue(name.getEditText().getText().toString());
                                    myRef.child(String.valueOf(bugCount)).child("email").setValue(email.getEditText().getText().toString());
                                    myRef.child(String.valueOf(bugCount)).child("bugDetail").setValue(bugDetail.getEditText().getText().toString());
                                    myRef.child(String.valueOf(bugCount)).child("reportedOnTime").setValue(String.valueOf(date.getTime()));
                                    myRef.child(String.valueOf(bugCount)).child("reportedOnDate").setValue(String.valueOf(date.getDate()));
                                    myRef.child(String.valueOf(bugCount)).child("isFixed").setValue(0);
                                    Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(Profile.this, Profile.class);
                                    alertDialog.dismiss();
                                    startActivity(intent);
                                    finish();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(Profile.this, "ERROR:"+databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

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

    public boolean isDollar() {
        SharedPreferences sharedPreferences = getSharedPreferences("currencyDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("isDollar", "").isEmpty()
                || sharedPreferences.getString("isDollar", "").equalsIgnoreCase(null)
                || sharedPreferences.getString("isDollar", "").equalsIgnoreCase("");
    }

    public boolean inLakhsCrore() {
        SharedPreferences sharedPreferences = getSharedPreferences("numberFormatDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("inLakhsCrore", "").isEmpty()
                || sharedPreferences.getString("inLakhsCrore", "").equalsIgnoreCase(null)
                || sharedPreferences.getString("inLakhsCrore", "").equalsIgnoreCase("");
    }

    public boolean isDarkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        return !(sharedPreferences.getString("isDarkMode", "").isEmpty()
                || sharedPreferences.getString("isDarkMode", "").equalsIgnoreCase(null)
                || sharedPreferences.getString("isDarkMode", "").equalsIgnoreCase(""));
    }

    public boolean isEmpty(String s){
        return s.isEmpty();
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
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
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
            otpSection.setVisibility(View.GONE);
            saveInfo.setVisibility(View.VISIBLE);
            verifyOtpBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
            mVerificationId = verificationId;
            otpSection.setVisibility(View.VISIBLE);
            saveInfo.setVisibility(View.GONE);
            verifyOtpBtn.setVisibility(View.VISIBLE);
            incorrectOtp.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Profile.this, "OTP has been sent, please enter to verify", Toast.LENGTH_LONG).show();
        }
    };

    public void verifyOtpAndUpdateInfo(String otp, String name, String number, String gender){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.toString().trim());
        signinByCredentials(credential, name, number, gender);
    }

    public void signinByCredentials(PhoneAuthCredential credential, String name, String number, String gender){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            otpSection.setVisibility(View.VISIBLE);
                            saveInfo.setVisibility(View.GONE);
                            verifyOtpBtn.setVisibility(View.VISIBLE);
                            incorrectOtp.setVisibility(View.GONE);
                            saveInfo(name, Long.parseLong(number), gender);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            otpSection.setVisibility(View.VISIBLE);
                            saveInfo.setVisibility(View.GONE);
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
                        saveInfo.setVisibility(View.GONE);
                        verifyOtpBtn.setVisibility(View.VISIBLE);
                        incorrectOtp.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void saveInfo(String name, Long number, String gender) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERNAME);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myRef.child("authUser").child("name").setValue(name);
                myRef.child("authUser").child("number").setValue(number);
                myRef.child("authUser").child("gender").setValue(gender);
                Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(Profile.this, Profile.class);
                alertDialog.dismiss();
                startActivity(intent);
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "ERROR:"+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean checkOTPInput(String otp){
        if (otp.isEmpty() || otp.length() != 1) return false;
        else return true;
    }

}