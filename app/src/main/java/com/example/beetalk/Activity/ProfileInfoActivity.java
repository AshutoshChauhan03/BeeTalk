package com.example.beetalk.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beetalk.Models.User;
import com.example.beetalk.databinding.ActivityProfileInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Objects;

public class ProfileInfoActivity extends AppCompatActivity {

    ActivityProfileInfoBinding binding;
    Uri selectedImage = null;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.nameBox.requestFocus();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        binding.profilePic.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 45);
        });

        binding.button3.setOnClickListener(v -> {

            String name = binding.nameBox.getText().toString();

            if (name.isEmpty())
                binding.nameBox.setError("*Required");
            else if (name.length() < 4)
                binding.nameBox.setError("Enter Full Name");
            else if (! internet())
                new StyleableToast
                        .Builder(this)
                        .backgroundColor(Color.BLACK)
                        .text("Internet Unavailable")
                        .textColor(Color.WHITE)
                        .show();
            else if (selectedImage != null){

                dialog.setMessage("Setting Up Profile...");
                dialog.show();
                StorageReference reference = storage.getReference().child("profilePictures").child(Objects.requireNonNull(auth.getUid()));
                reference.putFile(selectedImage).addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        reference.getDownloadUrl().addOnSuccessListener(uri -> {

                            String uid = Objects.requireNonNull(auth.getUid());
                            String phoneNumber = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();
                            String profilePicture = uri.toString();

                            User user = new User(name, phoneNumber, profilePicture, uid);

                            database.getReference().child("users").child(Objects.requireNonNull(auth.getUid()))
                                    .child("details")
                                    .setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(ProfileInfoActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    });

                        });

                    }
                    else {

                        dialog.dismiss();
                        new StyleableToast
                                .Builder(this)
                                .backgroundColor(Color.BLACK)
                                .text("Couldn't Upload Profile Picture. Contact Developer")
                                .textColor(Color.WHITE)
                                .show();

                    }
                });

            }

            else {

                dialog.setMessage("Setting Up Profile...");
                dialog.show();
                String uid = Objects.requireNonNull(auth.getUid());
                String phoneNumber = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();
                String profilePicture = "NULL";

                User user = new User(name, phoneNumber, profilePicture, uid);

                database.getReference().child("users").child(Objects.requireNonNull(auth.getUid()))
                        .child("details")
                        .setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            Intent intent = new Intent(ProfileInfoActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        });
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null)
            if (data.getData() != null){
                selectedImage = data.getData();
                binding.profilePic.setImageURI(selectedImage);
            }
    }

    private boolean internet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
    }

}