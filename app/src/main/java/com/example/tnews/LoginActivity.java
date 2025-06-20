package com.example.tnews;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, passwordInput;
    private MaterialButton loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        TextView registerNow = findViewById(R.id.registerNow);
        firebaseAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(view -> loginUser());
        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, SportsNewsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Log detailed error
                        Log.e("FirebaseAuth", "Login failed", task.getException());

                        // Display the error message
                        Toast.makeText(LoginActivity.this,
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
