package com.nwhacks.recharge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UsersActivity extends AppCompatActivity {

    // button declaring
    private Button usersBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // sets header title of activity
        setTitle("Users");

        // create objects
        usersBackBtn = findViewById(R.id.usersBackBtn);

        // button click listener
        usersBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), MapsActivity.class);

                startActivity(backIntent);
            }
        });
    }
}
