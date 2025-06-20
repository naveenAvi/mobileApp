package com.example.tnews;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;


public class DeveloperInfoActivity extends BaseActivity {

    private static final String PHONE_NUMBER = "0766308272";
    private static final String EMAIL_ADDRESS = "2020t00880@stu.cmb.ac.lk";
    private MaterialButton btnEmailMe;
    private MaterialButton btnCallMe;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);

        initViews();
        setupBottomNavigation();

//        setupToolbarWithUsername();

        setupClickListeners();
    }

    private void initViews() {
        btnEmailMe = findViewById(R.id.btn_email);
        btnCallMe = findViewById(R.id.btn_call);
        MaterialToolbar toolbar = findViewById(R.id.dropdown_anchor);
        setSupportActionBar(toolbar);
//        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupClickListeners() {
        btnEmailMe.setOnClickListener(v -> {
            openEmailApp();
        });
        btnCallMe.setOnClickListener(v -> {
            makePhoneCall();
        });
    }

    private void openEmailApp() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + EMAIL_ADDRESS));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact from News App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Tasheela,\n\nI would like to get in touch with you.\n\nBest regards,");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(emailIntent, "Choose Email App"));
            } else {
                copyEmailToClipboard();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open email app", Toast.LENGTH_SHORT).show();
            copyEmailToClipboard();
        }
    }

    private void copyEmailToClipboard() {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip =
                android.content.ClipData.newPlainText("Email", EMAIL_ADDRESS);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Email copied to clipboard: " + EMAIL_ADDRESS, Toast.LENGTH_LONG).show();
    }

    private void makePhoneCall() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            if (callIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(callIntent);
            } else {
                copyPhoneToClipboard();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make phone call", Toast.LENGTH_SHORT).show();
            copyPhoneToClipboard();
        }
    }

    private void copyPhoneToClipboard() {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip =
                android.content.ClipData.newPlainText("Phone", PHONE_NUMBER);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Phone number copied to clipboard: " + PHONE_NUMBER, Toast.LENGTH_LONG).show();
    }
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_academic) {
                    startActivity(new Intent(DeveloperInfoActivity.this, AcademicNewsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_events) {
                    startActivity(new Intent(DeveloperInfoActivity.this, EventsNewsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_sports) {
                    startActivity(new Intent(DeveloperInfoActivity.this, SportsNewsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
}