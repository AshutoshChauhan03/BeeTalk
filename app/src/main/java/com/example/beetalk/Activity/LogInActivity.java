package com.example.beetalk.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beetalk.databinding.ActivityLogInBinding;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.phoneBox.requestFocus();

        binding.button3.setOnClickListener(v -> {

            String phoneNumber = binding.phoneBox.getText().toString();

            if (phoneNumber.isEmpty())
                binding.phoneBox.setError("*Required");
            else if (phoneNumber.length() != 10)
                binding.phoneBox.setError("Invalid Number");
            else if (! internet())
                new StyleableToast
                        .Builder(this)
                        .backgroundColor(Color.BLACK)
                        .text("Internet Unavailable")
                        .textColor(Color.WHITE)
                        .show();
            else {
                Intent intent = new Intent();
                intent.setClass(this, OTPActivity.class);
                intent.putExtra("phoneNumber", "+91 " + phoneNumber);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
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