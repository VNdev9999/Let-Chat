package com.example.letchat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letchat.databinding.ItemContainerReceiverdMessageBinding;
import com.example.letchat.databinding.ItemContainerSentMessageBinding;
import com.example.letchat.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> mListChatMessage;
    private String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> mListChatMessage, String senderId) {
        this.mListChatMessage = mListChatMessage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceiverdMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(mListChatMessage.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setDta(mListChatMessage.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(mListChatMessage != null){
            return mListChatMessage.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mListChatMessage.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerSentMessageBinding binding;

        public SentMessageViewHolder(@NonNull ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        public void setData(ChatMessage chatMessage) {
            binding.tvMassage.setText(chatMessage.message);
            binding.tvDateTime.setText(chatMessage.dateTime);
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceiverdMessageBinding binding;


        public ReceivedMessageViewHolder(@NonNull ItemContainerReceiverdMessageBinding itemContainerReceiverdMessageBinding) {
            super(itemContainerReceiverdMessageBinding.getRoot());
            binding = itemContainerReceiverdMessageBinding;
        }

        void setDta(ChatMessage chatMessage) {
            binding.tvReceiverMessage.setText(chatMessage.message);
            //phut 11:16s
        }
    }
}
