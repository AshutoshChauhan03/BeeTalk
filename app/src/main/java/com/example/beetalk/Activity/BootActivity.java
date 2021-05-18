package com.example.beetalk.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beetalk.databinding.ActivityBootBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class BootActivity extends AppCompatActivity {

    ActivityBootBinding binding;
    Timer timer;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBootBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();

        Context context = this;

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(2000);
        binding.textView.startAnimation(animation);
        binding.textView2.startAnimation(animation);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent();

                if (auth.getCurrentUser() != null){
                    intent.setClass(context, MainActivity.class);
                }
                else {
                    intent.setClass(context, WelcomeActivity.class);
                }
                startActivity(intent);
                finish();

            }
        }, 1500);
    }
}