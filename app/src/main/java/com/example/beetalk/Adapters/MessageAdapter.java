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
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    static int MESSAGE_SENT = 1;
    static int MESSAGE_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);
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
