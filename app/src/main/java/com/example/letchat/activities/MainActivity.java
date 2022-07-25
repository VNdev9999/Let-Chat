package com.example.letchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letchat.adapters.ConversationsAdapter;
import com.example.letchat.databinding.ActivityMainBinding;
import com.example.letchat.listeners.ConversionListener;
import com.example.letchat.model.ChatMessage;
import com.example.letchat.model.User;
import com.example.letchat.ultilities.Constants;
import com.example.letchat.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConversionListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> mListChatMessage;
    private ConversationsAdapter conversationsAdapter;
    private FirebaseFirestore data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        getToken();
        setListeners();
        listenConversations();
    }

    private void init() {
        mListChatMessage = new ArrayList<>();
        conversationsAdapter = new ConversationsAdapter(mListChatMessage , this);
        binding.rcvConversation.setAdapter(conversationsAdapter);
        data = FirebaseFirestore.getInstance();
    }

    private void loadUserDetails() {
        binding.tvUserName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), UsersActivity.class));
        });
    }

    private void setListeners() {
        binding.imgSignOut.setOnClickListener(v -> {
            signOut();
        });
        Log.e("Huy", "setListeners: ");
    }

    private void showToast(String massage) {
        Toast.makeText(getApplicationContext(), massage, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations(){
        data.collection(Constants.KEY_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        data.collection(Constants.KEY_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.date = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    mListChatMessage.add(chatMessage);
                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for ( int i = 0 ; i < mListChatMessage.size(); i ++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(mListChatMessage.get(i).senderId.equals(senderId) && mListChatMessage.get(i).receiverId.equals(receiverId)){
                            mListChatMessage.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            mListChatMessage.get(i).date = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(mListChatMessage,(obj1,obj2) -> obj2.date.compareTo(obj1.date));
            conversationsAdapter.notifyDataSetChanged();
            binding.rcvConversation.smoothScrollToPosition(0);
            binding.rcvConversation.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                data.collection(Constants.KEY_USERS_COLLECTION).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_TOKEN, token)
                .addOnFailureListener(e -> showToast("Failed to update token"));
    }

    private void signOut() {
        showToast("Signing out");
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        DocumentReference documentReference = data.collection(Constants.KEY_USERS_COLLECTION).document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    Log.e("TAG", "signOut: ");
                    finish();
                })
                .addOnFailureListener(e -> showToast("Error"));
    }

    @Override
    public void onClickConversion(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}