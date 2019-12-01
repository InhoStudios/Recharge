package com.nwhacks.recharge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    // declare stuff
    private Button accountSettingsBackBtn;
    private Button logoutBtn;
    private FirebaseAuth firebaseAuth;

    private Button saveBtn;
    private TextView nameEditText;

    Button chooseChargerButton;
    TextView listChargersOwnedTextView;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();


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

        chooseChargerButton = findViewById(R.id.chooseChargerBtn);
        listChargersOwnedTextView = findViewById(R.id.listChargersOwnedTextView);

        listItems = getResources().getStringArray(R.array.charger_type);
        checkedItems = new boolean[listItems.length];

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

        chooseChargerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AccountSettingsActivity.this);
                mBuilder.setTitle("Choose you owned chargers.");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) {
                            if(!mUserItems.contains(which)) {
                                mUserItems.add(which);
                            } else {
                                mUserItems.remove(which);
                            }
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for(int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems[mUserItems.get(i)];
                            if(i != mUserItems.size()-1) {
                                item = item + ", ";
                            }
                        }
                        listChargersOwnedTextView.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialogInterface.dismiss();
                    }
                });
            }
        });


    }
}
