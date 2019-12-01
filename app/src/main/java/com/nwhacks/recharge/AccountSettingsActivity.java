package com.nwhacks.recharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettingsActivity extends AppCompatActivity {

    // declare stuff
    private Button accountSettingsBackBtn;
    private Button logoutBtn;
    private FirebaseAuth firebaseAuth;

    private Button saveBtn;
    private TextView nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // sets header title of activity
        setTitle("Account Settings");

        // create objects
        accountSettingsBackBtn = findViewById(R.id.accountSettingsBackBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        firebaseAuth = firebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        saveBtn = findViewById(R.id.saveBtn);
        nameEditText = findViewById(R.id.nameEditText);

        // setting user properties to textboxes
        //nameEditText.setText(mFirebaseAnalytics.getUserProperty("favorite_food", mFavoriteFood););

        // onclick listener
        accountSettingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(backIntent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                Toast toast = Toast.makeText(getApplicationContext(), "User logged out", Toast.LENGTH_LONG);
                toast.show();

                Intent backIntent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(backIntent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info from boxes
                String name = nameEditText.getText().toString().trim();

                Toast toast = Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_LONG);
                toast.show();
            }
        });


    }
}
