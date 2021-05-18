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
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOTPBinding binding;
    FirebaseAuth auth;
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
                        Intent intent = new Intent(OTPActivity.this, LogInActivity.class);
                        startActivity(intent);
                        new StyleableToast
                                .Builder(OTPActivity.this)
                                .backgroundColor(Color.BLACK)
                                .text("Phone Number Verification Failed")
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

                Intent intent = new Intent();

                if (task.isSuccessful()){
                    intent.setClass(this, ProfileInfoActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                else {
                    new StyleableToast
                            .Builder(this)
                            .backgroundColor(Color.BLACK)
                            .text("Invalid OTP")
                            .textColor(Color.WHITE)
                            .show();
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