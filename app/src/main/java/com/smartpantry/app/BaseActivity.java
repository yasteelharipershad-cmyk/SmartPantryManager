package com.smartpantry.app;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Shared bottom-navigation setup used by the Pantry, Recipes and Settings screens. */
public abstract class BaseActivity extends AppCompatActivity {

    private int navId;

    protected void setupBottomNav(int selectedId) {
        navId = selectedId;
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(selectedId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;

            Class<?> target;
            if (id == R.id.nav_pantry) target = MainActivity.class;
            else if (id == R.id.nav_suggested) target = SuggestedActivity.class;
            else target = SettingsActivity.class;

            Intent intent = new Intent(this, target);
            if (target == MainActivity.class) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            startActivity(intent);
            if (!(this instanceof MainActivity)) finish();
            return true;
        });
    }

    /** Re-highlights this screen's tab whenever the screen becomes visible again. */
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null && navId != 0) nav.setSelectedItemId(navId);
    }
}