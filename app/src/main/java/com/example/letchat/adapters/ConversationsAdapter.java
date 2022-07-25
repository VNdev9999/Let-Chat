package com.example.letchat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letchat.databinding.ItemRecentConversionBinding;
import com.example.letchat.listeners.ConversionListener;
import com.example.letchat.model.ChatMessage;
import com.example.letchat.model.User;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationsViewHolder> {

    private List<ChatMessage> mListChatMessage;
    private ConversionListener conversionListener;

    public ConversationsAdapter(List<ChatMessage> mListChatMessage , ConversionListener conversionListener) {
        this.mListChatMessage = mListChatMessage;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationsViewHolder(ItemRecentConversionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationsViewHolder holder, int position) {
        ChatMessage chatMessage = mListChatMessage.get(position);
        if (mListChatMessage == null) {
            return;
        }
        holder.setData(chatMessage);
    }

    @Override
    public int getItemCount() {
        if (mListChatMessage != null) {
            return mListChatMessage.size();
        }
        return 0;
    }

    public class ConversationsViewHolder extends RecyclerView.ViewHolder {

        private ItemRecentConversionBinding binding;

        public ConversationsViewHolder(@NonNull ItemRecentConversionBinding itemRecentConversionBinding) {
            super(itemRecentConversionBinding.getRoot());
            this.binding = itemRecentConversionBinding;
        }

        private void setData(ChatMessage chatMessage) {
            binding.tvNameUser.setText(chatMessage.conversionName);
            binding.tvRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                conversionListener.onClickConversion(user);
            });
        }
    }
}