package com.nwhacks.recharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    // buttons declaring
    Button settingsBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // sets header title of activity
        setTitle("Settings");

        // create objects
        settingsBackBtn = findViewById(R.id.settingsBackBtn);

        // button click listener
        settingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), MapsActivity.class);

                startActivity(backIntent);
            }
        });
    }
}
