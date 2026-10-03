package com.smartpantry.app;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.switchmaterial.SwitchMaterial;

/** Settings screen: toggle for expiring-soon alerts, stored in SharedPreferences. */
public class SettingsActivity extends BaseActivity {

    public static final String PREFS = "smart_pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SwitchMaterial sw = findViewById(R.id.swExpiry);
        sw.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        sw.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, checked).apply());

        setupBottomNav(R.id.nav_settings);
    }
}
