package com.example.letchat.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letchat.databinding.ItemUserBinding;
import com.example.letchat.listeners.UserListener;
import com.example.letchat.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> mListUser;
    private final UserListener userListener;

    public UserAdapter(List<User> mListUser, UserListener userListener) {
        this.mListUser = mListUser;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mListUser.get(position);
        if (mListUser == null) {
            return;
        }
        holder.setUserData(user);
    }

    @Override
    public int getItemCount() {
        if (mListUser != null) {
            return mListUser.size();

        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            binding = itemUserBinding;
        }

        void setUserData(User user) {
            binding.tvNameUser.setText(user.name);
            binding.tvEmailUser.setText(user.email);
            binding.getRoot().setOnClickListener( v ->{
                userListener.onClickUserListener(user);
            });
        }
    }

//    private Bitmap getUserImage(String encodedImage){
//
//    }

}
