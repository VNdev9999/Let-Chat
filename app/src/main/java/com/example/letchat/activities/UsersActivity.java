package com.example.letchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.letchat.adapters.UserAdapter;
import com.example.letchat.databinding.ActivityUsersBinding;
import com.example.letchat.listeners.UserListener;
import com.example.letchat.model.User;
import com.example.letchat.ultilities.Constants;
import com.example.letchat.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        data.collection(Constants.KEY_USERS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> mListUser = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            mListUser.add(user);
                            Log.e("TAG", "getUsers: " );
                        }
                        if (mListUser.size() > 0){
                            UserAdapter userAdapter = new UserAdapter(mListUser,this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                            binding.rcvUsers.setAdapter(userAdapter);
                            binding.rcvUsers.setLayoutManager(linearLayoutManager);
                            binding.rcvUsers.setVisibility(View.VISIBLE);
                            Log.e("Size", "getUsers: "+mListUser.size() );
                        } else {
                            showError();
                        }
                    }else {
                        showError();
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showError(){
        binding.tvErrorMassage.setText(String.format("%s", "No user available"));
        binding.tvErrorMassage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickUserListener(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}