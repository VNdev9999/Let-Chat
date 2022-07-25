package com.example.letchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letchat.databinding.ActivityLoginBinding;
import com.example.letchat.ultilities.Constants;
import com.example.letchat.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
//        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
//            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.tvLoginSignUp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        //       binding.btnLogin.setOnClickListener(v -> addDataToFirebase());
        binding.btnLogin.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                logIn();
            }
        });
    }

    private void logIn() {
        loading(true);
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        data.collection(Constants.KEY_USERS_COLLECTION)
                .whereEqualTo(Constants.KEY_EMAIL , binding.edtLoginEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.edtLoginPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                    && task.getResult().getDocuments().size() >0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        loading(false);
                        showToast("Unable to login , please check again ");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.edtLoginEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email login :");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtLoginEmail.getText().toString()).matches()) {
            showToast("Please enter valid email");
            return false;
        } else if (binding.edtLoginPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.btnLogin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

//    private void addDataToFirebase() {
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name", "LQH");
//        data.put("email", "lqh268899@gmail.com");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(), "Data inserted", Toast.LENGTH_SHORT).show();
//                    Log.e("TESTING", "addDataToFirebase: " );
//                })
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
    }
}