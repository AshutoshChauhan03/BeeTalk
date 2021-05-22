package com.example.beetalk.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beetalk.databinding.ActivityBootBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class BootActivity extends AppCompatActivity {

    ActivityBootBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBootBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Context context = this;

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(2000);
        binding.textView.startAnimation(animation);
        binding.textView2.startAnimation(animation);

        if (auth.getCurrentUser() != null) {
            database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child(Objects.requireNonNull(auth.getUid())).child("name").getValue(String.class);

                    Intent intent;
                    if (name != null) {
                        intent = new Intent(BootActivity.this, MainActivity.class);
                    }
                    else {
                        intent = new Intent(BootActivity.this, ProfileInfoActivity.class);
                    }
                    startActivity(intent);
                    finishAffinity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(BootActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }, 1500);
        }
    }
}