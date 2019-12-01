package com.nwhacks.recharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    // buttons declaring
    private Button settingsBackBtn;
    private Button accountSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // sets header title of activity
        setTitle("Settings");

        // create objects
        settingsBackBtn = findViewById(R.id.settingsBackBtn);
        accountSettingsBtn = findViewById(R.id.accountSettingsBtn);

        // button click listener
        settingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(backIntent);
            }
        });

        accountSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountSettingsIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(accountSettingsIntent);
            }
        });
    }
}
