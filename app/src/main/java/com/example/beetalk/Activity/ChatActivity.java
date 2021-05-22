package com.example.beetalk.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.beetalk.Adapters.MessageAdapter;
import com.example.beetalk.Models.Message;
import com.example.beetalk.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    MessageAdapter messageAdapter;
    ArrayList<Message> messages;

    String SENDER_ROOM, RECEIVER_ROOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String receiver_Name = getIntent().getStringExtra("name");
        String receiver_Uid = getIntent().getStringExtra("receiverUid");
        String sender_Uid = auth.getUid();

        SENDER_ROOM = sender_Uid + receiver_Uid;
        RECEIVER_ROOM =receiver_Uid + sender_Uid;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(receiver_Name);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        binding.recyclerView.setAdapter(messageAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Receiving message from firebase server
        database.getReference().child("Chats")
                .child(SENDER_ROOM)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();

                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            assert messages != null;
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }

                        // scrolling always to the last message
                        if (messageAdapter.getItemCount() != 0){
                            binding.recyclerView.post(() -> binding.recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));
                        }

                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Putting message in firebase server
        binding.send.setOnClickListener(v -> {

            String message_txt = binding.messageBox.getText().toString();
            binding.messageBox.setText("");

            if (message_txt.isEmpty()) {
                return;
            }
            if (! internet()){
                new StyleableToast
                        .Builder(this)
                        .text("Internet Unavailable")
                        .textColor(Color.WHITE)
                        .backgroundColor(Color.BLACK)
                        .show();
            }
            Date date = new Date();
            Message message = new Message(message_txt.trim(), sender_Uid, date.getTime());

            String unique_key = Objects.requireNonNull(database.getReference()).push().getKey();

            database.getReference().child("Chats")
                    .child(SENDER_ROOM)
                    .child(unique_key)
                    .setValue(message)
                    .addOnSuccessListener(aVoid -> database.getReference().child("Chats")
                            .child(RECEIVER_ROOM)
                            .child(unique_key)
                            .setValue(message)
                            .addOnSuccessListener(aVoid1 -> {

                            }));
        });

    }

    private boolean internet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}