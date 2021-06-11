package com.example.beetalk.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.beetalk.databinding.ActivityOTPBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOTPBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String verificationId;
    ProgressDialog dialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        binding.phoneLbl.setText("Verify : " + phoneNumber);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        dialog.setMessage("Sending OTP...");
        dialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Intent intent = new Intent(OTPActivity.this, PhoneNumberActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        new StyleableToast
                                .Builder(OTPActivity.this)
                                .backgroundColor(Color.BLACK)
                                .text("Verification Failed")
                                .textColor(Color.WHITE)
                                .show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        dialog.dismiss();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.requestFocus();

        binding.otpView.setOtpCompletionListener(otp -> {

            if (! internet()){
                new StyleableToast
                        .Builder(this)
                        .backgroundColor(Color.BLACK)
                        .text("Internet Unavailable")
                        .textColor(Color.WHITE)
                        .show();
                return;
            }

            dialog.setMessage("Verifying OTP...");
            dialog.show();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            auth.signInWithCredential(credential).addOnCompleteListener(task -> {

                if (task.isSuccessful()){

                    database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child(Objects.requireNonNull(auth.getUid())).child("details").child("name").getValue(String.class);
                            if (name != null) {
                                new StyleableToast
                                        .Builder(OTPActivity.this)
                                        .text("Welcome " + name)
                                        .textColor(Color.WHITE)
                                        .backgroundColor(Color.BLACK)
                                        .cornerRadius(20)
                                        .show();
                                Intent intent = new Intent(OTPActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(OTPActivity.this, ProfileInfoActivity.class);
                                startActivity(intent);
                            }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finishAffinity();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    new StyleableToast
                            .Builder(this)
                            .backgroundColor(Color.BLACK)
                            .text("Invalid OTP")
                            .textColor(Color.WHITE)
                            .show();
                    binding.otpView.setText("");
                }

                dialog.dismiss();

            });

        });
    }

    private boolean internet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}