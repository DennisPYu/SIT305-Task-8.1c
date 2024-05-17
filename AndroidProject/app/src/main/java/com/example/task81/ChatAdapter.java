package com.example.task81;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String, String>> messageList;
    private String userInitial;

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    public ChatAdapter(List<Map<String, String>> messageList, String userInitial) {
        this.messageList = messageList;
        this.userInitial = userInitial;
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, String> message = messageList.get(position);
        if (!message.get("User").isEmpty()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, String> message = messageList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserMessageViewHolder) holder).messageTextView.setText(message.get("User"));
            ((UserMessageViewHolder) holder).userIcon.setText(userInitial);
        } else {
            ((BotMessageViewHolder) holder).messageTextView.setText(message.get("Llama"));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        TextView userIcon;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            userIcon = itemView.findViewById(R.id.userIcon);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        ImageView botIcon;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            botIcon = itemView.findViewById(R.id.botIcon);
        }
    }
}
