package com.example.letchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letchat.databinding.ActivitySignUpBinding;
import com.example.letchat.ultilities.Constants;
import com.example.letchat.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.btnLogin.setOnClickListener(v -> onBackPressed());
        binding.btnSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetail()) {
                signUp();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME , binding.edtSignUpName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.edtSignUpEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.edtSignUpPassword.getText().toString());
        database.collection(Constants.KEY_USERS_COLLECTION)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.edtSignUpName.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private Boolean isValidSignUpDetail() {
        if (binding.edtSignUpName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your name :");
            return false;
        } else if (binding.edtSignUpEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter your email");
            return false;
        } else if (binding.edtSignUpPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter password");
            return false;
        } else if (binding.edtSignUpConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        } else if (!binding.edtSignUpConfirmPassword.getText().toString().equals(binding.edtSignUpPassword.getText().toString())) {
            showToast("Password and confirm password must be the same");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.btnSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.btnSignUp.setVisibility(View.VISIBLE);
        }
    }
}