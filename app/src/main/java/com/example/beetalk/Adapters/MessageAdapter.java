package com.example.beetalk.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beetalk.Models.Message;
import com.example.beetalk.R;
import com.example.beetalk.databinding.ReceiverMessageBinding;
import com.example.beetalk.databinding.SenderMessageBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;
    FirebaseDatabase database;
    String SENDER_ROOM, RECEIVER_ROOM;

    static int MESSAGE_SENT = 1;
    static int MESSAGE_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messages, String SENDER_ROOM, String RECEIVER_ROOM) {
        this.context = context;
        this.messages = messages;
        this.SENDER_ROOM = SENDER_ROOM;
        this.RECEIVER_ROOM = RECEIVER_ROOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        database = FirebaseDatabase.getInstance();

        View view;
        if (viewType == MESSAGE_SENT){
            view = LayoutInflater.from(context).inflate(R.layout.sender_message, parent, false);
            return new SenderViewHolder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.receiver_message, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        if (Objects.equals(FirebaseAuth.getInstance().getUid(), message.getSenderId()))
            return MESSAGE_SENT;
        else
            return MESSAGE_RECEIVE;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        int [] reactions = new int [] {
                R.drawable.ic_fb_angry,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_wow
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            // setting selected reaction to the receiver's message
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            if (pos >= 0) {
                receiverViewHolder.binding.reaction.setImageResource(reactions[pos]);
                receiverViewHolder.binding.reaction.setVisibility(View.VISIBLE);
            }
            else {
                receiverViewHolder.binding.reaction.setVisibility(View.GONE);
            }

            message.setFeeling(reactions[pos]);

            // overwriting current message in database with updated reaction
            database.getReference().child("Chats").child(SENDER_ROOM).child(message.getMessageId()).setValue(message);
            database.getReference().child("Chats").child(RECEIVER_ROOM).child(message.getMessageId()).setValue(message);
            return true;
        });

        if (holder.getClass() == SenderViewHolder.class){

            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.binding.message.setText(message.getMessage());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            senderViewHolder.binding.messageTime.setText(dateFormat.format(new Date(message.getTime())));

        }
        else {

            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.binding.message.setText(message.getMessage());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            receiverViewHolder.binding.messageTime.setText(dateFormat.format(new Date(message.getTime())));

            // showing popup only for receiver message
            receiverViewHolder.binding.linearLayout.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {

        SenderMessageBinding binding;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SenderMessageBinding.bind(itemView);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ReceiverMessageBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ReceiverMessageBinding.bind(itemView);
        }
    }
}
