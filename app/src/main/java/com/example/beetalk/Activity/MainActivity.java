package com.example.beetalk.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.beetalk.Adapters.UserAdapter;
import com.example.beetalk.Models.User;
import com.example.beetalk.R;
import com.example.beetalk.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    UserAdapter userAdapter;
    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomView.setItemIconTintList(null);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userAdapter = new UserAdapter(this, users);
        binding.chatRecycler.setAdapter(userAdapter);
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.chatRecycler.showShimmerAdapter();

        Toast.makeText(MainActivity.this, "dk" + database.getReference().child("chats").getKey(), Toast.LENGTH_SHORT).show();
        
        if (database.getReference().child("chats").getKey() != null) {
            database.getReference().child("chats").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users.clear();
                    for (DataSnapshot receiver:snapshot.getChildren()) {

                        database.getReference().child("users").child(receiver.toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.child("details").getValue(User.class);
                                Toast.makeText(MainActivity.this, "name" + user.getName(), Toast.LENGTH_SHORT).show();
                                assert user != null;
                                if (! Objects.equals(auth.getUid(), user.getUid())) {
                                    users.add(user);
                                }
                                userAdapter.notifyDataSetChanged();
                                binding.chatRecycler.hideShimmerAdapter();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            binding.chatRecycler.hideShimmerAdapter();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(this,"Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.groups:
                Toast.makeText(this,"Groups", Toast.LENGTH_SHORT).show();
                break;
            case R.id.invite:
                Toast.makeText(this,"Invite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                intent.setClass(this, ProfileInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                intent.setClass(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}