package com.example.tnews;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        Log.d(TAG, "Options menu created");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Menu item selected: " + item.getItemId());

        if (item.getItemId() == R.id.dropdown) {
            Log.d(TAG, "Dropdown menu item clicked");

            // Try multiple ways to find the anchor view
            View anchorView = findViewById(R.id.dropdown_anchor);
            if (anchorView == null) {
                // If dropdown_anchor not found, try using the toolbar itself
                anchorView = getSupportActionBar() != null ?
                        findViewById(android.R.id.home) :
                        findViewById(R.id.dropdown_anchor);
                Log.d(TAG, "Primary anchor not found, using alternative");
            }

            if (anchorView != null) {
                Log.d(TAG, "Anchor view found, showing popup");
                showPopupMenu(anchorView);
            } else {
                Log.e(TAG, "No suitable anchor view found");
                // Use the toolbar as fallback
                View toolbar = findViewById(R.id.dropdown_anchor);
                if (toolbar == null) {
                    // Create a temporary view if nothing else works
                    View tempView = new View(this);
                    showPopupMenu(tempView);
                } else {
                    showPopupMenu(toolbar);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void showPopupMenu(View anchor) {
        try {
            Log.d(TAG, "Creating popup menu");
            PopupMenu popupMenu = new PopupMenu(this, anchor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                popupMenu.setGravity(Gravity.END);
            }

            popupMenu.getMenuInflater().inflate(R.menu.top_dropdown_menu, popupMenu.getMenu());

            // Set rounded background to the internal ListView of the PopupMenu
            popupMenu.setOnDismissListener(menu -> Log.d(TAG, "Popup menu dismissed"));

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                Log.d(TAG, "Popup menu item clicked: " + id);

                if (id == R.id.menu_user_info) {
                    // Open UserInfoActivity instead of showing toast
                    Intent intent = new Intent(this, UserInfoActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menu_dev_info) {
                    Intent intent = new Intent(this, DeveloperInfoActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menu_add_news) {
                    Intent intent = new Intent(this, AddNewsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            // Show the popup first, so the internal ListView can be accessed
            popupMenu.show();

            // Access the popup's ListView and set custom background
            // Warning: This uses reflection and internal API, which might break in future Android versions
            try {
                java.lang.reflect.Field mPopupField = popupMenu.getClass().getDeclaredField("mPopup");
                mPopupField.setAccessible(true);
                Object mPopup = mPopupField.get(popupMenu);

                if (mPopup != null) {
                    java.lang.reflect.Method getListViewMethod = mPopup.getClass().getDeclaredMethod("getListView");
                    getListViewMethod.setAccessible(true);
                    ListView listView = (ListView) getListViewMethod.invoke(mPopup);

                    if (listView != null) {
                        Drawable bg = getResources().getDrawable(R.drawable.popup_menu_background);
                        listView.setBackground(bg);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to set popup menu background with rounded corners", e);
            }

            Log.d(TAG, "Popup menu shown successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error showing popup menu", e);
            Toast.makeText(this, "Error showing menu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
