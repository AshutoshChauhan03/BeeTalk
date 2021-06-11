package com.example.beetalk.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beetalk.databinding.ActivityTermsAndConditionBinding;

import java.util.Objects;

public class TermsAndCondition extends AppCompatActivity {

    ActivityTermsAndConditionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsAndConditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.textView7.setMovementMethod(new ScrollingMovementMethod());

        binding.button2.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhoneNumberActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}