package com.example.tnews;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private TextInputLayout usernameLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    private MaterialButton registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.username);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirmPassword);

        usernameLayout = findViewById(R.id.usernameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        registerButton = findViewById(R.id.registerButton);
        TextView loginNow = findViewById(R.id.loginNow);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(view -> registerUser());

        loginNow.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        usernameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm your password");
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", userId);
                        user.put("username", username);
                        user.put("email", email);

                        firestore.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Error saving user info", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
