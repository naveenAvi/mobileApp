package com.example.tnews;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    private TextView tvUsername, tvEmail , etFullName, etEmail;
    private ImageView ivAvatar;
    private Button btnUpdateInfo;
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initViews();
        setupToolbar();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserDataFromFirebase(userId);
        }

        setupClickListeners();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        ivAvatar = findViewById(R.id.iv_avatar);
        btnUpdateInfo = findViewById(R.id.btn_update_info);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Information");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserDataFromFirebase(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");

                        // Save to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_USERNAME, username != null ? username : "Guest User");
                        editor.putString(KEY_EMAIL, email != null ? email : "guest@example.com");
                        editor.apply();

                        // Update UI
                        tvUsername.setText(username != null ? username : "Guest User");
                        tvEmail.setText(email != null ? email : "guest@example.com");

                        etFullName.setText(username != null ? username : "Guest User");
                        etEmail.setText(email != null ? email : "guest@example.com");

                        ivAvatar.setImageResource(R.drawable.ic_user_avatar);
                        Log.d(TAG, "User data fetched from Firebase: " + username + ", " + email);
                    } else {
                        Log.d(TAG, "No user document found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch user data", e);
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupClickListeners() {
        btnUpdateInfo.setOnClickListener(v -> showUpdateDialog());
    }

    private void showUpdateDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_user);
        dialog.setCancelable(true);

        TextInputLayout tilUsername = dialog.findViewById(R.id.til_username);
        TextInputEditText etUsername = dialog.findViewById(R.id.et_username);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        String currentUsername = sharedPreferences.getString(KEY_USERNAME, "");
        etUsername.setText(currentUsername);

        btnSave.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();

            if (newUsername.isEmpty()) {
                tilUsername.setError("Username cannot be empty");
                return;
            }

            if (newUsername.length() < 3) {
                tilUsername.setError("Username must be at least 3 characters");
                return;
            }

            tilUsername.setError(null);

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, newUsername);
            editor.apply();

            // Update Firestore
            db.collection("users").document(userId)
                    .update("username", newUsername)
                    .addOnSuccessListener(aVoid -> {
                        tvUsername.setText(newUsername);
                        Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Log.d(TAG, "Username updated to: " + newUsername);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Firestore update failed", e);
                    });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }

    // Optional utility method to save user data
    public static void saveUserData(android.content.Context context, String username, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }
}

